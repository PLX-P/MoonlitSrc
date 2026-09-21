package net.moonlit.ui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitTab extends MoonlitComponent {

    private String text;
    private boolean selected = false;
    private Runnable<Boolean> onSelect;

    public MoonlitTab(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String text) {
        super(mc, fontRenderer, x, y, width, height);
        this.text = text;
        this.width = fontRenderer != null ? fontRenderer.getStringWidth(text) + 24 : 100;
        this.height = height;
    }

    public MoonlitTab(Minecraft mc, FontRenderer fontRenderer, String text) {
        super(mc, fontRenderer);
        this.text = text;
        this.width = fontRenderer != null ? fontRenderer.getStringWidth(text) + 24 : 100;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (fontRenderer != null) {
            int textWidth = fontRenderer.getStringWidth(text);
            if (textWidth + 24 > this.width) {
                this.width = textWidth + 24;
            }
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean changed = this.selected != selected;
        this.selected = selected;
        if (onSelect != null && changed) {
            onSelect.run(selected);
        }
    }

    public void setOnSelect(Runnable<Boolean> onSelect) {
        this.onSelect = onSelect;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        hovered = contains(mouseX, mouseY) && enabled;

        int bgColor = hovered && enabled ? 0xFF383838 : 0xFF222222;
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF505050;
        int textColor = hovered && enabled ? 0xFFCCCCCC : 0xFFDDDDDD;

        if (selected) {
            bgColor = 0xFF333333;
            borderColor = 0xFF8888FF;
            textColor = 0xFFCCCCFF;
        }

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        if (selected) {
            net.moonlit.render.MoonlitRenderer.drawRoundedRect(x + 4, y + height - 3, width - 8, 3, 0xFF8888FF, 1.5f);
        }

        if (text != null && fontRenderer != null) {
            int textX = x + (width - fontRenderer.getStringWidth(text)) / 2;
            int textY = y + (height - 8) / 2;

            fontRenderer.drawString(text, textX, textY, textColor);
        }

        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setSelected(!selected);
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setSelected(!selected);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    @Override
    public int getDefaultWidth() {
        if (fontRenderer != null && text != null) {
            return fontRenderer.getStringWidth(text) + 24;
        }
        return 100;
    }
}
