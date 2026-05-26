package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.narrnouille.customnpcsscoreboardcompat.CustomMarkTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MarkRenderer.class)
public abstract class MarkRendererCustomTypesMixin {
    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void customnpcsScoreboardCompat$renderCustomMark(
            LivingEntity entity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            MarkData.Mark mark,
            CallbackInfo callbackInfo
    ) {
        if (!CustomMarkTypes.isCustomType(mark.type)) {
            return;
        }

        callbackInfo.cancel();

        poseStack.pushPose();
        int color = mark.color;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        ResourceLocation location = CustomMarkTypes.textureFor(mark.type);

        poseStack.translate(0.0, (double) entity.getBbHeight() + 0.6, 0.0);
        poseStack.mulPose(Axis.XN.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.yHeadRot));
        poseStack.translate(-0.5f, 0.0f, 0.0f);
        MarkRenderer.renderer.render(location, poseStack, buffer.getBuffer(RenderType.entityCutout(location)), packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0f);
        poseStack.popPose();
    }
}
