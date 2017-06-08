package de.universallp.iidy.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * Created by universal on 15.12.2016 21:02.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class GuiButtonCycle extends GuiButton {

    private String[] options;
    private int index = 0;

    public GuiButtonCycle(int buttonId, int x, int y) {
        super(buttonId, x, y, 20, 20, "");
    }

    public GuiButtonCycle setOptions(String ... s) {
        this.options = s;
        this.displayString = options[0];
        return this;
    }

    public GuiButtonCycle setIndex(int i) {
        if (i >= 0 && i < options.length) {
            index = i;
            this.displayString = options[index];
        }
        return this;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        //super.drawButton(mc, mouseX, mouseY);
        this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 16777120);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean flag = super.mousePressed(mc, mouseX, mouseY);
        if (flag) {
            index++;
            if (index >= options.length)
                index = 0;
            this.displayString = options[index];
        }

        return flag;
    }

    public int getIndex() {
        return index;
    }
}
