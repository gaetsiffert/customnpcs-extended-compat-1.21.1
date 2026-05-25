package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = noppes.npcs.client.renderer.RenderNPCInterface.class, priority = 1500)
public abstract class RenderNPCInterfaceGeckoEquipmentMixin {
    private static final String GECKO_MODEL_CLASS = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";
    private static final String GECKO_MODID = "cnpcgeckoaddon";
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    @Inject(
            method = "render(Lnoppes/npcs/entity/EntityNPCInterface;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void customnpcsScoreboardCompat$syncArmorForGeckoNpc(
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
        if (modelEntity == null || !GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            return;
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = npc.getItemBySlot(slot);
            modelEntity.setItemSlot(slot, stack == null ? ItemStack.EMPTY : stack);
        }
    }
}
