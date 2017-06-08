package de.universallp.iidy.client.gui;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.client.task.InventoryTask;
import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.MessageModifyTask;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

/**
 * Created by universal on 10.04.2017.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENCE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiBlockStateTask extends GuiContainer {

    public static ResourceLocation bg = new ResourceLocation(IsItDoneYet.MODID, "textures/gui/blockstate.png");
    private final IInventory playerInventory;

    private ItemStack targetStateStack = new ItemStack(Blocks.STONE, 1);
    private GuiTextField taskMsg;
    private GuiButton btnAccept;
    private GuiButtonBlockState btnState;

    private String label1;
    private String label2;
    private String label3;

    public GuiBlockStateTask(Container inventorySlotsIn, IInventory playerInventory) {
        super(inventorySlotsIn);
        this.playerInventory = playerInventory;
        label1 = I18n.format("iidy.task.blockstate");
        label2 = I18n.format("iidy.lbl.finishmsg");
        label3 = I18n.format("iidy.lbl.changeto");
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        taskMsg = new GuiTextField(2, fontRendererObj, guiLeft + 8, guiTop + 50, 107, 10);

        ItemStack oldStack = ItemStack.EMPTY;
        if (btnState != null)
            oldStack = btnState.getDisplayStack();

        btnState = new GuiButtonBlockState(2, guiLeft + 138, guiTop + 21);

        IBlockState hoveredState = FMLClientHandler.instance().getWorldClient().getBlockState(ClientProxy.mc.objectMouseOver.getBlockPos());
        Block hoveredBlock = hoveredState.getBlock();
        ItemStack stackFromHoveredBlock = new ItemStack(hoveredBlock, 1, hoveredBlock.getMetaFromState(hoveredState));


        btnState.setDisplayStack(stackFromHoveredBlock);
        btnState.setResultState(hoveredState);

        if (!oldStack.isEmpty())
            btnState.setDisplayStack(oldStack);

        btnAccept = new GuiButton(1, guiLeft + 128, guiTop + 50, 40, 20, I18n.format("gui.done"));
        buttonList.add(btnAccept);
        buttonList.add(btnState);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        taskMsg.drawTextBox();
        btnState.drawTooltips(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, 64, 4210752);
        fontRendererObj.drawString(label1,  xSize / 2 - fontRendererObj.getStringWidth(label1) / 2, 12, 4210752);
        fontRendererObj.drawString(label2, 8,  39, 4210752);
        fontRendererObj.drawString(label3, 8,  26, 4210752);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 1) {
            mc.player.closeScreen();
            mc.mouseHelper.grabMouseCursor();
            PacketHandler.INSTANCE.sendToServer(new MessageModifyTask(mc.player.dimension, mc.objectMouseOver.getBlockPos(), btnState.getDisplayStack(), taskMsg.getText()));
            EventHandlers.currentTask = ITask.TaskType.NONE;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        taskMsg.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (Mouse.getEventDWheel() != 0) {
            int mouseButton = Mouse.getEventDWheel() == 120 ? 100 : 101;
            if (btnState.isMouseOver())
                btnState.scroll(mouseButton == 100);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        taskMsg.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bg);
        int i = (this.width - 176) / 2;
        int j = (this.height - 154) / 2;
        drawTexturedModalRect(i, j, 0, 0, 176, 154);
    }
}
