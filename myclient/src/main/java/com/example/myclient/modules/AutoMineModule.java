package com.example.myclient.modules;

import net.minecraft.client.Minecraft;

/** Holds the attack/mine button for you. */
public class AutoMineModule extends Module {
    public AutoMineModule() { super("AutoMine"); }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.screen == null) mc.options.keyAttack.setDown(true);
    }

    @Override
    protected void onDisable() { Minecraft.getInstance().options.keyAttack.setDown(false); }
}
