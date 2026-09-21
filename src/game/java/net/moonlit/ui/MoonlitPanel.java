package net.moonlit.ui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitPanel extends MoonlitComponent {

    private final List<MoonlitComponent> children = new ArrayList<>();
    private int backgroundColor = 0xFF1A1A1A;
    private int borderColor = 0xFF404040;
    private float borderWidth = 1.0f;
    private float radius = 6.0f;
    private boolean drawBackground = true;

    public MoonlitPanel(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(mc, fontRenderer, x, y, width, height);
    }

    public MoonlitPanel(Minecraft mc, FontRenderer fontRenderer) {
        super(mc, fontRenderer);
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderWidth(float width) {
        this.borderWidth = width;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setDrawBackground(boolean draw) {
        this.drawBackground = draw;
    }

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public void addChild(MoonlitComponent component) {
        children.add(component);
    }

    public void removeChild(MoonlitComponent component) {
        children.remove(component);
    }

    public List<MoonlitComponent> getChildren() {
        return new ArrayList<>(children);
    }

    public void clearChildren() {
        children.clear();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        if (drawBackground) {
            drawBorderedRoundedRect(x, y, width, height, backgroundColor, borderColor, borderWidth, radius);
        }

        for (MoonlitComponent child : children) {
            child.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void tick() {
        super.tick();
        for (MoonlitComponent child : children) {
            child.tick();
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        super.onMousePressed(mouseX, mouseY, mouseButton);
        for (int i = children.size() - 1; i >= 0; i--) {
            MoonlitComponent child = children.get(i);
            if (child.isVisible() && child.contains(mouseX, mouseY)) {
                child.onMousePressed(mouseX, mouseY, mouseButton);
                break;
            }
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.onMouseClicked(mouseX, mouseY, mouseButton);
        for (int i = children.size() - 1; i >= 0; i--) {
            MoonlitComponent child = children.get(i);
            if (child.isVisible() && child.contains(mouseX, mouseY)) {
                child.onMouseClicked(mouseX, mouseY, mouseButton);
                break;
            }
        }
    }

    @Override
    public void onMouseDrag(int mouseX, int mouseY, int mouseButton) {
        super.onMouseDrag(mouseX, mouseY, mouseButton);
        for (MoonlitComponent child : children) {
            if (child.isVisible() && child.contains(mouseX, mouseY)) {
                child.onMouseDrag(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x &&
                mouseY >= y &&
                mouseX < x + width &&
                mouseY < y + height;
    }
}
