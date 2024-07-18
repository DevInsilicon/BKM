package dev.insilicon.bkm.HUDRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class AddonBase {

    public boolean enabled = false;
    public String name;
    public Folders folder;
    public AddonBase(String name, Folders folder) {
        this.name = name;
        this.folder = folder;
    }

    public void render(DrawContext drawContext, float tickDelta) {

    }

    public void keypress(String key) {

    }

    public void tick() {

    }

    public void enabled() {

    }

    public void disabled() {

    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            enabled();
        } else {
            disabled();
        }
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static final int BASE_WIDTH = 2560;
    private static final int BASE_HEIGHT = 1440;

    public int getX(int originalX) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        return (int) ((double) originalX * screenWidth / BASE_WIDTH);
    }

    public int getY(int originalY) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenHeight = client.getWindow().getScaledHeight();
        return (int) ((double) originalY * screenHeight / BASE_HEIGHT);
    }


}
