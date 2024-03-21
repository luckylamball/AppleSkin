package squeek.appleskin.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import squeek.appleskin.helpers.HungerHelper;

public class MessageSaturationSync implements IMessage, IMessageHandler<MessageSaturationSync, IMessage> {
    private float saturationLevel;

    public MessageSaturationSync() {
    }

    public MessageSaturationSync(float saturationLevel) {
        this.saturationLevel = saturationLevel;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        saturationLevel = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(saturationLevel);
    }

    @Override
    public IMessage onMessage(MessageSaturationSync message, MessageContext ctx) {
        Minecraft.getMinecraft().func_152344_a(
                () -> HungerHelper.setExhaustion(NetworkHelper.getSidedPlayer(ctx), message.saturationLevel));

        return null;
    }
}