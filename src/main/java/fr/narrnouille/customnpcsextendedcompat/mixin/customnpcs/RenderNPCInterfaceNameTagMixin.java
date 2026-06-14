package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import fr.narrnouille.customnpcsextendedcompat.client.render.CustomNpcLabelSmoother;
import fr.narrnouille.customnpcsextendedcompat.client.render.CustomNpcNameTagDepthRenderer;
import fr.narrnouille.customnpcsextendedcompat.client.render.ShaderShadowPassState;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcNameTagBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderNPCInterface.class, remap = false)
public abstract class RenderNPCInterfaceNameTagMixin {
    private static final int MIN_NAME_TAG_BLOCK_LIGHT = 4;
    private static final int MIN_NAME_TAG_SKY_LIGHT = 2;
    private static final RenderType DEPTH_TESTED_NAME_TAG_BACKGROUND = RenderType.create(
            "customnpcs_extended_compat_name_tag_background_depth_test",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_TEXT_BACKGROUND_SHADER)
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false)
    );

    @Inject(
            method = "renderNameTag",
            at = @At("HEAD"),
            cancellable = true
    )
    private void customnpcsExtendedCompat$skipCustomNpcNameTagInShaderShadowPass(
            EntityNPCInterface npc,
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

    @ModifyConstant(
            method = "renderNameTag",
            constant = @Constant(doubleValue = 512.0D)
    )
    private double customnpcsExtendedCompat$useConfiguredNameVisibilityDistance(
            double original,
            EntityNPCInterface npc,
            Component component,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            float partialTick
    ) {
        return CustomNpcNameTagBridge.getNameVisibilityDistanceSqr(npc.display);
    }

    @ModifyConstant(
            method = "renderLivingLabel(Lnoppes/npcs/entity/EntityNPCInterface;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            constant = @Constant(doubleValue = 8.0D)
    )
    private double customnpcsExtendedCompat$useConfiguredTitleVisibilityDistance(
            double original,
            EntityNPCInterface npc,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        return CustomNpcNameTagBridge.getTitleVisibilityDistance(npc.display);
    }

    @Redirect(
            method = "renderLivingLabel(Lnoppes/npcs/entity/EntityNPCInterface;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
                    ordinal = 0
            )
    )
    private void customnpcsExtendedCompat$applyNameTagYOffset(
            PoseStack poseStack,
            float x,
            float y,
            float z,
            EntityNPCInterface npc,
            PoseStack methodPoseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        poseStack.translate(x, CustomNpcLabelSmoother.getNameTagBaseY(npc, customnpcsExtendedCompat$isTitleVisible(npc)), z);
    }

    @Redirect(
            method = "renderLivingLabel(Lnoppes/npcs/entity/EntityNPCInterface;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V",
                    ordinal = 0
            )
    )
    private void customnpcsExtendedCompat$clampNameTagScale(
            PoseStack poseStack,
            float x,
            float y,
            float z,
            EntityNPCInterface npc,
            PoseStack methodPoseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        float scale = Mth.clamp(
                CustomNpcNameTagBridge.getFinalNameTagScale(npc),
                CustomNpcNameTagBridge.MIN_NAME_TAG_SCALE,
                CustomNpcNameTagBridge.MAX_NAME_TAG_SCALE
        );
        poseStack.scale(scale, -scale, scale);
    }

    @Redirect(
            method = "renderLivingLabel(Lnoppes/npcs/entity/EntityNPCInterface;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"
            )
    )
    private int customnpcsExtendedCompat$renderDepthTestedNameTag(
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
        int nameTagLight = customnpcsExtendedCompat$clampNameTagLight(packedLight);
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
                    nameTagLight
            );
            return 0;
        }

        if (backgroundColor != 0) {
            customnpcsExtendedCompat$renderNameTagBackground(font, component, x, y, matrix, buffer, backgroundColor, nameTagLight);

            if (displayMode == Font.DisplayMode.SEE_THROUGH) {
                return 0;
            }
        }

        Font.DisplayMode depthTestedMode = displayMode == Font.DisplayMode.SEE_THROUGH
                ? Font.DisplayMode.NORMAL
                : displayMode;
        return font.drawInBatch(component, x, y, color, dropShadow, matrix, buffer, depthTestedMode, 0, nameTagLight);
    }

    private static int customnpcsExtendedCompat$clampNameTagLight(int packedLight) {
        int blockLight = Math.max(LightTexture.block(packedLight), MIN_NAME_TAG_BLOCK_LIGHT);
        int skyLight = Math.max(LightTexture.sky(packedLight), MIN_NAME_TAG_SKY_LIGHT);
        return LightTexture.pack(blockLight, skyLight);
    }

    private static void customnpcsExtendedCompat$renderNameTagBackground(
            Font font,
            Component component,
            float x,
            float y,
            Matrix4f matrix,
            MultiBufferSource buffer,
            int backgroundColor,
            int packedLight
    ) {
        int alpha = backgroundColor >>> 24;
        if (alpha <= 0) {
            return;
        }

        float width = font.width(component);
        VertexConsumer vertexConsumer = buffer.getBuffer(customnpcsExtendedCompat$getNameTagBackgroundRenderType());
        float left = x - 1.0F;
        float right = x + width + 1.0F;
        float top = y - 1.0F;
        float bottom = y + 9.0F;
        float z = -0.01F;
        float a = alpha / 255.0F;

        vertexConsumer.addVertex(matrix, left, bottom, z).setColor(0.0F, 0.0F, 0.0F, a).setLight(packedLight);
        vertexConsumer.addVertex(matrix, right, bottom, z).setColor(0.0F, 0.0F, 0.0F, a).setLight(packedLight);
        vertexConsumer.addVertex(matrix, right, top, z).setColor(0.0F, 0.0F, 0.0F, a).setLight(packedLight);
        vertexConsumer.addVertex(matrix, left, top, z).setColor(0.0F, 0.0F, 0.0F, a).setLight(packedLight);
    }

    private static RenderType customnpcsExtendedCompat$getNameTagBackgroundRenderType() {
        return DEPTH_TESTED_NAME_TAG_BACKGROUND;
    }

    private static boolean customnpcsExtendedCompat$isTitleVisible(EntityNPCInterface npc) {
        if (npc.display.getTitle().isEmpty()) {
            return false;
        }

        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        return cameraEntity != null
                && npc.isInRange(cameraEntity, CustomNpcNameTagBridge.getTitleVisibilityDistance(npc.display));
    }
}
