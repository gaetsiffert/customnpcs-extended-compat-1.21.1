package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsscoreboardcompat.MarkDataSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "noppes.npcs.entity.EntityNPCInterface")
public abstract class EntityNPCInterfaceMixin {
    @Inject(method = "startSeenByPlayer", at = @At("RETURN"))
    private void customnpcsScoreboardCompat$syncMarkDataOnStartSeen(ServerPlayer player, CallbackInfo ci) {
        MarkDataSync.sendExistingTo(player, (LivingEntity) (Object) this);
    }
}
