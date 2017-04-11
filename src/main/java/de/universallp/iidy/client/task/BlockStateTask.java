package de.universallp.iidy.client.task;

import com.mojang.realmsclient.gui.ChatFormatting;
import de.universallp.iidy.IsItDoneYet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.UUID;

/**
 * Created by universallp on 10.04.2017.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENCE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class BlockStateTask implements ITask {

    private BlockPos targetBlockPos;

    private Block targetBlock;

    private int targetMeta;

    private int dim;

    private String finishMsg;

    private String targetPlayerUUID;

    private int taskID;

    public BlockStateTask(int dim, BlockPos targetBlock, ItemStack targetState, String msg, String targetUUID) {
        this.dim = dim;
        this.targetBlock = Block.getBlockFromItem(targetState.getItem());
        this.targetMeta = targetState.getItemDamage();
        this.finishMsg = msg;
        this.targetPlayerUUID = targetUUID;
        this.targetBlockPos = targetBlock;
    }

    @Override
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    @Override
    public int getTaskID() {
        return taskID;
    }

    @Override
    public boolean needsUpdate(long worldTicks) {
        return true;
    }

    @Override
    public void finish(TaskResult result) {
        if (result == TaskResult.SUCCESS) {
            MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
            PlayerList pl = s.getPlayerList();
            EntityPlayerMP p = pl.getPlayerByUUID(UUID.fromString(targetPlayerUUID));

            p.sendMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "[IIDY] " + ChatFormatting.YELLOW + finishMsg + ChatFormatting.RESET));
        } else
            FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(UUID.fromString(targetPlayerUUID)).sendMessage(new TextComponentString(ChatFormatting.DARK_RED + "[IIDY Task Failed] " + ChatFormatting.YELLOW + finishMsg + ChatFormatting.RESET));
    }

    public ItemStack getTargetState() {
        return new ItemStack(targetBlock, 1, targetMeta);
    }

    @Override
    public TaskResult isDone() {
        World w = IsItDoneYet.proxy.getWorldFromDimension(dim);

        IBlockState blockState = w.getBlockState(targetBlockPos);
        Block b = blockState.getBlock();
        int currentMeta = b.getMetaFromState(blockState);

        if (currentMeta == targetMeta && b.equals(targetBlock))
            return TaskResult.SUCCESS;
        return TaskResult.UNFINISHED;
    }

    @Override
    public BlockPos getPos() {
        return targetBlockPos;
    }

    @Override
    public int getDim() {
        return dim;
    }

    @Override
    public TaskType getType() {
        return TaskType.BLOCK_STATE;
    }

    @Override
    public float getProgress() {
        // NO OP
        return 0;
    }

    @Override
    public void setProgress(float f) {
        // NO OP
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

        tag.setInteger("BlockX", targetBlockPos.getX());
        tag.setInteger("BlockY", targetBlockPos.getY());
        tag.setInteger("BlockZ", targetBlockPos.getZ());

        tag.setInteger("Dim", dim);

        NBTTagCompound stackTag = new NBTTagCompound();
        new ItemStack(targetBlock, 1, targetMeta).writeToNBT(stackTag);
        tag.setTag("TargetStack", stackTag);

        tag.setString("FinishMsg", finishMsg);
        tag.setString("TargetPlayer", targetPlayerUUID);

        tag.setInteger("ID", taskID);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag == null)
            return;

        if (tag.hasKey("BlockX") && tag.hasKey("BlockY") && tag.hasKey("BlockZ"))
            this.targetBlockPos = new BlockPos(tag.getInteger("BlockX"), tag.getInteger("BlockY"), tag.getInteger("BlockZ"));

        if (tag.hasKey("Dim"))
            this.dim = tag.getInteger("Dim");

        if (tag.hasKey("TargetStack")) {
            ItemStack s = new ItemStack(tag.getCompoundTag("TargetStack"));
            this.targetMeta = s.getItemDamage();
            this.targetBlock = Block.getBlockFromItem(s.getItem());
        }

        if (tag.hasKey("FinishMsg"))
            this.finishMsg = tag.getString("FinishMsg");

        if (tag.hasKey("TargetPlayer"))
            this.targetPlayerUUID = tag.getString("TargetPlayer");

        if (tag.hasKey("ID"))
            this.taskID = tag.getInteger("ID");
    }
}
