package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.client.render.CustomNpcMarkRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MarkRenderer.class, remap = false)
public abstract class MarkRendererCustomTypesMixin {
    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void customnpcsExtendedCompat$renderMarkFromNameTop(
            LivingEntity entity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            MarkData.Mark mark,
            CallbackInfo callbackInfo
    ) {
        if (CustomNpcMarkRenderer.render(entity, poseStack, buffer, packedLight, mark)) {
            callbackInfo.cancel();
        }
    }
}
