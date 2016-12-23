package de.universallp.iidy;

import de.universallp.iidy.core.CommonProxy;
import de.universallp.iidy.core.ServerTaskHandler;
import de.universallp.iidy.core.handler.EventHandlers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = IsItDoneYet.MODID, version = IsItDoneYet.VERSION)
public class IsItDoneYet {
    public static final String MODID = "iidy";
    public static final String VERSION = "1.11-0.1";

    @SidedProxy(clientSide = "de.universallp.iidy.client.ClientProxy", serverSide = "de.universallp.iidy.core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static IsItDoneYet instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerAboutToStartEvent e) {
        EventHandlers.serverTaskHandler = new ServerTaskHandler();
    }

}
