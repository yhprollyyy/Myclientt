package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class BaseFinderModule extends Module {
    private final Set<BlockPos> reported = new HashSet<>();
    private int tick;

    public BaseFinderModule() { super("BaseFinder"); }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.level == null || mc.player == null || ++tick % 10 != 0) return;
        int r = 32;
        BlockPos origin = mc.player.blockPosition();

        for (Villager villager : mc.level.getEntitiesOfClass(Villager.class,
                mc.player.getBoundingBox().inflate(r))) {
            BlockPos pos = villager.blockPosition();
            if (reported.add(pos)) {
                mc.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "[BaseFinder] Villager at " + pos.getX() + " " + pos.getY() + " " + pos.getZ()), false);
            }
        }

        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-r, -r, -r), origin.offset(r, r, r))) {
            var block = mc.level.getBlockState(pos).getBlock();
            if ((block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.SPAWNER)
                    && reported.add(pos.immutable())) {
                mc.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "[BaseFinder] " + block.getName().getString() + " at "
                                        + pos.getX() + " " + pos.getY() + " " + pos.getZ()), false);
            }
        }
    }

    @Override
    protected void onDisable() { reported.clear(); }
}
