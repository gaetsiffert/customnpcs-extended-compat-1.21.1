package fr.narrnouille.customnpcsextendedcompat.mixin.minecraft;

import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ServerScoreboard.class)
public interface ServerScoreboardAccessor {
    @Accessor("trackedObjectives")
    Set<Objective> customnpcsExtendedCompat$getTrackedObjectives();
}
