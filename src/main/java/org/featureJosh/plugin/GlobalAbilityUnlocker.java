package org.featureJosh.plugin;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.HytaleLogger.Api;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

public class GlobalAbilityUnlocker {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final String DUMMY_ID = "hytale:dummy_packet_trigger";

   public static void inject() {
      if (Item.getAssetMap() != null && !Item.getAssetMap().getAssetMap().isEmpty()) {
         runInjection();
      } else {
         ((Api)LOGGER.atInfo()).log("Waiting for Items to load");
         HytaleServer.get().getEventBus().register(LoadedAssetsEvent.class, Item.class, GlobalAbilityUnlocker::onItemsLoaded);
      }

   }

   private static void onItemsLoaded(LoadedAssetsEvent<String, Item, ?> event) {
      runInjection();
   }

   private static void runInjection() {
      ((Api)LOGGER.atInfo()).log("Unlocking Abilities 2");
      int count = 0;

      try {
         Field interactionsField = Item.class.getDeclaredField("interactions");
         interactionsField.setAccessible(true);
         Field cachedPacketField = Item.class.getDeclaredField("cachedPacket");
         cachedPacketField.setAccessible(true);
         if (Item.getAssetMap() != null) {
            Iterator var3 = Item.getAssetMap().getAssetMap().values().iterator();

            while(var3.hasNext()) {
               Item item = (Item)var3.next();

               try {
                  Map<InteractionType, String> current = item.getInteractions();
                  Map<InteractionType, String> newMap = new EnumMap(InteractionType.class);
                  newMap.putAll(current);
                  boolean modified = false;
                  if (!newMap.containsKey(InteractionType.Ability2)) {
                     newMap.put(InteractionType.Ability2, "hytale:dummy_packet_trigger");
                     modified = true;
                  }

                  if (modified) {
                     interactionsField.set(item, Collections.unmodifiableMap(newMap));
                     cachedPacketField.set(item, (Object)null);
                     ++count;
                  }
               } catch (Exception var8) {
                  ((Api)LOGGER.atWarning()).log("Exception: " + String.valueOf(var8));
               }
            }
         }
      } catch (Exception var9) {
         ((Api)LOGGER.atWarning()).log("Injection Failed:" + String.valueOf(var9));
      }

      ((Api)LOGGER.atInfo()).log("Unlocked abilities on " + count + " items.");
   }
}
