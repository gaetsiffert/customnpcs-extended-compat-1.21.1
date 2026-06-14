package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelSizeBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelTransitionBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderNPCInterface.class, priority = 2000)
public abstract class RenderNPCInterfaceGeckoSizeMixin {
    private static final String GECKO_MODEL_CLASS = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";
    private static final String GECKO_MODID = "cnpcgeckoaddon";
    private static final int DEFAULT_TRANSITION_LENGTH_TICKS = 10;

    @Inject(
            method = "render(Lnoppes/npcs/entity/EntityNPCInterface;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void customnpcsExtendedCompat$syncFloatSizeForGeckoNpc(
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
        if (modelEntity instanceof EntityCustomModelSizeBridge sizeBridge
                && GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            sizeBridge.customnpcsExtendedCompat$setNpcSizeScale(CustomNpcSizeBridge.getSizeScale(npc.display));
        }
        if (modelEntity instanceof EntityCustomModelTransitionBridge transitionBridge
                && GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            transitionBridge.customnpcsExtendedCompat$setAnimationTransitionTicks(readTransitionLengthTicks(npc.display));
            transitionBridge.customnpcsExtendedCompat$setDialogIdleAnimationEnabled(shouldUseDialogIdleAnimation(npc));
        }
    }

    private static boolean shouldUseDialogIdleAnimation(EntityNPCInterface npc) {
        return npc.ais.stopAndInteract && npc.isInteracting() && !npc.isAttacking();
    }

    private static int readTransitionLengthTicks(Object display) {
        if (display == null) {
            return DEFAULT_TRANSITION_LENGTH_TICKS;
        }

        try {
            Object customModelData = display.getClass().getMethod("getCustomModelData").invoke(display);
            if (customModelData == null) {
                return DEFAULT_TRANSITION_LENGTH_TICKS;
            }

            Object value = customModelData.getClass().getMethod("getTransitionLengthTicks").invoke(customModelData);
            if (value instanceof Number number) {
                return Math.max(0, number.intValue());
            }
        } catch (ReflectiveOperationException | LinkageError | SecurityException ignored) {
            return DEFAULT_TRANSITION_LENGTH_TICKS;
        }

        return DEFAULT_TRANSITION_LENGTH_TICKS;
    }
}
