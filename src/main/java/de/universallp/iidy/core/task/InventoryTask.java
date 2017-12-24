package de.universallp.iidy.core.task;

import de.universallp.iidy.IsItDoneYet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.UUID;

/**
 * Created by universal on 28.11.2016 15:40.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class InventoryTask implements ITask {

    private BlockPos targetBlock;

    private CompareType compareType;

    private int dim;

    private NBTTagCompound targetStack;

    private int targetSlot;

    private int interval;

    private String finishMsg;

    private String targetPlayerUUID;

    private int taskID;

    private float progress;

    public InventoryTask(int dim, String targetPlayerUUID, BlockPos targetBlock, ItemStack targetStack, int targetSlot, String finishMsg, CompareType c) {
        this.targetBlock = targetBlock;
        this.targetStack = new NBTTagCompound();
        targetStack.writeToNBT(this.targetStack);
        this.targetSlot = targetSlot;
        this.finishMsg = finishMsg;
        this.dim = dim;
        this.interval = 20;
        this.targetPlayerUUID = targetPlayerUUID;
        this.compareType = c;
    }

    public InventoryTask() { }

    @Override
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    @Override
    public int getTaskID() {
        return taskID;
    }

    @Override
    public void finish(TaskResult result) {
        if (result == TaskResult.SUCCESS) {
            MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
            PlayerList pl = s.getPlayerList();
            EntityPlayerMP p = pl.getPlayerByUUID(UUID.fromString(targetPlayerUUID));

            p.sendMessage(new TextComponentString(ITask.FORMAT_DARK_GREEN + "[IIDY] " + ITask.FORMAT_YELLOW + finishMsg + ITask.FORMAT_RESET));
        } else {
            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(targetPlayerUUID)).sendMessage(new TextComponentString(ITask.FORMAT_RED
                    + "[IIDY Task Failed] " + ITask.FORMAT_YELLOW + finishMsg + ITask.FORMAT_RESET));
        }
    }

    @Override
    public boolean needsUpdate(long worldTicks) { return true; }//worldTicks % interval == 0; }

    @Override
    public TaskResult isDone() {
        World w = IsItDoneYet.proxy.getWorldFromDimension(dim);
        TileEntity te = w.getTileEntity(targetBlock);
        ItemStack stack = new ItemStack(targetStack);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack stackInSlot = itemHandler.getStackInSlot(targetSlot);

            boolean areItemsEqual = ItemStack.areItemsEqual(stackInSlot, stack);
            if (areItemsEqual) {
                progress = ((float) stackInSlot.getCount()) / ((float) stack.getCount());
            } else {
                progress = 0;
            }

            return areItemsEqual && ItemStack.areItemStackTagsEqual(stackInSlot, stack) && compareType.compareStack(stack, stackInSlot) ? TaskResult.SUCCESS : TaskResult.UNFINISHED;
        } else
            return TaskResult.FAILURE;
    }

    @Override
    public BlockPos getPos() {
        return targetBlock;
    }

    @Override
    public int getDim() {
        return dim;
    }

    @Override
    public TaskType getType() {
        return TaskType.INVENTORY_SLOT;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(float f) {
        this.progress = f;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(targetStack);
    }

    @Override
    public String getOwnerUUID() {
        return targetPlayerUUID;
    }

    @Override
    public String getFinishMessage() {
        return finishMsg;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (tag == null)
            return null;


        tag.setInteger("BlockX", targetBlock.getX());
        tag.setInteger("BlockY", targetBlock.getY());
        tag.setInteger("BlockZ", targetBlock.getZ());

        tag.setInteger("Dim", dim);

        tag.setByte("CompareType", (byte) compareType.ordinal());

        tag.setTag("TargetStack", targetStack);

        tag.setInteger("TargetSlot", targetSlot);

        tag.setString("FinishMsg", finishMsg);
        tag.setString("TargetPlayer", targetPlayerUUID);

        tag.setInteger("ID", taskID);
        tag.setFloat("Progress", progress);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag == null)
            return;

        if (tag.hasKey("BlockX") && tag.hasKey("BlockY") && tag.hasKey("BlockZ"))
            this.targetBlock = new BlockPos(tag.getInteger("BlockX"), tag.getInteger("BlockY"), tag.getInteger("BlockZ"));

        if (tag.hasKey("Dim"))
            this.dim = tag.getInteger("Dim");

        if (tag.hasKey("CompareType"))
            this.compareType = CompareType.values()[tag.getByte("CompareType")];

        if (tag.hasKey("TargetStack"))
            this.targetStack = tag.getCompoundTag("TargetStack");

        if (tag.hasKey("TargetSlot"))
            this.targetSlot = tag.getInteger("TargetSlot");

        if (tag.hasKey("FinishMsg"))
            this.finishMsg = tag.getString("FinishMsg");

        if (tag.hasKey("TargetPlayer"))
            this.targetPlayerUUID = tag.getString("TargetPlayer");

        if (tag.hasKey("ID"))
            this.taskID = tag.getInteger("ID");
    }
    public int getTargetSlot() {
        return targetSlot;
    }

    public enum CompareType {
        LESSTHAN("<"),
        LESSTHAN_EQUAL("<="),
        MORETHAN(">"),
        MORETHAN_EQUAL(">="),
        EQUALS("=");

        String operator;

        CompareType(String op) {
            this.operator = op;
        }

        boolean compareStack(ItemStack target, ItemStack stack2) {
            boolean flag = ItemStack.areItemsEqual(target, stack2) && ItemStack.areItemStackTagsEqual(target, stack2);

            if (!flag)
                return false;
            int sizeTarget = target.getCount();
            int sizeStack = stack2.getCount();

            switch (this) {
                case LESSTHAN:
                    return sizeStack < sizeTarget;
                case LESSTHAN_EQUAL:
                    return sizeStack <= sizeTarget;
                case MORETHAN:
                    return sizeStack > sizeTarget;
                case MORETHAN_EQUAL:
                    return sizeStack >= sizeTarget;
                case EQUALS:
                    return  sizeStack == sizeTarget;
            }
            return false;
        }
    }
}
