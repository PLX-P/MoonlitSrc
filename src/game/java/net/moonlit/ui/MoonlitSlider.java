package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitSlider extends MoonlitComponent {

    private float value;
    private float minValue;
    private float maxValue;
    private boolean dragging;
    private String label;
    private boolean showValue = true;

    public MoonlitSlider(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String label, float min, float max, float start) {
        super(mc, fontRenderer, x, y, width, height);
        this.label = label;
        this.minValue = min;
        this.maxValue = max;
        this.value = start;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }

    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    public void setMinValue(float min) {
        this.minValue = min;
    }

    public void setMaxValue(float max) {
        this.maxValue = max;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setShowValue(boolean show) {
        this.showValue = show;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        hovered = contains(mouseX, mouseY) && enabled;

        int bgColor = hovered && enabled ? 0xFF383838 : 0xFF1E1E1E;
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF505050;
        int fillColor = hovered && enabled ? 0xFF8888FF : 0xFF5555FF;

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        int sliderY = y + (height - 4) / 2;
        int sliderWidth = width - 12;
        int fillWidth = (int) (sliderWidth * (value - minValue) / (maxValue - minValue));

        if (fillWidth > 0) {
            net.moonlit.render.MoonlitRenderer.drawRoundedRect(x + 6, sliderY, fillWidth, 4, fillColor, 2.0f);
        }

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(x + 6, sliderY, sliderWidth, 4, 0xFF333333, 2.0f);
        net.moonlit.render.MoonlitRenderer.drawRoundedRect(x + 6 + fillWidth - 2, sliderY, 4, 4, fillColor, 2.0f);

        if (label != null && fontRenderer != null) {
            int labelWidth = fontRenderer.getStringWidth(label);
            int labelX = x + 8;
            int labelY = y + (height - 8) / 2;

            fontRenderer.drawString(label, labelX, labelY, hovered && enabled ? 0xFFCCCCCC : 0xFFAAAAAA);
        }

        if (showValue && fontRenderer != null) {
            String valueText = String.format("%.1f", value);
            int valueWidth = fontRenderer.getStringWidth(valueText);
            int valueX = x + width - valueWidth - 8;
            int valueY = y + (height - 8) / 2;

            fontRenderer.drawString(valueText, valueX, valueY, hovered && enabled ? 0xFFCCCCCC : 0xFFAAAAAA);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            dragging = true;
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    public void onMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (dragging && enabled) {
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    private void updateValueFromMouse(int mouseX) {
        int sliderX = x + 6;
        int sliderWidth = width - 12;

        float ratio = (float) (mouseX - sliderX) / sliderWidth;
        ratio = Math.max(0, Math.min(1, ratio));

        value = minValue + ratio * (maxValue - minValue);
    }

    @Override
    public void tick() {
        super.tick();
        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }
}
