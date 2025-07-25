package developmentteam.teamrainy.mod.modules.impl.combat;

import developmentteam.teamrainy.api.utils.combat.CombatUtil;
import developmentteam.teamrainy.api.utils.entity.EntityUtil;
import developmentteam.teamrainy.api.utils.entity.InventoryUtil;
import developmentteam.teamrainy.api.utils.entity.MovementUtil;
import developmentteam.teamrainy.api.utils.math.Timer;
import developmentteam.teamrainy.api.utils.world.BlockUtil;
import developmentteam.teamrainy.core.impl.CommandManager;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.impl.client.AntiCheat;
import developmentteam.teamrainy.mod.modules.settings.Placement;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PistonCrystal extends Module {
    public static PistonCrystal INSTANCE;

    // 设置项 B+A
    private final BooleanSetting rotate = add(new BooleanSetting("Rotate", false));
    private final BooleanSetting pistonPacket = add(new BooleanSetting("PistonPacket", false));
    private final BooleanSetting noEating = add(new BooleanSetting("NoEating", true));
    private final BooleanSetting eatingBreak = add(new BooleanSetting("EatingBreak", false));
    private final SliderSetting placeRange = add(new SliderSetting("PlaceRange", 5.0f, 1.0f, 8.0f));
    private final SliderSetting range = add(new SliderSetting("Range", 4.0f, 1.0f, 8.0f));
    private final BooleanSetting fire = add(new BooleanSetting("Fire", true));
    private final BooleanSetting switchPos = add(new BooleanSetting("Switch", false));
    private final BooleanSetting onlyGround = add(new BooleanSetting("SelfGround", true));
    private final BooleanSetting onlyStatic = add(new BooleanSetting("MovingPause", true));
    private final SliderSetting updateDelay = add(new SliderSetting("PlaceDelay", 100, 0, 500));
    private final SliderSetting posUpdateDelay = add(new SliderSetting("PosUpdateDelay", 500, 0, 1000));
    private final SliderSetting stageSetting = add(new SliderSetting("Stage", 4, 1, 10));
    private final SliderSetting pistonStage = add(new SliderSetting("PistonStage", 1, 1, 10));
    private final SliderSetting pistonMaxStage = add(new SliderSetting("PistonMaxStage", 1, 1, 10));
    private final SliderSetting powerStage = add(new SliderSetting("PowerStage", 3, 1, 10));
    private final SliderSetting powerMaxStage = add(new SliderSetting("PowerMaxStage", 3, 1, 10));
    private final SliderSetting crystalStage = add(new SliderSetting("CrystalStage", 4, 1, 10));
    private final SliderSetting crystalMaxStage = add(new SliderSetting("CrystalMaxStage", 4, 1, 10));
    private final SliderSetting fireStage = add(new SliderSetting("FireStage", 2, 1, 10));
    private final SliderSetting fireMaxStage = add(new SliderSetting("FireMaxStage", 2, 1, 10));
    private final BooleanSetting inventory = add(new BooleanSetting("InventorySwap", true));
    private final BooleanSetting debug = add(new BooleanSetting("Debug", false));

    // 优化设置 A
    private final BooleanSetting preferAnchor = add(new BooleanSetting("PreferAnchor", true));
    private final BooleanSetting preferCrystal = add(new BooleanSetting("PreferCrystal", true));
    private final BooleanSetting yawDeceive = add(new BooleanSetting("YawDeceive", true));
    private final BooleanSetting autoYaw = add(new BooleanSetting("AutoYaw", true));
    private final SliderSetting yawStep = add(new SliderSetting("YawStep", 0.3f, 0.1f, 1.0f, 0.01f));
    private final BooleanSetting checkLook = add(new BooleanSetting("CheckLook", true));
    private final SliderSetting fov = add(new SliderSetting("Fov", 5f, 0f, 30f));

    private PlayerEntity target = null;
    private float lastYaw = 0;
    private float lastPitch = 0;

    public PistonCrystal() {
        super("PistonCrystal", Category.Combat);
        setChinese("活塞水晶");
        INSTANCE = this;
    }

    private final Timer timer = new Timer();
    private final Timer crystalTimer = new Timer();
    public BlockPos bestPos = null;
    public BlockPos bestOPos = null;
    public Direction bestFacing = null;
    public double distance = 100;
    public boolean getPos = false;
    private boolean isPiston = false;
    public int stage = 1;

    // 阶段管理逻辑 A
    public void onTick() {
        pistonStage.setValue(Math.min(pistonStage.getValue(), stageSetting.getValue()));
        fireStage.setValue(Math.min(fireStage.getValue(), stageSetting.getValue()));
        powerStage.setValue(Math.min(powerStage.getValue(), stageSetting.getValue()));
        crystalStage.setValue(Math.min(crystalStage.getValue(), stageSetting.getValue()));

        pistonMaxStage.setValue(Math.min(pistonMaxStage.getValue(), stageSetting.getValue()));
        fireMaxStage.setValue(Math.min(fireMaxStage.getValue(), stageSetting.getValue()));
        powerMaxStage.setValue(Math.min(powerMaxStage.getValue(), stageSetting.getValue()));
        crystalMaxStage.setValue(Math.min(crystalMaxStage.getValue(), stageSetting.getValue()));

        crystalStage.setValue(Math.min(crystalStage.getValue(), crystalMaxStage.getValue()));
        powerStage.setValue(Math.min(powerStage.getValue(), powerMaxStage.getValue()));
        pistonStage.setValue(Math.min(pistonStage.getValue(), pistonMaxStage.getValue()));
        fireStage.setValue(Math.min(fireStage.getValue(), fireMaxStage.getValue()));
    }

    // 主逻辑 A+B
    @Override
    public void onUpdate() {
        onTick();
        target = CombatUtil.getClosestEnemy(range.getValue());
        if (target == null) return;

        // 优先级系统 A
        if (preferAnchor.getValue() && RainyAnchor.INSTANCE.currentPos != null) return;
        if (preferCrystal.getValue() && RainyCrystal.crystalPos != null) return;

        if (noEating.getValue() && mc.player.isUsingItem()) return;
        if (check(onlyStatic.getValue(), !mc.player.isOnGround(), onlyGround.getValue())) return;

        BlockPos pos = EntityUtil.getEntityPos(target, true);

        // 水晶攻击 B
        if (!mc.player.isUsingItem() || eatingBreak.getValue()) {
            checkAndAttackCrystal(pos.up(0));
            checkAndAttackCrystal(pos.up(1));
            checkAndAttackCrystal(pos.up(2));
        }

        // 保留的活塞状态检测 B
        if (bestPos != null && mc.world.getBlockState(bestPos).getBlock() instanceof PistonBlock) {
            isPiston = true;
        } else if (isPiston) {
            isPiston = false;
            crystalTimer.reset();
            bestPos = null;
        }

        // 位置更新 A
        if (crystalTimer.passedMs(posUpdateDelay.getValueInt())) {
            resetPositionSearch();
            getBestPos(pos.up(2));
            getBestPos(pos.up());
        }

        // 执行活塞水晶
        if (timer.passedMs(updateDelay.getValueInt()) && getPos && bestPos != null) {
            timer.reset();
            if (debug.getValue()) {
                CommandManager.sendChatMessage("[PistonCrystal] Piston:" + bestPos +
                        " Facing:" + bestFacing +
                        " Crystal:" + bestOPos.offset(bestFacing));
            }
            doPistonAura(bestPos, bestFacing, bestOPos);
        }
    }

    private void checkAndAttackCrystal(BlockPos pos) {
        if (checkCrystal(pos)) {
            CombatUtil.attackCrystal(pos, rotate.getValue(), true);
        }
    }

    private void resetPositionSearch() {
        stage = 0;
        distance = 100;
        getPos = false;
    }

    // 位置搜索算法 A
    private void getBestPos(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN || dir == Direction.UP) continue;
            calculatePositions(pos, dir);
        }
    }

    private void calculatePositions(BlockPos pos, Direction dir) {
        if (!BlockUtil.canPlaceCrystal(pos.offset(dir)) && !checkCrystal2(pos.offset(dir))) return;

        // 位置算法优化 A
        addPositionCandidate(pos.offset(dir, 3), dir, pos);
        addPositionCandidate(pos.offset(dir, 3).up(), dir, pos);

        // 偏移位置计算
        int offsetX = pos.offset(dir).getX() - pos.getX();
        int offsetZ = pos.offset(dir).getZ() - pos.getZ();

        BlockPos[] candidates = {
                pos.offset(dir, 3).add(offsetZ, 0, offsetX),
                pos.offset(dir, 3).add(-offsetZ, 0, -offsetX),
                pos.offset(dir, 3).add(offsetZ, 1, offsetX),
                pos.offset(dir, 3).add(-offsetZ, 1, -offsetX),
                pos.offset(dir, 2),
                pos.offset(dir, 2).up(),
                pos.offset(dir, 2).add(offsetZ, 0, offsetX),
                pos.offset(dir, 2).add(-offsetZ, 0, -offsetX),
                pos.offset(dir, 2).add(offsetZ, 1, offsetX),
                pos.offset(dir, 2).add(-offsetZ, 1, -offsetX)
        };

        for (BlockPos candidate : candidates) {
            addPositionCandidate(candidate, dir, pos);
        }
    }

    private void addPositionCandidate(BlockPos pos, Direction facing, BlockPos oPos) {
        // 切换位置逻辑 B
        if (switchPos.getValue() && bestPos != null && bestPos.equals(pos) && mc.world.isAir(bestPos)) {
            return;
        }

        // 放置检查
        if (!BlockUtil.canPlace(pos, placeRange.getValue()) && !(getBlock(pos) instanceof PistonBlock)) return;
        if (findClass(PistonBlock.class) == -1) return;

        // 方向有效性检查 A
        if (!isTrueFacing(pos, facing)) return;

        // 环境检查
        if (!mc.world.isAir(pos.offset(facing, -1)) ||
                getBlock(pos.offset(facing, -1)) == Blocks.FIRE ||
                (getBlock(pos.offset(facing.getOpposite())) == Blocks.MOVING_PISTON &&
                        !checkCrystal2(pos.offset(facing.getOpposite())))) {
            return;
        }

        // 距离检查
        double dist = MathHelper.sqrt((float) EntityUtil.getEyesPos().squaredDistanceTo(pos.toCenterPos()));
        if (!(dist < distance || bestPos == null)) return;

        bestPos = pos;
        bestOPos = oPos;
        bestFacing = facing;
        distance = dist;
        getPos = true;
        crystalTimer.reset();
    }

    // 活塞水晶执行 B+A
    private void doPistonAura(BlockPos pos, Direction facing, BlockPos oPos) {
        if (stage >= stageSetting.getValue()) stage = 0;
        stage++;

        // 放置活塞 B+A
        if (mc.world.isAir(pos) && BlockUtil.canPlace(pos)) {
            if (stage >= pistonStage.getValue() && stage <= pistonMaxStage.getValue()) {
                Direction side = BlockUtil.getPlaceSide(pos);
                if (side == null) return;

                int oldSlot = mc.player.getInventory().selectedSlot;
                int pistonSlot = findClass(PistonBlock.class);

                // 活塞方向处理 A
                if (shouldYawCheck()) pistonFacing(facing);

                doSwap(pistonSlot);
                BlockUtil.placeBlock(pos, rotate.getValue(), pistonPacket.getValue());

                // 物品切换处理
                if (inventory.getValue()) {
                    doSwap(pistonSlot);
                    EntityUtil.syncInventory();
                } else {
                    doSwap(oldSlot);
                }
            }
        }

        // 放置红石块
        if (stage >= powerStage.getValue() && stage <= powerMaxStage.getValue()) {
            placeRedstone(pos, facing, oPos.offset(facing));
        }

        // 放置水晶
        if (stage >= crystalStage.getValue() && stage <= crystalMaxStage.getValue()) {
            placeCrystal(oPos, facing);
        }

    }

    private void placeCrystal(BlockPos pos, Direction facing) {
        BlockPos crystalPos = pos.offset(facing);
        if (!BlockUtil.canPlaceCrystal(crystalPos)) return;

        int crystalSlot = findItem(Items.END_CRYSTAL);
        if (crystalSlot == -1) return;

        int oldSlot = mc.player.getInventory().selectedSlot;
        doSwap(crystalSlot);
        BlockUtil.placeCrystal(crystalPos, rotate.getValue());

        if (inventory.getValue()) {
            doSwap(crystalSlot);
            EntityUtil.syncInventory();
        } else {
            doSwap(oldSlot);
        }
    }

    private void placeFire(BlockPos pos, Direction facing) {
        if (!fire.getValue()) return;

        int fireSlot = findItem(Items.FLINT_AND_STEEL);
        if (fireSlot == -1) return;

        // 火焰位置计算 A
        int[] offsetsX = {0, facing.getOffsetZ(), -facing.getOffsetZ()};
        int[] offsetsY = {0, 1};
        int[] offsetsZ = {0, facing.getOffsetX(), -facing.getOffsetX()};

        for (int x : offsetsX) {
            for (int y : offsetsY) {
                for (int z : offsetsZ) {
                    BlockPos firePos = pos.add(x, y, z);
                    if (canPlaceFire(firePos)) {
                        int oldSlot = mc.player.getInventory().selectedSlot;
                        doSwap(fireSlot);
                        placeFireAt(firePos);

                        if (inventory.getValue()) {
                            doSwap(fireSlot);
                            EntityUtil.syncInventory();
                        } else {
                            doSwap(oldSlot);
                        }
                        return;
                    }
                }
            }
        }
    }

    private void placeRedstone(BlockPos pos, Direction facing, BlockPos crystalPos) {
        if (!mc.world.isAir(pos.offset(facing, -1)) &&
                getBlock(pos.offset(facing, -1)) != Blocks.FIRE &&
                getBlock(pos.offset(facing.getOpposite())) != Blocks.MOVING_PISTON) {
            return;
        }

        // 检查已有红石块
        for (Direction dir : Direction.values()) {
            if (getBlock(pos.offset(dir)) == Blocks.REDSTONE_BLOCK) return;
        }

        int redstoneSlot = findBlock(Blocks.REDSTONE_BLOCK);
        if (redstoneSlot == -1) return;

        // 尝试最佳邻居位置 A
        Direction bestNeighbor = BlockUtil.getBestNeighboring(pos, facing);
        if (bestNeighbor != null && bestNeighbor != facing.getOpposite() &&
                BlockUtil.canPlace(pos.offset(bestNeighbor), placeRange.getValue()) &&
                !pos.offset(bestNeighbor).equals(crystalPos)) {

            placeRedstoneAt(pos.offset(bestNeighbor), redstoneSlot);
            return;
        }

        // 尝试其他方向
        for (Direction dir : Direction.values()) {
            if (dir == facing.getOpposite() ||
                    !BlockUtil.canPlace(pos.offset(dir), placeRange.getValue()) ||
                    pos.offset(dir).equals(crystalPos)) continue;

            placeRedstoneAt(pos.offset(dir), redstoneSlot);
            return;
        }
    }

    private void placeRedstoneAt(BlockPos pos, int slot) {
        int oldSlot = mc.player.getInventory().selectedSlot;
        doSwap(slot);
        BlockUtil.placeBlock(pos, rotate.getValue(), pistonPacket.getValue());

        if (inventory.getValue()) {
            doSwap(slot);
            EntityUtil.syncInventory();
        } else {
            doSwap(oldSlot);
        }
    }

    private void placeFireAt(BlockPos pos) {
        BlockUtil.clickBlock(pos.offset(Direction.DOWN), Direction.UP, rotate.getValue());
    }

    // 工具方法
    private boolean check(boolean onlyStatic, boolean onGround, boolean onlyGround) {
        return (MovementUtil.isMoving() && onlyStatic) ||
                (onGround && onlyGround) ||
                findBlock(Blocks.REDSTONE_BLOCK) == -1 ||
                findClass(PistonBlock.class) == -1 ||
                findItem(Items.END_CRYSTAL) == -1;
    }

    private boolean checkCrystal(BlockPos pos) {
        for (Entity entity : BlockUtil.getEntities(new Box(pos))) {
            if (entity instanceof EndCrystalEntity) {
                float damage = RainyCrystal.INSTANCE.calculateDamage(entity.getPos(), target, target);
                return damage > 7;
            }
        }
        return false;
    }

    private boolean checkCrystal2(BlockPos pos) {
        for (Entity entity : BlockUtil.getEntities(new Box(pos))) {
            if (entity instanceof EndCrystalEntity && EntityUtil.getEntityPos(entity).equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceFire(BlockPos pos) {
        if (BlockUtil.canReplace(pos.down())) return false;
        if (!mc.world.isAir(pos)) return false;
        if (!BlockUtil.canClick(pos.offset(Direction.DOWN))) return false;
        return AntiCheat.INSTANCE.placement.getValue() != Placement.Strict ||
                BlockUtil.isStrictDirection(pos.down(), Direction.UP);
    }

    // 旋转优化 A
    private boolean shouldYawCheck() {
        return yawDeceive.getValue() || (autoYaw.getValue() && !EntityUtil.isInsideBlock());
    }

    private boolean isTrueFacing(BlockPos pos, Direction facing) {
        if (shouldYawCheck()) return true;
        Direction side = BlockUtil.getPlaceSide(pos);
        if (side == null) side = Direction.UP;

        Vec3d hitVec = pos.offset(side.getOpposite()).toCenterPos()
                .add(new Vec3d(side.getVector().getX() * 0.5,
                        side.getVector().getY() * 0.5,
                        side.getVector().getZ() * 0.5));

        return Direction.fromRotation(EntityUtil.getLegitRotations(hitVec)[0]) == facing;
    }

    // 活塞方向处理 A
    public static void pistonFacing(Direction facing) {
        switch (facing) {
            case EAST: EntityUtil.sendYawAndPitch(-90.0f, 5.0f); break;
            case WEST: EntityUtil.sendYawAndPitch(90.0f, 5.0f); break;
            case NORTH: EntityUtil.sendYawAndPitch(180.0f, 5.0f); break;
            case SOUTH: EntityUtil.sendYawAndPitch(0.0f, 5.0f); break;
        }
    }

    private void doSwap(int slot) {
        if (inventory.getValue()) {
            InventoryUtil.inventorySwap(slot, mc.player.getInventory().selectedSlot);
        } else {
            InventoryUtil.switchToSlot(slot);
        }
    }

    public int findItem(Item item) {
        return inventory.getValue() ?
                InventoryUtil.findItemInventorySlot(item) :
                InventoryUtil.findItem(item);
    }

    public int findBlock(Block block) {
        return inventory.getValue() ?
                InventoryUtil.findBlockInventorySlot(block) :
                InventoryUtil.findBlock(block);
    }

    public int findClass(Class<?> clazz) {
        return inventory.getValue() ?
                InventoryUtil.findClassInventorySlot(clazz) :
                InventoryUtil.findClass(clazz);
    }

    private Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }
}
// @science_797:操你妈的这个活塞水晶我写了三遍，Byd死Java