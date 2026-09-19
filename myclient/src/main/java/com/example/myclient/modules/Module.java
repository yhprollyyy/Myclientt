package com.example.myclient.modules;

import net.minecraft.client.Minecraft;
import com.example.myclient.settings.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();
    protected Module(String name) { this.name=name; }
    public String getName(){return name;}
    public boolean isEnabled(){return enabled;}
    public void toggle(){setEnabled(!enabled);}
    public void setEnabled(boolean value){if(value==enabled)return; enabled=value; if(value)onEnable();else onDisable();}
    protected <T extends Setting<?>> T addSetting(T setting){settings.add(setting); return setting;}
    public List<Setting<?>> getSettings(){return settings;}
    protected void onEnable(){}
    protected void onDisable(){}
    public void onTick(Minecraft mc){}
}
