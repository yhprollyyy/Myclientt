package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** One-shot: copies your coordinates to the clipboard, then turns itself off. */
public class CordSnapperModule extends Module {
    public CordSnapperModule() { super("CordSnapper"); }

    @Override
    protected void onEnable() {
        var mc = Minecraft.getInstance();
        if (mc.player != null) {
            var pos = mc.player.blockPosition();
            String s = pos.getX() + " " + pos.getY() + " " + pos.getZ();
            mc.keyboardHandler.setClipboard(s);
            mc.player.displayClientMessage(Component.literal("Copied: " + s), false);
        }
        setEnabled(false);
    }
}
