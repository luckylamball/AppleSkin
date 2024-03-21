package squeek.appleskin;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import squeek.appleskin.common.ConfigurationItem;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ModConfig {

    public static Configuration config;

    public static final String CATEGORY_CLIENT = "client";
    private static final String CATEGORY_CLIENT_COMMENT =
            "These config settings are client-side only";

    public static ConfigurationItem SHOW_FOOD_VALUES_IN_TOOLTIP =
            new ConfigurationItem(true,
                    "showFoodValuesInTooltip",
                    "If true, shows the hunger and saturation values of food in its tooltip while holding SHIFT");

    public static ConfigurationItem ALWAYS_SHOW_FOOD_VALUES_TOOLTIP =
            new ConfigurationItem(true,
                    "showFoodValuesInTooltipAlways",
                    "If true, shows the hunger and saturation values of food in its tooltip automatically (without needing to hold SHIFT)");

    public static ConfigurationItem SHOW_SATURATION_OVERLAY =
            new ConfigurationItem(true,
                    "showSaturationHudOverlay",
                    "If true, shows your current saturation level overlay on the hunger bar");

    public static ConfigurationItem SHOW_FOOD_VALUES_OVERLAY =
            new ConfigurationItem(true,
                    "showFoodValuesHudOverlay",
                    "If true, shows the hunger (and saturation if " + SHOW_SATURATION_OVERLAY.getName() + " is true) that would be restored by food you are currently holding");

    @Deprecated
    public static ConfigurationItem SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND =
            new ConfigurationItem(true,
                    "showFoodValuesHudOverlayWhenOffhand",
                    "If true, enables the hunger/saturation/health overlays for food in your off-hand");

    public static ConfigurationItem SHOW_FOOD_EXHAUSTION_UNDERLAY =
            new ConfigurationItem(true,
                    "showFoodExhaustionHudUnderlay",
                    "If true, shows your food exhaustion as a progress bar behind the hunger bars");

    public static ConfigurationItem SHOW_FOOD_DEBUG_INFO =
            new ConfigurationItem(true,
                    "showFoodStatsInDebugOverlay",
                    "If true, adds a line that shows your hunger, saturation, and exhaustion level in the F3 debug overlay");

    public static ConfigurationItem SHOW_FOOD_HEALTH_HUD_OVERLAY =
            new ConfigurationItem(true,
                    "showFoodHealthHudOverlay",
                    "If true, shows estimated health restored by food on the health bar");

    public static ConfigurationItem SHOW_VANILLA_ANIMATION_OVERLAY =
            new ConfigurationItem(true,
                    "showVanillaAnimationsOverlay",
                    "If true, health/hunger overlay will shake to match Minecraft's icon animations");

    public static ConfigurationItem MAX_HUD_OVERLAY_FLASH_ALPHA =
            new ConfigurationItem(0.65d,
                    0.0d,
                    1.0d,
                    "maxHudOverlayFlashAlpha",
                    "Alpha value of the flashing icons at their most visible point (1.0 = fully opaque, 0.0 = fully transparent)");

    public static List<ConfigurationItem> SYNC_CONFIG_LIST = Arrays.asList(
            SHOW_FOOD_VALUES_IN_TOOLTIP, ALWAYS_SHOW_FOOD_VALUES_TOOLTIP, SHOW_SATURATION_OVERLAY,
            SHOW_FOOD_VALUES_OVERLAY, SHOW_FOOD_EXHAUSTION_UNDERLAY, SHOW_FOOD_DEBUG_INFO);

    public static void init(File file) {
        config = new Configuration(file);
        config.load();
        sync();

        MinecraftForge.EVENT_BUS.register(new ModConfig());
    }


    public static void sync() {
        config.getCategory(CATEGORY_CLIENT);

        for (ConfigurationItem item : SYNC_CONFIG_LIST) {
            syncItemValue(item);
        }

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void syncItemValue(ConfigurationItem item) {
        item.setDefaultBoolean(config.get(CATEGORY_CLIENT, item.getName(), true, item.getComment())
                .getBoolean(true));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModInfo.MOD_ID))
            ModConfig.sync();
    }
}