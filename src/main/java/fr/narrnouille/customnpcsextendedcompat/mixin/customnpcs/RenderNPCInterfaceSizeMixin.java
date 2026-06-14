package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderNPCInterface.class, remap = false)
public abstract class RenderNPCInterfaceSizeMixin {
    @Shadow
    protected abstract void renderColor(EntityNPCInterface npc);

    @Inject(
            method = "scale(Lnoppes/npcs/entity/EntityNPCInterface;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void customnpcsExtendedCompat$useFloatSizeScale(
            EntityNPCInterface npc,
            PoseStack poseStack,
            float partialTicks,
            CallbackInfo callbackInfo
    ) {
        this.renderColor(npc);
        float sizeScale = CustomNpcSizeBridge.getSizeScale(npc.display);
        poseStack.scale(npc.scaleX * sizeScale, npc.scaleY * sizeScale, npc.scaleZ * sizeScale);
        callbackInfo.cancel();
    }
}
