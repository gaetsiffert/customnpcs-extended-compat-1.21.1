package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.DialogInteractionTracker;
import noppes.npcs.packets.PacketServerBasic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "noppes.npcs.packets.server.SPacketQuestCompletionCheckAll")
public abstract class SPacketQuestCompletionCheckAllMixin {
    @Inject(method = "handle", at = @At("HEAD"))
    private void customnpcsExtendedCompat$closeTrackedDialog(CallbackInfo callbackInfo) {
        DialogInteractionTracker.closeActiveDialog(((PacketServerBasic) (Object) this).player);
    }
}
