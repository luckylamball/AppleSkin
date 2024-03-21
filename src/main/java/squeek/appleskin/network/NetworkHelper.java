package squeek.appleskin.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class NetworkHelper {
    public static EntityPlayer getSidedPlayer(MessageContext ctx) {
        return Side.SERVER == ctx.side ? ctx.getServerHandler().playerEntity : FMLClientHandler.instance().getClientPlayerEntity();
    }
}