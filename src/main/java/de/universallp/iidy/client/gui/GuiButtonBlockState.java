package de.universallp.iidy.client.gui;

import de.universallp.iidy.client.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by universal on 10.04.2017.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENCE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiButtonBlockState extends GuiButtonItem {

    private static final String MISSING_NO = TextureMap.LOCATION_MISSING_TEXTURE.getResourcePath();

    private List<IProperty<?>> properties;
    private IBlockState resultState = Blocks.STONE.getDefaultState();
    private int propertyToEdit = 0;

    public GuiButtonBlockState(int buttonId, int x, int y) {
        super(buttonId, x, y);
        Collection<IProperty<?>> coll = resultState.getPropertyKeys();

        if (coll instanceof List)
            properties = (ArrayList<IProperty<?>>) coll;
        else
            properties = new ArrayList(coll);
    }

    public void scroll(boolean up) {
        if (properties.size() > 0) {
            boolean isShift = GuiScreen.isShiftKeyDown();

            if (isShift && up)
                propertyToEdit = MathHelper.clamp(propertyToEdit - 1, 0, properties.size() - 1);
            else if (isShift && !up)
                propertyToEdit = MathHelper.clamp(propertyToEdit + 1, 0, properties.size() - 1);

            if (!isShift) {
                resultState = resultState.cycleProperty(properties.get(propertyToEdit));
                ItemStack temp = displayStack.copy();
                temp.setItemDamage(resultState.getBlock().getMetaFromState(resultState));

                if (checkValidModel(temp))
                    displayStack = temp.copy();
            }
        }
    }

    /**
     * Check if an ItemStack renders correctly or
     * shows the missing texture
     * @param s
     */
    private static boolean checkValidModel(ItemStack s) {
        IBakedModel bM;
        RenderItem rI = ClientProxy.mc.getRenderItem();
        bM = rI.getItemModelWithOverrides(s, null, null);

        return !bM.getParticleTexture().getIconName().equals(MISSING_NO);
    }

    @Override
    public void setDisplayStack(ItemStack displayStack) {
        Block b = Block.getBlockFromItem(displayStack.getItem());

        if (checkValidModel(displayStack))
            this.displayStack = displayStack.copy();
        else
            this.displayStack = new ItemStack(b, 1);

        Collection<IProperty<?>> coll = b.getBlockState().getProperties();
        propertyToEdit = 0;
        if (coll instanceof List)
            properties = (List<IProperty<?>>) coll;
        else
            properties = new ArrayList(coll);

        resultState = b.getDefaultState();
    }

    public void setResultState(IBlockState resultState) {
        this.resultState = resultState;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            Block b = Block.getBlockFromItem(mc.player.inventory.getItemStack().getItem());

            displayStack = mc.player.inventory.getItemStack().copy();
            propertyToEdit = 0;
            resultState = b.getDefaultState();

            if (b.equals(Blocks.AIR)) {
                properties = new ArrayList<IProperty<?>>();
            } else {
                Collection<IProperty<?>> coll = b.getBlockState().getProperties();
                if (coll instanceof List)
                    properties = (List<IProperty<?>>) coll;
                else
                    properties = new ArrayList(coll);
            }

            return true;
        }
        return false;
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        List<String> list = new ArrayList<String>();

        int xPos;
        int yPos;

        if (GuiScreen.isCtrlKeyDown() || properties.size() < 1) {
            list.addAll(stack.getTooltip(ClientProxy.mc.player, ClientProxy.getToolTipFlags()));

            for (int i = 0; i < list.size(); ++i) {
                if (i == 0) {
                    list.set(i, stack.getRarity().rarityColor + list.get(i));
                } else {
                    list.set(i, TextFormatting.GRAY + list.get(i));
                }
            }

            xPos = x;
            yPos = y;
        } else {
            list.add(stack.getDisplayName());
            FontRenderer f  = ClientProxy.mc.fontRenderer;
            int i = 0;
            int offset = f.getStringWidth(stack.getDisplayName());
            String line;

            for (IProperty<?> p : properties) {
                line = TextFormatting.AQUA + (i == propertyToEdit ? " >" : " ") + TextFormatting.GRAY + p.getName()
                        + ": " + TextFormatting.GREEN + resultState.getProperties().get(p);

                list.add(line);

                offset = f.getStringWidth(line) > offset ? f.getStringWidth(line) : offset;
                i++;
            }

            line = TextFormatting.GRAY + "" + TextFormatting.ITALIC + I18n.format("iidy.lbl.ctrlfordesc");
            offset = f.getStringWidth(line) > offset ? f.getStringWidth(line) : offset;

            xPos = x - offset - width - 5;
            yPos = y;
            list.add(line);
        }

        ScaledResolution res = new ScaledResolution(ClientProxy.mc);
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        GuiUtils.drawHoveringText(list, xPos, yPos, res.getScaledWidth(), res.getScaledHeight(), res.getScaledWidth(), (font == null ? ClientProxy.mc.fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }
}
