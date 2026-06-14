package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsextendedcompat.client.GeckoDialogPreviewClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.client.renderer.RenderCustomModel", remap = false)
public abstract class RenderCustomModelDialogPreviewMixin {
    @ModifyVariable(
            method = "applyRotations(Lcom/goodbird/cnpcgeckoaddon/entity/EntityCustomModel;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private float customnpcsExtendedCompat$useDialogPreviewYaw(float rotationYaw) {
        return GeckoDialogPreviewClient.getYaw(rotationYaw);
    }
}
