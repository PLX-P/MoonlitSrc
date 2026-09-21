package net.moonlit.render;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL.*;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.IVertexArrayGL;
import net.lax1dude.eaglercraft.internal.IBufferGL;
import net.lax1dude.eaglercraft.internal.IProgramGL;
import net.lax1dude.eaglercraft.internal.ITextureGL;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public class MoonlitRenderer {

    private static MoonlitShaderProgram uiShader;
    private static boolean initialized;

    private static final int FLOAT_SIZE = 4;
    private static final int VERTEX_STRIDE =
            2 * FLOAT_SIZE + // position
            4 * FLOAT_SIZE + // color
            2 * FLOAT_SIZE;  // tex coords

    public static void init() {
        if (initialized) return;
        uiShader = new MoonlitShaderProgram();
        initialized = true;
    }

    public static void shutdown() {
        if (uiShader != null) {
            uiShader.destroy();
            uiShader = null;
        }
        initialized = false;
    }

    public static void enableBlend() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void begin() {
        enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.alphaFunc(GL_GREATER, 0.0039215689f);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static void end() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawGradientRoundedRect(float x, float y, float w, float h,
                                               float r, float g, float b, float a,
                                               float radius,
                                               float start, float s1, float s2, float s3, float s4,
                                               float e1, float e2, float e3, float e4) {
        begin();
        uiShader.setColor(r, g, b, a);
        uiShader.setSize(w, h);
        uiShader.setPosition(x, y);
        uiShader.setRadius(radius);
        uiShader.setOpacity(1.0f);
        uiShader.setGradientStart(0.0f);
        uiShader.draw(x, y, w, h);
        end();
    }

    public static void drawRoundedRect(float x, float y, float w, float h, float r, int color) {
        begin();
        int rr = (color >> 16) & 255;
        int rg = (color >> 8) & 255;
        int rb = color & 255;
        int ra = (color >> 24) & 255;
        uiShader.setColor(rr / 255.0f, rg / 255.0f, rb / 255.0f, ra / 255.0f);
        uiShader.setSize(w, h);
        uiShader.setPosition(x, y);
        uiShader.setRadius(r);
        uiShader.setOpacity(1.0f);
        uiShader.draw(x, y, w, h);
        end();
    }

    public static void drawBorderedRoundedRect(float x, float y, float w, float h, float radius,
                                               int fillColor, float borderThickness, int borderColor) {
        begin();
        int fr = (fillColor >> 16) & 255;
        int fg = (fillColor >> 8) & 255;
        int fb = fillColor & 255;
        int fa = (fillColor >> 24) & 255;
        uiShader.setColor(fr / 255.0f, fg / 255.0f, fb / 255.0f, fa / 255.0f);
        uiShader.setBorderColor(
                ((borderColor >> 16) & 255) / 255.0f,
                ((borderColor >> 8) & 255) / 255.0f,
                (borderColor & 255) / 255.0f,
                ((borderColor >> 24) & 255) / 255.0f);
        uiShader.setSize(w, h);
        uiShader.setPosition(x, y);
        uiShader.setRadius(radius);
        uiShader.setBorderWidth(borderThickness);
        uiShader.setOpacity(1.0f);
        uiShader.draw(x, y, w, h);
        end();
    }

    public static void drawBorderedRoundedRect(float x, float y, float w, float h, float radius,
                                               int fillColor, float borderThickness, int borderColor,
                                               float shadowR, float shadowG, float shadowB, float shadowA,
                                               float shadowOffsetX, float shadowOffsetY, float shadowBlur) {
        begin();
        int fr = (fillColor >> 16) & 255;
        int fg = (fillColor >> 8) & 255;
        int fb = fillColor & 255;
        int fa = (fillColor >> 24) & 255;
        uiShader.setColor(fr / 255.0f, fg / 255.0f, fb / 255.0f, fa / 255.0f);
        uiShader.setShadowColor(shadowR, shadowG, shadowB, shadowA);
        uiShader.setShadowOffset(shadowOffsetX, shadowOffsetY);
        uiShader.setShadowBlur(shadowBlur);
        uiShader.setBorderColor(
                ((borderColor >> 16) & 255) / 255.0f,
                ((borderColor >> 8) & 255) / 255.0f,
                (borderColor & 255) / 255.0f,
                ((borderColor >> 24) & 255) / 255.0f);
        uiShader.setSize(w, h);
        uiShader.setPosition(x, y);
        uiShader.setRadius(radius);
        uiShader.setBorderWidth(borderThickness);
        uiShader.setOpacity(1.0f);
        uiShader.draw(x, y, w, h);
        end();
    }

    public static void drawBorderedRoundedRect(float x, float y, float w, float h, float radius,
                                               int fillColor, float borderThickness, int borderColor,
                                               float glowR, float glowG, float glowB, float glowIntensity) {
        begin();
        int fr = (fillColor >> 16) & 255;
        int fg = (fillColor >> 8) & 255;
        int fb = fillColor & 255;
        int fa = (fillColor >> 24) & 255;
        uiShader.setColor(fr / 255.0f, fg / 255.0f, fb / 255.0f, fa / 255.0f);
        uiShader.setGlowColor(glowR, glowG, glowB, 1.0f);
        uiShader.setGlowIntensity(glowIntensity);
        uiShader.setBorderColor(
                ((borderColor >> 16) & 255) / 255.0f,
                ((borderColor >> 8) & 255) / 255.0f,
                (borderColor & 255) / 255.0f,
                ((borderColor >> 24) & 255) / 255.0f);
        uiShader.setSize(w, h);
        uiShader.setPosition(x, y);
        uiShader.setRadius(radius);
        uiShader.setBorderWidth(borderThickness);
        uiShader.setOpacity(1.0f);
        uiShader.draw(x, y, w, h);
        end();
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        worldRenderer.pos(x, y + height, 0).endVertex();
        worldRenderer.pos(x + width, y + height, 0).endVertex();
        worldRenderer.pos(x + width, y, 0).endVertex();
        worldRenderer.pos(x, y, 0).endVertex();
        tessellator.draw();
    }

    public static void setClip(int x, int y, int w, int h) {
        GlStateManager.enableClip();
        // Eaglercraft clip is via GlStateManager
    }

    public static void clearClip() {
        GlStateManager.disableClip();
    }

    public static void pushScissor(int x, int y, int w, int h) {
        GlStateManager.viewport(x, y, w, h);
    }

    public static void popScissor() {
        // restore default viewport handled by caller
    }

    public static float scale() {
        return 1.0f;
    }

    public static void initDefault() {
        init();
    }
}
