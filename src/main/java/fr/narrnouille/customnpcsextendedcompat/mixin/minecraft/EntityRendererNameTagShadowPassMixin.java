package fr.narrnouille.customnpcsextendedcompat.mixin.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.client.render.ShaderShadowPassState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererNameTagShadowPassMixin<T extends Entity> {
    @Inject(
            method = "renderNameTag",
            at = @At("HEAD"),
            cancellable = true
    )
    private void customnpcsExtendedCompat$skipVanillaNameTagInShaderShadowPass(
            T entity,
            Component component,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            float partialTick,
            CallbackInfo callbackInfo
    ) {
        if (ShaderShadowPassState.isRenderingShadowPass()) {
            callbackInfo.cancel();
        }
    }
}
