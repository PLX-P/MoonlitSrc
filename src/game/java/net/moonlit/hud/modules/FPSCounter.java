package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class FPSCounter extends HUDModule {

    private int fps = 0;
    private int frameCount = 0;
    private long lastFpsUpdate = 0;
    private int displayFps = 0;

    public FPSCounter(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("FPS Counter");
    }

    public FPSCounter() {
        super();
        setName("FPS Counter");
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        frameCount++;

        if (currentTime - lastFpsUpdate >= 1000) {
            displayFps = frameCount;
            frameCount = 0;
            lastFpsUpdate = currentTime;
            fps = displayFps;
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String fpsText = "FPS: " + fps;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(fpsText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "FPS";
    }

    @Override
    public int getModuleType() {
        return 0;
    }
}
