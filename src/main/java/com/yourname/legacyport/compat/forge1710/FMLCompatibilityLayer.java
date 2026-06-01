package com.yourname.legacyport.compat.forge1710;

public class FMLCompatibilityLayer {
    public void simulatePreInit() {
        System.out.println("Simulating FML preInit");
    }

    public void simulateInit() {
        System.out.println("Simulating FML init");
    }

    public void simulatePostInit() {
        System.out.println("Simulating FML postInit");
    }
}