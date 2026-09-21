package net.moonlit.render;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL.*;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.IVertexArrayGL;
import net.lax1dude.eaglercraft.internal.IBufferGL;
import net.lax1dude.eaglercraft.internal.IProgramGL;
import net.lax1dude.eaglercraft.internal.IShaderGL;
import net.lax1dude.eaglercraft.internal.IUniformGL;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;

public class MoonlitShaderProgram {

    private final boolean gles3;
    private final IProgramGL program;
    private final IUniformGL u_color;
    private final IUniformGL u_borderColor;
    private final IUniformGL u_shadowColor;
    private final IUniformGL u_glowColor;
    private final IUniformGL u_size;
    private final IUniformGL u_pos;
    private final IUniformGL u_radius;
    private final IUniformGL u_borderWidth;
    private final IUniformGL u_shadowOffsetX;
    private final IUniformGL u_shadowOffsetY;
    private final IUniformGL u_shadowBlur;
    private final IUniformGL u_glowIntensity;
    private final IUniformGL u_gradientStart;
    private final IUniformGL u_gradientColor1;
    private final IUniformGL u_gradientColor2;
    private final IUniformGL u_opacity;
    private final IUniformGL u_texture;
    private final IUniformGL u_matrix;
    private final IVertexArrayGL vao;
    private final IBufferGL vbo;

    public MoonlitShaderProgram() {
        gles3 = EaglercraftGPU.checkOpenGLESVersion() >= 300;

        String vertexSrc = MoonlitShaderUniforms.buildQuadVertex(gles3);
        String fragmentSrc = MoonlitShaderUniforms.buildFragmentRoundedRect(gles3, false);

        vertexSrc = MoonlitShaderUniforms.buildHeader(gles3) + vertexSrc;
        fragmentSrc = MoonlitShaderUniforms.buildHeader(gles3) + fragmentSrc;

        String fullFrag = fragmentSrc +
                "\nvoid main() {\n" +
                "    vec4 outColor = vec4(0.0);\n" +
                "    vec2 uv = " + (gles3 ? "v_pos" : "v_pos") + " * u_size;\n" +
                "    vec2 c = u_pos + uv;\n" +
                "    vec2 s = u_size;\n" +
                "    float r = min(u_radius, min(s.x*0.5, s.y*0.5));\n" +
                "    vec2 dc = abs(c - (u_pos + s*0.5)) - (s*0.5 - r);\n" +
                "    float d = length(max(dc, 0.0));\n" +
                "    float corner = 1.0 - smoothstep(r-0.004, r+0.004, d);\n" +
                "    float edge = 1.0 - step(0.0, d - r + 0.001);\n" +
                "    float mask = corner * edge;\n" +
                "    vec4 base = u_color;\n" +
                "    if (u_gradientStart > 0.0) {\n" +
                "        float t = clamp((uv.y / s.y - u_gradientStart) / (1.0 - u_gradientStart), 0.0, 1.0);\n" +
                "        base = mix(u_gradientColor1, u_gradientColor2, t);\n" +
                "    }\n" +
                "    vec4 finalColor = vec4(base.rgb, base.a * u_opacity * mask);\n" +
                "    fragColor = finalColor;\n" +
                "}\n";

        IShaderGL vert = _wglCreateShader(GL_VERTEX_SHADER);
        IShaderGL frag = _wglCreateShader(GL_FRAGMENT_SHADER);

        _wglShaderSource(vert, vertexSrc);
        _wglShaderSource(frag, fullFrag);

        _wglCompileShader(vert);
        _wglCompileShader(frag);

        if (_wglGetShaderi(vert, GL_COMPILE_STATUS) != GL_TRUE || _wglGetShaderi(frag, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new IllegalStateException("Moonlit shader compilation failed");
        }

        program = _wglCreateProgram();
        _wglAttachShader(program, vert);
        _wglAttachShader(program, frag);

        if (!gles3) {
            _wglBindAttribLocation(program, 0, "a_pos");
        }

        _wglLinkProgram(program);
        _wglDetachShader(program, vert);
        _wglDetachShader(program, frag);
        _wglDeleteShader(vert);
        _wglDeleteShader(frag);

        if (_wglGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
            throw new IllegalStateException("Moonlit shader program link failed");
        }

        EaglercraftGPU.bindGLShaderProgram(program);

        u_color = _wglGetUniformLocation(program, "u_color");
        u_borderColor = _wglGetUniformLocation(program, "u_borderColor");
        u_shadowColor = _wglGetUniformLocation(program, "u_shadowColor");
        u_glowColor = _wglGetUniformLocation(program, "u_glowColor");
        u_size = _wglGetUniformLocation(program, "u_size");
        u_pos = _wglGetUniformLocation(program, "u_pos");
        u_radius = _wglGetUniformLocation(program, "u_radius");
        u_borderWidth = _wglGetUniformLocation(program, "u_borderWidth");
        u_shadowOffsetX = _wglGetUniformLocation(program, "u_shadowOffsetX");
        u_shadowOffsetY = _wglGetUniformLocation(program, "u_shadowOffsetY");
        u_shadowBlur = _wglGetUniformLocation(program, "u_shadowBlur");
        u_glowIntensity = _wglGetUniformLocation(program, "u_glowIntensity");
        u_gradientStart = _wglGetUniformLocation(program, "u_gradientStart");
        u_gradientColor1 = _wglGetUniformLocation(program, "u_gradientColor1");
        u_gradientColor2 = _wglGetUniformLocation(program, "u_gradientColor2");
        u_opacity = _wglGetUniformLocation(program, "u_opacity");
        u_texture = _whlGetUniformLocationSafe(program, "u_texture");
        u_matrix = _whlGetUniformLocationSafe(program, "u_matrix");

        FloatBuffer verts = EagRuntime.allocateFloatBuffer(8);
        verts.put(new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f});
        verts.flip();

        vbo = _wglGenBuffers();
        _wglBindBuffer(GL_ARRAY_BUFFER, vbo);
        _wglBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

        vao = gles3 ? _wglGenVertexArrays() : null;
        if (gles3) {
            _wglBindVertexArray(vao);
            _wglBindBuffer(GL_ARRAY_BUFFER, vbo);
            _wglEnableVertexAttribArray(0);
            _wglVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
        }

        EagRuntime.freeFloatBuffer(verts);
    }

    private static IUniformGL _whlGetUniformLocationSafe(IProgramGL prog, String name) {
        int loc = _wglGetUniformLocation(prog, name);
        if (loc == -1) return null;
        return new UniformProxy(loc);
    }

    public void setColor(float r, float g, float b, float a) {
        if (u_color != null) _wglUniform4f(u_color, r, g, b, a);
    }

    public void setBorderColor(float r, float g, float b, float a) {
        if (u_borderColor != null) _wglUniform4f(u_borderColor, r, g, b, a);
    }

    public void setShadowColor(float r, float g, float b, float a) {
        if (u_shadowColor != null) _wglUniform4f(u_shadowColor, r, g, b, a);
    }

    public void setGlowColor(float r, float g, float b, float a) {
        if (u_glowColor != null) _wglUniform4f(u_glowColor, r, g, b, a);
    }

    public void setSize(float w, float h) {
        if (u_size != null) _wglUniform2f(u_size, w, h);
    }

    public void setPosition(float x, float y) {
        if (u_pos != null) _wglUniform2f(u_pos, x, y);
    }

    public void setRadius(float r) {
        if (u_radius != null) _wglUniform1f(u_radius, r);
    }

    public void setBorderWidth(float w) {
        if (u_borderWidth != null) _wglUniform1f(u_borderWidth, w);
    }

    public void setShadowOffset(float x, float y) {
        if (u_shadowOffsetX != null) _wglUniform1f(u_shadowOffsetX, x);
        if (u_shadowOffsetY != null) _wglUniform1f(u_shadowOffsetY, y);
    }

    public void setShadowBlur(float b) {
        if (u_shadowBlur != null) _wglUniform1f(u_shadowBlur, b);
    }

    public void setGlowIntensity(float v) {
        if (u_glowIntensity != null) _wglUniform1f(u_glowIntensity, v);
    }

    public void setGradientStart(float v) {
        if (u_gradientStart != null) _wglUniform1f(u_gradientStart, v);
    }

    public void setGradientColor1(float r, float g, float b, float a) {
        if (u_gradientColor1 != null) _wglUniform4f(u_gradientColor1, r, g, b, a);
    }

    public void setGradientColor2(float r, float g, float b, float a) {
        if (u_gradientColor2 != null) _wglUniform4f(u_gradientColor2, r, g, b, a);
    }

    public void setOpacity(float v) {
        if (u_opacity != null) _wglUniform1f(u_opacity, v);
    }

    public void setTexture(int tex) {
        if (u_texture != null) _wglUniform1i(u_texture, tex);
    }

    public void draw(float x, float y, float w, float h) {
        EaglercraftGPU.bindGLShaderProgram(program);
        setPosition(x, y);
        setSize(w, h);
        if (gles3) {
            _wglBindVertexArray(vao);
            _wglBindBuffer(GL_ARRAY_BUFFER, vbo);
            _wglEnableVertexAttribArray(0);
            _wglVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
            _wglDrawArrays(GL_TRIANGLES, 0, 6);
            _wglDisableVertexAttribArray(0);
            _wglBindBuffer(GL_ARRAY_BUFFER, null);
            _wglBindVertexArray(null);
        } else {
            _wglBindBuffer(GL_ARRAY_BUFFER, vbo);
            _wglEnableVertexAttribArray(0);
            _wglVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
            _wglDrawArrays(GL_TRIANGLES, 0, 6);
            _wglDisableVertexAttribArray(0);
            _wglBindBuffer(GL_ARRAY_BUFFER, null);
        }
    }

    public void destroy() {
        _wglDeleteProgram(program);
    }

    private static class UniformProxy implements IUniformGL {
        private final int location;
        UniformProxy(int location) { this.location = location; }
        @Override public int location() { return location; }
    }
}
