package de.universallp.iidy.core.network.messages;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.gui.GuiListTasks;
import de.universallp.iidy.client.task.BlockStateTask;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.client.task.InventoryTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by universal on 02.12.2016 20:43.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class MessageListTasks implements IMessage, IMessageHandler<MessageListTasks, IMessage>
{

    public List<ITask> tasks;

    public MessageListTasks() { }

    public MessageListTasks(List<ITask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int i = buf.readInt();

        tasks = new ArrayList<ITask>();
        ITask task = null;
        ITask.TaskType type;
        for (int b = 0; b < i; b++) {
            type = ITask.TaskType.values()[buf.readInt()];
            if (type == ITask.TaskType.INVENTORY_SLOT) {
                task = new InventoryTask(0, "", BlockPos.ORIGIN, ByteBufUtils.readItemStack(buf), buf.readInt(), ByteBufUtils.readUTF8String(buf), InventoryTask.CompareType.EQUALS);
                task.setTaskID(buf.readInt());
                task.setProgress(buf.readFloat());
            } else if (type == ITask.TaskType.BLOCK_STATE) {
                task = new BlockStateTask(0, BlockPos.ORIGIN, ByteBufUtils.readItemStack(buf), ByteBufUtils.readUTF8String(buf), "");
                task.setTaskID(buf.readInt());
            }
            tasks.add(task);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tasks.size());

        for (ITask t : tasks) {
            buf.writeInt(t.getType().ordinal());
            if (t.getType() == ITask.TaskType.INVENTORY_SLOT) {
                ByteBufUtils.writeItemStack(buf, ((InventoryTask) t).getTargetStack());
                buf.writeInt(((InventoryTask) t).getTargetSlot());
                ByteBufUtils.writeUTF8String(buf, t.getFinishMessage());
                buf.writeInt(t.getTaskID());
                buf.writeFloat(t.getProgress());
            } else if (t.getType() == ITask.TaskType.BLOCK_STATE) {
                ByteBufUtils.writeItemStack(buf, ((BlockStateTask) t).getTargetState());
                ByteBufUtils.writeUTF8String(buf, t.getFinishMessage());
                buf.writeInt(t.getTaskID());
            }
        }
    }

    @Override
    public IMessage onMessage(MessageListTasks message, MessageContext ctx) {
        IsItDoneYet.proxy.openListGui(message.tasks);
        return null;
    }
}
