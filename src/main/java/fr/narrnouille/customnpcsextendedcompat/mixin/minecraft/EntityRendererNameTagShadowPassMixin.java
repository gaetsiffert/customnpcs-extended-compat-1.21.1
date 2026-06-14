package fr.narrnouille.customnpcsextendedcompat.mixin.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.client.render.CustomNpcNameTagDepthRenderer;
import fr.narrnouille.customnpcsextendedcompat.client.render.ShaderShadowPassState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(
            method = "renderNameTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"
            )
    )
    private int customnpcsExtendedCompat$deferVanillaNameTagWithDepthBackground(
            Font font,
            Component component,
            float x,
            float y,
            int color,
            boolean dropShadow,
            Matrix4f matrix,
            MultiBufferSource buffer,
            Font.DisplayMode displayMode,
            int backgroundColor,
            int packedLight
    ) {
        if (CustomNpcNameTagDepthRenderer.shouldDeferDepthWritingNameTags()) {
            CustomNpcNameTagDepthRenderer.enqueueNameTag(
                    font,
                    component,
                    x,
                    y,
                    color,
                    dropShadow,
                    matrix,
                    displayMode,
                    backgroundColor,
                    packedLight
            );
            return 0;
        }

        return font.drawInBatch(component, x, y, color, dropShadow, matrix, buffer, displayMode, backgroundColor, packedLight);
    }
}
