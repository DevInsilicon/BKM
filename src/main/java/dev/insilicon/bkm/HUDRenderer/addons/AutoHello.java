package dev.insilicon.bkm.HUDRenderer.addons;

import dev.insilicon.bkm.HUDRenderer.AddonBase;
import dev.insilicon.bkm.HUDRenderer.Folders;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class AutoHello extends AddonBase {
    private static final String MOD_FOLDER = "mods/BKM";
    private static final String FILE_PATH = MOD_FOLDER + "/players.txt";
    private Set<String> knownPlayerNames = new HashSet<>();
    private Set<String> playersAtEnable = new HashSet<>();
    private Map<String, Long> lastWelcomeTime = new HashMap<>();
    private static final long WELCOME_COOLDOWN = 300000; // 5 minutes in milliseconds
    private boolean initialized = false;
    private ClientWorld lastWorld = null;

    private BlockingQueue<WelcomeMessage> messageQueue = new LinkedBlockingQueue<>();
    private Thread messageProcessorThread;
    private volatile boolean running = false;

    public AutoHello() {
        super("AutoHello", Folders.STAFF);
        createModFolder();
        loadPlayerNames();
        System.out.println("AutoHello addon initialized. Known players: " + knownPlayerNames.size());
    }

    @Override
    public void enabled() {
        playersAtEnable.clear();
        lastWelcomeTime.clear();
        initializeCurrentPlayers();
        running = true;
        messageProcessorThread = new Thread(this::processMessages);
        messageProcessorThread.start();
        System.out.println("AutoHello addon enabled. Players at enable: " + playersAtEnable.size());
    }

    @Override
    public void disabled() {
        running = false;
        messageProcessorThread.interrupt();
        try {
            messageProcessorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        playersAtEnable.clear();
        lastWelcomeTime.clear();
        initialized = false;
        messageQueue.clear();
        lastWorld = null;
        System.out.println("AutoHello addon disabled.");
    }

    private void initializeCurrentPlayers() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
                String playerName = entry.getProfile().getName();
                playersAtEnable.add(playerName);
            }
            initialized = true;
        }
        System.out.println("Current players initialized. Players at enable: " + playersAtEnable.size());
    }

    @Override
    public void tick() {
        if (!isEnabled() || !initialized) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) return;

        ClientWorld currentWorld = client.world;
        if (currentWorld != lastWorld) {
            System.out.println("World changed. Reinitializing players.");
            lastWorld = currentWorld;
            playersAtEnable.clear();
            lastWelcomeTime.clear();
            initializeCurrentPlayers();
            return;
        }

        Set<String> currentPlayers = new HashSet<>();
        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            String playerName = entry.getProfile().getName();
            currentPlayers.add(playerName);

            if (!playersAtEnable.contains(playerName) && !playerName.startsWith("CIT")) {
                System.out.println("New player detected: " + playerName);
                handlePlayerJoin(entry);
                playersAtEnable.add(playerName);
                System.out.println("Player " + playerName + " handled and added to playersAtEnable.");
            }
        }

        // Check for players who have left
        Iterator<String> iterator = playersAtEnable.iterator();
        while (iterator.hasNext()) {
            String player = iterator.next();
            if (!currentPlayers.contains(player)) {
                System.out.println("Player left: " + player);
                iterator.remove();
                lastWelcomeTime.remove(player);
            }
        }
    }

    private void handlePlayerJoin(PlayerListEntry entry) {
        String playerName = entry.getProfile().getName();
        long currentTime = System.currentTimeMillis();

        System.out.println("Handling player join: " + playerName);

        if (lastWelcomeTime.containsKey(playerName)) {
            long timeSinceLastWelcome = currentTime - lastWelcomeTime.get(playerName);
            if (timeSinceLastWelcome < WELCOME_COOLDOWN) {
                System.out.println("Skipping welcome for " + playerName + " (cooldown active)");
                return;
            }
        }

        if (!knownPlayerNames.contains(playerName)) {
            knownPlayerNames.add(playerName);
            savePlayerName(playerName);
            queueMessage("Welcome " + playerName + "!");
            System.out.println("Queued welcome message for first-time player: " + playerName);
        } else {
            queueMessage("Welcome back " + playerName + "!");
            System.out.println("Queued welcome back message for returning player: " + playerName);
        }

        lastWelcomeTime.put(playerName, currentTime);
    }

    private void queueMessage(String message) {
        long delay = ThreadLocalRandom.current().nextLong(2000, 4001); // 2-4 seconds delay
        messageQueue.offer(new WelcomeMessage(message, System.currentTimeMillis() + delay));
        System.out.println("Message queued: " + message + " (Delay: " + delay + "ms)");
    }

    private void processMessages() {
        System.out.println("Message processor thread started.");
        while (running) {
            try {
                WelcomeMessage message = messageQueue.take();
                System.out.println("Processing message: " + message.content);
                long now = System.currentTimeMillis();
                if (now < message.sendTime) {
                    Thread.sleep(message.sendTime - now);
                }
                sendMessage(message.content);
                System.out.println("Message sent: " + message.content);
                Thread.sleep(3000); // 3 seconds between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Message processor thread interrupted.");
                break;
            }
        }
        System.out.println("Message processor thread stopped.");
    }

    private void sendMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendChatMessage(message);
            System.out.println("Chat message sent: " + message);
        } else {
            System.out.println("Failed to send message: NetworkHandler is null");
        }
    }

    private void createModFolder() {
        File modFolder = new File(MOD_FOLDER);
        if (!modFolder.exists()) {
            modFolder.mkdirs();
            System.out.println("Created mod folder: " + MOD_FOLDER);
        }
    }

    private void loadPlayerNames() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String name = line.trim();
                    if (!name.isEmpty()) {
                        knownPlayerNames.add(name);
                    }
                }
                System.out.println("Loaded " + knownPlayerNames.size() + " player names from file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Player names file does not exist. Starting with empty known players list.");
        }
    }

    private void savePlayerName(String playerName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(playerName);
            writer.newLine();
            System.out.println("Saved new player name to file: " + playerName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class WelcomeMessage {
        String content;
        long sendTime;

        WelcomeMessage(String content, long sendTime) {
            this.content = content;
            this.sendTime = sendTime;
        }
    }
}