package de.universallp.iidy.core;

import de.universallp.iidy.client.gui.GuiSelectTask;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.MessageListTasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.List;

/**
 * Created by universallp on 28.11.2016 16:15.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class GuiHandler implements IGuiHandler {


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 1) { // List Screen
            List<ITask> tasks = EventHandlers.serverTaskHandler.getTasksForPlayer(player.getUniqueID().toString());
            if (tasks != null)
                PacketHandler.INSTANCE.sendTo(new MessageListTasks(tasks), (EntityPlayerMP) player);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) { // Select screen
            return new GuiSelectTask(false, false, new BlockPos(x, y, z), EnumFacing.DOWN);
        }

        return null;
    }
}