package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.Blocks;
import com.example.myclient.settings.BooleanSetting;
import com.example.myclient.settings.IntSetting;
import java.util.HashSet;
import java.util.Set;

public class BaseFinderModule extends Module {
    private final Set<BlockPos> reported = new HashSet<>();
    private int tick;
    private final IntSetting range = addSetting(new IntSetting("Range",32,8,64));
    private final BooleanSetting villagers = addSetting(new BooleanSetting("Villagers",true));
    private final BooleanSetting chests = addSetting(new BooleanSetting("Chests",true));
    private final BooleanSetting spawners = addSetting(new BooleanSetting("Spawners",true));

    public BaseFinderModule(){super("BaseFinder");}

    @Override public void onTick(Minecraft mc){
        if(mc.level==null||mc.player==null||++tick%10!=0)return;
        int r=range.get(), rr=r*r;
        BlockPos origin=mc.player.blockPosition();

        if(villagers.get()){
            for(Villager v:mc.level.getEntitiesOfClass(Villager.class,mc.player.getBoundingBox().inflate(r))){
                if(v.distanceToSqr(mc.player)>rr)continue;
                BlockPos pos=v.blockPosition();
                if(reported.add(pos)) msg(mc,"Villager at "+pos.getX()+" "+pos.getY()+" "+pos.getZ());
            }
        }
        for(BlockPos pos:BlockPos.betweenClosed(origin.offset(-r,-r,-r),origin.offset(r,r,r))){
            var block=mc.level.getBlockState(pos).getBlock();
            boolean match=(chests.get()&&(block==Blocks.CHEST||block==Blocks.TRAPPED_CHEST))||(spawners.get()&&block==Blocks.SPAWNER);
            if(match&&reported.add(pos.immutable()))
                msg(mc,block.getName().getString()+" at "+pos.getX()+" "+pos.getY()+" "+pos.getZ());
        }
    }
    private void msg(Minecraft mc,String s){mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("[BaseFinder] "+s),false);}
    @Override protected void onDisable(){reported.clear();tick=0;}
}
