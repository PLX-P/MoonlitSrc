package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitCheckbox extends MoonlitComponent {

    private boolean isChecked = false;
    private String label;
    private Runnable<Boolean> onChange;

    public MoonlitCheckbox(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String label) {
        super(mc, fontRenderer, x, y, width, height);
        this.label = label;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        boolean changed = this.isChecked != checked;
        this.isChecked = checked;
        if (onChange != null && changed) {
            onChange.run(checked);
        }
    }

    public void setOnChange(Runnable<Boolean> onChange) {
        this.onChange = onChange;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        hovered = contains(mouseX, mouseY) && enabled;

        int bgColor = hovered && enabled ? 0xFF333333 : 0xFF222222;
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF444444;
        int checkColor = 0xFF8888FF;

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        int checkSize = 12;
        int checkX = x + 2;
        int checkY = y + (height - checkSize) / 2;

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(checkX, checkY, checkSize, checkSize, checkColor, 3.0f);

        if (isChecked) {
            net.moonlit.render.MoonlitRenderer.drawRoundedRect(checkX + 1, checkY + 1, checkSize - 2, checkSize - 2, 0xFF8888FF, 2.0f);
        }

        if (label != null && fontRenderer != null) {
            int labelX = x + checkSize + 8;
            int labelY = y + (height - 8) / 2;

            fontRenderer.drawString(label, labelX, labelY, hovered && enabled ? 0xFFCCCCCC : 0xFFDDDDDD);
        }

        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setChecked(!isChecked);
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setChecked(!isChecked);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }
}
