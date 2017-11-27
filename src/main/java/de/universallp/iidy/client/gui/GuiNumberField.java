package de.universallp.iidy.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * Created by universal on 29.11.2016 16:33.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiNumberField extends GuiTextField {

    private static final String numbers = "0123456789";

    private int max = 255;

    public GuiNumberField(int componentId, FontRenderer fontRenderer, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontRenderer, x, y, par5Width, par6Height);
        setText(String.valueOf(0));
        setMaxStringLength(6);
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (isInteger(getText().concat(String.valueOf(typedChar))) || keyCode == 14)
            return super.textboxKeyTyped(typedChar, keyCode);
        else
            return false;
    }

    @Override
    public void setFocused(boolean isFocusedIn) {
        super.setFocused(isFocusedIn);
        if (!isFocusedIn) {
            if (!isInteger(getText()) || Integer.parseInt(getText()) > max)
                setText(String.valueOf(0));
        }
    }

    @Override
    public void drawTextBox() {
        super.drawTextBox();

        if (!isInteger(getText()) || Integer.parseInt(getText()) > max) {
            drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y, 0xFFFF0000);
            drawRect(this.x - 1, this.y + this.height, this.x + this.width + 1, this.y + this.height + 1, 0xFFFF0000);
            drawRect(this.x - 1, this.y - 1, this.x, this.y + this.height, 0xFFFF0000);
            drawRect(this.x + this.width, this.y, this.x + this.width + 1, this.y + this.height, 0xFFFF0000);
        }
    }

    public int getValue() {
        if (isInteger(getText()))
            return Integer.parseInt(getText());
        return 0;
    }

    public GuiNumberField setMaximum(int i) {
        this.max = i;
        return this;
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
