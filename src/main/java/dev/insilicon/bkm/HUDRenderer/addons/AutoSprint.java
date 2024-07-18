package dev.insilicon.bkm.HUDRenderer.addons;

import dev.insilicon.bkm.HUDRenderer.AddonBase;
import dev.insilicon.bkm.HUDRenderer.Folders;
import net.minecraft.client.MinecraftClient;

public class AutoSprint extends AddonBase {

    private MinecraftClient client;

    public AutoSprint() {
        super("AutoSprint", Folders.GENERAL);
    }


    @Override
    public void tick() {
        client.player.setSprinting(true);
    }

    @Override
    public void enabled() {
        client = MinecraftClient.getInstance();
    }

    @Override
    public void disabled() {
        client.player.setSprinting(false);
    }


}
