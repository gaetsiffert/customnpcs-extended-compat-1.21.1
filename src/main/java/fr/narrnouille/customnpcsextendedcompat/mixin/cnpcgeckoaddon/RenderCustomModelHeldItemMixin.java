package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.client.renderer.RenderCustomModel", remap = false)
public abstract class RenderCustomModelHeldItemMixin {
    @Inject(
            method = "renderItem(Lsoftware/bernie/geckolib/cache/object/GeoBone;Lcom/goodbird/cnpcgeckoaddon/entity/EntityCustomModel;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void customnpcsExtendedCompat$renderHeldItemLikeVanillaHand(
            GeoBone bone,
            @Coerce LivingEntity entity,
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CallbackInfo callbackInfo
    ) {
        callbackInfo.cancel();
    }
}
