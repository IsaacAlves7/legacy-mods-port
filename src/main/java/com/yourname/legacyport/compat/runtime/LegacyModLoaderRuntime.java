package com.yourname.legacyport.compat.runtime;

public class LegacyModLoaderRuntime {

    public void loadMods() {
        System.out.println("Loading legacy mods...");
    }

    public void injectClass(String name) {
        System.out.println("Injecting: " + name);
    }
}