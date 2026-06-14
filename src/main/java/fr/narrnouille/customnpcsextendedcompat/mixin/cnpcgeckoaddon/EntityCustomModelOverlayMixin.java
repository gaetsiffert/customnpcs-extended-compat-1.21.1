package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelOverlayBridge;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel", remap = false)
public abstract class EntityCustomModelOverlayMixin implements EntityCustomModelOverlayBridge {
    @Unique
    private ResourceLocation customnpcsExtendedCompat$overlayTexture;

    @Unique
    private boolean customnpcsExtendedCompat$overlayGlowing;

    @Override
    public void customnpcsExtendedCompat$setOverlayTexture(ResourceLocation texture) {
        this.customnpcsExtendedCompat$overlayTexture = texture;
    }

    @Override
    public ResourceLocation customnpcsExtendedCompat$getOverlayTexture() {
        return this.customnpcsExtendedCompat$overlayTexture;
    }

    @Override
    public void customnpcsExtendedCompat$setOverlayGlowing(boolean glowing) {
        this.customnpcsExtendedCompat$overlayGlowing = glowing;
    }

    @Override
    public boolean customnpcsExtendedCompat$isOverlayGlowing() {
        return this.customnpcsExtendedCompat$overlayGlowing;
    }
}
