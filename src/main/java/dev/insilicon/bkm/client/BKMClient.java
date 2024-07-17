package dev.insilicon.bkm.client;

import dev.insilicon.bkm.HUDRenderer.HudRenderer;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Window;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BKMClient implements ClientModInitializer {

    public static HudRenderer hudRenderer = new HudRenderer();
    private int entersPerHalfSecond = 0;
    private long lastTime = Util.getMeasuringTimeMs();
    private static final String MOD_FOLDER = "mods/BKM";
    private static final String FILE_PATH = MOD_FOLDER + "/players.txt";
    private static Set<UUID> playerUUIDs = new HashSet<>();
    private static final long INTERVAL_MS = 500; // 0.5 seconds

    @Override
    public void onInitializeClient() {
        createModFolder();
        loadPlayerUUIDs();

        MinecraftClient client = MinecraftClient.getInstance();

        // Events
        HudRenderCallback.EVENT.register(this::onHudRender);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        ClientPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
    }

    private void onHudRender(DrawContext drawContext, float tickDelta) {
        long currentTime = Util.getMeasuringTimeMs();
        if (currentTime - lastTime >= INTERVAL_MS) {
            resetEntersPerHalfSecond();
            lastTime = currentTime;
        }

        hudRenderer.render(drawContext, tickDelta);
    }

    public void onPlayerJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient playerJoining) {
        UUID playerUUID = playerJoining.player.getUuid();
        boolean isFirstTime = !playerUUIDs.contains(playerUUID);

        if (isFirstTime) {
            // Add the player's UUID to the set and save it
            playerUUIDs.add(playerUUID);
            savePlayerUUID(playerUUID);

            // Send welcome message to the player
            String message = "Welcome " + playerJoining.player.getName().getString() + "! It's your first time joining!";
            sendMessageToServer(message);
        } else {
            // Send regular welcome message
            String message = "Welcome back " + playerJoining.player.getName().getString() + "!";
            sendMessageToServer(message);
        }
    }

    private static void savePlayerUUID(UUID playerUUID) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(playerUUID.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessageToServer(String message) {
        MinecraftClient local = MinecraftClient.getInstance();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(message);

        ChatMessageC2SPacket packet = new ChatMessageC2SPacket(buf);
        local.getNetworkHandler().sendPacket(packet);
    }

    private void onClientTick(MinecraftClient client) {
        if (client.currentScreen == null) {
            if (client.options.forwardKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_W);
            }
            if (client.options.leftKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_A);
            }
            if (client.options.backKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_S);
            }
            if (client.options.rightKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_D);
            }
            if (client.options.jumpKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_SPACE);
            }
            if (client.options.sneakKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_LEFT_SHIFT);
            }
            if (client.options.sprintKey.isPressed()) {
                handleKeyPress(GLFW.GLFW_KEY_LEFT_CONTROL);
            }
        }
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

    private static void loadPlayerUUIDs() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    playerUUIDs.add(UUID.fromString(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createModFolder() {
        File modFolder = new File(MOD_FOLDER);
        if (!modFolder.exists()) {
            modFolder.mkdirs();
        }
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
