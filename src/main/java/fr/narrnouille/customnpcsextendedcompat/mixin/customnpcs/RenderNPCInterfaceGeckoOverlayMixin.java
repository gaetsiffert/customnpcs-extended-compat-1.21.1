package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelOverlayBridge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = noppes.npcs.client.renderer.RenderNPCInterface.class, priority = 1500)
public abstract class RenderNPCInterfaceGeckoOverlayMixin {
    private static final String GECKO_MODEL_CLASS = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";
    private static final String GECKO_MODID = "cnpcgeckoaddon";

    @Inject(
            method = "render(Lnoppes/npcs/entity/EntityNPCInterface;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void customnpcsExtendedCompat$syncOverlayForGeckoNpc(
            EntityNPCInterface npc,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CallbackInfo callbackInfo
    ) {
        if (!ModList.get().isLoaded(GECKO_MODID) || !(npc instanceof EntityCustomNpc customNpc) || customNpc.modelData == null) {
            return;
        }

        LivingEntity modelEntity = customNpc.modelData.getEntity(npc);
        if (!(modelEntity instanceof EntityCustomModelOverlayBridge overlayBridge)
                || !GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            return;
        }

        String overlayTexture = npc.display == null ? null : npc.display.getOverlayTexture();
        overlayBridge.customnpcsExtendedCompat$setOverlayTexture(parseOverlayTexture(overlayTexture));
        overlayBridge.customnpcsExtendedCompat$setOverlayGlowing(npc.display != null && npc.display.isOverlayGlowing());
    }

    private static ResourceLocation parseOverlayTexture(String texture) {
        if (texture == null || texture.isEmpty()) {
            return null;
        }
        return ResourceLocation.tryParse(texture);
    }
}
