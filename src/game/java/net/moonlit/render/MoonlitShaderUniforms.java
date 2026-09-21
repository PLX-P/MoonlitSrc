package net.moonlit.render;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL.*;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.IShaderGL;
import net.lax1dude.eaglercraft.internal.IUniformGL;

public class MoonlitShaderUniforms {

    public static String buildHeader(boolean gles3) {
        StringBuilder sb = new StringBuilder();
        if (gles3) {
            sb.append("#version 300 es\n");
        } else {
            sb.append("#version 100\n");
        }
        sb.append("precision mediump float;\n");
        sb.append("precision mediump sampler2D;\n");
        if (!gles3) {
            sb.append("varying vec2 v_pos;\n");
        }
        return sb.toString();
    }

    public static String buildVertexOut(boolean gles3) {
        return gles3 ? "layout(location = 0) out vec2 v_pos;\n" : "";
    }

    public static String buildFragmentOut(boolean gles3) {
        return gles3 ? "layout(location = 0) out vec4 fragColor;\n" : "";
    }

    public static String buildQuadVertex(boolean gles3) {
        String in = gles3 ? "layout(location = 0) in vec2 a_pos;\n" : "attribute vec2 a_pos;\n";
        String out = gles3 ? "out vec2 v_pos;\n" : "";
        return in + out +
                "void main() {\n" +
                "    " + (gles3 ? "v_pos = a_pos;" : "v_pos = a_pos;") +
                "    gl_Position = vec4((a_pos - 0.5) * vec2(2.0, -2.0), 0.0, 1.0);\n" +
                "}\n";
    }

    public static String buildUniformDeclarations() {
        return
                "uniform vec4 u_color;\n" +
                "uniform vec4 u_borderColor;\n" +
                "uniform vec4 u_shadowColor;\n" +
                "uniform vec4 u_glowColor;\n" +
                "uniform vec2 u_size;\n" +
                "uniform vec2 u_pos;\n" +
                "uniform float u_radius;\n" +
                "uniform float u_borderWidth;\n" +
                "uniform float u_shadowOffsetX;\n" +
                "uniform float u_shadowOffsetY;\n" +
                "uniform float u_shadowBlur;\n" +
                "uniform float u_glowIntensity;\n" +
                "uniform float u_gradientStart;\n" +
                "uniform vec4 u_gradientColor1;\n" +
                "uniform vec4 u_gradientColor2;\n" +
                "uniform float u_opacity;\n" +
                "uniform sampler2D u_texture;\n";
    }

    public static String buildFragmentRoundedRect(boolean gles3, boolean supportBlur) {
        String fragOut = gles3 ? "out vec4 fragColor;\n" : "";
        String vpos = gles3 ? "v_pos" : "v_pos";

        StringBuilder sb = new StringBuilder();
        sb.append(fragOut);
        sb.append(buildUniformDeclarations());
        sb.append("\n");
        sb.append("vec4 composeColor(vec4 base) {\n");
        sb.append("    base.a *= u_opacity;\n");
        sb.append("    return base;\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("float roundedBox(vec2 p, vec2 b, float r) {\n");
        sb.append("    vec2 q = abs(p - 0.5) - b + r;\n");
        sb.append("    return min(max(q.x, q.y), 0.0) + r;\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("void main() {\n");
        sb.append("    vec2 uv = " + vpos + " * u_size;\n");
        sb.append("    vec2 c = u_pos + uv;\n");
        sb.append("    vec2 s = u_size;\n");
        sb.append("    float r = u_radius;\n");
        sb.append("    vec2 d = abs(c - (u_pos + s*0.5)) - (s*0.5 - r);\n");
        sb.append("    float outside = length(max(d, 0.0));\n");
        sb.append("    float inside = min(max(d.x, d.y), 0.0);\n");
        sb.append("    float alpha = 1.0 - smoothstep(r*0.0 - 0.001, r*0.0 + 0.001, outside + inside);\n");
        sb.append("    alpha = 1.0 - step(0.0, outside + inside);\n");
        sb.append("    vec4 color = u_color;\n");
        sb.append("    if (u_gradientStart > 0.0) {\n");
        sb.append("        float t = clamp((uv.y / s.y - u_gradientStart) / (1.0 - u_gradientStart), 0.0, 1.0);\n");
        sb.append("        color = mix(u_gradientColor1, u_gradientColor2, t);\n");
        sb.append("    }\n");
        sb.append("    fragColor = composeColor(vec4(color.rgb, alpha * u_color.a));\n");
        sb.append("}\n");
        return sb.toString();
    }
}
