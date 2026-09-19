package com.example.myclient.gui;

import com.example.myclient.MyClient;
import com.example.myclient.modules.FeatureModule;
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
    private String hovered = "nothing";

    public ClickGuiScreen(){super(Component.literal("Myclient ClickGUI"));}

    private Map<String,List<Module>> grouped(){
        Map<String,List<Module>> groups=new LinkedHashMap<>();
        for(Module m:MyClient.modules.all()){
            String c = m instanceof FeatureModule
                    ? ((FeatureModule)m).getCategory()
                    : legacyCategory(m.getName());
            groups.computeIfAbsent(c,k->new ArrayList<>()).add(m);
        }
        return groups;
    }

    private String legacyCategory(String n){
        if(n.equals("Sprint")||n.equals("Freecam")) return "Movement";
        if(n.startsWith("Auto")||n.equals("AutoTotem")||n.equals("AutoEat")||n.equals("AutoTool")) return "Combat";
        if(n.equals("BaseFinder")||n.equals("SusChunk")||n.equals("CordSnapper")) return "BaseFinding";
        return "Render";
    }

    @Override protected void init(){scroll=0;}

    private boolean inside(double mx,double my,int x,int y,int w,int h){
        return mx>=x&&mx<x+w&&my>=y&&my<y+h;
    }

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
        super.render(g,mouseX,mouseY,delta);

        g.fill(0,0,width,height,0xF0101014);
        g.fill(18,8,width-18,31,0xFF252530);
        g.drawString(font,Component.literal("MYCLIENT  •  CLICK GUI"),30,16,0xFFFFFF);
        g.drawString(font,Component.literal("Left click = toggle   •   Scroll = move"),width-235,16,0xAAAAAA);

        int left=24;
        int y=42-scroll;
        hovered="nothing";

        for(var group:grouped().entrySet()){
            int headerY=y;
            if(headerY+18>=35 && headerY<height-30){
                g.fill(left,headerY,left+360,headerY+20,0xFF343441);
                g.drawString(font,Component.literal(group.getKey()),left+8,headerY+6,0xFFFFFF);
            }
            y+=24;

            for(Module m:group.getValue()){
                int h=24+(m.isEnabled()?m.getSettings().size()*20:0);
                boolean visible=y+h>35&&y<height-30;
                boolean over=inside(mouseX,mouseY,left,y,360,h);

                if(over) hovered=m.getName();

                if(visible){
                    int bg=m.isEnabled()?0xFF263D30:0xFF202028;
                    if(over) bg=0xFF3B3B4A;
                    g.fill(left,y,left+360,y+h-2,bg);

                    // Bright outline/left marker makes the exact clickable row obvious.
                    g.fill(left,y,left+4,y+h-2,m.isEnabled()?0xFF55FF88:0xFF7777FF);
                    if(over){
                        g.fill(left+4,y,left+360,y+2,0xFFFFFFFF);
                        g.fill(left+4,y+h-4,left+360,y+h-2,0xFFFFFFFF);
                    }

                    String state=m.isEnabled()?"[ON]":"[OFF]";
                    g.drawString(font,Component.literal(m.getName()),left+14,y+6,0xFFFFFF);
                    g.drawString(font,Component.literal(state),left+302,y+6,m.isEnabled()?0x55FF88:0xAAAAAA);

                    if(m.isEnabled()){
                        int sy=y+25;
                        for(Setting<?> s:m.getSettings()){
                            boolean settingOver=inside(mouseX,mouseY,left+10,sy-2,340,20);
                            if(settingOver){
                                g.fill(left+8,sy-3,left+352,sy+17,0xFF454554);
                            }
                            g.drawString(font,Component.literal(s.getName()+": "+s.get().toString()),left+18,sy+3,0xDDDDDD);
                            sy+=20;
                        }
                    }
                }
                y+=h+5;
            }
            y+=12;
        }

        g.fill(18,height-28,width-18,height-8,0xFF252530);
        g.drawString(font,Component.literal("Hovering: "+hovered),28,height-21,0xFFFFFF);
        g.drawString(font,Component.literal("Click the highlighted row to toggle it"),width-255,height-21,0xAAAAAA);
    }

    @Override public boolean mouseClicked(MouseButtonEvent click,boolean doubled){
        if(click.button()!=0)return super.mouseClicked(click,doubled);
        double x=click.x(), mouseY=click.y();
        int left=24, y=42-scroll;

        for(var group:grouped().entrySet()){
            y+=24;
            for(Module m:group.getValue()){
                int h=24+(m.isEnabled()?m.getSettings().size()*20:0);

                if(inside(x,mouseY,left,y,360,24)){
                    m.toggle();
                    return true;
                }

                if(m.isEnabled()){
                    int sy=y+25;
                    for(Setting<?> s:m.getSettings()){
                        if(inside(x,mouseY,left+8,sy-3,344,20)){
                            if(s instanceof BooleanSetting b)b.toggle();
                            else if(s instanceof IntSetting i){if(x<left+180)i.decrement();else i.increment();}
                            else if(s instanceof DoubleSetting d){if(x<left+180)d.decrement();else d.increment();}
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
        scroll=Math.max(0,scroll+(int)(-v*24));
        return true;
    }

    @Override public boolean isPauseScreen(){return false;}
}
