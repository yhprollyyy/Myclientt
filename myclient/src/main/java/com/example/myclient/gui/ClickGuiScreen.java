package com.example.myclient.gui;

import com.example.myclient.MyClient;
import com.example.myclient.modules.Module;
import com.example.myclient.settings.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private int scroll;

    public ClickGuiScreen(){super(Component.literal("Myclient ClickGUI"));}

    private Map<String,List<Module>> grouped(){
        Map<String,List<Module>> groups=new LinkedHashMap<>();
        for(Module m:MyClient.modules.all()){
            String n=m.getName();
            String c;
            if(n.equals("Sprint")||n.equals("Freecam"))c="Movement";
            else if(n.startsWith("Auto")||n.equals("AutoTotem")||n.equals("AutoEat")||n.equals("AutoTool"))c="Combat";
            else if(n.equals("BaseFinder")||n.equals("SusChunk")||n.equals("CordSnapper"))c="World";
            else c="Misc";
            groups.computeIfAbsent(c,k->new ArrayList<>()).add(m);
        }
        return groups;
    }

    @Override protected void init(){scroll=0;}

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
        int left=30, y=30-scroll;
        g.fill(0,0,width,height,0xCC101014);
        g.drawString(font,"MYCLIENT",left,12,0xFFFFFF);

        for(var group:grouped().entrySet()){
            g.drawString(font,"§l"+group.getKey(),left,y,0xFFFFFF);
            y+=20;
            for(Module m:group.getValue()){
                int h=24+(m.isEnabled()?m.getSettings().size()*20:0);
                g.fill(left,y,left+300,y+h-2,0xFF202028);
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
            y+=12;
        }
        super.render(g,mouseX,mouseY,delta);
    }

    @Override public boolean mouseClicked(MouseButtonEvent click,boolean doubled){
        if(click.button()!=0)return super.mouseClicked(click,doubled);
        double x=click.x(), mouseY=click.y();
        int left=30, y=30-scroll;

        for(var group:grouped().entrySet()){
            y+=20;
            for(Module m:group.getValue()){
                int h=24+(m.isEnabled()?m.getSettings().size()*20:0);
                if(x>=left&&x<=left+300&&mouseY>=y&&mouseY<y+24){m.toggle();return true;}
                if(m.isEnabled()){
                    int sy=y+25;
                    for(Setting<?> s:m.getSettings()){
                        if(x>=left&&x<=left+300&&mouseY>=sy&&mouseY<sy+20){
                            if(s instanceof BooleanSetting b)b.toggle();
                            else if(s instanceof IntSetting i){if(x<left+150)i.decrement();else i.increment();}
                            else if(s instanceof DoubleSetting d){if(x<left+150)d.decrement();else d.increment();}
                            return true;
                        }
                        sy+=20;
                    }
                }
                y+=h+5;
            }
            y+=12;
        }
        return super.mouseClicked(click,doubled);
    }

    @Override public boolean mouseScrolled(double x,double y,double h,double v){
        scroll=Math.max(0,scroll+(int)(-v*20));
        return true;
    }

    @Override public boolean isPauseScreen(){return false;}
}
