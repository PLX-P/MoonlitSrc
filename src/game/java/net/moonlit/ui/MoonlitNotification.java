package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitNotification extends MoonlitComponent {

    private String title;
    private String message;
    private long displayTime = 3000;
    private long startTime = 0;
    private String type = "INFO";
    private int titleColor = 0xFFCCCCCC;
    private int messageColor = 0xFFDDDDDD;

    public MoonlitNotification(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String title, String message, String type) {
        super(mc, fontRenderer, x, y, width, height);
        this.title = title;
        this.message = message;
        this.type = type;
        setTypeColors(type);
        startTime = System.currentTimeMillis();
    }

    private void setTypeColors(String type) {
        switch (type) {
            case "SUCCESS":
                titleColor = 0xFF88FF88;
                messageColor = 0xFFAAFFAA;
                break;
            case "ERROR":
                titleColor = 0xFFFF4444;
                messageColor = 0xFFFF8888;
                break;
            case "WARNING":
                titleColor = 0xFFFFAA44;
                messageColor = 0xFFFFCC88;
                break;
            case "INFO":
            default:
                titleColor = 0xFF8888FF;
                messageColor = 0xFFAAAAFF;
                break;
        }
    }

    public void setDisplayTime(long ms) {
        this.displayTime = ms;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        long elapsed = System.currentTimeMillis() - startTime;
        float alpha = Math.min(1.0f, (float) (elapsed) / 100.0f);

        if (alpha >= 1.0f && elapsed >= displayTime) {
            alpha = Math.max(0.0f, (float) (displayTime - elapsed) / 200.0f);
            if (alpha <= 0.0f) {
                visible = false;
                return;
            }
        }

        int bgColor = 0xFF1A1A1ACC;
        int borderColor = 0xFF444444AA;

        if (alpha < 1.0f) {
            borderColor = (borderColor & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            bgColor = (bgColor & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
        }

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 6.0f);

        if (title != null && fontRenderer != null) {
            fontRenderer.drawString(title, x + 12, y + 8, titleColor);
        }

        if (message != null && fontRenderer != null) {
            fontRenderer.drawString(message, x + 12, y + 22, messageColor);
        }

        if (type != null && fontRenderer != null) {
            int typeWidth = fontRenderer.getStringWidth(type);
            fontRenderer.drawString(type, x + width - typeWidth - 12, y + 8, titleColor);
        }

        int closeX = x + width - 20;
        int closeY = y + 4;
        int closeSize = 14;

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(closeX, closeY, closeSize, closeSize, 0xFF333333, 3.0f);
        fontRenderer.drawString("✕", closeX + 4, closeY + 2, 0xFF888888);

        if (contains(mouseX, mouseY) && hovered && enabled) {
            if (mouseX >= closeX && mouseX < closeX + closeSize && mouseY >= closeY && mouseY < closeY + closeSize) {
                Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
            }
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        int closeX = x + width - 20;
        int closeY = y + 4;
        int closeSize = 14;

        if (mouseX >= closeX && mouseX < closeX + closeSize && mouseY >= closeY && mouseY < closeY + closeSize) {
            visible = false;
        }
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > displayTime;
    }

    public void update() {
        if (isExpired()) {
            visible = false;
        }
    }
}
