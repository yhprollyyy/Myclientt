package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

/** Keeps a Totem of Undying in the offhand. */
public class AutoTotemModule extends Module {
    private int cooldown;

    public AutoTotemModule() { super("AutoTotem"); }

    @Override
    public void onTick(Minecraft mc) {
        if (cooldown > 0) { cooldown--; return; }
        var p = mc.player;
        if (mc.screen != null || mc.gameMode == null) return;
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;

        var inv = p.getInventory();
        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                int menuSlot = i < 9 ? i + 36 : i; // inventory index -> container slot
                // button 40 = offhand swap
                mc.gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, menuSlot, 40, ClickType.SWAP, p);
                cooldown = 4;
                return;
            }
        }
    }
}
