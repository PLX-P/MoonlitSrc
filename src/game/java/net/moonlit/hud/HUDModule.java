package net.moonlit.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class HUDModule {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected boolean visible = true;
    protected boolean enabled = true;
    protected int color = 0xFFCCCCCC;
    protected float alpha = 1.0f;
    protected float scale = 1.0f;
    protected boolean selected = false;
    protected String name = "";

    public HUDModule(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public HUDModule() {
        this(0, 0, 100, 20);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
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

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void resetPosition() {
        this.x = 0;
        this.y = 0;
    }

    public void update() {
        // To be overridden
    }

    public abstract void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY);

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Default implementation
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return "Unknown";
    }

    public int getDefaultWidth() {
        return width;
    }

    public int getDefaultHeight() {
        return height;
    }

    public void resetToDefaults() {
        this.width = getDefaultWidth();
        this.height = getDefaultHeight();
        this.x = 0;
        this.y = 0;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getRenderX() {
        return (int) (x * scale);
    }

    public int getRenderY() {
        return (int) (y * scale);
    }

    public int getRenderWidth() {
        return (int) (width * scale);
    }

    public int getRenderHeight() {
        return (int) (height * scale);
    }

    public abstract int getModuleType();

    protected void drawBackground(int x, int y, int w, int h, int color, FontRenderer fontRenderer) {
        drawRoundedRect(x, y, w, h, color, fontRenderer, 4.0f);
    }

    protected void drawBorder(int x, int y, int w, int h, int color, FontRenderer fontRenderer) {
        int borderWidth = 1;
        int borderColor = 0xFF444444;

        drawBorderRect(x, y, w, h, borderColor, fontRenderer, borderWidth);
    }

    protected void drawText(String text, int x, int y, int color, FontRenderer fontRenderer) {
        fontRenderer.drawString(text, x, y, color);
    }

    protected void drawCenteredText(String text, int x, int y, int color, FontRenderer fontRenderer) {
        fontRenderer.drawCenteredString(text, x, y, color);
    }

    protected void drawTextWithShadow(String text, int x, int y, int color, FontRenderer fontRenderer) {
        fontRenderer.drawString(text, x, y, color, true);
    }

    protected void drawCenteredTextWithShadow(String text, int x, int y, int color, FontRenderer fontRenderer) {
        fontRenderer.drawCenteredString(text, x, y, color, true);
    }

    protected void drawIcon(int icon, int x, int y, int color, FontRenderer fontRenderer) {
        // Placeholder for icon rendering
    }

    protected void drawBar(int x, int y, int w, int h, float percent, int bgColor, int fillColor, FontRenderer fontRenderer) {
        drawBackground(x, y, w, h, bgColor, fontRenderer);
        int fillW = (int) (w * percent);
        drawBackground(x, y, fillW, h, fillColor, fontRenderer);
    }

    protected void drawRoundedRect(int x, int y, int w, int h, int color, FontRenderer fontRenderer, float radius) {
        int colorR = (color >> 16 & 0xFF) / 255.0F;
        int colorG = (color >> 8 & 0xFF) / 255.0F;
        int colorB = (color & 0xFF) / 255.0F;
        int colorA = (color >> 24 & 0xFF) / 255.0F * alpha;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        worldrenderer.pos(x, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    protected void drawBorderRect(int x, int y, int w, int h, int color, FontRenderer fontRenderer, int borderWidth) {
        int colorR = (color >> 16 & 0xFF) / 255.0F;
        int colorG = (color >> 8 & 0xFF) / 255.0F;
        int colorB = (color & 0xFF) / 255.0F;
        int colorA = (color >> 24 & 0xFF) / 255.0F * alpha;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        // Top
        worldrenderer.pos(x, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y + borderWidth, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x, y + borderWidth, 0).color(colorR, colorG, colorB, colorA).endVertex();

        // Bottom
        worldrenderer.pos(x, y + h - borderWidth, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y + h - borderWidth, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();

        // Left
        worldrenderer.pos(x, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + borderWidth, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + borderWidth, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();

        // Right
        worldrenderer.pos(x + w - borderWidth, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w - borderWidth, y + h, 0).color(colorR, colorG, colorB, colorA).endVertex();

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    protected void drawScaledBackground(int x, int y, int w, int h, int color, FontRenderer fontRenderer, float scaleFactor) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);
        int scaledW = (int) (w * scaleFactor);
        int scaledH = (int) (h * scaleFactor);

        drawRoundedRect(scaledX, scaledY, scaledW, scaledH, color, fontRenderer, 4.0f);
    }

    protected void drawScaledBorder(int x, int y, int w, int h, int color, FontRenderer fontRenderer, float scaleFactor, int borderWidth) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);
        int scaledW = (int) (w * scaleFactor);
        int scaledH = (int) (h * scaleFactor);

        drawBorderRect(scaledX, scaledY, scaledW, scaledH, color, fontRenderer, borderWidth);
    }

    protected void drawScaledText(String text, int x, int y, int color, FontRenderer fontRenderer, float scaleFactor) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);

        fontRenderer.drawString(text, scaledX, scaledY, color);
    }

    protected void drawScaledCenteredText(String text, int x, int y, int color, FontRenderer fontRenderer, float scaleFactor) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);

        fontRenderer.drawCenteredString(text, scaledX, scaledY, color);
    }

    protected void drawScaledTextWithShadow(String text, int x, int y, int color, FontRenderer fontRenderer, float scaleFactor) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);

        fontRenderer.drawString(text, scaledX, scaledY, color, true);
    }

    protected void drawScaledCenteredTextWithShadow(String text, int x, int y, int color, FontRenderer fontRenderer, float scaleFactor) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);

        fontRenderer.drawCenteredString(text, scaledX, scaledY, color, true);
    }

    protected void drawScaledBar(int x, int y, int w, int h, float percent, int bgColor, int fillColor, FontRenderer fontRenderer, float scaleFactor) {
        int scaledX = (int) (x * scaleFactor);
        int scaledY = (int) (y * scaleFactor);
        int scaledW = (int) (w * scaleFactor);
        int scaledH = (int) (h * scaleFactor);

        drawBackground(scaledX, scaledY, scaledW, scaledH, bgColor, fontRenderer);
        int fillW = (int) (scaledW * percent);
        drawBackground(scaledX, scaledY, fillW, scaledH, fillColor, fontRenderer);
    }
}
