package squeek.appleskin;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import squeek.appleskin.common.BooleanConfiguration;
import squeek.appleskin.common.DoubleConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ModConfig {

    public static Configuration config;

    public static final String CATEGORY_CLIENT = "client";
    private static final String CATEGORY_CLIENT_COMMENT =
            "These config settings are client-side only";

    public static BooleanConfiguration SHOW_FOOD_VALUES_IN_TOOLTIP =
            new BooleanConfiguration(true,
                    "showFoodValuesInTooltip",
                    "If true, shows the hunger and saturation values of food in its tooltip while holding SHIFT");

    public static BooleanConfiguration ALWAYS_SHOW_FOOD_VALUES_TOOLTIP =
            new BooleanConfiguration(true,
                    "showFoodValuesInTooltipAlways",
                    "If true, shows the hunger and saturation values of food in its tooltip automatically (without needing to hold SHIFT)");

    public static BooleanConfiguration SHOW_SATURATION_OVERLAY =
            new BooleanConfiguration(true,
                    "showSaturationHudOverlay",
                    "If true, shows your current saturation level overlay on the hunger bar");

    public static BooleanConfiguration SHOW_FOOD_VALUES_OVERLAY =
            new BooleanConfiguration(true,
                    "showFoodValuesHudOverlay",
                    "If true, shows the hunger (and saturation if " + SHOW_SATURATION_OVERLAY.getName() + " is true) that would be restored by food you are currently holding");

    @Deprecated
    public static BooleanConfiguration SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND =
            new BooleanConfiguration(true,
                    "showFoodValuesHudOverlayWhenOffhand",
                    "If true, enables the hunger/saturation/health overlays for food in your off-hand");

    public static BooleanConfiguration SHOW_FOOD_EXHAUSTION_UNDERLAY =
            new BooleanConfiguration(true,
                    "showFoodExhaustionHudUnderlay",
                    "If true, shows your food exhaustion as a progress bar behind the hunger bars");

    public static BooleanConfiguration SHOW_FOOD_DEBUG_INFO =
            new BooleanConfiguration(true,
                    "showFoodStatsInDebugOverlay",
                    "If true, adds a line that shows your hunger, saturation, and exhaustion level in the F3 debug overlay");

    public static BooleanConfiguration SHOW_FOOD_HEALTH_HUD_OVERLAY =
            new BooleanConfiguration(true,
                    "showFoodHealthHudOverlay",
                    "If true, shows estimated health restored by food on the health bar");

    public static BooleanConfiguration SHOW_VANILLA_ANIMATION_OVERLAY =
            new BooleanConfiguration(true,
                    "showVanillaAnimationsOverlay",
                    "If true, health/hunger overlay will shake to match Minecraft's icon animations");

    public static DoubleConfiguration MAX_HUD_OVERLAY_FLASH_ALPHA =
            new DoubleConfiguration(0.65d,
                    0.0d,
                    1.0d,
                    "maxHudOverlayFlashAlpha",
                    "Alpha value of the flashing icons at their most visible point (1.0 = fully opaque, 0.0 = fully transparent)");

    public static List<BooleanConfiguration> SYNC_CONFIG_LIST = Arrays.asList(
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

        for (BooleanConfiguration item : SYNC_CONFIG_LIST) {
            syncItemValue(item);
        }

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void syncItemValue(BooleanConfiguration item) {
        item.set(config.get(CATEGORY_CLIENT, item.getName(), true, item.getComment())
                .getBoolean(true));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModInfo.MOD_ID))
            ModConfig.sync();
    }
}