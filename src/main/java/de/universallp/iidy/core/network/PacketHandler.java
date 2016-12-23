package de.universallp.iidy.core.network;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.core.network.messages.MessageListTasks;
import de.universallp.iidy.core.network.messages.MessageModifyTask;
import de.universallp.iidy.core.network.messages.MessageRequestList;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by universallp on 01.12.2016 18:04.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(IsItDoneYet.MODID);

    public static void registerMessages() {
        INSTANCE.registerMessage(MessageModifyTask.class, MessageModifyTask.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageListTasks.class, MessageListTasks.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MessageRequestList.class, MessageRequestList.class, 2, Side.SERVER);
    }

    public static void writeBlockPos(ByteBuf to, BlockPos pos) {
        to.writeInt(pos.getX());
        to.writeInt(pos.getY());
        to.writeInt(pos.getZ());
    }

    public static BlockPos readBlockPos(ByteBuf from) {
        return new BlockPos(from.readInt(), from.readInt(), from.readInt());
    }
}
