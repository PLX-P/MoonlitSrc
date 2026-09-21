package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Clock extends HUDModule {

    private String time = "";

    public Clock(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Clock");
    }

    public Clock() {
        super();
        setName("Clock");
    }

    @Override
    public void update() {
        int hour = Minecraft.getMinecraft().world.getWorldTime() / 24000;
        int minute = (Minecraft.getMinecraft().world.getWorldTime() % 24000) / 1000;
        if (minute < 0) minute += 60;
        if (hour < 0) hour += 24;
        hour %= 24;
        time = String.format("%02d:%02d", hour, minute);
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawCenteredText(time, getRenderX() + getRenderWidth() / 2, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Clock";
    }

    @Override
    public int getModuleType() {
        return 8;
    }
}
