package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.Blocks;
import com.example.myclient.settings.IntSetting;
import com.example.myclient.settings.BooleanSetting;

public class SusChunkModule extends Module {
    private long lastChunk=Long.MIN_VALUE;
    private final IntSetting range=addSetting(new IntSetting("Range",16,8,32));
    private final IntSetting villagerWeight=addSetting(new IntSetting("VillagerWeight",1,0,5));
    private final IntSetting chestWeight=addSetting(new IntSetting("ChestWeight",2,0,5));
    private final IntSetting spawnerWeight=addSetting(new IntSetting("SpawnerWeight",3,0,5));
    private final IntSetting threshold=addSetting(new IntSetting("AlertThreshold",1,1,20));
    private final BooleanSetting showSafe=addSetting(new BooleanSetting("ShowSafe",false));

    public SusChunkModule(){super("SusChunk");}

    @Override public void onTick(Minecraft mc){
        if(mc.level==null||mc.player==null)return;
        int cx=mc.player.chunkPosition().x,cz=mc.player.chunkPosition().z;
        long key=(((long)cx)<<32)^(cz&0xffffffffL);
        if(key==lastChunk)return; lastChunk=key;
        int r=range.get(),villagers=mc.level.getEntitiesOfClass(Villager.class,mc.player.getBoundingBox().inflate(r)).size();
        int chests=0,spawners=0; BlockPos base=mc.player.blockPosition();
        for(BlockPos pos:BlockPos.betweenClosed(base.offset(-r,-r,-r),base.offset(r,r,r))){
            var b=mc.level.getBlockState(pos).getBlock();
            if(b==Blocks.CHEST||b==Blocks.TRAPPED_CHEST)chests++;
            if(b==Blocks.SPAWNER)spawners++;
        }
        int score=villagers*villagerWeight.get()+chests*chestWeight.get()+spawners*spawnerWeight.get();
        if(score>=threshold.get()||showSafe.get())
            mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("[SusChunk] Chunk "+cx+", "+cz+" | villagers="+villagers+" chests="+chests+" spawners="+spawners+" score="+score),false);
    }
    @Override protected void onDisable(){lastChunk=Long.MIN_VALUE;}
}
