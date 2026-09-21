package net.moonlit.font;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.ITextureGL;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;

public class MoonlitFontRenderer {

    private static final int GL_TEXTURE0 = 0x84C0;

    private static ITextureGL atlasTexture;
    private static int atlasWidth;
    private static int atlasHeight;

    private static final int GLYPH_WIDTH = 8;
    private static final int GLYPH_HEIGHT = 8;
    private static final int MAX_GLYPHS = 256;

    private static final Map<Integer, Glyph> glyphCache = new HashMap<>();

    static {
        try {
            AtlasInfo info = buildAtlas();
            atlasTexture = EaglercraftGPU.mapTexturesGL.get(info.textureId);
            atlasWidth = info.width;
            atlasHeight = info.height;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        try {
            AtlasInfo info = buildAtlas();
            // atlas already created
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class AtlasInfo {
        int textureId;
        int width;
        int height;
    }

    static AtlasInfo buildAtlas() throws Exception {
        int texId = EaglercraftGPU.mapTexturesGL.register(EaglercraftGPU._wglGenTextures());

        ByteBuffer pixelData = EagRuntime.allocateByteBuffer(atlasWidth * atlasHeight * 4);

        IntBuffer buf = pixelData.asIntBuffer();
        for (int i = 0; i < atlasWidth * atlasHeight; i++) {
            buf.put(0);
        }
        buf.flip();

        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBindTexture(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_2D,
                EaglercraftGPU.mapTexturesGL.get(texId));
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglTexParameteri(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_2D,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_MAG_FILTER,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_NEAREST);
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglTexParameteri(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_2D,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_MIN_FILTER,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_NEAREST);
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglTexParameteri(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_2D,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_WRAP_S,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_CLAMP_TO_EDGE);
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglTexParameteri(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_2D,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_WRAP_T,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_CLAMP_TO_EDGE);

        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglTexImage2D(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE_2D,
                0,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_RGBA,
                atlasWidth,
                atlasHeight,
                0,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_RGBA,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_UNSIGNED_BYTE,
                pixelData);

        EagRuntime.freeByteBuffer(pixelData);

        AtlasInfo info = new AtlasInfo();
        info.textureId = texId;
        info.width = atlasWidth;
        info.height = atlasHeight;
        return info;
    }

    public static void bindAtlas() {
        GlStateManager.bindTexture(atlasTexture.textureId);
    }

    public static int getStringWidth(String text) {
        if (text == null || text.isEmpty()) return 0;
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Glyph g = getGlyph(c);
            if (g != null) {
                width += g.width;
            }
        }
        return width;
    }

    public static int drawString(String text, float x, float y, int color) {
        if (text == null || text.isEmpty()) return (int) x;
        bindAtlas();

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Glyph glyph = getGlyph(c);
            if (glyph != null) {
                drawGlyph(glyph, x, y, r, g, b);
                x += glyph.width;
            }
        }

        return (int) x;
    }

    static Glyph getGlyph(char c) {
        Integer codePoint = (int) c;
        if (glyphCache.containsKey(codePoint)) {
            return glyphCache.get(codePoint);
        }

        Glyph glyph = new Glyph();
        glyph.texX = (c % 16) * GLYPH_WIDTH;
        glyph.texY = (c / 16) * GLYPH_HEIGHT;
        glyph.width = GLYPH_WIDTH;
        glyph.height = GLYPH_HEIGHT;
        glyphCache.put(codePoint, glyph);

        return glyph;
    }

    static void drawGlyph(Glyph glyph, float x, float y, int r, int g, int b) {
        GlStateManager.color(
                r / 255.0f,
                g / 255.0f,
                b / 255.0f,
                1.0f);

        net.moonlit.render.MoonlitRenderer.drawTexturedModalRect(
                (int) x,
                (int) y,
                glyph.texX,
                glyph.texY,
                glyph.width,
                glyph.height);
    }

    public static int drawString(String text, float x, float y, int color, boolean shadow) {
        if (shadow) {
            drawString(text, x + 1, y + 1, 0xFF000000);
        }
        return drawString(text, x, y, color);
    }

    public static int getCharWidth(char c) {
        Glyph glyph = getGlyph(c);
        if (glyph != null) {
            return glyph.width;
        }
        return GLYPH_WIDTH;
    }

    public static int drawRightString(String text, float x, float y, int color) {
        int width = getStringWidth(text);
        return drawString(text, x - width, y, color);
    }

    public static int drawCenteredString(String text, float x, float y, int color) {
        int width = getStringWidth(text);
        return drawString(text, x - width / 2, y, color);
    }

    public static int drawStringWithShadow(String text, float x, float y, int color) {
        return drawString(text, x, y, color, true);
    }

    public static float drawStringScaled(String text, float x, float y, float scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        int result = drawString(text, x / scale, y / scale, color);
        GlStateManager.popMatrix();
        return result * scale;
    }

    public static float drawStringScaledWithShadow(String text, float x, float y, float scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        int result = drawString(text, x / scale, y / scale, color, true);
        GlStateManager.popMatrix();
        return result * scale;
    }

    public static float drawStringWithGradient(String text, float x, float y, int colorStart, int colorEnd) {
        if (text == null || text.isEmpty()) return 0;
        bindAtlas();
        int r1 = (colorStart >> 16) & 0xFF;
        int g1 = (colorStart >> 8) & 0xFF;
        int b1 = colorStart & 0xFF;
        int r2 = (colorEnd >> 16) & 0xFF;
        int g2 = (colorEnd >> 8) & 0xFF;
        int b2 = colorEnd & 0xFF;

        float totalWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Glyph glyph = getGlyph(c);
            if (glyph != null) {
                float t = totalWidth / (getStringWidth(text) + 1);
                int r = (int) (r1 + (r2 - r1) * t);
                int g = (int) (g1 + (g2 - g1) * t);
                int b = (int) (b1 + (b2 - b1) * t);
                drawGlyph(glyph, x + totalWidth, y, r, g, b);
                totalWidth += glyph.width;
            }
        }
        return totalWidth;
    }

    public static String wordWrap(String text, float maxWidth, float fontSize) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        int lineWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = getCharWidth(c) * (int) fontSize;

            if (lineWidth + charWidth > maxWidth && line.length() > 0) {
                result.append(line).append('\n');
                line.setLength(0);
                lineWidth = 0;
            }

            if (c == ' ') {
                line.append(c);
                lineWidth += charWidth;
            } else {
                line.append(c);
                lineWidth += charWidth;
            }
        }

        if (line.length() > 0) {
            result.append(line);
        }

        return result.toString();
    }

    public static String[] splitIntoLines(String text, float maxWidth, float fontSize) {
        return wordWrap(text, maxWidth, fontSize).split("\n");
    }

    public static float drawStringWrapped(String text, float x, float y, float maxWidth, float fontSize, int color, int lineHeight) {
        String[] lines = splitIntoLines(text, maxWidth, fontSize);
        float resultY = y;

        for (String line : lines) {
            drawString(line, x, resultY, color);
            resultY += lineHeight;
        }

        return resultY;
    }

    public static float drawStringWrappedWithShadow(String text, float x, float y, float maxWidth, float fontSize, int color, int lineHeight) {
        String[] lines = splitIntoLines(text, maxWidth, fontSize);
        float resultY = y;

        for (String line : lines) {
            drawString(line, x + 1, resultY + 1, 0xFF000000);
            drawString(line, x, resultY, color);
            resultY += lineHeight;
        }

        return resultY;
    }

    static class Glyph {
        int texX;
        int texY;
        int width;
        int height;
    }

    public static boolean isInitialized() {
        return atlasTexture != null;
    }

    public static int getAtlasWidth() {
        return atlasWidth;
    }

    public static int getAtlasHeight() {
        return atlasHeight;
    }

    public static ITextureGL getTexture() {
        return atlasTexture;
    }

    public static void dispose() {
        if (atlasTexture != null) {
            EaglercraftGPU.mapTexturesGL.free(atlasTexture.textureId);
            atlasTexture = null;
        }
        glyphCache.clear();
    }

    public static void destroy() {
        dispose();
    }
}
