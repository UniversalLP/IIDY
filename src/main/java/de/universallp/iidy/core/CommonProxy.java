package de.universallp.iidy.core;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.core.network.PacketHandler;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * Created by universallp on 28.11.2016 15:47.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(IsItDoneYet.instance, new GuiHandler());
        PacketHandler.registerMessages();
    }

    public void postInit(FMLPostInitializationEvent e) {

    }

    public WorldServer getWorldFromDimension(int dim) {
        return DimensionManager.getWorld(dim);
    }
}
