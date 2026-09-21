package net.moonlit.hud;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.Minecraft;

public class MoonlitHUD {

    public static final int HUD_WIDTH = 256;
    public static final int HUD_HEIGHT = 256;

    private static boolean isHUDVisible = true;
    private static boolean isHUDEditorVisible = false;

    private static final List<HUDModule> hudModules = new ArrayList<>();
    private static HUDModule selectedModule;
    private static boolean isDragging;
    private static boolean isResizing;
    private static int resizeCorner;

    public static HUDModule fpsCounter;
    public static HUDModule cpsCounter;
    public static HUDModule coordinates;
    public static HUDModule ping;
    public static HUDModule keystrokes;
    public static HUDModule armor;
    public static HUDModule potionEffects;
    public static HUDModule direction;
    public static HUDModule clock;
    public static HUDModule memory;
    public static HUDModule serverInfo;

    public static void initialize(MoonlitFontRenderer fontRenderer) {
        fpsCounter = new FPSCounter(50, 50, 100, 20);
        cpsCounter = new CPSCounter(50, 75, 100, 20);
        coordinates = new Coordinates(50, 100, 150, 20);
        ping = new Ping(50, 125, 100, 20);
        keystrokes = new Keystrokes(50, 150, 100, 20);
        armor = new ArmorHUD(50, 175, 100, 20);
        potionEffects = new PotionEffects(50, 200, 150, 20);
        direction = new Direction(50, 225, 100, 20);
        clock = new Clock(50, 250, 100, 20);
        memory = new Memory(50, 275, 100, 20);
        serverInfo = new ServerInfo(50, 300, 150, 20);

        hudModules.add(fpsCounter);
        hudModules.add(cpsCounter);
        hudModules.add(coordinates);
        hudModules.add(ping);
        hudModules.add(keystrokes);
        hudModules.add(armor);
        hudModules.add(potionEffects);
        hudModules.add(direction);
        hudModules.add(clock);
        hudModules.add(memory);
        hudModules.add(serverInfo);
    }

    public static boolean isHUDVisible() {
        return isHUDVisible;
    }

    public static void setHUDVisible(boolean visible) {
        isHUDVisible = visible;
    }

    public static boolean isHUDEditorVisible() {
        return isHUDEditorVisible;
    }

    public static void setHUDEditorVisible(boolean visible) {
        isHUDEditorVisible = visible;
        if (isHUDEditorVisible) {
            selectedModule = null;
            isDragging = false;
            isResizing = false;
        }
        else {
            selectedModule = null;
            isDragging = false;
            isResizing = false;
        }
    }

    public static List<HUDModule> getHUDModules() {
        return new ArrayList<>(hudModules);
    }

    public static void addModule(HUDModule module) {
        if (!hudModules.contains(module)) {
            hudModules.add(module);
        }
    }

    public static void removeModule(HUDModule module) {
        hudModules.remove(module);
    }

    public static void toggleModule(HUDModule module) {
        module.setVisible(!module.isVisible());
    }

    public static void setModulePosition(HUDModule module, int x, int y) {
        module.setPosition(x, y);
    }

    public static void setModuleSize(HUDModule module, int width, int height) {
        module.setSize(width, height);
    }

    public static HUDModule getSelectedModule() {
        return selectedModule;
    }

    public static void setSelectedModule(HUDModule module) {
        if (selectedModule != null) {
            selectedModule.setSelected(false);
        }
        selectedModule = module;
        if (selectedModule != null) {
            selectedModule.setSelected(true);
        }
    }

    public static void update() {
        if (!isHUDVisible || isHUDEditorVisible) {
            return;
        }

        for (HUDModule module : hudModules) {
            module.update();
        }
    }

    public static void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!isHUDVisible) {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        for (HUDModule module : hudModules) {
            if (module.isVisible()) {
                module.render(mc, fontRenderer, mouseX, mouseY);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    public static void handleMouseInput(Minecraft mc, int mouseX, int mouseY, int mouseButton, int mouseState) {
        if (!isHUDVisible || isHUDEditorVisible) {
            return;
        }

        if (mouseState == 0 && mouseButton == 0) {
            for (int i = hudModules.size() - 1; i >= 0; i--) {
                HUDModule module = hudModules.get(i);
                if (module.isVisible() && module.contains(mouseX, mouseY)) {
                    if (isHUDEditorVisible) {
                        setSelectedModule(module);
                        isDragging = true;
                        isResizing = false;
                    } else {
                        module.onMouseClicked(mouseX, mouseY, mouseButton);
                    }
                    break;
                }
            }
        }

        if (mouseState == 1 && mouseButton == 0) {
            isDragging = false;
            setSelectedModule(null);
        }
    }

    public static void handleMouseDrag(Minecraft mc, int mouseX, int mouseY) {
        if (!isHUDVisible || !isHUDEditorVisible) {
            return;
        }

        if (isDragging && selectedModule != null) {
            int newX = mouseX - selectedModule.getWidth() / 2;
            int newY = mouseY - selectedModule.getHeight() / 2;
            selectedModule.setPosition(newX, newY);
        }
    }

    public static void handleScroll(Minecraft mc, int mouseX, int mouseY, float scrollAmount) {
        if (!isHUDVisible || isHUDEditorVisible) {
            return;
        }

        if (selectedModule != null && selectedModule.isVisible()) {
            if (scrollAmount > 0) {
                selectedModule.setPosition(selectedModule.getX() - 1, selectedModule.getY());
            } else {
                selectedModule.setPosition(selectedModule.getX() + 1, selectedModule.getY());
            }
        }
    }

    public static void resetAllModulePositions() {
        for (HUDModule module : hudModules) {
            module.resetPosition();
        }
    }

    public static void resizeModule(HUDModule module, int newWidth, int newHeight) {
        module.setSize(newWidth, newHeight);
    }

    public static void setHUDScale(float scale) {
        for (HUDModule module : hudModules) {
            module.setScale(scale);
        }
    }

    public static void toggleHUD() {
        isHUDVisible = !isHUDVisible;
    }

    public static void toggleHUDEditor() {
        isHUDEditorVisible = !isHUDEditorVisible;
    }

    public static boolean isHUDEditorEnabled() {
        return isHUDEditorVisible;
    }

    public static void enableHUDEditor() {
        isHUDEditorVisible = true;
    }

    public static void disableHUDEditor() {
        isHUDEditorVisible = false;
    }

    public static void updateModule(HUDModule module) {
        module.update();
    }

    public static void renderHUDBackground(Minecraft mc, FontRenderer fontRenderer) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        int bgX = 0;
        int bgY = 0;
        int bgW = mc.displayWidth;
        int bgH = mc.displayHeight;

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(bgX, bgY, bgW, bgH, fontRenderer, 0xFF1A1A1ACC);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    public static void renderHUDBorder(Minecraft mc, FontRenderer fontRenderer) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        int borderX = 0;
        int borderY = 0;
        int borderW = mc.displayWidth;
        int borderH = mc.displayHeight;

        net.moonlit.render.MoonlitRenderer.drawBorderRect(borderX, borderY, borderW, borderH, fontRenderer, 0xFF444444AA);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    public static void renderHUDTitle(Minecraft mc, FontRenderer fontRenderer, String title) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        int titleX = mc.displayWidth / 2 - fontRenderer.getStringWidth(title) / 2;
        int titleY = 10;

        fontRenderer.drawString(title, titleX, titleY, 0xFFCCCCCC);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    public static void setHUDEnabled(boolean enabled) {
        isHUDVisible = enabled;
    }

    public static void setHUDAlpha(float alpha) {
        for (HUDModule module : hudModules) {
            module.setAlpha(alpha);
        }
    }

    public static void setHUDColor(int color) {
        for (HUDModule module : hudModules) {
            module.setColor(color);
        }
    }

    public static boolean shouldRenderHUD() {
        return isHUDVisible && Minecraft.getMinecraft().currentScreen == null;
    }

    public static boolean shouldRenderHUDEditor() {
        return isHUDEditorVisible && Minecraft.getMinecraft().currentScreen == null;
    }

    public static void updateHUDModules() {
        for (HUDModule module : hudModules) {
            module.update();
        }
    }

    public static HUDModule getModuleByName(String name) {
        for (HUDModule module : hudModules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    public static void removeModuleByName(String name) {
        HUDModule module = getModuleByName(name);
        if (module != null) {
            removeModule(module);
        }
    }

    public static HUDModule createModule(String type) {
        HUDModule module = null;

        if (type.equals("FPS")) {
            module = new FPSCounter(0, 0, 100, 20);
        } else if (type.equals("CPS")) {
            module = new CPSCounter(0, 0, 100, 20);
        } else if (type.equals("Coordinates")) {
            module = new Coordinates(0, 0, 150, 20);
        } else if (type.equals("Ping")) {
            module = new Ping(0, 0, 100, 20);
        } else if (type.equals("Keystrokes")) {
            module = new Keystrokes(0, 0, 100, 20);
        } else if (type.equals("Armor")) {
            module = new ArmorHUD(0, 0, 100, 20);
        } else if (type.equals("PotionEffects")) {
            module = new PotionEffects(0, 0, 150, 20);
        } else if (type.equals("Direction")) {
            module = new Direction(0, 0, 100, 20);
        } else if (type.equals("Clock")) {
            module = new Clock(0, 0, 100, 20);
        } else if (type.equals("Memory")) {
            module = new Memory(0, 0, 100, 20);
        } else if (type.equals("ServerInfo")) {
            module = new ServerInfo(0, 0, 150, 20);
        }

        return module;
    }

    public static void moveModule(HUDModule module, int x, int y) {
        module.setPosition(x, y);
    }

    public static void resizeModule(HUDModule module, int width, int height) {
        module.setSize(width, height);
    }

    public static void setModuleEnabled(HUDModule module, boolean enabled) {
        module.setVisible(enabled);
    }

    public static void setModuleAlpha(HUDModule module, float alpha) {
        module.setAlpha(alpha);
    }

    public static void setModuleColor(HUDModule module, int color) {
        module.setColor(color);
    }

    public static int getModuleCount() {
        return hudModules.size();
    }

    public static List<HUDModule> getAllModules() {
        return new ArrayList<>(hudModules);
    }

    public static List<HUDModule> getVisibleModules() {
        List<HUDModule> visible = new ArrayList<>();
        for (HUDModule module : hudModules) {
            if (module.isVisible()) {
                visible.add(module);
            }
        }
        return visible;
    }

    public static List<HUDModule> getInvisibleModules() {
        List<HUDModule> invisible = new ArrayList<>();
        for (HUDModule module : hudModules) {
            if (!module.isVisible()) {
                invisible.add(module);
            }
        }
        return invisible;
    }

    public static void sortModulesByPosition() {
        hudModules.sort((a, b) -> {
            if (a.getY() != b.getY()) {
                return Integer.compare(a.getY(), b.getY());
            }
            return Integer.compare(a.getX(), b.getX());
        });
    }

    public static void sortModulesByName() {
        hudModules.sort((a, b) -> a.getName().compareTo(b.getName()));
    }

    public static void sortModulesByType() {
        hudModules.sort((a, b) -> a.getType().compareTo(b.getType()));
    }

    public static void clearModules() {
        hudModules.clear();
    }

    public static void addModuleAtPosition(HUDModule module, int x, int y) {
        module.setPosition(x, y);
        addModule(module);
    }

    public static void removeAllModules() {
        hudModules.clear();
    }

    public static void resetAllModules() {
        hudModules.clear();
        initialize(null);
    }

    public static void saveLayout(String filename) {
        String json = "{\n";

        for (HUDModule module : hudModules) {
            json += "\t\"" + module.getName() + "\": {\n";
            json += "\t\t\"x\": " + module.getX() + ",\n";
            json += "\t\t\"y\": " + module.getY() + ",\n";
            json += "\t\t\"width\": " + module.getWidth() + ",\n";
            json += "\t\t\"height\": " + module.getHeight() + ",\n";
            json += "\t\t\"visible\": " + module.isVisible() + "\n";
            json += "\t},\n";
        }

        json += "}";

        try {
            java.io.FileWriter writer = new java.io.FileWriter(filename);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadLayout(String filename) {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename));
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            reader.close();

            String content = json.toString();
            int start = content.indexOf("{");
            int end = content.lastIndexOf("}");

            if (start != -1 && end != -1) {
                String jsonContent = content.substring(start, end + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MoonlitHUD getInstance() {
        return new MoonlitHUD();
    }
}
