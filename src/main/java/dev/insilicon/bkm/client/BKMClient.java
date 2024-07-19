package dev.insilicon.bkm.client;

import dev.insilicon.bkm.HUDRenderer.HudRenderer;
import dev.insilicon.bkm.HUDRenderer.addons.AutoHello;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class BKMClient implements ClientModInitializer {

    public static HudRenderer hudRenderer = new HudRenderer();
    private int entersPerHalfSecond = 0;
    private long lastTime = Util.getMeasuringTimeMs();
    private static final long INTERVAL_MS = 500; // 0.5 seconds

    // Define key bindings
    private static KeyBinding keyBindingUp;
    private static KeyBinding keyBindingDown;
    private static KeyBinding keyBindingLeft;
    private static KeyBinding keyBindingRight;
    private static KeyBinding keyBindingEnter;

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Register key bindings
        keyBindingUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bkm.up",
                GLFW.GLFW_KEY_UP,
                "category.bkm"
        ));
        keyBindingDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bkm.down",
                GLFW.GLFW_KEY_DOWN,
                "category.bkm"
        ));
        keyBindingLeft = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bkm.left",
                GLFW.GLFW_KEY_LEFT,
                "category.bkm"
        ));
        keyBindingRight = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bkm.right",
                GLFW.GLFW_KEY_RIGHT,
                "category.bkm"
        ));
        keyBindingEnter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bkm.enter",
                GLFW.GLFW_KEY_ENTER,
                "category.bkm"
        ));



        // Events
        HudRenderCallback.EVENT.register(this::onHudRender);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onHudRender(DrawContext drawContext, float tickDelta) {
        long currentTime = Util.getMeasuringTimeMs();
        if (currentTime - lastTime >= INTERVAL_MS) {
            resetEntersPerHalfSecond();
            lastTime = currentTime;
        }

        hudRenderer.render(drawContext, tickDelta);
    }

    private void onClientTick(MinecraftClient client) {
        if (client.currentScreen == null) {
            // Check for key presses using the registered key bindings
            if (keyBindingUp.wasPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_UP);
            }
            if (keyBindingDown.wasPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_DOWN);
            }
            if (keyBindingLeft.wasPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_LEFT);
            }
            if (keyBindingRight.wasPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_RIGHT);
            }
            if (keyBindingEnter.wasPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_ENTER);
            }
        }

        hudRenderer.tick();
    }

    private void handleKeyPress(int key) {
        String keyString = keycodeToString(key);
        hudRenderer.keyPress(keyString);
    }

    private void resetEntersPerHalfSecond() {
        hudRenderer.entersPerHalfSecond(entersPerHalfSecond);
        entersPerHalfSecond = 0;
    }

    public static String keycodeToString(int keycode) {
        switch (keycode) {
            case 263:
                return "LEFT";
            case 264:
                return "DOWN";
            case 265:
                return "UP";
            case 262:
                return "RIGHT";
            case 257:
                return "ENTER";
            default:
                return "UNKNOWN";
        }
    }

    public BKMClient ClientModInitializer() {
        return this;
    }
}