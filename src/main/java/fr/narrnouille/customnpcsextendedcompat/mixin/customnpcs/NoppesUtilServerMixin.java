package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.MarkDataSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "noppes.npcs.NoppesUtilServer")
public abstract class NoppesUtilServerMixin {
    @Inject(method = "setEditingNpc", at = @At("RETURN"))
    private static void customnpcsExtendedCompat$syncMarkDataOnNpcEdit(Player player, EntityNPCInterface npc, CallbackInfo ci) {
        if (npc != null && player instanceof ServerPlayer serverPlayer) {
            MarkDataSync.sendTo(serverPlayer, npc);
        }
    }
}
