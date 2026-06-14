package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.DialogInteractionTracker;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "noppes.npcs.packets.server.SPacketDialogSelected")
public abstract class SPacketDialogSelectedMixin {
    @Inject(method = "closeDialog", at = @At("TAIL"))
    private void customnpcsExtendedCompat$closeTrackedDialog(
            ServerPlayer player,
            EntityNPCInterface npc,
            boolean closeGui,
            CallbackInfo callbackInfo
    ) {
        if (closeGui) {
            DialogInteractionTracker.closeDialog(player, npc);
        }
    }
}
