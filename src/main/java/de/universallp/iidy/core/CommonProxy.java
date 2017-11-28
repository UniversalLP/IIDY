package de.universallp.iidy.core;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.MessageListTasks;
import de.universallp.iidy.core.network.messages.MessageModifyTask;
import de.universallp.iidy.core.network.messages.MessageOpenBlockStateGui;
import de.universallp.iidy.core.network.messages.MessageRequestList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by universal on 28.11.2016 15:47.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(IsItDoneYet.instance, new GuiHandler());
        registerMessages();
    }

    public String translate(String s, Object ... args) {
        return null;
    }

    public void registerMessages() {
        PacketHandler.INSTANCE.registerMessage(MessageModifyTask.class, MessageModifyTask.class, 0, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageRequestList.class, MessageRequestList.class, 2, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageOpenBlockStateGui.class, MessageOpenBlockStateGui.class, 3, Side.SERVER);
    }

    public void postInit(FMLPostInitializationEvent e) {

    }

    public WorldServer getWorldFromDimension(int dim) {
        return DimensionManager.getWorld(dim);
    }
}
