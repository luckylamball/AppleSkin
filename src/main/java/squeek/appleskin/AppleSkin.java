package squeek.appleskin;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.appleskin.client.DebugInfoHandler;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.client.TooltipOverlayHandler;
import squeek.appleskin.network.SyncHandler;
import squeek.appleskin.proxy.CommonProxy;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, acceptedMinecraftVersions = "[1.7.10]")
public class AppleSkin {
    public static Logger Log = LogManager.getLogger(ModInfo.MOD_ID);

    @SidedProxy(clientSide = "squeek.appleskin.proxy.ClientProxy", serverSide = "squeek.appleskin.proxy.CommonProxy")
    public static CommonProxy proxy;

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.init(event.getSuggestedConfigurationFile());
        proxy.preInit(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        SyncHandler.init();
        if (Side.CLIENT == FMLCommonHandler.instance().getSide()) {
            DebugInfoHandler.init();
            HUDOverlayHandler.init();
            TooltipOverlayHandler.init();
        }
        proxy.init(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
