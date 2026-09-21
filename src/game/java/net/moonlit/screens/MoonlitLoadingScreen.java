package net.moonlit.screens;

import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.Guiscreen;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitLoadingScreen extends Guiscreen {

    private static final String CLIENT_NAME = "Moonlit Client";
    private static final String VERSION = "v0.1";

    private int progress = 0;
    private int totalSteps = 100;
    private int currentStep = 0;
    private String statusMessage = "Initializing...";
    private boolean showLogoAnimation = true;

    private float logoRotation = 0.0f;
    private long lastUpdateTime = 0;
    private int starFieldOffset = 0;

    public MoonlitLoadingScreen() {
        super(null);
        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        progress = 0;
        currentStep = 0;
        statusMessage = "Initializing...";
        showLogoAnimation = true;
        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        int centerX = width / 2;
        int centerY = height / 2;

        drawBackgroundGradient();

        drawStarField(partialTicks);

        if (showLogoAnimation) {
            drawMoonlitLogo(centerX, centerY - 50, partialTicks);
        }

        if (fontRenderer != null) {
            int titleY = centerY + 80;
            int titleX = centerX - fontRenderer.getStringWidth(CLIENT_NAME) / 2;

            fontRenderer.drawString(CLIENT_NAME, titleX, titleY, 0xFFCCCCFF);

            int versionY = titleY - 18;
            int versionX = centerX - fontRenderer.getStringWidth(VERSION) / 2;

            fontRenderer.drawString(VERSION, versionX, versionY, 0xFF888888);
        }

        drawProgressBar(centerX, centerY + 110);

        if (fontRenderer != null) {
            int statusY = centerY + 145;
            int statusX = centerX - fontRenderer.getStringWidth(statusMessage) / 2;

            fontRenderer.drawString(statusMessage, statusX, statusY, 0xFFAAAAAA);
        }

        GlStateManager.disableBlend();

        handleMouseInput(mouseX, mouseY);
    }

    private void drawBackgroundGradient() {
        float[] bg = getBackgroundColors();

        GlStateManager.color(bg[0], bg[1], bg[2], 1.0F);
        drawGradientRect(0, 0, width, height, bg[0], bg[1], bg[2], bg[3], bg[4], bg[5], bg[6], bg[7]);
    }

    private float[] getBackgroundColors() {
        float r1 = 0.05F;
        float g1 = 0.02F;
        float b1 = 0.10F;
        float a1 = 1.0F;
        float r2 = 0.02F;
        float g2 = 0.01F;
        float b2 = 0.05F;
        float a2 = 1.0F;
        return new float[] { r1, g1, b1, a1, r2, g2, b2, a2 };
    }

    private void drawGradientRect(int x, int y, int w, int h, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        wr.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        wr.pos((double) x, (double) y, 0.0D).color(r1, g1, b1, a1).endVertex();
        wr.pos((double) (x + w), (double) y, 0.0D).color(r2, g2, b2, a2).endVertex();
        wr.pos((double) (x + w), (double) (y + h), 0.0D).color(r2, g2, b2, a2).endVertex();
        wr.pos((double) x, (double) (y + h), 0.0D).color(r1, g1, b1, a1).endVertex();

        tess.draw();
    }

    private void drawStarField(float partialTicks) {
        int starCount = 50;
        int centerX = width / 2;
        int centerY = height / 2;

        float time = (System.currentTimeMillis() - lastUpdateTime) / 1000.0F;
        lastUpdateTime = System.currentTimeMillis();
        starFieldOffset = (int) (time * 10) % 100;

        for (int i = 0; i < starCount; i++) {
            int seed = i * 7919;
            int x = (seed % width);
            int y = ((seed >> 4) % height);
            int brightness = 100 + ((seed >> 8) % 100);

            float alpha = (float) brightness / 255.0F;
            int color = (int) (alpha * 255.0F) << 24 | 0xFFFFFF;

            drawPixel(x + starFieldOffset, y, color, 1);
            drawPixel(x - starFieldOffset, y, color, 1);
        }
    }

    private void drawPixel(int x, int y, int color, int size) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = (color >> 24 & 0xFF) / 255.0F;

        wr.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        wr.pos((double) x, (double) y, 0.0D).color(r, g, b, a).endVertex();
        wr.pos((double) (x + size), (double) y, 0.0D).color(r, g, b, a).endVertex();
        wr.pos((double) (x + size), (double) (y + size), 0.0D).color(r, g, b, a).endVertex();
        wr.pos((double) x, (double) (y + size), 0.0D).color(r, g, b, a).endVertex();

        tess.draw();
    }

    private void drawMoonlitLogo(int centerX, int centerY, float partialTicks) {
        int logoSize = 120;
        int logoX = centerX - logoSize / 2;
        int logoY = centerY - logoSize / 2 - 20;

        float pulse = Math.min(1.0F, (float) (System.currentTimeMillis() % 2000) / 1000.0F);
        float scale = 1.0F + (pulse * 0.05F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX, centerY - 20, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-centerX, -(centerY - 20), 0.0F);

        drawCrescentMoon(logoX, logoY, logoSize, logoSize);

        GlStateManager.popMatrix();
    }

    private void drawCrescentMoon(int x, int y, int w, int h) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        int moonColor = 0xFFCCCCFF;

        drawRoundedRect(x, y, w, h, moonColor, 0.0F, 0.0F, w / 2.0F);
        drawRoundedRect(x + w / 4, y - h / 4, w / 2, h, 0xFF1A1A2E, 0.0F, 0.0F, h / 2.0F);

        GlStateManager.disableBlend();
    }

    private void drawRoundedRect(int x, int y, int w, int h, int color, float r, float g, float b) {
        int rr = (color >> 16 & 0xFF) / 255.0F;
        int gg = (color >> 8 & 0xFF) / 255.0F;
        int bb = (color & 0xFF) / 255.0F;
        int aa = (color >> 24 & 0xFF) / 255.0F;

        drawGradientRect(x, y, w, h, rr, gg, bb, aa, rr, gg, bb, aa);
    }

    private void drawProgressBar(int centerX, int centerY) {
        int barWidth = 300;
        int barHeight = 6;
        int barX = centerX - barWidth / 2;
        int barY = centerY;

        int progressPercent = (progress * 100) / totalSteps;
        int fillWidth = (int) ((float) progressPercent / 100.0F * barWidth);

        int bgColor = 0xFF333333;
        int fillColor = 0xFF8888FF;
        int borderColor = 0xFF666666;

        drawRoundedRect(barX, barY, barWidth, barHeight, bgColor, 3.0F, 4.0F);
        drawRoundedRect(barX, barY, fillWidth, barHeight, fillColor, 3.0F, 4.0F);

        drawRoundedRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2, borderColor, 0.0F, 0.0F, 0.0F);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mc != null) {
            showCursor(false);
        }
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        int mouseButton = Mouse.getButton();

        if (mouseButton == 0) {
            handleMouseClick(mouseX, mouseY, 0);
        }
    }

    @Override
    public void handleKeyboardInput() {
        if (mc != null) {
            int keyCode = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();

            if (keyCode == 0) {
                handleKeyPress(0, (char) 0);
            }
        }
    }

    @Override
    protected void keyPressed(int keyCode, char typedChar) throws IOException {
        super.keyPressed(keyCode, typedChar);
    }

    @Override
    public void setProgressBar(int p_110353_1_) {
        progress = p_110353_1_;
        currentStep = p_110353_1_;
    }

    @Override
    public void setProgressMax(int p_110354_1_) {
        totalSteps = p_110354_1_;
    }

    public void setStatusMessage(String message) {
        this.statusMessage = message;
    }

    public void advanceStep() {
        if (currentStep < totalSteps) {
            currentStep++;
            progress = currentStep;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (currentStep < totalSteps) {
            currentStep++;
            progress = currentStep;
        }

        starFieldOffset += 1;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
