package com.example.myclient.modules;

public class FeatureModule extends Module {
    private final String category;

    public FeatureModule(String name, String category) {
        super(name);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
