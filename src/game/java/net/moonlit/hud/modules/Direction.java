package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Direction extends HUDModule {

    private String direction = "N";

    public Direction(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Direction");
    }

    public Direction() {
        super();
        setName("Direction");
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            int yaw = (int) mc.player.rotationYaw;
            if (yaw < 0) yaw += 360;
            int sector = yaw / 45;
            switch (sector) {
                case 0: direction = "N"; break;
                case 1: direction = "NE"; break;
                case 2: direction = "E"; break;
                case 3: direction = "SE"; break;
                case 4: direction = "S"; break;
                case 5: direction = "SW"; break;
                case 6: direction = "W"; break;
                case 7: direction = "NW"; break;
                default: direction = "N"; break;
            }
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawCenteredText(direction, getRenderX() + getRenderWidth() / 2, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Direction";
    }

    @Override
    public int getModuleType() {
        return 7;
    }
}
