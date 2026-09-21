package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class CPSCounter extends HUDModule {

    private int cps = 0;
    private int clickCount = 0;
    private long lastCpsUpdate = 0;
    private int displayCps = 0;
    private long lastClickTime = 0;

    public CPSCounter(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("CPS Counter");
    }

    public CPSCounter() {
        super();
        setName("CPS Counter");
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCpsUpdate >= 1000) {
            displayCps = clickCount;
            clickCount = 0;
            lastCpsUpdate = currentTime;
            cps = displayCps;
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= 50) {
            clickCount++;
            lastClickTime = currentTime;
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String cpsText = "CPS: " + cps;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(cpsText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "CPS";
    }

    @Override
    public int getModuleType() {
        return 1;
    }
}
