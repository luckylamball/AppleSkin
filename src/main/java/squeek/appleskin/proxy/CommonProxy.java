package squeek.appleskin.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import squeek.appleskin.AppleSkin;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        AppleSkin.Log.info("apple skin is working");
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
