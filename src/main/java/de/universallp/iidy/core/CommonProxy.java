package de.universallp.iidy.core;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.core.task.ITask;
import de.universallp.iidy.core.handler.ServerEventHandler;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by universal on 28.11.2016 15:47.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class CommonProxy {

    public Logger log;

    public void preInit(FMLPreInitializationEvent e) {
        log = e.getModLog();
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(IsItDoneYet.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        registerMessages();
    }

    public String translate(String s, Object ... args) {
        return null;
    }

    public void registerMessages() {
        PacketHandler.INSTANCE.registerMessage(MessageModifyTask.class, MessageModifyTask.class, 0, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageListTasks.class, MessageListTasks.class, 1, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(MessageRequestList.class, MessageRequestList.class, 2, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageOpenBlockStateGui.class, MessageOpenBlockStateGui.class, 3, Side.SERVER);
    }

    public void openListGui(List<ITask> tasks) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }

    public WorldServer getWorldFromDimension(int dim) {
        return DimensionManager.getWorld(dim);
    }
}
