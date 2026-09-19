package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FullbrightModule extends Module {
    public FullbrightModule() { super("Fullbright"); }

    @Override
    public void onTick(Minecraft mc) {
        // Client-side only visual effect
        mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
    }

    @Override
    protected void onDisable() {
        var p = Minecraft.getInstance().player;
        if (p != null) p.removeEffect(MobEffects.NIGHT_VISION);
    }
}
