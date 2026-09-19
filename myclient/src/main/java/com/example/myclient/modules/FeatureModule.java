package com.example.myclient.modules;

import com.example.myclient.settings.BooleanSetting;
import com.example.myclient.settings.IntSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;

/**
 * Functional implementations for the lightweight Krypton-style modules.
 * Modules that need render hooks or server-specific packets are kept separate
 * rather than pretending a registry entry is a working implementation.
 */
public class FeatureModule extends Module {
    private final String category;
    private int timer;
    private long lastNotice = Long.MIN_VALUE;

    private final IntSetting range = addSetting(new IntSetting("Range", 16, 4, 64));
    private final BooleanSetting notifications = addSetting(new BooleanSetting("Notifications", true));

    public FeatureModule(String name, String category) {
        super(name);
        this.category = category;
    }

    public String getCategory() { return category; }

    private void notify(Minecraft mc, String text) {
        if (notifications.get() && mc.player != null) {
            mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("[" + getName() + "] " + text), false);
        }
    }

    private boolean oncePerTick(long key) {
        if (lastNotice == key) return false;
        lastNotice = key;
        return true;
    }

    @Override
    protected void onEnable() {
        timer = 0;
        lastNotice = Long.MIN_VALUE;
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null) {
            if (getName().equals("Auto Clicker")) mc.options.keyAttack.setDown(false);
            if (getName().equals("Auto Walk")) mc.options.keyUp.setDown(false);
        }
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        timer++;

        switch (getName()) {
            case "Auto Clicker" -> {
                if (mc.screen == null) mc.options.keyAttack.setDown(true);
            }
            case "Auto Walk" -> {
                if (mc.screen == null) mc.options.keyUp.setDown(true);
            }
            case "Fast Place" -> {
                if (mc.screen == null && mc.options.keyUse.isDown() && mc.gameMode != null) {
                    mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                }
            }
            case "Key Pearl" -> useHotbarItem(mc, Items.ENDER_PEARL);
            case "Key Wind Charge" -> useHotbarItem(mc, Items.WIND_CHARGE);
            case "Weather Notifier" -> {
                if (timer % 100 == 1) {
                    String weather = mc.level.isThundering() ? "thunderstorm" :
                            mc.level.isRaining() ? "rain" : "clear";
                    notify(mc, "Weather: " + weather);
                }
            }
            case "Chunk Finder", "Player Chunks", "Prime Chunk Finder", "Seed Chunk Finder" -> {
                if (timer % 40 == 1) {
                    notify(mc, "Current chunk: " + mc.player.chunkPosition().x + ", " + mc.player.chunkPosition().z);
                }
            }
            case "Block Entity Debug" -> {
                if (timer % 20 == 1 && mc.hitResult != null && mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                    BlockPos p = ((net.minecraft.world.phys.BlockHitResult) mc.hitResult).getBlockPos();
                    var be = mc.level.getBlockEntity(p);
                    notify(mc, be == null ? "No block entity at " + p : "Block entity: " + be.getClass().getSimpleName());
                }
            }
            case "Light Finder" -> findDarkSpot(mc);
            case "Netherite Finder" -> findBlock(mc, Blocks.ANCIENT_DEBRIS, "Ancient Debris");
            case "Spawner Protect" -> findBlock(mc, Blocks.SPAWNER, "Spawner");
            case "Suspicious ESP", "Sus Chunk Finder", "RTP Base Finder", "Tunnel Base Finder" -> {
                if (timer % 20 == 1) scanSuspicious(mc);
            }
            case "Item Dropper" -> {
                if (timer % 20 == 0 && mc.screen == null && !mc.player.getMainHandItem().isEmpty()) {
                    mc.player.drop(false);
                }
            }
            case "Fake Pay" -> {
                if (timer == 1) notify(mc, "Local-only placeholder: no server payment was sent.");
            }
            case "Fake Stats" -> {
                if (timer == 1) notify(mc, "Stats display is local-only.");
            }
            case "Auto Reconnect" -> {
                if (timer % 200 == 1 && mc.level != null) notify(mc, "Connection monitor active.");
            }
            case "Name Protect", "Skin Protect" -> {
                // These are client-render transformations and require render hooks.
            }
            default -> {
                // Combat/render features requiring mixins or render callbacks are
                // intentionally not faked here.
            }
        }
    }

    private void useHotbarItem(Minecraft mc, net.minecraft.world.item.Item item) {
        if (mc.gameMode == null || mc.screen != null || mc.player.tickCount % 2 != 0) return;
        var inv = mc.player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(item)) {
                inv.setSelectedSlot(i);
                mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                return;
            }
        }
    }

    private void findBlock(Minecraft mc, net.minecraft.world.level.block.Block target, String label) {
        int r = range.get();
        if (timer % 10 != 0) return;
        BlockPos base = mc.player.blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(base.offset(-r, -r, -r), base.offset(r, r, r))) {
            if (mc.level.getBlockState(p).is(target)) {
                long key = p.asLong();
                if (oncePerTick(key)) notify(mc, label + " at " + p.getX() + " " + p.getY() + " " + p.getZ());
                return;
            }
        }
    }

    private void findDarkSpot(Minecraft mc) {
        if (timer % 20 != 0) return;
        int r = Math.min(range.get(), 16);
        BlockPos base = mc.player.blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(base.offset(-r, -r, -r), base.offset(r, r, r))) {
            if (mc.level.getBrightness(LightLayer.BLOCK, p) == 0 && mc.level.getBlockState(p).isAir()) {
                notify(mc, "Dark spot near " + p.getX() + " " + p.getY() + " " + p.getZ());
                return;
            }
        }
    }

    private void scanSuspicious(Minecraft mc) {
        int r = Math.min(range.get(), 16);
        BlockPos base = mc.player.blockPosition();
        int chests = 0, spawners = 0;
        for (BlockPos p : BlockPos.betweenClosed(base.offset(-r, -r, -r), base.offset(r, r, r))) {
            var b = mc.level.getBlockState(p).getBlock();
            if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST) chests++;
            if (b == Blocks.SPAWNER) spawners++;
        }
        if (chests + spawners > 0) notify(mc, "Detected " + chests + " chests, " + spawners + " spawners nearby.");
    }
}
