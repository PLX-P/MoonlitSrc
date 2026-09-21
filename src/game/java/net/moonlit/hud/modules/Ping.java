package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Ping extends HUDModule {

    private int ping = 0;

    public Ping(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Ping");
    }

    public Ping() {
        super();
        setName("Ping");
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        if (netHandler != null) {
            ping = netHandler.getPlayerInfoForUsername(mc.getSession().getUsername()).pingToServer;
            if (ping < 0) ping = 0;
        } else {
            ping = 0;
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String pingText = "Ping: " + ping + "ms";

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(pingText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Ping";
    }

    @Override
    public int getModuleType() {
        return 3;
    }
}
