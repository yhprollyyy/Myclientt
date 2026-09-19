package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Switches to the fastest hotbar tool for the block you're breaking. */
public class AutoToolModule extends Module {
    public AutoToolModule() { super("AutoTool"); }

    @Override
    public void onTick(Minecraft mc) {
        if (!mc.options.keyAttack.isDown()) return;
        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;

        var state = mc.level.getBlockState(hit.getBlockPos());
        var inv = mc.player.getInventory();
        int best = inv.getSelectedSlot();
        float bestSpeed = inv.getItem(best).getDestroySpeed(state);
        for (int i = 0; i < 9; i++) {
            float s = inv.getItem(i).getDestroySpeed(state);
            if (s > bestSpeed) { bestSpeed = s; best = i; }
        }
        if (best != inv.getSelectedSlot()) inv.setSelectedSlot(best);
    }
}
