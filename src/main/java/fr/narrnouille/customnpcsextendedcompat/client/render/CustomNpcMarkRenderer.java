package fr.narrnouille.customnpcsextendedcompat.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.narrnouille.customnpcsextendedcompat.CustomMarkTypes;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcNameTagBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityNPCInterface;

public final class CustomNpcMarkRenderer {
    private static final int MIN_MARK_BLOCK_LIGHT = 4;
    private static final int MIN_MARK_SKY_LIGHT = 2;
    private static final float DEFAULT_MARK_SCALE = 0.90F;
    private static final float MIN_MARK_SCALE = DEFAULT_MARK_SCALE
            * CustomNpcNameTagBridge.MIN_NAME_TAG_SCALE
            / CustomNpcNameTagBridge.VANILLA_NAME_TAG_RENDER_SCALE;
    private static final float MAX_MARK_SCALE = DEFAULT_MARK_SCALE
            * CustomNpcNameTagBridge.MAX_NAME_TAG_SCALE
            / CustomNpcNameTagBridge.VANILLA_NAME_TAG_RENDER_SCALE;

    private CustomNpcMarkRenderer() {
    }

    public static boolean render(
            LivingEntity entity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            MarkData.Mark mark
    ) {
        if (ShaderShadowPassState.isRenderingShadowPass()) {
            return true;
        }

        ResourceLocation location = getMarkTexture(mark);
        if (location == null) {
            return false;
        }

        poseStack.pushPose();
        int color = mark.color;
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        poseStack.translate(0.0D, getMarkY(entity), 0.0D);
        poseStack.mulPose(Axis.XN.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(getHorizontalCameraFacingYaw(entity)));
        float markScale = getMarkScale(entity);
        poseStack.scale(markScale, markScale, markScale);
        poseStack.translate(-0.5F, 0.0F, 0.0F);
        MarkRenderer.renderer.render(
                location,
                poseStack,
                buffer.getBuffer(RenderType.entityCutout(location)),
                clampMarkLight(packedLight),
                OverlayTexture.NO_OVERLAY,
                red,
                green,
                blue,
                1.0F
        );
        poseStack.popPose();
        return true;
    }

    private static int clampMarkLight(int packedLight) {
        int blockLight = Math.max(LightTexture.block(packedLight), MIN_MARK_BLOCK_LIGHT);
        int skyLight = Math.max(LightTexture.sky(packedLight), MIN_MARK_SKY_LIGHT);
        return LightTexture.pack(blockLight, skyLight);
    }

    private static double getMarkY(LivingEntity entity) {
        if (entity instanceof EntityNPCInterface npc) {
            boolean nameVisible = isNameVisible(npc);
            return CustomNpcLabelSmoother.getMarkY(npc, nameVisible, nameVisible && isTitleVisible(npc));
        }
        return (double) entity.getBbHeight() + 0.6D;
    }

    private static float getMarkScale(LivingEntity entity) {
        if (entity instanceof EntityNPCInterface npc) {
            float rawScale = DEFAULT_MARK_SCALE * CustomNpcSizeBridge.getSizeScale(npc.display);
            return Math.max(MIN_MARK_SCALE, Math.min(MAX_MARK_SCALE, rawScale));
        }

        return 1.0F;
    }

    private static float getHorizontalCameraFacingYaw(LivingEntity entity) {
        Vec3 cameraPosition = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        double dx = cameraPosition.x - entity.getX();
        double dz = cameraPosition.z - entity.getZ();
        if (dx == 0.0D && dz == 0.0D) {
            return entity.yHeadRot;
        }

        return (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
    }

    private static boolean isNameVisible(EntityNPCInterface npc) {
        if (!npc.display.showName()) {
            return false;
        }

        return Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(npc)
                <= CustomNpcNameTagBridge.getNameVisibilityDistanceSqr(npc.display);
    }

    private static boolean isTitleVisible(EntityNPCInterface npc) {
        if (!isNameVisible(npc)) {
            return false;
        }

        if (npc.display.getTitle().isEmpty()) {
            return false;
        }

        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        return cameraEntity != null
                && npc.isInRange(cameraEntity, CustomNpcNameTagBridge.getTitleVisibilityDistance(npc.display));
    }

    private static ResourceLocation getMarkTexture(MarkData.Mark mark) {
        if (CustomMarkTypes.isCustomType(mark.type)) {
            return CustomMarkTypes.textureFor(mark.type);
        }

        return switch (mark.type) {
            case 1 -> MarkRenderer.markQuestion;
            case 2 -> MarkRenderer.markExclamation;
            case 3 -> MarkRenderer.markPointer;
            case 4 -> MarkRenderer.markSkull;
            case 5 -> MarkRenderer.markCross;
            case 6 -> MarkRenderer.markStar;
            default -> null;
        };
    }
}
