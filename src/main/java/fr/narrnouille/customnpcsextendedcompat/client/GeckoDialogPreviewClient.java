package fr.narrnouille.customnpcsextendedcompat.client;

public final class GeckoDialogPreviewClient {
    private static int renderDepth;
    private static float yaw;

    private GeckoDialogPreviewClient() {
    }

    public static void begin(float previewYaw) {
        renderDepth++;
        yaw = previewYaw;
    }

    public static void end() {
        if (renderDepth > 0) {
            renderDepth--;
        }
    }

    public static float getYaw(float fallbackYaw) {
        if (renderDepth > 0) {
            return yaw;
        }
        return fallbackYaw;
    }

    public static boolean isRendering() {
        return renderDepth > 0;
    }
}
