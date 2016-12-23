package de.universallp.iidy.core.network.messages;

import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by universallp on 04.12.2016 13:20.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class MessageRequestList  implements IMessage, IMessageHandler<MessageRequestList, IMessage> {

    public MessageRequestList() { }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(MessageRequestList message, MessageContext ctx) {
        EntityPlayer pl = ctx.getServerHandler().playerEntity;
        List<ITask> tasks = EventHandlers.serverTaskHandler.getTasksForPlayer(pl.getUniqueID().toString());
        if (tasks != null)
            PacketHandler.INSTANCE.sendTo(new MessageListTasks(tasks), (EntityPlayerMP) pl);
        else
            PacketHandler.INSTANCE.sendTo(new MessageListTasks(new ArrayList<ITask>()), (EntityPlayerMP) pl);

        return null;
    }
}
