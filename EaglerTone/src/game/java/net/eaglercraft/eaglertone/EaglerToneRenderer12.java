package net.eaglercraft.eaglertone;

import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Renders the planned path without changing the world or sending packets. */
public final class EaglerToneRenderer12 {
    private EaglerToneRenderer12() {
    }

    public static void render(World world, double cameraX, double cameraY, double cameraZ) {
        if (world == null || !EaglerToneClientAdapter12.engine().isEnabled()
                || EaglerToneClientAdapter12.engine().getPath().isEmpty()) {
            return;
        }
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA,
                RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA, RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
        GlStateManager.depthMask(false);
        EaglercraftGPU.glLineWidth(2.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getBuffer();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (EaglerTonePathfinder.Node node : EaglerToneClientAdapter12.engine().getPath()) {
            BlockPos position = new BlockPos(node.x, node.y, node.z);
            RenderGlobal.drawBoundingBox(buffer, position.getX() - cameraX, position.getY() - cameraY,
                    position.getZ() - cameraZ, position.getX() + 1.0D - cameraX,
                    position.getY() + 0.08D - cameraY, position.getZ() + 1.0D - cameraZ,
                    0.25F, 0.85F, 1.0F, 0.85F);
        }
        tessellator.draw();
        EaglercraftGPU.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }
}
