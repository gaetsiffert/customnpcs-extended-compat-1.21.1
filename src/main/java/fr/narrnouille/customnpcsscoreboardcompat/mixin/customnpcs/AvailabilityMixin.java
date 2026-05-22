package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsscoreboardcompat.mixin.minecraft.ServerScoreboardAccessor;
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
    private void customnpcsScoreboardCompat$startTrackingObjectiveOnce(ServerScoreboard scoreboard, Objective objective) {
        if (!((ServerScoreboardAccessor) scoreboard).customnpcsScoreboardCompat$getTrackedObjectives().contains(objective)) {
            scoreboard.startTrackingObjective(objective);
        }
    }
}
