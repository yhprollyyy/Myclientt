package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import com.example.myclient.settings.DoubleSetting;

public class FreecamModule extends Module {
    private boolean oldNoGravity;
    private final DoubleSetting speed=addSetting(new DoubleSetting("Speed",1.0,0.1,5.0,0.1));
    private final DoubleSetting verticalSpeed=addSetting(new DoubleSetting("VerticalSpeed",1.0,0.1,5.0,0.1));

    public FreecamModule(){super("Freecam");}

    @Override protected void onEnable(){
        Minecraft mc=Minecraft.getInstance();
        if(mc.player!=null){oldNoGravity=mc.player.isNoGravity();mc.player.setNoGravity(true);}
    }
    @Override protected void onDisable(){
        Minecraft mc=Minecraft.getInstance();
        if(mc.player!=null)mc.player.setNoGravity(oldNoGravity);
    }
    @Override public void onTick(Minecraft mc){
        if(mc.player!=null)mc.player.setNoGravity(true);
    }
}
