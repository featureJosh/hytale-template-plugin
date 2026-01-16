package com.mod.nightmaremode;

public class NightmareMode {

    private static NightmareMode instance;
    
    public NightmareMode() {
        instance = this;
        System.out.println("[Nightmare Mode] Plugin loaded!");
    }
    
    public void onEnable() {
        System.out.println("[Nightmare Mode] Plugin enabled!");
    }
    
    public void onDisable() {
        System.out.println("[Nightmare Mode] Plugin disabled!");
    }
    
    public static NightmareMode getInstance() {
        return instance;
    }
}