package com.example.myclient.modules;

import com.example.myclient.settings.DoubleSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.phys.Vec3;

public class FreecamModule extends Module {
    private Marker camera;
    private Vec3 playerPos;
    private float playerYaw;
    private float playerPitch;

    private final DoubleSetting speed=addSetting(new DoubleSetting("Speed",1.0,0.1,5.0,0.1));
    private final DoubleSetting verticalSpeed=addSetting(new DoubleSetting("VerticalSpeed",1.0,0.1,5.0,0.1));

    public FreecamModule(){super("Freecam");}

    @Override protected void onEnable(){
        Minecraft mc=Minecraft.getInstance();
        if(mc.player==null||mc.level==null)return;

        playerPos=mc.player.position();
        playerYaw=mc.player.getYRot();
        playerPitch=mc.player.getXRot();

        camera=new Marker(EntityType.MARKER,mc.level);
        Vec3 eye=mc.player.getEyePosition(1.0F);
        camera.refreshPositionAndAngles(eye,playerYaw,playerPitch);
        camera.setNoGravity(true);
        camera.setInvisible(true);
        mc.setCameraEntity(camera);
    }

    @Override protected void onDisable(){
        Minecraft mc=Minecraft.getInstance();
        if(mc.player!=null){
            mc.setCameraEntity(mc.player);
            if(playerPos!=null){
                mc.player.setPos(playerPos);
                mc.player.setDeltaMovement(Vec3.ZERO);
            }
        }
        if(camera!=null)camera.discard();
        camera=null;
        playerPos=null;
    }

    @Override public void onTick(Minecraft mc){
        if(camera==null||mc.player==null)return;

        mc.player.setPos(playerPos);
        mc.player.setDeltaMovement(Vec3.ZERO);

        double horizontal=speed.get()*(mc.options.keySprint.isDown()?2.0:1.0);
        double vertical=verticalSpeed.get();

        Vec3 forward=camera.getLookAngle();
        Vec3 flat=new Vec3(forward.x,0,forward.z);
        if(flat.lengthSqr()>1.0E-6)flat=flat.normalize();
        Vec3 right=new Vec3(-flat.z,0,flat.x);

        double x=0,y=0,z=0;
        if(mc.options.keyUp.isDown()){x+=flat.x*horizontal;z+=flat.z*horizontal;}
        if(mc.options.keyDown.isDown()){x-=flat.x*horizontal;z-=flat.z*horizontal;}
        if(mc.options.keyRight.isDown()){x+=right.x*horizontal;z+=right.z*horizontal;}
        if(mc.options.keyLeft.isDown()){x-=right.x*horizontal;z-=right.z*horizontal;}
        if(mc.options.keyJump.isDown())y+=vertical;
        if(mc.options.keyShift.isDown())y-=vertical;

        camera.setPos(camera.getX()+x,camera.getY()+y,camera.getZ()+z);
        camera.setDeltaMovement(Vec3.ZERO);
    }
}
