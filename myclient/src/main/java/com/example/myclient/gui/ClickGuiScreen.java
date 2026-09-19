package com.example.myclient.gui;

import com.example.myclient.MyClient;
import com.example.myclient.modules.Module;
import com.example.myclient.settings.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.Click;
import net.minecraft.network.chat.Component;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private int scroll;
    public ClickGuiScreen(){super(Component.literal("Myclient ClickGUI"));}

    @Override protected void init(){scroll=0;}

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
        int left=30, top=30-scroll;
        g.fill(0,0,width,height,0xCC101014);
        g.drawString(font,"MYCLIENT",left,12,0xFFFFFF);
        List<Module> mods=MyClient.modules.all();
        int y=top;
        for(Module m:mods){
            int h=24+(m.isEnabled()?m.getSettings().size()*20:0);
            g.fill(left,y,left+260,y+h-2,0xFF202028);
            g.drawString(font,m.getName()+(m.isEnabled()?"  ON":"  OFF"),left+8,y+7,m.isEnabled()?0x55FF88:0xBBBBBB);
            if(m.isEnabled()){
                int sy=y+25;
                for(Setting<?> s:m.getSettings()){
                    g.drawString(font,s.getName()+": "+s.get().toString(),left+14,sy+3,0xDDDDDD);
                    sy+=20;
                }
            }
            y+=h+5;
        }
        super.render(g,mouseX,mouseY,delta);
    }

    @Override public boolean mouseClicked(Click click, boolean doubled){
        double x=click.x(), y=click.y();
        if(click.button()!=0)return super.mouseClicked(click,doubled);
        int left=30, yy=30-scroll;
        for(Module m:MyClient.modules.all()){
            int h=24+(m.isEnabled()?m.getSettings().size()*20:0);
            if(x>=left&&x<=left+260&&y>=yy&&y<yy+24){m.toggle(); return true;}
            if(m.isEnabled()){
                int sy=yy+25;
                for(Setting<?> s:m.getSettings()){
                    if(x>=left&&x<=left+260&&y>=sy&&y<sy+20){
                        if(s instanceof BooleanSetting b)b.toggle();
                        else if(s instanceof IntSetting i){if(x<left+130)i.decrement();else i.increment();}
                        else if(s instanceof DoubleSetting d){if(x<left+130)d.decrement();else d.increment();}
                        return true;
                    }
                    sy+=20;
                }
            }
            yy+=h+5;
        }
        return super.mouseClicked(click,doubled);
    }

    @Override public boolean mouseScrolled(double x,double y,double h,double v){
        scroll=Math.max(0,scroll+(int)(-v*20));
        return true;
    }

    @Override public boolean isPauseScreen(){return false;}
}
