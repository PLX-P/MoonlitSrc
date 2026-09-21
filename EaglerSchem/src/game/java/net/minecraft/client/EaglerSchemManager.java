package net.minecraft.client;

/* EaglerSchem - made by PLX. Manual preview only: never sends placement packets automatically. */

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.FileChooserResult;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Small, browser-safe schematic preview implementation for Eaglercraft 1.12.2.
 * It intentionally does not modify the world or automate server interactions.
 */
public final class EaglerSchemManager {
    public static final String BRAND = "EaglerSchem - made by PLX";
    private static final int MAX_BLOCKS = 250000;
    private static final List<Entry> entries = new ArrayList<Entry>();
    private static int originX;
    private static int originY;
    private static int originZ;
    private static boolean enabled;
    private static boolean choosing;
    private static String status = "No schematic loaded";

    private EaglerSchemManager() { }

    public static void openFileChooser() {
        choosing = true;
        // The browser chooser cannot express three extensions consistently across browsers.
        EagRuntime.displayFileChooser("application/octet-stream", null);
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.world == null) return;
        if (choosing && EagRuntime.fileChooserHasResult()) {
            FileChooserResult result = EagRuntime.getFileChooserResult();
            choosing = false;
            EagRuntime.clearFileChooserResult();
            if (result != null) load(result.fileName, result.fileData, mc);
        }
        if (mc.gameSettings.keyBindEaglerSchem.isPressed()) {
            enabled = !enabled;
            if (entries.isEmpty() && enabled) openFileChooser();
        }
        if (!enabled || entries.isEmpty()) return;
        int step = 1;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) originX -= step;
        if (mc.gameSettings.keyBindRight.isKeyDown()) originX += step;
        if (mc.gameSettings.keyBindForward.isKeyDown()) originZ -= step;
        if (mc.gameSettings.keyBindBack.isKeyDown()) originZ += step;
        if (mc.gameSettings.keyBindJump.isKeyDown()) originY += step;
        if (mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) originY -= step;
    }

    private static void load(String name, byte[] data, Minecraft mc) {
        entries.clear();
        try {
            if (name == null || name.toLowerCase().endsWith(".litematica")) {
                status = "Litematica is not supported by this 1.12 browser build; export as .schem or .schematic";
                EagRuntime.showPopup(status);
                return;
            }
            NBTTagCompound root;
            try {
                root = CompressedStreamTools.readCompressed(new ByteArrayInputStream(data));
            } catch (IOException compressedFailure) {
                root = CompressedStreamTools.read(new DataInputStream(new ByteArrayInputStream(data)));
            }
            if (name.toLowerCase().endsWith(".schematic")) parseClassic(root);
            else parseSponge(root);
            if (entries.size() > MAX_BLOCKS) throw new IOException("schematic exceeds " + MAX_BLOCKS + " blocks");
            BlockPos p = new BlockPos(mc.player);
            originX = p.getX(); originY = p.getY(); originZ = p.getZ();
            enabled = true;
            status = "Loaded " + name + " (" + entries.size() + " blocks)";
        } catch (Exception ex) {
            entries.clear();
            status = "Could not load " + name + ": " + ex.getMessage();
            EagRuntime.showPopup(status);
        }
    }

    private static void parseClassic(NBTTagCompound root) throws IOException {
        int w = root.getShort("Width") & 65535;
        int h = root.getShort("Height") & 65535;
        int d = root.getShort("Length") & 65535;
        byte[] blocks = root.getByteArray("Blocks");
        byte[] meta = root.getByteArray("Data");
        if (w <= 0 || h <= 0 || d <= 0 || blocks.length < w * h * d) throw new IOException("invalid .schematic dimensions");
        for (int y = 0; y < h; ++y) for (int z = 0; z < d; ++z) for (int x = 0; x < w; ++x) {
            int i = x + z * w + y * w * d;
            int id = blocks[i] & 255;
            if (id == 0) continue;
            int m = meta.length > i ? meta[i] & 15 : 0;
            IBlockState state = Block.getBlockById(id).getStateFromMeta(m);
            if (state.getBlock() != Blocks.AIR) entries.add(new Entry(x, y, z, state));
        }
    }

    private static void parseSponge(NBTTagCompound root) throws IOException {
        int w = root.getShort("Width") & 65535;
        int h = root.getShort("Height") & 65535;
        int d = root.getShort("Length") & 65535;
        NBTTagCompound palette = root.getCompoundTag("Palette");
        byte[] encoded = root.getByteArray("BlockData");
        if (w <= 0 || h <= 0 || d <= 0 || palette.hasNoTags()) throw new IOException("invalid Sponge .schem palette");
        IBlockState[] states = new IBlockState[palette.getKeySet().size()];
        for (String key : palette.getKeySet()) {
            int paletteId = palette.getInteger(key);
            Block block = Block.REGISTRY.getObject(new ResourceLocation(key));
            states[paletteId] = block == null ? Blocks.AIR.getDefaultState() : block.getDefaultState();
        }
        int[] ids = readVarInts(encoded, w * h * d);
        for (int i = 0; i < ids.length; ++i) {
            int id = ids[i];
            if (id < 0 || id >= states.length || states[id].getBlock() == Blocks.AIR) continue;
            int x = i % w;
            int z = (i / w) % d;
            int y = i / (w * d);
            entries.add(new Entry(x, y, z, states[id]));
        }
    }

    private static int[] readVarInts(byte[] bytes, int count) throws IOException {
        int[] result = new int[count]; int index = 0; int value = 0; int shift = 0;
        for (byte b : bytes) {
            value |= (b & 127) << shift;
            if ((b & 128) == 0) { if (index == count) break; result[index++] = value; value = 0; shift = 0; }
            else { shift += 7; if (shift > 28) throw new IOException("invalid BlockData varint"); }
        }
        if (index != count) throw new IOException("truncated BlockData");
        return result;
    }

    /** Render block-sized translucent outlines after the world has rendered. */
    public static void render(World world, double cameraX, double cameraY, double cameraZ) {
        if (!enabled || entries.isEmpty() || world == null) return;
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA, RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
        GlStateManager.depthMask(false);
        EaglercraftGPU.glLineWidth(1.5F);
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer buffer = tess.getBuffer();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (Entry e : entries) {
            BlockPos pos = new BlockPos(originX + e.x, originY + e.y, originZ + e.z);
            IBlockState actual = world.getBlockState(pos);
            boolean conflict = actual.getBlock() != Blocks.AIR && actual.getBlock() != e.state.getBlock();
            float r = conflict ? 1.0F : 0.25F, g = conflict ? 0.08F : 0.9F, b = conflict ? 0.08F : 0.35F;
            RenderGlobal.drawBoundingBox(buffer, pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ,
                    pos.getX() + 1.0D - cameraX, pos.getY() + 1.0D - cameraY, pos.getZ() + 1.0D - cameraZ, r, g, b, 0.65F);
        }
        tess.draw();
        EaglercraftGPU.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    public static String getStatus() { return BRAND + " | " + status + (enabled ? " | ON" : " | OFF"); }

    private static final class Entry {
        final int x, y, z; final IBlockState state;
        Entry(int x, int y, int z, IBlockState state) { this.x = x; this.y = y; this.z = z; this.state = state; }
    }
}
