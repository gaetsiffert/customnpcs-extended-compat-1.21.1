package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.DialogInteractionTracker;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "noppes.npcs.ai.EntityAILook")
public abstract class EntityAILookDialogTargetMixin {
    @Shadow
    @Final
    private EntityNPCInterface npc;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$lookAtQueuedDialogPlayer(CallbackInfo callbackInfo) {
        if (DialogInteractionTracker.applyServerDialogLook(this.npc)) {
            callbackInfo.cancel();
        }
    }
}
