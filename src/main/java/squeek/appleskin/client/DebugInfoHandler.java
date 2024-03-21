package squeek.appleskin.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FoodStats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import squeek.appleskin.ModConfig;
import squeek.appleskin.helpers.HungerHelper;

import java.text.DecimalFormat;

@SideOnly(Side.CLIENT)
public class DebugInfoHandler {
    private static final DecimalFormat saturationDF = new DecimalFormat("#.##");
    private static final DecimalFormat exhaustionValDF = new DecimalFormat("0.00");
    private static final DecimalFormat exhaustionMaxDF = new DecimalFormat("#.##");

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new DebugInfoHandler());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTextRender(RenderGameOverlayEvent.Text textEvent) {
        if (!ModConfig.SHOW_FOOD_DEBUG_INFO.getDefaultBoolean())
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (!mc.gameSettings.showDebugInfo)
            return;

        FoodStats stats = mc.thePlayer.getFoodStats();
        float curExhaustion = HungerHelper.getExhaustion(mc.thePlayer);
        float maxExhaustion = HungerHelper.getMaxExhaustion(mc.thePlayer);
        textEvent.left.add("hunger: " + stats.getFoodLevel() + ", sat: " + saturationDF.format(stats.getSaturationLevel()) + ", exh: " + exhaustionValDF.format(curExhaustion) + "/" + exhaustionMaxDF.format(maxExhaustion));
    }
}
