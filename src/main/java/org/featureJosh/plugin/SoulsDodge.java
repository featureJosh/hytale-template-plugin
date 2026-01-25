package org.featureJosh.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.HytaleLogger.Api;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.io.File;
import javax.annotation.Nonnull;

public class SoulsDodge extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final File CONFIG_DIR = new File("mods/Souls_Dodge");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ComponentType<EntityStore, DodgeComponent> dodgeComponentType;
    private PacketFilter inboundFilter;

    public SoulsDodge(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static ComponentType<EntityStore, DodgeComponent> getDodgeComponentType() {
        return dodgeComponentType;
    }

    protected void setup() {
        ((Api) LOGGER.atInfo()).log("SoulsDodge: Setting Up");
        initConfig();
        initComponents();
        initSystems();
        initPacketHandler();
        getCommandRegistry().registerCommand(new DodgeModCommand());
        ((Api) LOGGER.atInfo()).log("SoulsDodge: Setup Successful");
    }

    protected void shutdown() {
        if (inboundFilter != null) {
            PacketAdapters.deregisterInbound(inboundFilter);
        }
    }

    private void initConfig() {
        if (!CONFIG_DIR.exists() && !CONFIG_DIR.mkdirs()) {
            ((Api) LOGGER.atWarning()).log("SoulsDodge: Failed to create config directory");
        }
        SoulsDodgeSettings.load(CONFIG_DIR, GSON);
    }

    private void initComponents() {
        ComponentRegistryProxy<EntityStore> registry = getEntityStoreRegistry();
        dodgeComponentType = registry.registerComponent(DodgeComponent.class, DodgeComponent::new);
    }

    private void initSystems() {
        ComponentRegistryProxy<EntityStore> registry = getEntityStoreRegistry();
        registry.registerSystem(new PlayerJoinDodgeAdder(dodgeComponentType), true);
        registry.registerSystem(new DodgeInvincibilitySystem(dodgeComponentType), true);
        registry.registerSystem(new DodgeSystem(dodgeComponentType), true);
    }

    private void initPacketHandler() {
        GlobalAbilityUnlocker.inject();
        DodgePacketHandler handler = new DodgePacketHandler();
        inboundFilter = PacketAdapters.registerInbound(handler);
        ((Api) LOGGER.atInfo()).log("SoulsDodge: Packet Filter Registered");
    }
}
