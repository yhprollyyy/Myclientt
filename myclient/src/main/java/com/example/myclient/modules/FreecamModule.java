package com.example.myclient.modules;

import net.minecraft.client.Minecraft;

public class FreecamModule extends Module {
    private boolean oldNoGravity;

    public FreecamModule() { super("Freecam"); }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            oldNoGravity = mc.player.isNoGravity();
            mc.player.setNoGravity(true);
        }
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.setNoGravity(oldNoGravity);
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player != null) mc.player.setNoGravity(true);
    }
}
