package dev.insilicon.bkm.client;

import dev.insilicon.bkm.HUDRenderer.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.*;
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

    // Define key bindings
    private static KeyBinding keyBindingUp;
    private static KeyBinding keyBindingDown;
    private static KeyBinding keyBindingLeft;
    private static KeyBinding keyBindingRight;
    private static KeyBinding keyBindingEnter;

    @Override
    public void onInitializeClient() {
        createModFolder();
        loadPlayerUUIDs();

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

    public void onPlayerJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
        ClientPlayerEntity playerJoining = client.player;

        if (playerJoining == null) {
            System.err.println("Player information is not available yet.");
            return;
        }

        UUID playerUUID = playerJoining.getUuid();
        boolean isFirstTime = !playerUUIDs.contains(playerUUID);

        System.out.println("Player UUID: " + playerUUID);
        System.out.println("Is first time joining: " + isFirstTime);

        if (isFirstTime) {
            // Add the player's UUID to the set and save it
            playerUUIDs.add(playerUUID);
            savePlayerUUID(playerUUID);

            // Send welcome message to the player
            String message = "Welcome " + playerJoining.getName().getString() + "! It's your first time joining!";
            sendMessageToServer(message);
        } else {
            // Send regular welcome message
            String message = "Welcome back " + playerJoining.getName().getString() + "!";
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
