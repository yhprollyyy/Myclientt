package com.example.myclient.modules;

import net.minecraft.client.Minecraft;

public abstract class Module {
    private final String name;
    private boolean enabled;

    protected Module(String name) { this.name = name; }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick(Minecraft mc) {}
}
