package de.universallp.iidy.client.gui.elements;

import de.universallp.iidy.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

/**
 * Created by universal on 17.12.2016 13:21.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiScrollBar extends GuiScreen {

    private int x, y, h, w;
    private float scrollPercent;
    private boolean hasFocus;
    private boolean isVisible;
    private static final int scrollH = 20; // height of the button

    public GuiScrollBar(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.w = 12;
        this.h = height;
        this.isVisible = true;
        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
    }

    public boolean isHovered() {
        return hasFocus;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public float getScrollPercent() {
        return scrollPercent;
    }

    public void setDimensions(int x, int y, int h) {
        this.x = x;
        this.y = y;
        this.h = h;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (Mouse.isButtonDown(0)) {
            if (isHovered()) {
                int scroll = Math.max(ClientProxy.getMouseY() - y - (scrollH / 2), 0);
                scrollPercent = (float)scroll / h;
            }
        }
    }

    public void setScrollPercent(float scrollPercent) {
        this.scrollPercent = Math.min(scrollPercent, 1.0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (isVisible) {
            hasFocus = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
            super.drawScreen(mouseX, mouseY, partialTicks);

            drawGradientRect(x + 1, y + 1, x + w - 1, y + h - 1, 0x55000000, 0x55000000);
            // Border
            drawGradientRect(x,         y,         x + 1,     y + h, 0x88000000, 0x88000000);
            drawGradientRect(x + w - 1, y,         x + w,     y + h, 0x88000000, 0x88000000);
            drawGradientRect(x + 1,     y,         x + w - 1, y + 1, 0x88000000, 0x88000000);
            drawGradientRect(x + 1,     y + h - 1, x + w - 1, y + h, 0x88000000, 0x88000000);

            // Scroll button
            int scrollY = Math.min((int) ((h - 2) * scrollPercent), h - scrollH - 2);

            drawGradientRect(x + 1,     y + 1 + scrollY, x + w - 1, y + 1 + scrollY + scrollH, hasFocus ? 0x66000000 : 0x22000000, hasFocus ? 0x66000000 : 0x22000000);

        }
    }
}
