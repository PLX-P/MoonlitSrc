package net.moonlit.modules;

import java.util.ArrayList;
import java.util.List;

public class MoonlitModuleManager {

    private static MoonlitModuleManager instance;
    private final List<MoonlitModule> modules = new ArrayList<>();
    private final List<String> enabledModules = new ArrayList<>();
    private final List<String> disabledModules = new ArrayList<>();
    private boolean initialized = false;

    private MoonlitModuleManager() {}

    public static MoonlitModuleManager getInstance() {
        if (instance == null) {
            instance = new MoonlitModuleManager();
        }
        return instance;
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;
    }

    public void registerModule(MoonlitModule module) {
        if (!modules.contains(module)) {
            modules.add(module);
        }
        if (!enabledModules.contains(module.getName()) && !disabledModules.contains(module.getName())) {
            enabledModules.add(module.getName());
        }
    }

    public void unregisterModule(MoonlitModule module) {
        modules.remove(module);
        enabledModules.remove(module.getName());
        disabledModules.remove(module.getName());
    }

    public void enableModule(String name) {
        MoonlitModule module = getModule(name);
        if (module != null) {
            module.setEnabled(true);
            if (!enabledModules.contains(name)) {
                enabledModules.add(name);
            }
            disabledModules.remove(name);
        }
    }

    public void disableModule(String name) {
        MoonlitModule module = getModule(name);
        if (module != null) {
            module.setEnabled(false);
            if (!disabledModules.contains(name)) {
                disabledModules.add(name);
            }
            enabledModules.remove(name);
        }
    }

    public void toggleModule(String name) {
        MoonlitModule module = getModule(name);
        if (module != null) {
            if (module.isEnabled()) {
                disableModule(name);
            } else {
                enableModule(name);
            }
        }
    }

    public boolean isModuleEnabled(String name) {
        MoonlitModule module = getModule(name);
        return module != null && module.isEnabled();
    }

    public MoonlitModule getModule(String name) {
        for (MoonlitModule module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    public List<MoonlitModule> getModules() {
        return new ArrayList<>(modules);
    }

    public List<String> getEnabledModuleNames() {
        return new ArrayList<>(enabledModules);
    }

    public List<String> getDisabledModuleNames() {
        return new ArrayList<>(disabledModules);
    }

    public List<MoonlitModule> getEnabledModules() {
        List<MoonlitModule> enabled = new ArrayList<>();
        for (MoonlitModule module : modules) {
            if (module.isEnabled()) {
                enabled.add(module);
            }
        }
        return enabled;
    }

    public List<MoonlitModule> getDisabledModules() {
        List<MoonlitModule> disabled = new ArrayList<>();
        for (MoonlitModule module : modules) {
            if (!module.isEnabled()) {
                disabled.add(module);
            }
        }
        return disabled;
    }

    public void update() {
        for (MoonlitModule module : modules) {
            if (module.isEnabled()) {
                module.update();
            }
        }
    }

    public void render() {
        for (MoonlitModule module : modules) {
            if (module.isEnabled()) {
                module.render();
            }
        }
    }

    public void shutdown() {
        for (MoonlitModule module : modules) {
            module.disable();
        }
        modules.clear();
        enabledModules.clear();
        disabledModules.clear();
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
