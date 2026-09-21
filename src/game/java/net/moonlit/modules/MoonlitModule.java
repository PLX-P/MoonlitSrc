package net.moonlit.modules;

public abstract class MoonlitModule {

    protected boolean enabled = true;
    protected boolean visible = true;
    protected String name = "";
    protected String description = "";
    protected String category = "Misc";
    protected int keybind = 0;
    protected float radius = 10.0f;

    public MoonlitModule() {}

    public MoonlitModule(String name) {
        this.name = name;
    }

    public MoonlitModule(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public int getKeybind() {
        return keybind;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void update();

    public abstract void render();

    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public boolean isOn() {
        return enabled;
    }

    public boolean isOff() {
        return !enabled;
    }

    public void onEnable() {
        // Optional override
    }

    public void onDisable() {
        // Optional override
    }
}