package net.moonlit.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.StringUtils;

public class MoonlitMultiplayer extends GuiScreen {

    private GuiScreen parent;
    private List<PlayerInfo> playerList = new ArrayList<>();
    private final List<GuiButton> buttonList = new ArrayList<>();
    private Guiscreen currentScreen;
    private NetworkPlayerInfo selectedPlayer;
    private int selectedIndex = -1;
    private boolean showPlayerList = false;
    private boolean addServerVisible = false;
    private boolean refreshServerList;
    private int selectedListEntry = -1;
    private int serverListSize = 0;
    private final List<String> recentServers = new ArrayList<>();
    private int pageOffset = 0;
    private int currentPage = 0;
    private int maxPages;
    private String serverAddress = "";
    private String serverName = "";

    public MoonlitMultiplayer(GuiScreen parent) {
        this.parent = parent;
        refreshServerList = true;
    }

    @Override
    public void initGui() {
        int width = this.width;
        int height = this.height;

        int centerX = width / 2;
        int buttonY = height / 2 - 30;
        int buttonSpacing = 25;

        GuiButton lanServerButton = new GuiButton(0, centerX - 100, buttonY, 200, 20, "Lan World");
        GuiButton directConnectButton = new GuiButton(1, centerX - 100, buttonY + buttonSpacing, 200, 20, "Direct Connect");
        GuiButton multiplayerButton = new GuiButton(2, centerX - 100, buttonY + buttonSpacing * 2, 200, 20, "Multiplayer");
        GuiButton backButton = new GuiButton(3, centerX - 100, buttonY + buttonSpacing * 3, 200, 20, "Back");

        buttonList.clear();
        buttonList.add(lanServerButton);
        buttonList.add(directConnectButton);
        buttonList.add(multiplayerButton);
        buttonList.add(backButton);

        if (parent != null) {
            parent = this;
        }

        showPlayerList = false;
        addServerVisible = false;
        serverListSize = 0;
        selectedListEntry = -1;
        pageOffset = 0;
        currentPage = 0;
        maxPages = 0;
        refreshServerList = true;
        serverAddress = "";
        serverName = "";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled && button.id == 0) {
            mc.displayGuiScreen(new GuiCreateWorld(this));
        } else if (button.enabled && button.id == 1) {
            mc.displayGuiScreen(new GuiDirectConnect(this));
        } else if (button.enabled && button.id == 2) {
            mc.displayGuiScreen(new GuiMultiplayerServerList(this));
        } else if (button.enabled && button.id == 3) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground();

        int centerX = width / 2;

        drawCenteredString(fontRenderer, "Multiplayer", centerX, 20, 0xFFCCCCCC);

        if (fontRenderer != null) {
            fontRenderer.drawString("Moonlit Client", centerX - fontRenderer.getStringWidth("Moonlit Client") / 2, height - 20, 0xFF888888);
            fontRenderer.drawString("v0.1", centerX - fontRenderer.getStringWidth("v0.1") / 2, height - 35, 0xFF666666);
        }

        drawMoonlitBackground(mouseX, mouseY);

        if (showPlayerList) {
            drawPlayerList(mouseX, mouseY);
        }

        if (addServerVisible) {
            drawAddServerPanel(mouseX, mouseY);
        }

        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = buttonList.get(i);
            int buttonX = centerX - 100;
            int buttonY = height / 2 - 30 + i * 25;

            drawButton(button, buttonX, buttonY, mouseX, mouseY);
        }
    }

    private void drawButton(GuiButton button, int x, int y, int mouseX, int mouseY) {
        if (!button.enabled) {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            drawCenteredString(fontRenderer, button.displayString, x + 100, y + 10, 0xFF666666);
            return;
        }

        int hovered = (button.hovered || button.isMouseOver()) ? 1 : 0;
        int bgColor = (hovered == 1) ? 0xFF333333 : 0xFF222222;
        int borderColor = (hovered == 1) ? 0xFF888888 : 0xFF444444;

        drawRoundedRect(x, y, 200, 20, bgColor, borderColor, 1.0f, 4.0f);
        drawCenteredString(fontRenderer, button.displayString, x + 100, y + 10, 0xFFCCCCCC);
    }

    private void drawMoonlitBackground(int mouseX, int mouseY) {
        int bgX = 100;
        int bgY = 100;
        int bgW = width - 200;
        int bgH = height - 200;

        drawRoundedRect(bgX, bgY, bgW, bgH, 0xFF1A1A1ACC, 0xFF444444AA, 1.0f, 8.0f);
    }

    private void drawPlayerList(int mouseX, int mouseY) {
        int panelX = width / 2 - 150;
        int panelY = height / 2 - 80;
        int panelW = 300;
        int panelH = 160;

        drawRoundedRect(panelX, panelY, panelW, panelH, 0xFF1A1A1ACC, 0xFF444444AA, 1.0f, 8.0f);

        drawCenteredString(fontRenderer, "Players", panelX + panelW / 2, panelY + 5, 0xFFCCCCCC);

        int listX = panelX + 10;
        int listY = panelY + 20;

        for (int i = 0; i < playerList.size(); i++) {
            PlayerInfo player = playerList.get(i);

            int playerX = listX;
            int playerY = listY + i * 15;

            if (playerY > panelY + panelH - 20) {
                break;
            }

            String name = StringUtils.stripControlCodes(player.name);
            int nameWidth = fontRenderer.getStringWidth(name);

            if (nameWidth > panelW - 40) {
                name = name.substring(0, Math.max(0, name.length() - 3)) + "...";
                nameWidth = fontRenderer.getStringWidth(name);
            }

            int playerColor = getPlayerListColor(player);
            int playerTextX = listX + panelW - nameWidth - 10;

            drawString(fontRenderer, name, playerTextX, playerY + 5, playerColor);

            drawSeparator(panelX + 20, playerY + 10, panelW - 40, 0xFF444444);
        }

        if (playerList.isEmpty()) {
            drawCenteredString(fontRenderer, "No players online", panelX + panelW / 2, panelY + panelH / 2, 0xFF666666);
        }
    }

    private int getPlayerListColor(PlayerInfo player) {
        if (player.getGameProfile().hasPlayerId()) {
            return 0xFF8888FF;
        } else if (player.getName().equals(mc.getSession().getUsername())) {
            return 0xFF88FF88;
        } else {
            return 0xFFCCCCCC;
        }
    }

    private void drawSeparator(int x, int y, int width, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        worldrenderer.pos((double) x, (double) y, 0.0D).color((float) ((color >> 16) & 255) / 255.0F, (float) ((color >> 8) & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) ((color >> 24) & 255) / 255.0F).endVertex();
        worldrenderer.pos((double) (x + width), (double) y, 0.0D).color((float) ((color >> 16) & 255) / 255.0F, (float) ((color >> 8) & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) ((color >> 24) & 255) / 255.0F).endVertex();
        tessellator.draw();
    }

    private void drawAddServerPanel(int mouseX, int mouseY) {
        int panelX = width / 2 - 200;
        int panelY = height / 2 - 100;
        int panelW = 400;
        int panelH = 100;

        drawRoundedRect(panelX, panelY, panelW, panelH, 0xFF1A1A1ACC, 0xFF444444AA, 1.0f, 8.0f);

        drawCenteredString(fontRenderer, "Add Server", panelX + panelW / 2, panelY + 20, 0xFFCCCCCC);

        int textX = panelX + 20;
        int textY = panelY + 45;

        drawString(fontRenderer, "Server Address:", textX, textY, 0xFFAAAAAA);

        int inputX = panelX + 20;
        int inputY = panelY + 70;

        drawRoundedRect(inputX, inputY, panelW - 40, 20, 0xFF2A2A2A, 0xFF444444, 1.0f, 3.0f);

        String serverText = serverAddress.isEmpty() ? "localhost" : serverAddress;
        drawString(fontRenderer, serverText, inputX + 5, inputY + 5, 0xFFCCCCCC);

        if (mouseX >= inputX && mouseX < inputX + panelW - 40 && mouseY >= inputY && mouseY < inputY + 20) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }

        GuiButton addButton = new GuiButton(4, panelX + panelW - 60, panelY + panelH - 25, 80, 20, "Add");

        if (addButton.enabled) {
            int hovered = (addButton.hovered || addButton.isMouseOver()) ? 1 : 0;
            int bgColor = (hovered == 1) ? 0xFF333333 : 0xFF222222;
            int borderColor = (hovered == 1) ? 0xFF888888 : 0xFF444444;

            drawRoundedRect(panelX + panelW - 60, panelY + panelH - 25, 80, 20, bgColor, borderColor, 1.0f, 4.0f);
            drawCenteredString(fontRenderer, "Add", panelX + panelW - 20, panelY + panelH - 15, 0xFFCCCCCC);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int centerX = width / 2;
            int panelX = centerX - 150;
            int panelY = height / 2 - 80;
            int panelW = 300;
            int panelH = 160;

            if (mouseX >= panelX && mouseX < panelX + panelW && mouseY >= panelY && mouseY < panelY + panelH) {
                showPlayerList = !showPlayerList;
            }

            if (showPlayerList) {
                int addServerX = panelX + panelW + 10;
                int addServerY = panelY;
                int addServerW = 30;
                int addServerH = panelH;

                if (mouseX >= addServerX && mouseX < addServerX + addServerW && mouseY >= addServerY && mouseY < addServerY + addServerH) {
                    addServerVisible = !addServerVisible;
                }
            }

            if (addServerVisible) {
                int panelX2 = width / 2 - 200;
                int panelY2 = height / 2 - 100;
                int panelW2 = 400;
                int panelH2 = 100;

                GuiButton addButton = new GuiButton(4, panelX2 + panelW2 - 60, panelY2 + panelH2 - 25, 80, 20, "Add");

                if (mouseX >= addButton.x && mouseX < addButton.x + addButton.width && mouseY >= addButton.y && mouseY < addButton.y + addButton.height) {
                    actionPerformed(addButton);
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiButton button : buttonList) {
            if (button.enabled && button.mousePressed(mc, mouseX, mouseY)) {
                actionPerformed(button);
                break;
            }
        }
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        int mouseButton = Mouse.getButton();

        if (mouseButton == 0) {
            mouseClicked(mouseX, mouseY, 0);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() throws IOException {
        parent = null;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void keyPressed(int keyCode, char typedChar) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
        }
    }

    private void drawRoundedRect(int x1, int y1, int x2, int y2, int color, float r, float g, float b) {
        float f = (float) ((color >> 16) & 255) / 255.0F;
        float f1 = (float) ((color >> 8) & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        float f3 = (float) ((color >> 24) & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        worldrenderer.pos((double) x2, (double) y1, 0.0D).color(f, f1, f2, f3).endVertex();
        worldrenderer.pos((double) x1, (double) y1, 0.0D).color(f, f1, f2, f3).endVertex();
        worldrenderer.pos((double) x1, (double) y2, 0.0D).color(f, f1, f2, f3).endVertex();
        worldrenderer.pos((double) x2, (double) y2, 0.0D).color(f, f1, f2, f3).endVertex();
        tessellator.draw();
    }

    private void drawCenteredString(FontRenderer font, String text, int x, int y, int color) {
        drawString(font, text, x - font.getStringWidth(text) / 2, y, color);
    }

    private void drawString(FontRenderer font, String text, int x, int y, int color) {
        font.drawString(text, x, y, color);
    }
}
