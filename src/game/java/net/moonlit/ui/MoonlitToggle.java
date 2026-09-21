package net.moonlit.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitToggle extends MoonlitComponent {

    private boolean isToggled = false;
    private boolean isHovered = false;
    private String label;
    private Runnable<Boolean> onChange;

    public MoonlitToggle(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String label) {
        super(mc, fontRenderer, x, y, width, height);
        this.label = label;
        if (width == 100) {
            this.width = 100;
        }
    }

    public boolean isToggled() {
        return isToggled;
    }

    public void setToggled(boolean toggled) {
        boolean changed = this.isToggled != toggled;
        this.isToggled = toggled;
        if (onChange != null && changed) {
            onChange.run(toggled);
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

        if (isToggled) {
            borderColor = 0xFF8888FF;
            bgColor = 0xFF222244;
        }

        int toggleX = x;
        int toggleY = y + (height - 16) / 2;
        int toggleSize = 12;

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);
        drawBorderedRoundedRect(toggleX, toggleY, toggleSize, toggleSize, 0xFF2A2A2A, 0xFF444444, 1.0f, 3.0f);

        if (isToggled) {
            int toggleOffset = width - toggleSize - 4;
            drawBorderedRoundedRect(toggleX + toggleOffset, toggleY, toggleSize, toggleSize, 0xFF8888FF, 0xFFAAAAFF, 1.0f, 3.0f);
        }

        if (label != null && fontRenderer != null) {
            int labelWidth = fontRenderer.getStringWidth(label);
            int labelX = x + 16 + 4;
            int labelY = y + (height - 8) / 2;

            fontRenderer.drawString(label, labelX, labelY, hovered && enabled ? 0xFFCCCCCC : 0xFFDDDDDD);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setToggled(!isToggled);
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setToggled(!isToggled);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (hovered && enabled) {
            net.lax1dude.eaglercraft.Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }
}
