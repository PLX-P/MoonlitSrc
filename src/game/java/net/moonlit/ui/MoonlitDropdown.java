package net.moonlit.ui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitDropdown extends MoonlitComponent {

    private String selectedOption;
    private final List<String> options = new ArrayList<>();
    private final List<Runnable<String>> onOptionSelect = new ArrayList<>();

    private boolean isOpen = false;
    private int dropdownHeight = 200;

    public MoonlitDropdown(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height, String label) {
        super(mc, fontRenderer, x, y, width, height);
        this.selectedOption = "";
    }

    public void setOptions(String... options) {
        this.options.clear();
        for (String option : options) {
            this.options.add(option);
        }
        if (!this.options.isEmpty()) {
            this.selectedOption = this.options.get(0);
        }
    }

    public void addOption(String option) {
        options.add(option);
        if (selectedOption == null || selectedOption.isEmpty()) {
            selectedOption = option;
        }
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String option) {
        if (options.contains(option)) {
            this.selectedOption = option;
        }
    }

    public void setOnOptionSelect(Runnable<String> listener) {
        onOptionSelect.add(listener);
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        hovered = contains(mouseX, mouseY) && enabled;

        int bgColor = hovered && enabled ? 0xFF333333 : 0xFF222222;
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF444444;

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        int dropdownX = x + 8;
        int dropdownY = y + (height - 14) / 2;
        int dropdownWidth = width - 16;

        if (isOpen) {
            int dropdownBgHeight = options.size() * 20 + 20;

            drawBorderedRoundedRect(x, y + height, width, dropdownBgHeight, 0xFF2A2A2A, 0xFF444444, 1.0f, 4.0f);
            drawBorderedRoundedRect(x + 4, y + height + 4, width - 8, dropdownBgHeight - 8, 0xFF1A1A1A, 0xFF333333, 1.0f, 3.0f);

            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i);
                int optionY = y + height + 8 + i * 20;

                int optionBgColor = hovered && enabled ? 0xFF333333 : 0xFF222222;

                if (mouseX >= x + 4 && mouseX < x + width - 4 &&
                    mouseY >= y + height + 4 && mouseY < y + height + dropdownBgHeight - 4) {
                    optionBgColor = 0xFF333333;
                    if (enabled && Mouse.isButtonDown(0)) {
                        Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
                    }
                }

                net.moonlit.render.MoonlitRenderer.drawRoundedRect(
                        x + 4, optionY, width - 8, 20, optionBgColor, 3.0f);

                if (fontRenderer != null) {
                    fontRenderer.drawString(option, x + 12, optionY + 8, 0xFFCCCCCC);
                }

                if (i == options.size() - 1) {
                    drawBorderedRoundedRect(x + 4, optionY + 20 - 4, width - 8, 4, 0x00000000, 0x00000000, 0, 0);
                }
            }

            if (fontRenderer != null) {
                fontRenderer.drawString(selectedOption, x + 12, dropdownY, 0xFFCCCCCC);
            }
        } else {
            if (fontRenderer != null) {
                fontRenderer.drawString(selectedOption, dropdownX, dropdownY, 0xFFCCCCCC);
            }
        }

        int arrowX = x + width - 16;
        int arrowY = y + (height - 10) / 2;

        fontRenderer.drawString(isOpen ? "\u25BC" : "\u25B6", arrowX, arrowY, hovered && enabled ? 0xFFCCCCCC : 0xFF888888);

        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        super.onMousePressed(mouseX, mouseY, mouseButton);

        if (!enabled || !visible) return;

        if (mouseButton == 0 && contains(mouseX, mouseY)) {
            isOpen = !isOpen;
            return;
        }

        if (isOpen) {
            int dropdownBgHeight = options.size() * 20 + 20;

            if (mouseX >= x + 4 && mouseX < x + width - 4 &&
                mouseY >= y + height + 4 && mouseY < y + height + dropdownBgHeight - 4) {
                int index = (mouseY - (y + height + 8)) / 20;
                if (index >= 0 && index < options.size()) {
                    String selected = options.get(index);
                    setSelectedOption(selected);
                    for (Runnable<String> listener : onOptionSelect) {
                        listener.run(selected);
                    }
                    isOpen = false;
                }
            } else {
                isOpen = false;
            }
        }
    }

    @Override
    public void onMouseDrag(int mouseX, int mouseY, int mouseButton) {
        super.onMouseDrag(mouseX, mouseY, mouseButton);
        if (!enabled || !visible) return;

        if (isOpen) {
            int dropdownBgHeight = options.size() * 20 + 20;

            if (mouseX >= x + 4 && mouseX < x + width - 4 &&
                mouseY >= y + height + 4 && mouseY < y + height + dropdownBgHeight - 4) {
                int index = (mouseY - (y + height + 8)) / 20;
                if (index >= 0 && index < options.size()) {
                    String selected = options.get(index);
                    setSelectedOption(selected);
                    for (Runnable<String> listener : onOptionSelect) {
                        listener.run(selected);
                    }
                    isOpen = false;
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (hovered && enabled) {
            Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
        }
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }
}
