package de.universallp.iidy.client.gui;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.MessageOpenBlockStateGui;
import de.universallp.iidy.core.network.messages.MessageRequestList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by universal on 28.11.2016 16:13.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiSelectTask extends GuiScreen {

    public static ResourceLocation bg = new ResourceLocation(IsItDoneYet.MODID, "textures/gui/select.png");

    private GuiButton btnInventoryTask;
    private GuiButton btnBlockStateTask;
    private GuiButton btnListTasks;

    private boolean inventoryBlock;
    private boolean blockstate;
    private BlockPos blockPos;
    private EnumFacing face;

    public GuiSelectTask(boolean iinv, boolean blockstate, BlockPos loc, EnumFacing face) {
        this.inventoryBlock = iinv;
        this.blockstate = blockstate;
        this.blockPos = loc;
        this.face = face;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = this.width / 2 - 75;
        int y = this.height / 2;

        btnInventoryTask = new GuiButton(0, x, y - 24, 150, 20, I18n.format("iidy.btn.inventorytask"));
        btnBlockStateTask = new GuiButton(1, x, y - 1, 150, 20, I18n.format("iidy.btn.blockstatetask"));
        btnListTasks = new GuiButton(2, x, y + 22, 150, 20, I18n.format("iidy.btn.listtask"));

        btnInventoryTask.enabled = inventoryBlock;
        btnBlockStateTask.enabled = blockstate;

        buttonList.add(btnInventoryTask);
        buttonList.add(btnBlockStateTask);
        buttonList.add(btnListTasks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            EventHandlers.currentTask = ITask.TaskType.INVENTORY_SLOT;
            ClientProxy.mc.currentScreen = null;
            EntityPlayerSP p = Minecraft.getMinecraft().player;
            ClientProxy.mc.playerController.processRightClickBlock(p, FMLClientHandler.instance().getWorldClient(), blockPos, face, Vec3d.ZERO, EnumHand.MAIN_HAND);
        } else if (button.id == 1) {
            EventHandlers.currentTask = ITask.TaskType.BLOCK_STATE;
            //Minecraft.getMinecraft().currentScreen = null;
            PacketHandler.INSTANCE.sendToServer(new MessageOpenBlockStateGui());
        } else if (button.id == 2) {
            PacketHandler.INSTANCE.sendToServer(new MessageRequestList());
        }

        super.actionPerformed(button);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();
        this.mc.getTextureManager().bindTexture(bg);

        int i = (this.width - 176) / 2;
        int j = (this.height - 70) / 2;
        drawTexturedModalRect(i, j, 0, 0, 176, 86);

        super.drawScreen(mouseX, mouseY, partialTicks);

        String text = IsItDoneYet.MODID.toUpperCase();
        fontRenderer.drawStringWithShadow(text, width / 2 - (fontRenderer.getStringWidth(text) / 2), j - 40, 0xFFFFFF);

        text = "Select your task";
        fontRenderer.drawString(text, width / 2 - (fontRenderer.getStringWidth(text) / 2), j - 20, 0xBBBBBB);


        if (btnInventoryTask.isMouseOver()) {
            if (btnInventoryTask.enabled)
                drawHoveringText(Arrays.asList(I18n.format("iidy.inventorytask.desc")), mouseX, mouseY);
            else
                drawHoveringText(Arrays.asList(I18n.format("iidy.inventorytask.noinv.desc")), mouseX, mouseY);
        } else if (btnBlockStateTask.isMouseOver()) {
            if (btnBlockStateTask.enabled)
                drawHoveringText(Arrays.asList(I18n.format("iidy.blockstatetask.desc")), mouseX, mouseY);
            else
                drawHoveringText(Arrays.asList(I18n.format("iidy.blockstatetask.noblock")), mouseX, mouseY);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        EventHandlers.currentTask = ITask.TaskType.NONE;
    }
}
