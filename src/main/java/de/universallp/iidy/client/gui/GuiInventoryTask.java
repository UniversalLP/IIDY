package de.universallp.iidy.client.gui;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.client.task.InventoryTask;
import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.MessageModifyTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by universallp on 28.11.2016 16:43.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class GuiInventoryTask extends GuiScreen {

    static ResourceLocation bg = new ResourceLocation(IsItDoneYet.MODID, "textures/gui/task.png");

    private GuiButtonItem btnItem;
    private GuiNumberField slotTextbox;
    private GuiTextField taskMsg;
    private GuiButton   btnAccept;
    private GuiButtonCycle btnCycle;

    private BlockPos targetPos;
    private int      targetDim;

    private int xOffset = 0;
    private int yOffset = 0;
    private int maxSlots;

    private String label1;
    private String label2;
    private String label3;
    private String label4;

    private Container parentContainer;
    private GuiContainer parentGui;

    public GuiInventoryTask(BlockPos target, int dimension, Container parent, GuiContainer parentScreen) {
        this.mc = ClientProxy.mc;
        this.targetDim = dimension;
        this.targetPos = target;
        this.parentContainer = parent;
        this.parentGui = parentScreen;
        this.xOffset = ClientProxy.getGuiLeft(parentScreen);
        this.yOffset = ClientProxy.getGuiTop(parentScreen);

        fontRendererObj = ClientProxy.mc.fontRendererObj;

        for (maxSlots = 0; maxSlots < parentContainer.inventorySlots.size(); maxSlots++)
            if (parent.inventorySlots.get(maxSlots).inventory.equals(Minecraft.getMinecraft().thePlayer.inventory)) break;

        slotTextbox = new GuiNumberField(0, fontRendererObj, 0, 18, 56, 10);
        slotTextbox.xPosition = xOffset - slotTextbox.width - 9;
        slotTextbox.yPosition = yOffset + 17;
        slotTextbox.setMaximum(maxSlots);

        taskMsg = new GuiTextField(2, fontRendererObj, 0, 8, 107, 10);
        taskMsg.xPosition = xOffset - taskMsg.width - 5;
        taskMsg.yPosition = yOffset + 30;

        btnItem = new GuiButtonItem(3, xOffset - 22, yOffset + 33);
        btnCycle = new GuiButtonCycle(4, xOffset - 46, yOffset + 28).setOptions("<", "<=", ">", ">=", "=").setIndex(3);

        Keyboard.enableRepeatEvents(true);

        label1 = I18n.format("iidy.lbl.whenslot");
        label2 = I18n.format("iidy.lbl.contains");
        label3 = I18n.format("iidy.task");
        label4 = I18n.format("iidy.lbl.finishmsg");

        btnAccept = new GuiButton(1, xOffset - 72, yOffset + 60, 60, 20, I18n.format("gui.done"));
        buttonList.add(btnAccept);
        buttonList.add(btnItem);
        buttonList.add(btnCycle);
    }

    private void update() {
        this.xOffset = ClientProxy.getGuiLeft(parentGui);
        this.yOffset = ClientProxy.getGuiTop(parentGui);

        slotTextbox.xPosition = xOffset - slotTextbox.width - 9;
        slotTextbox.yPosition = yOffset + 17;

        taskMsg.xPosition = xOffset - taskMsg.width - 9;
        taskMsg.yPosition = yOffset + 58;

        btnItem.xPosition = xOffset - 27;
        btnItem.yPosition = yOffset + 29;

        btnAccept.xPosition = xOffset - 69;
        btnAccept.yPosition = yOffset + 91;

    }

    public boolean handleClick(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton > 99) { // 100 & 101 for scrolling
            if (btnItem.isMouseOver())
                btnItem.scroll(mouseButton == 100);
        }
        slotTextbox.mouseClicked(mouseX, mouseY, mouseButton);
        taskMsg.mouseClicked(mouseX, mouseY, mouseButton);

        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 1) {
            mc.thePlayer.closeScreen();
            mc.mouseHelper.grabMouseCursor();
            InventoryTask.CompareType c = InventoryTask.CompareType.values()[btnCycle.getIndex()];
            PacketHandler.INSTANCE.sendToServer(new MessageModifyTask(targetDim, targetPos, btnItem.getDisplayStack(), slotTextbox.getValue(), taskMsg.getText(), c));
            EventHandlers.currentTask = ITask.TaskType.NONE;
        }
    }

    public boolean handleKey(char typedChar, int keyCode) {

        if (keyCode == 0 && typedChar >= 32)
            return true;

        if (Keyboard.getEventKeyState())
            if (slotTextbox.isFocused() && slotTextbox.textboxKeyTyped(typedChar, keyCode) || taskMsg.isFocused() && taskMsg.textboxKeyTyped(typedChar, keyCode))
                return true;

        return false;
    }

    @Override
    public void initGui() {
        super.initGui();

    }

    public void drawScreenPost(int mouseX, int mouseY) {
        GlStateManager.disableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        int selectedSlot = slotTextbox.getValue();
        for (int i = 0; i < maxSlots; i++) {
            Slot s = parentContainer.getSlot(i);

            if (!s.inventory.equals(Minecraft.getMinecraft().thePlayer.inventory)) {
                ClientProxy.mc.fontRendererObj.drawStringWithShadow(String.valueOf(s.getSlotIndex()), xOffset + s.xDisplayPosition + 2 + (s.getSlotIndex() > 9 ? 0 : 3), yOffset + s.yDisplayPosition + 4, i == selectedSlot ? 0xFF11 : 0xFFFF);
            }
        }
        GlStateManager.enableDepth();
        btnItem.drawTooltips(mouseX, mouseY);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        update();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        this.mc.getTextureManager().bindTexture(bg);

        int i = xOffset - 125;
        int j = yOffset;
        GlStateManager.enableDepth();
        RenderHelper.disableStandardItemLighting();
        drawTexturedModalRect(i, j, 0, 0, 123, 118);

        slotTextbox.drawTextBox();
        taskMsg.drawTextBox();

        fontRendererObj.drawString(label1, xOffset - slotTextbox.width - fontRendererObj.getStringWidth(label1) - 13, yOffset + 18, 4210752);
        fontRendererObj.drawString(label2, xOffset - slotTextbox.width - fontRendererObj.getStringWidth(label1) - 13, yOffset + 33, 4210752);
        fontRendererObj.drawString(label3, xOffset - 60 - fontRendererObj.getStringWidth(label3) / 2, yOffset + 5, 4210752);
        fontRendererObj.drawString(label4, xOffset - slotTextbox.width - fontRendererObj.getStringWidth(label1) - 13, yOffset + 47, 4210752);

        GlStateManager.enableLighting();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
