package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

/** While gliding with an elytra, fires a hotbar firework rocket periodically. */
public class AutoFireworkModule extends Module {
    private static final int INTERVAL = 50; // ticks
    private int timer;

    public AutoFireworkModule() { super("AutoFirework"); }

    @Override
    public void onTick(Minecraft mc) {
        var p = mc.player;
        if (!p.isFallFlying() || mc.gameMode == null) { timer = 0; return; }
        if (++timer < INTERVAL) return;

        var inv = p.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(Items.FIREWORK_ROCKET)) {
                inv.setSelectedSlot(i);
                mc.gameMode.useItem(p, InteractionHand.MAIN_HAND);
                timer = 0;
                return;
            }
        }
    }
}
