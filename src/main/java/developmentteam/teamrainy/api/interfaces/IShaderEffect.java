package developmentteam.teamrainy.api.interfaces;

import net.minecraft.client.gl.Framebuffer;

public interface IShaderEffect {
    void addHook(String name, Framebuffer buffer);
}