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

public final class DodgeConfig {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static DodgeConfig instance;

    public long dodgeCooldownMs;
    public long iFrameDurationMs;
    public float dodgeVelocity;
    public float verticalHop;
    public float staminaCost;
    public boolean allowAirDash;

    public static void load(File configDir, Gson gson) {
        File diskFile = new File(configDir, "dodge_config.json");
        DodgeConfig defaults = loadDefaults(gson);
        DodgeConfig config;

        if (!diskFile.exists()) {
            config = defaults;
            save(diskFile, defaults, gson);
            ((Api) LOGGER.atInfo()).log("DodgeConfig: Created new dodge_config.json");
        } else {
            config = loadAndMerge(diskFile, defaults, gson);
        }

        set(config);
        ((Api) LOGGER.atInfo()).log("DodgeConfig: Loaded successfully");
    }

    private static DodgeConfig loadDefaults(Gson gson) {
        try {
            InputStream is = DodgeConfig.class.getClassLoader().getResourceAsStream("dodge_defaults.json");
            if (is != null) {
                DodgeConfig config = gson.fromJson(new InputStreamReader(is), DodgeConfig.class);
                is.close();
                return config;
            }
        } catch (IOException e) {
            ((Api) LOGGER.atWarning()).log("DodgeConfig: Failed to load defaults from JAR");
        }

        DodgeConfig defaults = new DodgeConfig();
        defaults.dodgeCooldownMs = 500L;
        defaults.iFrameDurationMs = 200L;
        defaults.dodgeVelocity = 20.0F;
        defaults.verticalHop = 0.1F;
        defaults.staminaCost = 1.5F;
        defaults.allowAirDash = false;
        return defaults;
    }

    private static DodgeConfig loadAndMerge(File diskFile, DodgeConfig defaults, Gson gson) {
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
                    ((Api) LOGGER.atInfo()).log("DodgeConfig: Added missing field: " + key);
                    updated = true;
                }
            }

            DodgeConfig config = gson.fromJson(userJson, DodgeConfig.class);
            reader.close();

            if (updated) {
                save(diskFile, config, gson);
            }

            return config;
        } catch (IOException e) {
            ((Api) LOGGER.atWarning()).log("DodgeConfig: Failed to read config; using defaults");
            return defaults;
        }
    }

    private static void save(File file, DodgeConfig config, Gson gson) {
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            ((Api) LOGGER.atWarning()).log("DodgeConfig: Failed to save config");
        }
    }

    public static void set(DodgeConfig config) {
        instance = config;
    }

    public static DodgeConfig get() {
        return instance;
    }
}
