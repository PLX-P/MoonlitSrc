package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Coordinates extends HUDModule {

    private int x = 0;
    private int y = 0;
    private int z = 0;
    private int dimension = 0;

    public Coordinates(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Coordinates");
    }

    public Coordinates() {
        super();
        setName("Coordinates");
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null && mc.player != null) {
            x = (int) mc.player.posX;
            y = (int) mc.player.posY;
            z = (int) mc.player.posZ;
            dimension = mc.world.provider.getDimension();
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String coordsText = String.format("X: %d  Y: %d  Z: %d", x, y, z);

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(coordsText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Coordinates";
    }

    @Override
    public int getModuleType() {
        return 2;
    }
}
