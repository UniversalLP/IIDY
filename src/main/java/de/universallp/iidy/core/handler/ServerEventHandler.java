package de.universallp.iidy.core.handler;

import de.universallp.iidy.core.ServerTaskHandler;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


/**
 * Created by universal on 28.11.2016 15:35.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class ServerEventHandler {

    public static ServerTaskHandler serverTaskHandler;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if (serverTaskHandler != null && !serverTaskHandler.isIdle())
            serverTaskHandler.updateTasks();
    }

    @SubscribeEvent
    public void onWorldJoin(EntityJoinWorldEvent event) {

   }


}
