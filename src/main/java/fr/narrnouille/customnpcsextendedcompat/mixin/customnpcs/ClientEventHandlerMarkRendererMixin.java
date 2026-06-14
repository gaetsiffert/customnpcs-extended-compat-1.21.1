package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.client.render.CustomNpcMarkRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientEventHandler.class, remap = false)
public abstract class ClientEventHandlerMarkRendererMixin {
    @Redirect(
            method = "post",
            at = @At(
                    value = "INVOKE",
                    target = "Lnoppes/npcs/client/renderer/MarkRenderer;render(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnoppes/npcs/controllers/data/MarkData$Mark;)V"
            )
    )
    private static void customnpcsExtendedCompat$renderMarkFromNameTop(
            LivingEntity entity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            MarkData.Mark mark
    ) {
        if (!CustomNpcMarkRenderer.render(entity, poseStack, buffer, packedLight, mark)) {
            MarkRenderer.render(entity, poseStack, buffer, packedLight, mark);
        }
    }
}
