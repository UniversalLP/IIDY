package de.universallp.iidy.client.gui;

import com.google.common.collect.ImmutableList;
import de.universallp.iidy.client.ClientProxy;
import de.universallp.iidy.core.handler.EventHandlers;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by universal on 30.11.2016 18:03.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiButtonItem extends GuiButton {

    protected ItemStack displayStack = new ItemStack(Blocks.STONE, 1);


    public GuiButtonItem(int buttonId, int x, int y) {
        super(buttonId, x, y, "");
        setWidth(19);
        height = 19;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            if (!mc.player.inventory.getItemStack().isEmpty()) {
                displayStack = mc.player.inventory.getItemStack().copy();
                return true;
            }
        }
        return false;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public void setDisplayStack(ItemStack displayStack) {
        this.displayStack = displayStack;
    }

    public void scroll(boolean up) {
        if (up) {
            if (displayStack.getMaxStackSize() >= displayStack.getCount() + 1)
                displayStack.grow(1);
        } else {
            if (displayStack.getCount() > 1)
                displayStack.shrink(1);
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y) {
        List<String> list = stack.getTooltip(ClientProxy.mc.player, ClientProxy.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        ScaledResolution res = new ScaledResolution(ClientProxy.mc);
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        GuiUtils.drawHoveringText(list, x, y, res.getScaledWidth(), res.getScaledHeight(), res.getScaledWidth(), (font == null ? ClientProxy.mc.fontRendererObj : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    public void drawTooltips(int mouseX, int mouseY) {
        if (hovered && visible) {
            renderToolTip(displayStack, mouseX, mouseY);
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)  {
            hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            EventHandlers.skipClick = hovered;
            mc.getTextureManager().bindTexture(GuiInventoryTask.bg);;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.enableDepth();
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 123, 0, this.width, this.height);

            RenderHelper.enableGUIStandardItemLighting();

            mc.getRenderItem().renderItemAndEffectIntoGUI(displayStack, xPosition + 1, yPosition + 1);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            mc.fontRendererObj.drawStringWithShadow(String.valueOf(displayStack.getCount()), this.xPosition + (displayStack.getCount() > 9 ? 7 : 12), this.yPosition + 10, 0xFFFFFF);
        }
    }
}
