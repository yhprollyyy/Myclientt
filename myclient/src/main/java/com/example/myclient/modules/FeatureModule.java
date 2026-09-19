package com.example.myclient.modules;

import com.example.myclient.settings.BooleanSetting;

public class FeatureModule extends Module {
    private final String category;
    private final BooleanSetting implemented = addSetting(new BooleanSetting("Implemented", false));

    public FeatureModule(String name, String category) {
        super(name);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
