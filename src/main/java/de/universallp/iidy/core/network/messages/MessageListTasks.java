package de.universallp.iidy.core.network.messages;

import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.gui.GuiListTasks;
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
 * Created by universallp on 02.12.2016 20:43.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class MessageListTasks implements IMessage, IMessageHandler<MessageListTasks, IMessage> {

    private List<ITask> tasks;

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
            }
            tasks.add(task);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tasks.size());

        for (ITask t : tasks) {
            if (t.getType() == ITask.TaskType.INVENTORY_SLOT) {
                buf.writeInt(t.getType().ordinal());
                ByteBufUtils.writeItemStack(buf, ((InventoryTask) t).getTargetStack());
                buf.writeInt(((InventoryTask) t).getTargetSlot());
                ByteBufUtils.writeUTF8String(buf, t.getFinishMessage());
                buf.writeInt(t.getTaskID());
                buf.writeFloat(t.getProgress());
            }

        }
    }

    @Override
    public IMessage onMessage(MessageListTasks message, MessageContext ctx) {
        FMLClientHandler.instance().displayGuiScreen(ClientProxy.mc.thePlayer, new GuiListTasks(message.tasks));
        return null;
    }
}