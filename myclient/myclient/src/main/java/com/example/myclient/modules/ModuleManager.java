package com.example.myclient.modules;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new SprintModule());
        modules.add(new FullbrightModule());
        modules.add(new AutoTotemModule());
        modules.add(new AutoEatModule());
        modules.add(new AutoToolModule());
        modules.add(new AutoLogModule());
        modules.add(new AutoMineModule());
        modules.add(new AutoFireworkModule());
        modules.add(new CordSnapperModule());
    }

    public List<Module> all() { return modules; }

    public Optional<Module> byName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void tick(Minecraft mc) {
        if (mc.player == null) return;
        for (Module m : modules) if (m.isEnabled()) m.onTick(mc);
    }
}
