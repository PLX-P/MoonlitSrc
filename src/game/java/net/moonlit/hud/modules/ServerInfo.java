package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ServerInfo extends HUDModule {

    private String serverInfo = "Not connected";

    public ServerInfo(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Server Info");
    }

    public ServerInfo() {
        super();
        setName("Server Info");
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isConnectedToServer()) {
            serverInfo = "Server: " + (mc.currentServerData != null ? mc.currentServerData.serverIP + " (" + mc.currentServerData.serverName + ")" : "Unknown");
        } else {
            serverInfo = "Not connected";
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(serverInfo, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Server Info";
    }

    @Override
    public int getModuleType() {
        return 10;
    }
}
