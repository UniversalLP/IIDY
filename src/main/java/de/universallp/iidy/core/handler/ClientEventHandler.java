/**
 * Created by universal on 01.12.2017 13:23
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENCE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
package de.universallp.iidy.core.handler;

import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.gui.GuiInventoryTask;
import de.universallp.iidy.client.gui.GuiSelectTask;
import de.universallp.iidy.core.task.ITask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class ClientEventHandler {
    public static boolean skipClick = false;
    public static ITask.TaskType currentTask = ITask.TaskType.NONE;
    public static BlockPos current_target;
    public static int current_dimension = 0;
    private static GuiInventoryTask guiInventoryTask;

    @SubscribeEvent
    public void onKeyDown(InputEvent.KeyInputEvent e) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && ClientProxy.KEY_MAKE_TASK.isPressed()) {
            RayTraceResult rayTraceResult = ClientProxy.mc.objectMouseOver;
            current_target = rayTraceResult.getBlockPos();
            current_dimension = Minecraft.getMinecraft().player.dimension;
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(current_target);
            EnumFacing face = rayTraceResult.sideHit;
            FMLCommonHandler.instance().showGuiScreen(new GuiSelectTask(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK, current_target, face));
        } else {
            current_target = null;
            currentTask = null;
        }
    }

    @SubscribeEvent
    public void onDrawBackgroundEventPost(GuiScreenEvent.BackgroundDrawnEvent e)  {
        if (currentTask == ITask.TaskType.INVENTORY_SLOT) {
            GuiScreen currentGui = e.getGui();
            if (currentGui instanceof GuiContainer) {

                Container openContainer = ClientProxy.mc.player.openContainer;
                if (openContainer != null) {

                    if (guiInventoryTask == null)
                        guiInventoryTask = new GuiInventoryTask(current_target, current_dimension, openContainer, (GuiContainer) currentGui);
                    guiInventoryTask.drawScreen(e.getMouseX(), e.getMouseY(), 1.0F);
                }
            }
        } else {
            guiInventoryTask = null;
        }
    }

    @SubscribeEvent
    public void onDrawScreenEventPost(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (currentTask == ITask.TaskType.INVENTORY_SLOT) {
            GuiScreen currentGui = e.getGui();
            if (currentGui instanceof GuiContainer) {
                if (guiInventoryTask != null)
                    guiInventoryTask.drawScreenPost(e.getMouseX(), e.getMouseY());
            }
        }
    }

    @SubscribeEvent
    public void onMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (guiInventoryTask != null && currentTask != ITask.TaskType.NONE) {
            if (skipClick) {
                if (event.isCancelable())
                    event.setCanceled(true);
                event.setResult(Event.Result.DENY);
            }
            if (Mouse.getEventDWheel() != 0) {
                try {
                    guiInventoryTask.handleClick(ClientProxy.getMouseX(), ClientProxy.getMouseY(), Mouse.getEventDWheel() == 120 ? 100 : 101);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (Mouse.getEventButton() != -1 && Mouse.getEventButtonState())
                try {

                    guiInventoryTask.handleClick(ClientProxy.getMouseX(), ClientProxy.getMouseY(), Mouse.getEventButton());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Pre event){
        if (guiInventoryTask != null && currentTask != ITask.TaskType.NONE) {
            if (guiInventoryTask.handleKey(Keyboard.getEventCharacter(), Keyboard.getEventKey())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
        if (guiInventoryTask != null && currentTask != ITask.TaskType.NONE) {
            if ( guiInventoryTask.handleKey(Keyboard.getEventCharacter(), Keyboard.getEventKey())) {
                event.setCanceled(true);
            }
        }
    }
}
