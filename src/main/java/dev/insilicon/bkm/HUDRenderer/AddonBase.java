package dev.insilicon.bkm.HUDRenderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public static Vec2f getScreenPosition(double x, double y, double z) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) {
            return new Vec2f(-1, -1);
        }

        // Get the camera's position and rotation
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        // Create a vector representing the player's position
        Vec3d playerPos = new Vec3d(x, y + 1.8, z);

        // Calculate the relative position of the player to the camera
        Vec3d relativePos = playerPos.subtract(cameraPos);

        // Calculate the distance between the camera and the player
        double distance = relativePos.length();

        // Create a quaternion representing the camera's rotation
        Quaternionf cameraRotation = new Quaternionf().rotateXYZ(
                (float) Math.toRadians(-pitch),
                (float) Math.toRadians(-yaw),
                0.0f // No roll rotation
        );

        // Rotate the relative position using the camera's rotation
        Vector3f rotatedPos = new Vector3f((float) relativePos.x, (float) relativePos.y, (float) relativePos.z);
        cameraRotation.transform(rotatedPos);

        // Project the rotated position onto the screen
        double fov = Math.toRadians(client.options.getFov().getValue());
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        double aspectRatio = (double) screenWidth / screenHeight;

        // Calculate the projection matrix
        Matrix4f projectionMatrix = new Matrix4f().setPerspective((float) fov, (float) aspectRatio, 0.05f, 1000.0f);

        // Transform the rotated position using the projection matrix
        Vector4f transformedPos = new Vector4f(rotatedPos.x(), rotatedPos.y(), rotatedPos.z(), 1.0f);
        projectionMatrix.transform(transformedPos);

        // Normalize the transformed position
        float normalizedX = transformedPos.x() / transformedPos.w();
        float normalizedY = transformedPos.y() / transformedPos.w();

        // Convert the normalized position to screen coordinates
        double screenX = (normalizedX + 1.0) * screenWidth / 2.0;
        double screenY = (1.0 - normalizedY) * screenHeight / 2.0;

        // Adjust the screen position based on distance
        double adjustedScreenX = screenX + (screenWidth / 2.0 - screenX) * (1.0 - Math.min(1.0, distance / 10.0));
        double adjustedScreenY = screenY + (screenHeight / 2.0 - screenY) * (1.0 - Math.min(1.0, distance / 10.0));

        // Check if the point is within the screen bounds
        if (adjustedScreenX < 0 || adjustedScreenX > screenWidth || adjustedScreenY < 0 || adjustedScreenY > screenHeight) {
            return new Vec2f(-1, -1);
        }

        return new Vec2f((float) adjustedScreenX, (float) adjustedScreenY);
    }


}
