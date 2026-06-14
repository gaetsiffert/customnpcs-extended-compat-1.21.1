package fr.narrnouille.customnpcsextendedcompat.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelOverlayBridge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CnpcGeckoOverlayLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    public CnpcGeckoOverlayLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            T animatable,
            BakedGeoModel bakedModel,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            float partialTick,
            int packedLight,
            int packedOverlay
    ) {
        if (!(animatable instanceof EntityCustomModelOverlayBridge overlayBridge)) {
            return;
        }

        ResourceLocation texture = overlayBridge.customnpcsExtendedCompat$getOverlayTexture();
        if (texture == null) {
            return;
        }

        RenderType overlayRenderType = overlayBridge.customnpcsExtendedCompat$isOverlayGlowing()
                ? RenderType.entityTranslucentEmissive(texture)
                : RenderType.entityTranslucent(texture);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayRenderType);
        getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                overlayRenderType,
                overlayBuffer,
                partialTick,
                packedLight,
                packedOverlay,
                0xFFFFFFFF
        );
    }
}
