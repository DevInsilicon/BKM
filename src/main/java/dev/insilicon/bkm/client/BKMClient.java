package dev.insilicon.bkm.client;

import dev.insilicon.bkm.HUDRenderer.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BKMClient implements ClientModInitializer {

    public static HudRenderer hudRenderer = new HudRenderer();
    private boolean setupGLFW = false;
    private GLFWKeyCallback keyCallback;
    private int entersPerHalfSecond = 0;
    private long lastTime = Util.getMeasuringTimeMs();
    private static final long INTERVAL_MS = 500; // 0.5 seconds


    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();

        //Events
        HudRenderCallback.EVENT.register(this::onHudRender);







    }

    private void onHudRender(DrawContext drawContext, float tickDelta) {
        if (setupGLFW == false) {
            Window window = MinecraftClient.getInstance().getWindow();
            if (window == null) {
                return;
            }
            long windowHandle = window.getHandle();


            keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if (action == GLFW.GLFW_PRESS) {
                        handleKeyPress(key);
                    }
                }
            };

            GLFW.glfwSetKeyCallback(windowHandle, keyCallback);
            setupGLFW = true;
        }

        long currentTime = Util.getMeasuringTimeMs();
        if (currentTime - lastTime >= INTERVAL_MS) {
            resetEntersPerHalfSecond();
            lastTime = currentTime;
        }

        hudRenderer.render(drawContext, tickDelta);
    }

    private void handleKeyPress(int key) {
        MinecraftClient client = MinecraftClient.getInstance();
        String keyString = keycodeToString(key);
        hudRenderer.keyPress(keyString);
    }

    private void resetEntersPerHalfSecond() {
        hudRenderer.entersPerHalfSecond(entersPerHalfSecond);
        entersPerHalfSecond = 0;
    }

    public static String keycodeToString(int keycode) {
        switch (keycode) {
            case GLFW.GLFW_KEY_A: return "A";
            case GLFW.GLFW_KEY_B: return "B";
            case GLFW.GLFW_KEY_C: return "C";
            case GLFW.GLFW_KEY_D: return "D";
            case GLFW.GLFW_KEY_E: return "E";
            case GLFW.GLFW_KEY_F: return "F";
            case GLFW.GLFW_KEY_G: return "G";
            case GLFW.GLFW_KEY_H: return "H";
            case GLFW.GLFW_KEY_J: return "J";
            case GLFW.GLFW_KEY_K: return "K";
            case GLFW.GLFW_KEY_L: return "L";
            case GLFW.GLFW_KEY_M: return "M";
            case GLFW.GLFW_KEY_N: return "N";
            case GLFW.GLFW_KEY_O: return "O";
            case GLFW.GLFW_KEY_P: return "P";
            case GLFW.GLFW_KEY_Q: return "Q";
            case GLFW.GLFW_KEY_I: return "I";
            case GLFW.GLFW_KEY_R: return "R";
            case GLFW.GLFW_KEY_S: return "S";
            case GLFW.GLFW_KEY_T: return "T";
            case GLFW.GLFW_KEY_U: return "U";
            case GLFW.GLFW_KEY_V: return "V";
            case GLFW.GLFW_KEY_W: return "W";
            case GLFW.GLFW_KEY_X: return "X";
            case GLFW.GLFW_KEY_Y: return "Y";
            case GLFW.GLFW_KEY_Z: return "Z";
            case GLFW.GLFW_KEY_0: return "0";
            case GLFW.GLFW_KEY_1: return "1";
            case GLFW.GLFW_KEY_2: return "2";
            case GLFW.GLFW_KEY_3: return "3";
            case GLFW.GLFW_KEY_4: return "4";
            case GLFW.GLFW_KEY_5: return "5";
            case GLFW.GLFW_KEY_6: return "6";
            case GLFW.GLFW_KEY_7: return "7";
            case GLFW.GLFW_KEY_8: return "8";
            case GLFW.GLFW_KEY_9: return "9";
            case GLFW.GLFW_KEY_SPACE: return " ";
            case GLFW.GLFW_KEY_ENTER: return "Enter";
            case GLFW.GLFW_KEY_TAB: return "Tab";
            case GLFW.GLFW_KEY_BACKSPACE: return "Backspace";
            case GLFW.GLFW_KEY_DELETE: return "Delete";
            case GLFW.GLFW_KEY_ESCAPE: return "Escape";
            case GLFW.GLFW_KEY_LEFT: return "Left";
            case GLFW.GLFW_KEY_RIGHT: return "Right";
            case GLFW.GLFW_KEY_UP: return "Up";
            case GLFW.GLFW_KEY_DOWN: return "Down";
            // Add more keycodes as needed
            default: return "Unknown Key";
        }
    }
}
