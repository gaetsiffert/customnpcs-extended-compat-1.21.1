package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.mixin.minecraft.ServerScoreboardAccessor;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "noppes.npcs.controllers.data.Availability")
public abstract class AvailabilityMixin {
    @Redirect(
            method = "initScore",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ServerScoreboard;startTrackingObjective(Lnet/minecraft/world/scores/Objective;)V"
            )
    )
    private void customnpcsExtendedCompat$startTrackingObjectiveOnce(ServerScoreboard scoreboard, Objective objective) {
        if (!((ServerScoreboardAccessor) scoreboard).customnpcsExtendedCompat$getTrackedObjectives().contains(objective)) {
            scoreboard.startTrackingObjective(objective);
        }
    }
}
