package squeek.appleskin.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import squeek.appleskin.ModInfo;
import squeek.appleskin.helpers.HungerHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler {
    private static final int PROTOCOL_VERSION = 1;
    public static final SimpleNetworkWrapper CHANNEL =
            NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_ID);

    public static void init() {
        CHANNEL.registerMessage(MessageExhaustionSync.class, MessageExhaustionSync.class, 1, Side.CLIENT);
        CHANNEL.registerMessage(MessageSaturationSync.class, MessageSaturationSync.class, 2, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new SyncHandler());
    }

    /*
     * Sync saturation (vanilla MC only syncs when it hits 0)
     * Sync exhaustion (vanilla MC does not sync it at all)
     */
    private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
    private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();

    @SubscribeEvent
    public void onLivingTickEvent(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayerMP))
            return;

        EntityPlayerMP player = (EntityPlayerMP) event.entity;
        Float lastSaturationLevel = lastSaturationLevels.get(player.getUniqueID());
        Float lastExhaustionLevel = lastExhaustionLevels.get(player.getUniqueID());

        if (lastSaturationLevel == null || lastSaturationLevel != player.getFoodStats().getSaturationLevel()) {
            MessageSaturationSync msg = new MessageSaturationSync(player.getFoodStats().getSaturationLevel());
            CHANNEL.sendTo(msg, player);
            lastSaturationLevels.put(player.getUniqueID(), player.getFoodStats().getSaturationLevel());
        }

        float exhaustionLevel = HungerHelper.getExhaustion(player);
        if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
            MessageExhaustionSync msg = new MessageExhaustionSync(exhaustionLevel);
            CHANNEL.sendTo(msg, player);
            lastExhaustionLevels.put(player.getUniqueID(), exhaustionLevel);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.LoadFromFile event) {
        if (!(event.entity instanceof EntityPlayerMP))
            return;

        lastSaturationLevels.remove(event.entity.getUniqueID());
        lastExhaustionLevels.remove(event.entity.getUniqueID());
    }
}
