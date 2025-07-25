package developmentteam.teamrainy;

import developmentteam.teamrainy.api.events.eventbus.EventBus;
import developmentteam.teamrainy.core.impl.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.fabricmc.api.ModInitializer;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.util.Asserts;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class TeamRainy implements ModInitializer {

    @Override
    public void onInitialize() {
        load();
    }

    public static final String NAME = "TeamRainy";
    public static final String CHAT_SUFFIX = "| TeamRainy";
    public static final String VERSION = "0.0.2";
    public static String PREFIX = "+";
    public static final EventBus EVENT_BUS = new EventBus();
    public static ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    // 核心
    public static HoleManager HOLE;
    public static PlayerManager PLAYER;
    public static TradeManager TRADE;
    public static XrayManager XRAY;
    public static ModuleManager MODULE;
    public static CommandManager COMMAND;
    public static GuiManager GUI;
    public static ConfigManager CONFIG;
    public static RotationManager ROTATION;
    public static BreakManager BREAK;
    public static PopManager POP;
    public static FriendManager FRIEND;
    public static TimerManager TIMER;
    public static ShaderManager SHADER;
    public static FPSManager FPS;
    public static ServerManager SERVER;
    public static ThreadManager THREAD;
    public static boolean loaded = false;

    public static void load() {
        EVENT_BUS.registerLambdaFactory((lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        CONFIG = new ConfigManager();

        PREFIX = TeamRainy.CONFIG.getString("prefix", "+");
        THREAD = new ThreadManager();
        HOLE = new HoleManager();
        MODULE = new ModuleManager();
        COMMAND = new CommandManager();
        GUI = new GuiManager();
        FRIEND = new FriendManager();
        XRAY = new XrayManager();
        TRADE = new TradeManager();
        ROTATION = new RotationManager();
        BREAK = new BreakManager();
        PLAYER = new PlayerManager();

        POP = new PopManager();
        TIMER = new TimerManager();
        SHADER = new ShaderManager();
        FPS = new FPSManager();
        SERVER = new ServerManager();
        CONFIG.loadSettings();
        System.out.println("[" + TeamRainy.NAME + "] loaded");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (loaded) {
                save();
            }
        }));
        loaded = true;
    }

    public static void unload() {
        loaded = false;
        System.out.println("[" + TeamRainy.NAME + "] Unloading..");
        EVENT_BUS.listenerMap.clear();
        ConfigManager.resetModule();
        System.out.println("[" + TeamRainy.NAME + "] Unloaded");
    }

    public static void save() {
        System.out.println("[" + TeamRainy.NAME + "] Saving");
        CONFIG.saveSettings();
        FRIEND.save();
        XRAY.save();
        TRADE.save();
        System.out.println("[" + TeamRainy.NAME + "] Saved");
    }
}

/*
 *   _____                              ____            _                   _   _                  _      ____
 *  |_   _|   ___    __ _   _ __ ___   |  _ \    __ _  (_)  _ __    _   _  | | | |   __ _    ___  | | __ |  _ \    ___  __   __
 *    | |    / _ \  / _` | | '_ ` _ \  | |_) |  / _` | | | | '_ \  | | | | | |_| |  / _` |  / __| | |/ / | | | |  / _ \ \ \ / /
 *    | |   |  __/ | (_| | | | | | | | |  _ <  | (_| | | | | | | | | |_| | |  _  | | (_| | | (__  |   <  | |_| | |  __/  \ V /
 *    |_|    \___|  \__,_| |_| |_| |_| |_| \_\  \__,_| |_| |_| |_|  \__, | |_| |_|  \__,_|  \___| |_|\_\ |____/   \___|   \_/
 *                                                                  |___/
 *                _                                        _____    ___    _____
 *   ___    ___  (_)   ___   _ __     ___    ___          |___  |  / _ \  |___  |
 *  / __|  / __| | |  / _ \ | '_ \   / __|  / _ \            / /  | (_) |    / /
 *  \__ \ | (__  | | |  __/ | | | | | (__  |  __/           / /    \__, |   / /
 *  |___/  \___| |_|  \___| |_| |_|  \___|  \___|  _____   /_/       /_/   /_/
 *                                                |_____|
 *    __           _                                    ____    ___     ____    ___
 *   / _|  _   _  | |_   _   _   _ __    ___           / ___|  / _ \   / ___|  / _ \
 *  | |_  | | | | | __| | | | | | '__|  / _ \         | |     | | | | | |     | | | |
 *  |  _| | |_| | | |_  | |_| | | |    |  __/         | |___  | |_| | | |___  | |_| |
 *  |_|    \__,_|  \__|  \__,_| |_|     \___|  _____   \____|  \___/   \____|  \___/
 *                                            |_____|
 *    __    __     _____                __                  ___
 *   / _|  / /_   |___  |  _ __ ___    / /_    _ __ ___    ( _ )
 *  | |_  | '_ \     / /  | '_ ` _ \  | '_ \  | '_ ` _ \   / _ \
 *  |  _| | (_) |   / /   | | | | | | | (_) | | | | | | | | (_) |
 *  |_|    \___/   /_/    |_| |_| |_|  \___/  |_| |_| |_|  \___/
 *
 */
