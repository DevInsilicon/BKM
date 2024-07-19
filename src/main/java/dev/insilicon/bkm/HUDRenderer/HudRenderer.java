package dev.insilicon.bkm.HUDRenderer;

import dev.insilicon.bkm.HUDRenderer.addons.AutoHello;
import dev.insilicon.bkm.HUDRenderer.addons.AutoSprint;
import dev.insilicon.bkm.HUDRenderer.addons.NameESP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {

    private static final int BASE_WIDTH = 2560;
    private static final int BASE_HEIGHT = 1440;

    private boolean Open = false;
    private int X = 0;
    private int Y = -1;
    private int Z = -1;


    // Settings
    private int addonHeight = 100;

    private List<AddonBase> addons = new ArrayList<>();

    public HudRenderer() {

        addons.add(new AutoSprint());
        addons.add(new NameESP());
        addons.add(new AutoHello());

    }

    public void tick() {
        for (AddonBase addon : addons) {
            if (addon.getEnabled()) {
                addon.tick();

            }
        }
    }

    public void render(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        if (client.player == null) {
            return;
        }
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();


        // non-transparent Background
        int backgroundWidth = getX(600 / 2);
        int backgroundHeight = getY(550);

        int backgroundX = getX(5);
        int backgroundY = getY(10);

        //drawContext.fillGradient(backgroundX, backgroundY, backgroundX + backgroundWidth, backgroundY + backgroundHeight, 0x80000000, 0x80000000);


        // Visualized Folders
        // Folders.values();

        for (int i = 0; i < Folders.values().length; i++) {
            Folders folder = Folders.values()[i];
            int addonX = getX(10);
            int addonY = getY(10 + i * addonHeight);

            int addonWidth = getX(600 / 2 - 20);
            int addonSizeHeight = getY(addonHeight);

            int addonColor = 0x80000000;
            if (this.X == i) {
                addonColor = 0xA0000000; //
                if (this.Open) {
                    addonColor = 0x801E75FF;
                }
            }

            drawContext.fillGradient(addonX, addonY, addonX + addonWidth, addonY + addonSizeHeight, addonColor, addonColor);

            // Put the name of the folder
            Text text = Text.of(folder.getDisplay());
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, folder.getDisplay(), (addonX + 10), (addonY + 5), 0xFFFFFF, true);
        }

        if (!this.Open) {
            return;
        }

        int currentAddonIndex = 0;
        for (int i = 0; i < addons.size(); i++) {
            AddonBase addon = addons.get(i);
            if (addon.folder != Folders.values()[this.X]) {
                continue;
            }

            int addonX = getX(310);
            int addonY = getY(10 + currentAddonIndex * addonHeight); // Use currentAddonIndex for position

            int addonWidth = getX(600 / 2 - 20);
            int addonSizeHeight = getY(addonHeight);

            int addonColor = 0x80000000;
            if (this.Y == currentAddonIndex) { // Compare with currentAddonIndex
                addonColor = 0xA0000000;
            }
            if (addon.getEnabled()) {
                addonColor = 0x801E75FF;
            }

            drawContext.fillGradient(addonX, addonY, addonX + addonWidth, addonY + addonSizeHeight, addonColor, addonColor);

            // Put the name of the addon
            Text text = Text.of(addon.getName());
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, addon.getName(), (addonX + 10), (addonY + 5), 0xFFFFFF, true);

            currentAddonIndex++; // Increment the index for the current folder
        }




        //Then render for all addons
        for (AddonBase addon : addons) {
            if (addon.getEnabled()) {
                addon.render(drawContext, tickDelta);
            }
        }




        return;
    }

    public void interaction() {
        System.out.println("Interaction");
        if (!this.Open) {
            System.out.println("Open");
            this.Open = true;
            this.Y = 0;
        } else {
            System.out.println("already open");
            if (this.Z == -1) {
                // Find the addon in the current folder at position this.Y
                AddonBase addon = null;
                int currentAddonIndex = 0;

                for (int i = 0; i < addons.size(); i++) {
                    AddonBase currentAddon = addons.get(i);
                    if (currentAddon.folder == Folders.values()[this.X]) {
                        if (currentAddonIndex == this.Y) {
                            addon = currentAddon;
                            break;
                        }
                        currentAddonIndex++;
                    }
                }

                if (addon != null) {
                    if (addon.enabled) {
                        addon.setEnabled(false);
                        System.out.println("Disabled " + addon.name);
                    } else {
                        addon.setEnabled(true);
                        System.out.println("Enabled " + addon.name);
                    }
                } else {
                    System.out.println("No addon found at position " + this.Y + " in folder " + Folders.values()[this.X]);
                }

            } else {
                if (this.Z != -1) {
                    // Additional logic if needed
                }
            }
        }
    }

    public void backout() {

        if (this.Open) {
            if (this.Z == -1) {
                this.Open = false;
                this.Y = -1;
            } else {
                if (this.Z != -1) {
                    this.Z = -1;
                }
            }
        }

    }

    public void up() {
        if (this.Y == -1) {
            if (this.X > 0) {
                this.X -= 1;
            } else {
                this.X = 0;
            }
        } else {
            if (this.Y > 0) {
                this.Y -= 1;
            } else {
                this.Y = 0;
            }
        }
    }

    public void down() {
        if (this.Y == -1) {
            if (this.X < Folders.values().length - 1) {
                this.X += 1;
            } else {
                this.X = Folders.values().length - 1;
            }
        } else {
            if (this.Y < addons.size() - 1) {
                this.Y += 1;
            } else {
                this.Y = addons.size() - 1;
            }
        }
    }

    public void keyPress(String key) {
        System.out.println(key);
        System.out.println(this.X);
        if (key == "UP") {
            up();
        } else {
            if (key == "DOWN") {
                down();
            } else {

                if (key == "ENTER") {
                    interaction();
                } else {

                    if (key == "LEFT") {
                        backout();
                    }

                }

            }
        }

    }

    public void entersPerHalfSecond(int entersPerHalfSecond) {



    }




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
