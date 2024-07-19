package dev.insilicon.bkm.HUDRenderer.addons;

import dev.insilicon.bkm.HUDRenderer.AddonBase;
import dev.insilicon.bkm.HUDRenderer.Folders;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class NameESP extends AddonBase {

    private MinecraftClient client;

    public NameESP() {
        super("NameESP", Folders.STAFF);
    }


    @Override
    public void render(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;



        if (player == null || client.world == null) {
            return; // Return if the player or world is null
        }

        List<AbstractClientPlayerEntity> players = client.world.getPlayers();
        for (AbstractClientPlayerEntity otherPlayer : players) {
            if (otherPlayer == player) {
                continue; // Skip rendering the name for the local player
            }

            if (otherPlayer.getName().getString().startsWith("CIT")) {
                continue;
            }

            Vec3d pos = otherPlayer.getPos();

            double x = pos.x;
            double y = pos.y;
            double z = pos.z;

            Vec2f screenPos = getScreenPosition(x, y, z);
            // Check if any of the values are -1
            if (screenPos.x == -1 || screenPos.y == -1) {
                continue;
            }

            drawContext.drawText(client.textRenderer, otherPlayer.getName().getString().substring(0,1), (int) screenPos.x, (int) screenPos.y, 0xFFFFFF, true);
        }
    }



    @Override
    public void tick() {

    }

    @Override
    public void enabled() {
        client = MinecraftClient.getInstance();
    }

    @Override
    public void disabled() {

    }


}
