package fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon;

import net.minecraft.resources.ResourceLocation;

public interface EntityCustomModelOverlayBridge {
    void customnpcsExtendedCompat$setOverlayTexture(ResourceLocation texture);

    ResourceLocation customnpcsExtendedCompat$getOverlayTexture();

    void customnpcsExtendedCompat$setOverlayGlowing(boolean glowing);

    boolean customnpcsExtendedCompat$isOverlayGlowing();
}
