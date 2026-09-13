package net.fabricmc.loader;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class EntrypointLoader {

    public static void loadMain(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (instance instanceof ModInitializer) {
                ((ModInitializer) instance).onInitialize();
            }

            System.out.println("[FABRIC] Loaded main entrypoint: " + className);
        } catch (Exception e) {
            System.err.println("[FABRIC] Failed to load main entrypoint: " + className);
            e.printStackTrace();
        }
    }

    public static void loadClient(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (instance instanceof ClientModInitializer) {
                ((ClientModInitializer) instance).onInitializeClient();
            }

            System.out.println("[FABRIC] Loaded client entrypoint: " + className);
        } catch (Exception e) {
            System.err.println("[FABRIC] Failed to load client entrypoint: " + className);
            e.printStackTrace();
        }
    }
}
