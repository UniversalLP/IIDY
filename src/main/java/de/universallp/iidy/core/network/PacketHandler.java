package de.universallp.iidy.core.network;

import de.universallp.iidy.IsItDoneYet;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by universal on 01.12.2016 18:04.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(IsItDoneYet.MODID);

    public static void writeBlockPos(ByteBuf to, BlockPos pos) {
        to.writeInt(pos.getX());
        to.writeInt(pos.getY());
        to.writeInt(pos.getZ());
    }

    public static BlockPos readBlockPos(ByteBuf from) {
        return new BlockPos(from.readInt(), from.readInt(), from.readInt());
    }
}
