package net.moonlit.ui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.opengl.GlStateManager;

public class MoonlitUIManager {

    private static MoonlitUIManager instance;
    private final List<MoonlitComponent> components = new ArrayList<>();
    private MoonlitComponent focusedComponent;
    private boolean isHUDOpen;

    private MoonlitUIManager() {}

    public static MoonlitUIManager getInstance() {
        if (instance == null) {
            instance = new MoonlitUIManager();
        }
        return instance;
    }

    public void registerComponent(MoonlitComponent component) {
        components.add(component);
    }

    public void unregisterComponent(MoonlitComponent component) {
        components.remove(component);
        if (focusedComponent == component) {
            focusedComponent = null;
        }
    }

    public void setFocusedComponent(MoonlitComponent component) {
        if (focusedComponent != null) {
            focusedComponent.onFocusLost();
        }
        focusedComponent = component;
        if (focusedComponent != null) {
            focusedComponent.onFocusGained();
        }
    }

    public MoonlitComponent getFocusedComponent() {
        return focusedComponent;
    }

    public void openHUD() {
        isHUDOpen = true;
    }

    public void closeHUD() {
        isHUDOpen = false;
    }

    public boolean isHUDOpen() {
        return isHUDOpen;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!isHUDOpen) {
            return;
        }

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_SRC_ALPHA,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA,
                1, 0);
        GlStateManager.blendFunc(
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_SRC_ALPHA,
                net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        for (MoonlitComponent component : components) {
            if (component.isVisible()) {
                component.render(mouseX, mouseY, partialTicks);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public void handleMouseInput(int mouseX, int mouseY, int mouseButton, int mouseState) {
        if (!isHUDOpen) {
            return;
        }

        MoonlitComponent clickedComponent = null;
        for (int i = components.size() - 1; i >= 0; i--) {
            MoonlitComponent component = components.get(i);
            if (component.isVisible() && component.contains(mouseX, mouseY)) {
                clickedComponent = component;
                break;
            }
        }

        if (clickedComponent != null && mouseState == 0) {
            clickedComponent.onMousePressed(mouseX, mouseY, mouseButton);
        }

        if (focusedComponent != null) {
            focusedComponent.onMouseDrag(mouseX, mouseY, mouseButton);
        }
    }

    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!isHUDOpen) {
            return;
        }

        for (int i = components.size() - 1; i >= 0; i--) {
            MoonlitComponent component = components.get(i);
            if (component.isVisible() && component.contains(mouseX, mouseY)) {
                component.onMouseClicked(mouseX, mouseY, mouseButton);
                if (component instanceof MoonlitButton) {
                    break;
                }
            }
        }
    }

    public void handleMouseScroll(int mouseX, int mouseY, float scrollAmount) {
        if (!isHUDOpen) {
            return;
        }

        MoonlitComponent component = null;
        for (int i = components.size() - 1; i >= 0; i--) {
            MoonlitComponent comp = components.get(i);
            if (comp.isVisible() && comp.contains(mouseX, mouseY)) {
                if (comp instanceof MoonlitScrollPanel) {
                    component = comp;
                    break;
                }
            }
        }

        if (component != null) {
            ((MoonlitScrollPanel) component).scroll(scrollAmount);
        }
    }

    public void handleTextInput(char typedChar, int typedCodePoint) {
        if (!isHUDOpen || focusedComponent == null) {
            return;
        }

        if (focusedComponent instanceof MoonlitTextField) {
            ((MoonlitTextField) focusedComponent).insertText(typedChar, typedCodePoint);
        }
    }

    public void handleKeyInput(int keyCode, int scanCode, int modifiers, boolean isDown) {
        if (!isHUDOpen) {
            return;
        }

        if (isDown && keyCode == net.minecraft.client.settings.KeyBinding.getKeyCodeFromName("key.mouseButtonLeft")) {
            handleMouseClick(Mouse.getX(), Mouse.getY(), 0);
        }
        if (isDown && keyCode == net.minecraft.client.settings.KeyBinding.getKeyCodeFromName("key.mouseButtonRight")) {
            handleMouseClick(Mouse.getX(), Mouse.getY(), 1);
        }
        if (isDown && keyCode == net.minecraft.client.settings.KeyBinding.getKeyCodeFromName("key.mouseButtonMiddle")) {
            handleMouseClick(Mouse.getX(), Mouse.getY(), 2);
        }
    }

    public void tick() {
        for (MoonlitComponent component : components) {
            component.tick();
        }
    }

    public void clear() {
        components.clear();
        focusedComponent = null;
    }
}
