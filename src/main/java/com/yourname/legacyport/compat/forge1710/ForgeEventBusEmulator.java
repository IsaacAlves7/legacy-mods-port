package com.yourname.legacyport.compat.forge1710;

public class ForgeEventBusEmulator {
    public void post(Object event) {
        System.out.println("Event posted: " + event);
    }
}