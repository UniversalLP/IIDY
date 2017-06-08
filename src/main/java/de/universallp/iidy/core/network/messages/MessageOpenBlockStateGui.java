package de.universallp.iidy.core.network.messages;

import de.universallp.iidy.IsItDoneYet;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by universal on 10.04.2017.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENCE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class MessageOpenBlockStateGui implements IMessage, IMessageHandler<MessageOpenBlockStateGui, IMessage> {

    public MessageOpenBlockStateGui() { }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(MessageOpenBlockStateGui message, MessageContext ctx) {
        FMLNetworkHandler.openGui(ctx.getServerHandler().playerEntity, IsItDoneYet.instance, 2, ctx.getServerHandler().playerEntity.getEntityWorld(),0, 0, 0);
        return null;
    }
}
