package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitButton extends MoonlitComponent {

    private String text;
    private Runnable onClick;

    public MoonlitButton(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String text) {
        super(mc, fontRenderer, x, y, width, height);
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        } else if (!Mouse.isCursorVisible()) {
            // no-op
        }

        int backgroundColor = hovered && enabled ? 0xFF333333 : 0xFF222222;
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF444444;

        drawBorderedRoundedRect(x, y, width, height, backgroundColor, borderColor, 1.0f, 4.0f);

        if (text != null && fontRenderer != null) {
            int textWidth = fontRenderer.getStringWidth(text);
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height - 8) / 2;

            int textColor = enabled ? (hovered ? 0xFFCCCCCC : 0xFFDDDDDD) : 0xFF666666;

            fontRenderer.drawString(text, textX, textY, textColor);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            if (onClick != null) {
                onClick.run();
            }
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            if (onClick != null) {
                onClick.run();
            }
        }
    }
}
