package de.universallp.iidy.core.network.messages;

import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.client.task.InventoryTask;
import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by universallp on 01.12.2016 18:06.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class MessageModifyTask implements IMessage, IMessageHandler<MessageModifyTask, IMessage> {

    private int targetDim;
    private BlockPos targetPos;
    private ItemStack targetStack;
    private int targetSlot;
    private String finishMsg;
    private ITask.TaskType taskType;
    private int taskID;
    private InventoryTask.CompareType comparettype;

    public MessageModifyTask() {  }

    public MessageModifyTask(int dim, BlockPos targetPos, ItemStack targetStack, int targetSlot, String finishMsg, InventoryTask.CompareType compareType) {
        this.targetDim = dim;
        this.targetPos = targetPos;
        this.targetStack = targetStack;
        this.targetSlot = targetSlot;
        this.finishMsg = finishMsg;
        this.taskType = ITask.TaskType.INVENTORY_SLOT;
        this.comparettype = compareType;
    }

    public MessageModifyTask(int taskID) {
        this.taskID = taskID;
        this.taskType = ITask.TaskType.DELETE;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        taskType = ITask.TaskType.values()[buf.readInt()];
        if (taskType == ITask.TaskType.INVENTORY_SLOT) {
            targetDim = buf.readInt();
            targetSlot = buf.readInt();
            finishMsg = ByteBufUtils.readUTF8String(buf);
            targetStack = ByteBufUtils.readItemStack(buf);
            targetPos = PacketHandler.readBlockPos(buf);
            comparettype = InventoryTask.CompareType.values()[buf.readByte()];
        } else if (taskType == ITask.TaskType.DELETE) {
            taskID = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(taskType.ordinal());
        if (taskType == ITask.TaskType.INVENTORY_SLOT) {
            buf.writeInt(targetDim);
            buf.writeInt(targetSlot);
            ByteBufUtils.writeUTF8String(buf, finishMsg);
            ByteBufUtils.writeItemStack(buf, targetStack);
            PacketHandler.writeBlockPos(buf, targetPos);
            buf.writeByte(comparettype.ordinal());
        } else if (taskType == ITask.TaskType.DELETE) {
            buf.writeInt(taskID);
        }
    }

    @Override
    public IMessage onMessage(MessageModifyTask message, MessageContext ctx) {
        EntityPlayer pl = ctx.getServerHandler().playerEntity;

        if (message.taskType == ITask.TaskType.INVENTORY_SLOT) {
            EventHandlers.serverTaskHandler.addTask(new InventoryTask(message.targetDim, pl.getUniqueID().toString(), message.targetPos, message.targetStack, message.targetSlot, message.finishMsg, message.comparettype));
        } else if (message.taskType == ITask.TaskType.DELETE) {
            EventHandlers.serverTaskHandler.tryRemoveTask(message.taskID, pl.getUniqueID().toString());
        }
        return null;
    }
}