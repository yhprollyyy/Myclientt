package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Eats hotbar food when hunger drops to the threshold. */
public class AutoEatModule extends Module {
    private static final int THRESHOLD = 14;
    private boolean eating;

    public AutoEatModule() { super("AutoEat"); }

    private static boolean isSafeFood(ItemStack s) {
        return s.has(DataComponents.FOOD)
            && !s.is(Items.ROTTEN_FLESH) && !s.is(Items.SPIDER_EYE)
            && !s.is(Items.POISONOUS_POTATO) && !s.is(Items.PUFFERFISH)
            && !s.is(Items.CHORUS_FRUIT);
    }

    @Override
    public void onTick(Minecraft mc) {
        var p = mc.player;
        if (p.getFoodData().getFoodLevel() > THRESHOLD) { release(mc); return; }
        var inv = p.getInventory();
        for (int i = 0; i < 9; i++) {
            if (isSafeFood(inv.getItem(i))) {
                if (inv.getSelectedSlot() != i) inv.setSelectedSlot(i);
                mc.options.keyUse.setDown(true);
                eating = true;
                return;
            }
        }
        release(mc);
    }

    private void release(Minecraft mc) {
        if (eating) { mc.options.keyUse.setDown(false); eating = false; }
    }

    @Override
    protected void onDisable() { release(Minecraft.getInstance()); }
}
