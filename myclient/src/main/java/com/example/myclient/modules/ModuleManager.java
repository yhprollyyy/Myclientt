package com.example.myclient.modules;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Existing implemented modules.
        modules.add(new SprintModule());
        modules.add(new FullbrightModule());
        modules.add(new AutoTotemModule());
        modules.add(new AutoEatModule());
        modules.add(new AutoToolModule());
        modules.add(new AutoLogModule());
        modules.add(new AutoMineModule());
        modules.add(new AutoFireworkModule());
        modules.add(new CordSnapperModule());
        modules.add(new BaseFinderModule());
        modules.add(new SusChunkModule());
        modules.add(new FreecamModule());

        // Krypton module registry (Client category intentionally excluded).
        add("Aim Assist", "Combat");
        add("Anchor Macro", "Combat");
        add("Auto Crystal", "Combat");
        add("Auto Double Hand", "Combat");
        add("Auto Hit Crystal", "Combat");
        add("Auto Inv Totem", "Combat");
        add("Auto Jump Reset", "Combat");
        add("Crystal Optimizer", "Combat");
        add("Double Anchor", "Combat");
        add("Elytra Swap", "Combat");
        add("HitBox", "Combat");
        add("Hover Totem", "Combat");
        add("Mace Bomber", "Combat");
        add("Mace Swap", "Combat");
        add("No Hit Delay", "Combat");
        add("Shield Breaker", "Combat");
        add("Spear Swap", "Combat");
        add("Static HitBoxes", "Combat");
        add("Totem Offhand", "Combat");
        add("Trigger Bot", "Combat");

        add("Auto Clicker", "Misc");
        add("Auto Reconnect", "Misc");
        add("Auto Loot", "Misc");
        add("Auto TPA", "Misc");
        add("Auto Walk", "Misc");
        add("Elytra Glide", "Misc");
        add("Fakeplayer", "Misc");
        add("Fast Place", "Misc");
        add("Key Pearl", "Misc");
        add("Key Wind Charge", "Misc");
        add("Name Protect", "Misc");
        add("Skin Protect", "Misc");
        add("Weather Notifier", "Misc");

        add("Anti Trap", "Donut");
        add("Auto Sell", "Donut");
        add("Auto Spawner Sell", "Donut");
        add("Chunk Finder", "Donut");
        add("Fake Pay", "Donut");
        add("Fake Stats", "Donut");
        add("Item Dropper", "Donut");
        add("Netherite Finder", "Donut");
        add("Player Chunks", "Donut");
        add("Spawner Protect", "Donut");

        add("Block Entity Debug", "BaseFinding");
        add("Hole ESP", "BaseFinding");
        add("Light Finder", "BaseFinding");
        add("Prime Chunk Finder", "BaseFinding");
        add("RTP Base Finder", "BaseFinding");
        add("Sus Chunk Finder", "BaseFinding");
        add("Suspicious ESP", "BaseFinding");
        add("Tunnel Base Finder", "BaseFinding");
        add("Seed Chunk Finder", "BaseFinding");

        add("Block ESP", "Render");
        add("Block Notifier", "Render");
        add("Free Look", "Render");
        add("HUD", "Render");
        add("Jump Circles", "Render");
        add("Mob ESP", "Render");
        add("Name Tags", "Render");
        add("Ore Sim", "Render");
        add("Pearl Trajectory", "Render");
        add("Player ESP", "Render");
        add("RealHitBox", "Render");
        add("Music HUD", "Render");
        add("Storage ESP", "Render");
        add("SwingSpeed", "Render");
        add("Target HUD", "Render");
    }

    private void add(String name, String category) {
        if (byName(name).isEmpty()) modules.add(new FeatureModule(name, category));
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
