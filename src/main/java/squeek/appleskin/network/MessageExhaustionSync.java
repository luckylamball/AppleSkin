package squeek.appleskin.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import squeek.appleskin.helpers.HungerHelper;

public class MessageExhaustionSync implements IMessage, IMessageHandler<MessageExhaustionSync, IMessage> {
    private float exhaustionLevel;

    public MessageExhaustionSync() {
    }

    public MessageExhaustionSync(float exhaustionLevel) {
        this.exhaustionLevel = exhaustionLevel;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        exhaustionLevel = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(exhaustionLevel);
    }

    @Override
    public IMessage onMessage(MessageExhaustionSync message, MessageContext ctx) {
        Minecraft.getMinecraft().func_152344_a(
                () -> HungerHelper.setExhaustion(NetworkHelper.getSidedPlayer(ctx), message.exhaustionLevel));

        return null;
    }
}