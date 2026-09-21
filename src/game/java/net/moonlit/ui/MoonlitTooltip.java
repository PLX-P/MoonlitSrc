package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitTooltip extends MoonlitComponent {

    private String text;
    private int displayDuration = 2000;
    private long startTime = 0;
    private boolean isVisible = false;

    public MoonlitTooltip(Minecraft mc, FontRenderer fontRenderer) {
        super(mc, fontRenderer);
        this.width = 200;
        this.height = 30;
    }

    public void show(String text, int x, int y) {
        this.text = text;
        setPosition(x, y);
        startTime = System.currentTimeMillis();
        isVisible = true;
        visible = true;
    }

    public void hide() {
        isVisible = false;
        visible = false;
    }

    public boolean isShown() {
        return isVisible && visible;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!isVisible || !visible) return;

        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed > displayDuration) {
            visible = false;
            isVisible = false;
            return;
        }

        float alpha = Math.min(1.0f, (float) elapsed / 100.0f);

        int bgColor = 0xFF1A1A1ACC;
        int borderColor = 0xFF444444AA;

        if (alpha < 1.0f) {
            borderColor = (borderColor & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            bgColor = (bgColor & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
        }

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        if (text != null && fontRenderer != null) {
            fontRenderer.drawString(text, x + 12, y + (height - 8) / 2, 0xFFCCCCCC);
        }

        if (contains(mouseX, mouseY) && hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY)) {
            hide();
        }
    }

    public void update() {
        if (isVisible && visible && System.currentTimeMillis() - startTime > displayDuration) {
            visible = false;
            isVisible = false;
        }
    }

    public void setDisplayDuration(int duration) {
        this.displayDuration = duration;
    }

    public int getDisplayDuration() {
        return displayDuration;
    }
}
