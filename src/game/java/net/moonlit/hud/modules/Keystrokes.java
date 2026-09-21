package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Keystrokes extends HUDModule {

    private int keystrokes = 0;
    private long lastKeystrokeUpdate = 0;
    private int displayKeystrokes = 0;

    public Keystrokes(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Keystrokes");
    }

    public Keystrokes() {
        super();
        setName("Keystrokes");
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeystrokeUpdate >= 1000) {
            displayKeystrokes = keystrokes;
            keystrokes = 0;
            lastKeystrokeUpdate = currentTime;
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String keystrokesText = "Keystrokes: " + displayKeystrokes;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(keystrokesText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Keystrokes";
    }

    @Override
    public int getModuleType() {
        return 4;
    }
}
