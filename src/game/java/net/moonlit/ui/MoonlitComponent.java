package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class MoonlitComponent {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean hovered = false;
    protected boolean focused = false;
    protected boolean dragged = false;

    protected Minecraft mc;
    protected FontRenderer fontRenderer;

    public MoonlitComponent(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height) {
        this.mc = mc;
        this.fontRenderer = fontRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public MoonlitComponent(Minecraft mc, FontRenderer fontRenderer) {
        this.mc = mc;
        this.fontRenderer = fontRenderer;
        this.x = 0;
        this.y = 0;
        this.width = 100;
        this.height = 20;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isFocused() {
        return focused;
    }

    public boolean isDragged() {
        return dragged;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x &&
                mouseY >= y &&
                mouseX < x + width &&
                mouseY < y + height;
    }

    public void updateHover(int mouseX, int mouseY) {
        hovered = contains(mouseX, mouseY) && enabled && visible;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        updateHover(mouseX, mouseY);
    }

    public void tick() {}

    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {}

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {}

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {}

    public void onMouseDrag(int mouseX, int mouseY, int mouseButton) {}

    public void onFocusGained() {}

    public void onFocusLost() {}

    public void onTextInput(char character, int codePoint) {}

    public void setMinWidth(int minWidth) {}

    public void setMaxWidth(int maxWidth) {}

    public int getMinWidth() {
        return 0;
    }

    public int getMaxWidth() {
        return Integer.MAX_VALUE;
    }

    public void setMinHeight(int minHeight) {}

    public void setMaxHeight(int maxHeight) {}

    public int getMinHeight() {
        return 0;
    }

    public int getMaxHeight() {
        return Integer.MAX_VALUE;
    }

    public void setSnapToGrid(boolean snap) {}

    public void setSnapSize(int snapSize) {}

    public boolean isSnapToGrid() {
        return false;
    }

    public int getSnapSize() {
        return 10;
    }

    public void snapToGrid() {}

    public void drawRoundedRect(int x, int y, int w, int h, int color, float radius) {
        net.moonlit.render.MoonlitRenderer.drawRoundedRect(x, y, w, h, radius, color);
    }

    public void drawBorderedRoundedRect(int x, int y, int w, int h, int color, int borderColor, float borderWidth, float radius) {
        net.moonlit.render.MoonlitRenderer.drawBorderedRoundedRect(x, y, w, h, radius, color, borderWidth, borderColor);
    }

    public int getDefaultWidth() {
        return width;
    }

    public int getDefaultHeight() {
        return height;
    }

    public void resetToDefaults() {
        width = getDefaultWidth();
        height = getDefaultHeight();
    }
}
