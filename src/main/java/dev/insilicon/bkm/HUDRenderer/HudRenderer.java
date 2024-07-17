package dev.insilicon.bkm.HUDRenderer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class HudRenderer {

    private static final int BASE_WIDTH = 2560;
    private static final int BASE_HEIGHT = 1440;

    private boolean Open = false;
    private int X = 0;
    private int Y = 0;
    private int Z = 0;

    public void render(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        if (client.player == null) {
            return;
        }
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();


        // non-transparent Background
        int backgroundWidth = getX(400 / 2);
        int backgroundHeight = getY(550);

        int backgroundX = getX(5);
        int backgroundY = getY(10);

        drawContext.fillGradient(backgroundX, backgroundY, backgroundX + backgroundWidth, backgroundY + backgroundHeight, 0x80000000, 0x80000000);

        // Calculate the width of the title text
        String titleText = "BKM";
        int titleTextWidth = textRenderer.getWidth(titleText);

        // Calculate the centered X position for the title text
        int titleX = backgroundX + (backgroundWidth - titleTextWidth) / 2;
        int titleY = getY(15);

        drawContext.drawText(textRenderer, Text.literal(titleText), titleX, titleY, 0xFFFFFF, true);

        // PVP Section










        return;
    }

    public void keyPress(String key) {



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
