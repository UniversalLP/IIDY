package de.universallp.iidy.client;

import de.universallp.iidy.core.handler.EventHandlers;
import de.universallp.iidy.core.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Created by universal on 28.11.2016 15:48.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class ClientProxy extends CommonProxy {

    public static Minecraft mc;

    // Obfuscation
    public static final String[] GUI_LEFT = new String[] { "i", "field_147003_i", "guiLeft" };
    public static final String[] GUI_TOP = new String[] { "r", "field_147009_r", "guiTop" };

    public static KeyBinding KEY_MAKE_TASK = new KeyBinding("iidy.key.maketask", Keyboard.KEY_C, "key.categories.gameplay");

    public static ITooltipFlag getToolTipFlags() {
        return ClientProxy.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
    }

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ClientRegistry.registerKeyBinding(KEY_MAKE_TASK);
        MinecraftForge.EVENT_BUS.register(new EventHandlers());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        mc = Minecraft.getMinecraft();
    }

    public static int getGuiLeft(GuiContainer g) {
        return ReflectionHelper.getPrivateValue(GuiContainer.class, g, GUI_LEFT);
    }

    public static int getGuiTop(GuiContainer g) {
        return ReflectionHelper.getPrivateValue(GuiContainer.class, g, GUI_TOP);
    }

    public static int getMouseX() {
        ScaledResolution resolution = new ScaledResolution(ClientProxy.mc);
        int mX = Mouse.getX() * resolution.getScaledWidth() / ClientProxy.mc.displayWidth;
        return mX + 1;
    }

    public static int getMouseY() {
        ScaledResolution resolution = new ScaledResolution(ClientProxy.mc);
        int mY = resolution.getScaledHeight() - Mouse.getY() * resolution.getScaledHeight() / ClientProxy.mc.displayHeight - 1;
        return mY;
    }
}
