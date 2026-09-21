package net.moonlit.ui;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class MoonlitTextField extends MoonlitComponent {

    private String text = "";
    private String placeholder = "";
    private boolean focused = false;
    private boolean obscured = false;
    private int cursorIndex = 0;
    private long lastCursorBlink = 0;
    private boolean cursorVisible = true;

    private static final int CURSOR_BLINK_MS = 500;

    public MoonlitTextField(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String placeholder) {
        super(mc, fontRenderer, x, y, width, height);
        this.placeholder = placeholder;
        this.height = height;
    }

    public MoonlitTextField(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(mc, fontRenderer, x, y, width, height);
    }

    public void setText(String text) {
        this.text = text;
        if (cursorIndex > text.length()) {
            cursorIndex = text.length();
        }
    }

    public String getText() {
        return text;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setObscured(boolean obscured) {
        this.obscured = obscured;
    }

    public boolean isObscured() {
        return obscured;
    }

    public void setFocused(boolean focused) {
        boolean changed = this.focused != focused;
        this.focused = focused;
        if (focused) {
            lastCursorBlink = System.currentTimeMillis();
            cursorVisible = true;
        }
    }

    public boolean isFocused() {
        return focused;
    }

    public void insertText(char character, int codePoint) {
        if (!focused) return;

        String beforeCursor = text.substring(0, cursorIndex);
        String afterCursor = text.substring(cursorIndex);

        if (codePoint > 0 && codePoint < 0x10000) {
            String characterString = String.valueOf(character);
            text = beforeCursor + characterString + afterCursor;
            cursorIndex += characterString.length();
        }
    }

    public void deleteText() {
        if (!focused || cursorIndex <= 0) return;

        text = text.substring(0, cursorIndex - 1) + text.substring(cursorIndex);
        cursorIndex--;
    }

    public void deleteTextForward() {
        if (!focused || cursorIndex >= text.length()) return;

        text = text.substring(0, cursorIndex) + text.substring(cursorIndex + 1);
    }

    public void setCursorIndex(int index) {
        if (index < 0) index = 0;
        if (index > text.length()) index = text.length();
        cursorIndex = index;
    }

    public int getCursorIndex() {
        return cursorIndex;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        hovered = contains(mouseX, mouseY) && enabled;

        int bgColor = focused ? 0xFF333355 : (hovered && enabled ? 0xFF333333 : 0xFF222222);
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF444444;
        if (focused) {
            borderColor = 0xFF8888FF;
        }

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        if (text.isEmpty() && !placeholder.isEmpty() && fontRenderer != null) {
            fontRenderer.drawString(placeholder, x + 8, y + (height - 8) / 2, 0xFF666666);
        } else if (fontRenderer != null) {
            String displayText = obscured ? "•".repeat(text.length()) : text;

            fontRenderer.drawString(displayText, x + 8, y + (height - 8) / 2, focused ? 0xFFCCCCFF : 0xFFCCCCCC);
        }

        if (focused) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCursorBlink >= CURSOR_BLINK_MS) {
                cursorVisible = !cursorVisible;
                lastCursorBlink = currentTime;
            }

            if (cursorVisible && fontRenderer != null) {
                String beforeCursor = text.substring(0, cursorIndex);
                int cursorX = x + 8 + fontRenderer.getStringWidth(beforeCursor);
                int cursorY = y + 2;

                fontRenderer.drawString("|", cursorX, cursorY, 0xFFCCCCFF);
            }
        }

        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setFocused(true);
            if (!Mouse.isButtonDown(0)) {
                updateCursorIndexFromMouse(mouseX);
            }
        } else {
            setFocused(false);
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (contains(mouseX, mouseY) && enabled) {
            setFocused(true);
            if (!Mouse.isButtonDown(0)) {
                updateCursorIndexFromMouse(mouseX);
            }
        } else {
            setFocused(false);
        }
    }

    private void updateCursorIndexFromMouse(int mouseX) {
        if (fontRenderer == null) return;

        String displayText = obscured ? "•".repeat(text.length()) : text;
        int cursorX = x + 8;
        int targetX = mouseX - cursorX;

        int index = 0;
        int currentX = 0;

        for (int i = 0; i < displayText.length(); i++) {
            char c = displayText.charAt(i);
            int charWidth = fontRenderer.getStringWidth(String.valueOf(c));

            if (currentX + charWidth / 2 >= targetX) {
                index = i;
                break;
            }

            currentX += charWidth;
            index = i + 1;
        }

        cursorIndex = index;
    }

    @Override
    public void tick() {
        super.tick();
        if (focused) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCursorBlink >= CURSOR_BLINK_MS) {
                cursorVisible = !cursorVisible;
                lastCursorBlink = currentTime;
            }
        }
    }
}
