package net.moonlit.ui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MoonlitScrollPanel extends MoonlitComponent {

    private final List<MoonlitComponent> children = new ArrayList<>();

    private int contentHeight;
    private int scrollPosition;
    private int maxScroll;
    private boolean scrolling;
    private float scrollSpeed = 1.0f;

    private int backgroundColor = 0xFF1A1A1A;
    private int scrollBarColor = 0xFF404040;
    private float scrollBarWidth = 8.0f;

    private static final int SCROLLBAR_THUMB_HEIGHT = 40;

    public MoonlitScrollPanel(Minecraft mc, FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(mc, fontRenderer, x, y, width, height);
        contentHeight = height;
        scrollPosition = 0;
        maxScroll = 0;
    }

    public void addChild(MoonlitComponent component) {
        children.add(component);
        recalculateContentHeight();
    }

    public void removeChild(MoonlitComponent component) {
        children.remove(component);
        recalculateContentHeight();
    }

    public void clearChildren() {
        children.clear();
        recalculateContentHeight();
    }

    private void recalculateContentHeight() {
        contentHeight = height;

        for (MoonlitComponent child : children) {
            contentHeight += child.getHeight() + 4;
        }

        maxScroll = Math.max(0, contentHeight - height);
        scrollPosition = Math.max(0, Math.min(scrollPosition, maxScroll));
    }

    public List<MoonlitComponent> getChildren() {
        return new ArrayList<>(children);
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int position) {
        scrollPosition = Math.max(0, Math.min(position, maxScroll));
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public void setScrollBarColor(int color) {
        this.scrollBarColor = color;
    }

    public void setScrollBarWidth(float width) {
        this.scrollBarWidth = width;
    }

    public void scroll(float amount) {
        scrollPosition = Math.max(0, Math.min(maxScroll, scrollPosition - (int) (amount * scrollSpeed)));
    }

    public void scrollToBottom() {
        scrollPosition = maxScroll;
    }

    public void scrollToTop() {
        scrollPosition = 0;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!visible) return;

        hovered = contains(mouseX, mouseY) && enabled;

        int bgColor = hovered && enabled ? 0xFF1E1E1E : backgroundColor;
        int borderColor = hovered && enabled ? 0xFF888888 : 0xFF505050;

        drawBorderedRoundedRect(x, y, width, height, bgColor, borderColor, 1.0f, 4.0f);

        int contentWidth = width - 20;
        int contentX = x + 10;
        int contentY = y + 10 - scrollPosition;

        for (int i = 0; i < children.size(); i++) {
            MoonlitComponent child = children.get(i);

            int childX = contentX;
            int childY = contentY + i * (child.getHeight() + 4);

            if (child.isVisible() && childY + child.getHeight() > y + 10 && childY < y + height - 10) {
                child.setPosition(childX, childY);
                child.render(mouseX, mouseY, partialTicks);
            }
        }

        if (maxScroll > 0) {
            int scrollBarX = x + width - (int) scrollBarWidth - 5;
            int scrollBarY = y + 10;

            int thumbHeight = Math.max(SCROLLBAR_THUMB_HEIGHT, height * SCROLLBAR_THUMB_HEIGHT / (contentHeight + SCROLLBAR_THUMB_HEIGHT));

            int thumbY = scrollBarY + (int) ((float) scrollPosition / maxScroll * (height - 20 - thumbHeight));

            net.moonlit.render.MoonlitRenderer.drawRoundedRect(scrollBarX, scrollBarY, (int) scrollBarWidth, height - 20, scrollBarColor, 3.0f);
            net.moonlit.render.MoonlitRenderer.drawRoundedRect(scrollBarX, thumbY, (int) scrollBarWidth, thumbHeight, 0xFF606060, 3.0f);

            hovered = mouseX >= scrollBarX && mouseX < scrollBarX + (int) scrollBarWidth &&
                    mouseY >= scrollBarY && mouseY < scrollBarY + height - 20;

            if (hovered) {
                Mouse.showCursor(net.lax1dude.eaglercraft.internal.EnumCursorType.HAND);
            }
        }
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        super.onMousePressed(mouseX, mouseY, mouseButton);

        if (!enabled || !visible) return;

        if (maxScroll > 0) {
            int scrollBarX = x + width - (int) scrollBarWidth - 5;
            int scrollBarY = y + 10;

            if (mouseX >= scrollBarX && mouseX < scrollBarX + (int) scrollBarWidth &&
                    mouseY >= scrollBarY && mouseY < scrollBarY + height - 20) {
                scrolling = true;

                int thumbHeight = Math.max(SCROLLBAR_THUMB_HEIGHT, height * SCROLLBAR_THUMB_HEIGHT / (contentHeight + SCROLLBAR_THUMB_HEIGHT));
                int thumbY = scrollBarY + (int) ((float) scrollPosition / maxScroll * (height - 20 - thumbHeight));

                if (mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
                    scrollPosition = (int) ((float) (mouseY - scrollBarY) / (height - 20) * maxScroll);
                } else if (mouseY < thumbY) {
                    scrollToTop();
                } else {
                    scrollToBottom();
                }
            }
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            MoonlitComponent child = children.get(i);
            int childX = x + 10;
            int childY = y + 10 - scrollPosition + i * (child.getHeight() + 4);

            if (child.isVisible() && child.contains(mouseX, mouseY)) {
                child.onMousePressed(mouseX, mouseY, mouseButton);
                break;
            }
        }
    }

    @Override
    public void onMouseDrag(int mouseX, int mouseY, int mouseButton) {
        super.onMouseDrag(mouseX, mouseY, mouseButton);

        if (scrolling && enabled) {
            int scrollBarX = x + width - (int) scrollBarWidth - 5;
            int scrollBarY = y + 10;
            int scrollBarHeight = height - 20;

            scrollPosition = (int) ((float) (mouseY - scrollBarY) / scrollBarHeight * maxScroll);
            scrollPosition = Math.max(0, Math.min(maxScroll, scrollPosition));
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.onMouseReleased(mouseX, mouseY, mouseButton);
        scrolling = false;
    }

    @Override
    public void tick() {
        super.tick();
        for (MoonlitComponent child : children) {
            child.tick();
        }
        recalculateContentHeight();
    }
}
