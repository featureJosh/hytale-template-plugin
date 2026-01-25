package org.featureJosh.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.HytaleLogger.Api;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public final class SoulsDodgeSettings {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static SoulsDodgeSettings instance;

    public long cooldown;
    public long invincibility;
    public float speed;
    public float stamina;
    public boolean airDodge;

    public static void load(File configDir, Gson gson) {
        File diskFile = new File(configDir, "settings.json");
        SoulsDodgeSettings defaults = loadDefaults(gson);
        SoulsDodgeSettings config;

        if (!diskFile.exists()) {
            config = defaults;
            save(diskFile, defaults, gson);
            ((Api) LOGGER.atInfo()).log("SoulsDodgeSettings: Created new settings.json");
        } else {
            config = loadAndMerge(diskFile, defaults, gson);
        }

        set(config);
        ((Api) LOGGER.atInfo()).log("SoulsDodgeSettings: Loaded successfully");
    }

    private static SoulsDodgeSettings loadDefaults(Gson gson) {
        try {
            InputStream is = SoulsDodgeSettings.class.getClassLoader().getResourceAsStream("dodge_defaults.json");
            if (is != null) {
                SoulsDodgeSettings config = gson.fromJson(new InputStreamReader(is), SoulsDodgeSettings.class);
                is.close();
                return config;
            }
        } catch (IOException e) {
            ((Api) LOGGER.atWarning()).log("SoulsDodgeSettings: Failed to load defaults from JAR");
        }

        SoulsDodgeSettings defaults = new SoulsDodgeSettings();
        defaults.cooldown = 500L;
        defaults.invincibility = 200L;
        defaults.speed = 20.0F;
        defaults.stamina = 1.5F;
        defaults.airDodge = false;
        return defaults;
    }

    private static SoulsDodgeSettings loadAndMerge(File diskFile, SoulsDodgeSettings defaults, Gson gson) {
        try {
            FileReader reader = new FileReader(diskFile);
            JsonObject userJson = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject defaultsJson = gson.toJsonTree(defaults).getAsJsonObject();
            boolean updated = false;

            Iterator<String> keys = defaultsJson.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                if (!userJson.has(key)) {
                    userJson.add(key, defaultsJson.get(key));
                    ((Api) LOGGER.atInfo()).log("SoulsDodgeSettings: Added missing field: " + key);
                    updated = true;
                }
            }

            SoulsDodgeSettings config = gson.fromJson(userJson, SoulsDodgeSettings.class);
            reader.close();

            if (updated) {
                save(diskFile, config, gson);
            }

            return config;
        } catch (IOException e) {
            ((Api) LOGGER.atWarning()).log("SoulsDodgeSettings: Failed to read config; using defaults");
            return defaults;
        }
    }

    private static void save(File file, SoulsDodgeSettings config, Gson gson) {
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            ((Api) LOGGER.atWarning()).log("SoulsDodgeSettings: Failed to save config");
        }
    }

    public static void set(SoulsDodgeSettings config) {
        instance = config;
    }

    public static SoulsDodgeSettings get() {
        return instance;
    }
}
