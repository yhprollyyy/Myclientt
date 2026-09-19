package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Disconnects when health falls to the threshold (3 hearts). */
public class AutoLogModule extends Module {
    private static final float THRESHOLD = 6.0f;

    public AutoLogModule() { super("AutoLog"); }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player.getHealth() > THRESHOLD || mc.getConnection() == null) return;
        setEnabled(false);
        mc.getConnection().getConnection().disconnect(Component.literal("AutoLog: health low"));
    }
}
