package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;

public class SusChunkModule extends Module {
    private long lastChunk = Long.MIN_VALUE;

    public SusChunkModule() { super("SusChunk"); }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;

        int cx = mc.player.chunkPosition().x;
        int cz = mc.player.chunkPosition().z;
        long key = (((long) cx) << 32) ^ (cz & 0xffffffffL);
        if (key == lastChunk) return;
        lastChunk = key;

        int villagers = mc.level.getEntitiesOfClass(Villager.class,
                mc.player.getBoundingBox().inflate(16)).size();
        int chests = 0, spawners = 0;
        BlockPos base = mc.player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                base.offset(-16, -16, -16), base.offset(16, 16, 16))) {
            var block = mc.level.getBlockState(pos).getBlock();
            if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) chests++;
            if (block == Blocks.SPAWNER) spawners++;
        }

        int score = villagers + chests * 2 + spawners * 3;
        if (score > 0) {
            mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "[SusChunk] Chunk " + cx + ", " + cz + " | villagers=" + villagers
                                    + " chests=" + chests + " spawners=" + spawners + " score=" + score), false);
        }
    }

    @Override
    protected void onDisable() { lastChunk = Long.MIN_VALUE; }
}
