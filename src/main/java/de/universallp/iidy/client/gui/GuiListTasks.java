package de.universallp.iidy.client.gui;

import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.client.gui.elements.GuiButtonBlockState;
import de.universallp.iidy.client.gui.elements.GuiScrollBar;
import de.universallp.iidy.core.task.ITask;
import de.universallp.iidy.core.task.InventoryTask;
import de.universallp.iidy.core.network.PacketHandler;
import de.universallp.iidy.core.network.messages.MessageModifyTask;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

/**
 * Created by universal on 03.12.2016 13:54.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiListTasks extends GuiScreen implements IInteractionObject {
    private static final int bgColor = 0xFFC6C6C6;

    private List<ITask> tasks;
    private int leftOffset;
    private int x;
    private int y;
    private int w;
    private int h;
    private int scrollOffset = 0;
    private int maxTasks;
    private String title;
    private GuiScrollBar scrollBar;

    public GuiListTasks(List<ITask> t) {
        this.tasks = t;
        scrollBar = new GuiScrollBar(x + w - 18, y + 15, h - 25);
        scrollBar.setVisible(false);
    }

    @Override
    public void initGui() {
        super.initGui();
        h = height - 60;
        w = width / 2;
        x = width / 2 - w / 2;
        y = height / 2 - h / 2;
        scrollBar = new GuiScrollBar(x + w - 18, y + 15, h - 25);

        maxTasks = (h - 27) / (fontRenderer.FONT_HEIGHT * 2 + 4);
        int b;
        for (ITask t : tasks) {
            b = fontRenderer.getStringWidth(t.getFinishMessage());
            if (b > leftOffset)
                leftOffset = b;
        }

        if (tasks.size() <= maxTasks)
            scrollBar.setVisible(false);
        else
            scrollBar.setVisible(true);
        title = I18n.format("iidy.gui.activetasks");
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        h = height - 60;
        w = width / 2;
        x = width / 2 - w / 2;
        y = height / 2 - h / 2;
        scrollBar.setDimensions(x + w - 18, y + 15, h - 25);
        maxTasks = (this.h - 27) / (fontRenderer.FONT_HEIGHT * 2 + 4);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawGuiBody() {
        drawGradientRect(x + 5, y + 5,  x + w + 5, y + h + 5, 0x55000000, 0x55000000);
        drawGradientRect(x - 1, y - 1,  x + w + 1, y + h + 1, 0xFF000000, 0xFF000000);
        drawGradientRect(x, y,  x + w, y + h, bgColor, bgColor);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        scrollBar.handleMouseInput();
        int i = Mouse.getEventDWheel();
        scrollOffset = (int) (tasks.size() * scrollBar.getScrollPercent());
        if (i != 0) {

            if (i > 0) {
                if (scrollOffset > 0)
                    scrollOffset--;
            } else {
                if (scrollOffset + maxTasks < tasks.size())
                    scrollOffset++;
            }
            scrollBar.setScrollPercent(MathHelper.clamp(((float)scrollOffset / maxTasks), 0.0F, 1.0F));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            int yClick = (mouseY - y - 25) / (fontRenderer.FONT_HEIGHT * 2 + 4) + scrollOffset;

            int yTask = y + 25 + (fontRenderer.FONT_HEIGHT + 2) * ((yClick - scrollOffset) * 2);
            if (mouseX >= x + 2 && mouseX <= x + 12 && mouseY >= yTask && mouseY <= yTask + fontRenderer.FONT_HEIGHT) {
                if (yClick < tasks.size() && yClick >= 0) {
                    PacketHandler.INSTANCE.sendToServer(new MessageModifyTask(tasks.get(yClick).getTaskID()));
                    tasks.remove(yClick);
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                    if (tasks.size() <= maxTasks) {
                        scrollBar.setVisible(false);
                        scrollOffset = 0;
                    }
                }
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawGuiBody();

        fontRenderer.drawStringWithShadow(title, w - fontRenderer.getStringWidth(title) / 2, y + 5, 0xFFFFFF);
        int yTask = y + 25;
        ITask t;

        for (int i = scrollOffset; i < maxTasks + scrollOffset; i++) {
            if (i >= tasks.size())
                break;
            t = tasks.get(i);

            fontRenderer.drawStringWithShadow(t.getType().getLocalizedName() + " - " + ((int)(t.getProgress() * 100)) + "% ", x + 36, yTask, 0xFFFFFF);
            drawStack(t.getIcon(), x + 13, yTask);

            if (mouseX >= x + 2 && mouseX <= x + 12 && mouseY >= yTask && mouseY <= yTask + fontRenderer.FONT_HEIGHT)
                fontRenderer.drawString("x", x + 5, yTask , 0xFF0000);
            else
                fontRenderer.drawString("x", x + 5, yTask, 0x660000);

            yTask += fontRenderer.FONT_HEIGHT + 2;
            fontRenderer.drawString(t.getFinishMessage(), x + 36, yTask, 0x505050);
            yTask += fontRenderer.FONT_HEIGHT + 2;

        }

        scrollBar.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawStack(ItemStack s, int x, int y) {
        ItemStack display = s.copy();

        if (!GuiButtonBlockState.checkValidModel(s))
            display.setItemDamage(0);

        RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(display, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        mc.fontRenderer.drawStringWithShadow(String.valueOf(display.getCount()), x + (display.getCount() > 9 ? 7 : 12), y + 10, 0xFFFFFF);
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return null;
    }

    @Override
    public String getGuiID()
    {
        return "IIDY-Tasks";
    }

    @Override
    public String getName()
    {
        return "IIDY-Task-List";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return null;
    }
}
