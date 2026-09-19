package com.example.myclient.modules;

import net.minecraft.client.Minecraft;

public class SprintModule extends Module {
    public SprintModule() { super("Sprint"); }

    @Override
    public void onTick(Minecraft mc) {
        var p = mc.player;
        if (mc.options.keyUp.isDown() && !p.isUsingItem() && !p.horizontalCollision) {
            p.setSprinting(true);
        }
    }
}
