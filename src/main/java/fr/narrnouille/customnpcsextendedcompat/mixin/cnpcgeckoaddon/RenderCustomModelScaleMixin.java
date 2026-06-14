package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelSizeBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.client.renderer.RenderCustomModel", remap = false)
public abstract class RenderCustomModelScaleMixin {
    private static final float CUSTOMNPCS_MODEL_RENDER_SCALE = 0.9375F;

    @Inject(
            method = "applyRotations(Lcom/goodbird/cnpcgeckoaddon/entity/EntityCustomModel;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
            at = @At("HEAD")
    )
    private void customnpcsExtendedCompat$matchCustomNpcModelScale(
            @Coerce Object entity,
            PoseStack poseStack,
            float ageInTicks,
            float rotationYaw,
            float partialTicks,
            float nativeScale,
            CallbackInfo callbackInfo
    ) {
        float scaleCorrection = 1.0F;
        if (entity instanceof EntityCustomModelSizeBridge sizeBridge) {
            float floatScale = sizeBridge.customnpcsExtendedCompat$getNpcSizeScale();
            float legacyScale = CustomNpcSizeBridge.legacySizeToScale(CustomNpcSizeBridge.scaleToLegacySize(floatScale));
            if (legacyScale > 0.0F) {
                scaleCorrection = floatScale / legacyScale;
            }
        }

        float scale = CUSTOMNPCS_MODEL_RENDER_SCALE * scaleCorrection;
        poseStack.scale(scale, scale, scale);
    }
}
