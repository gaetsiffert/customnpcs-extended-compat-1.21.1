package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomAvailabilityScoreboardBridge;
import fr.narrnouille.customnpcsextendedcompat.mixin.minecraft.ServerScoreboardAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.controllers.data.Availability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "noppes.npcs.controllers.data.Availability")
public abstract class AvailabilityMixin implements CustomAvailabilityScoreboardBridge {
    @Unique
    private static final String SCOREBOARD_3_OBJECTIVE_KEY = "AvailabilityScoreboard3Objective";
    @Unique
    private static final String SCOREBOARD_3_TYPE_KEY = "AvailabilityScoreboard3Type";
    @Unique
    private static final String SCOREBOARD_3_VALUE_KEY = "AvailabilityScoreboard3Value";

    @Unique
    private String customnpcsExtendedCompat$scoreboard3Objective = "";
    @Unique
    private EnumAvailabilityScoreboard customnpcsExtendedCompat$scoreboard3Type = EnumAvailabilityScoreboard.EQUAL;
    @Unique
    private int customnpcsExtendedCompat$scoreboard3Value = 1;

    @Shadow
    private boolean hasOptions;

    @Invoker("scoreboardAvailable")
    protected abstract boolean customnpcsExtendedCompat$scoreboardAvailable(
            Player player,
            String objective,
            EnumAvailabilityScoreboard type,
            int value
    );

    @Invoker("checkHasOptions")
    protected abstract boolean customnpcsExtendedCompat$checkHasOptions();

    @Override
    public String customnpcsExtendedCompat$getScoreboard3Objective() {
        return this.customnpcsExtendedCompat$scoreboard3Objective;
    }

    @Override
    public void customnpcsExtendedCompat$setScoreboard3Objective(String objective) {
        this.customnpcsExtendedCompat$scoreboard3Objective = objective == null ? "" : objective;
        customnpcsExtendedCompat$initScore(this.customnpcsExtendedCompat$scoreboard3Objective);
        customnpcsExtendedCompat$refreshHasOptions();
    }

    @Override
    public EnumAvailabilityScoreboard customnpcsExtendedCompat$getScoreboard3Type() {
        return this.customnpcsExtendedCompat$scoreboard3Type;
    }

    @Override
    public void customnpcsExtendedCompat$setScoreboard3Type(EnumAvailabilityScoreboard type) {
        this.customnpcsExtendedCompat$scoreboard3Type = type == null ? EnumAvailabilityScoreboard.EQUAL : type;
    }

    @Override
    public int customnpcsExtendedCompat$getScoreboard3Value() {
        return this.customnpcsExtendedCompat$scoreboard3Value;
    }

    @Override
    public void customnpcsExtendedCompat$setScoreboard3Value(int value) {
        this.customnpcsExtendedCompat$scoreboard3Value = value;
    }

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

    @Inject(method = "load", at = @At("RETURN"))
    private void customnpcsExtendedCompat$loadThirdScoreboard(
            HolderLookup.Provider lookupProvider,
            CompoundTag compound,
            CallbackInfo callbackInfo
    ) {
        this.customnpcsExtendedCompat$scoreboard3Objective = compound.getString(SCOREBOARD_3_OBJECTIVE_KEY);
        customnpcsExtendedCompat$initScore(this.customnpcsExtendedCompat$scoreboard3Objective);
        this.customnpcsExtendedCompat$scoreboard3Type = EnumAvailabilityScoreboard.values()[
                Math.max(0, Math.min(EnumAvailabilityScoreboard.values().length - 1, compound.getInt(SCOREBOARD_3_TYPE_KEY)))
        ];
        this.customnpcsExtendedCompat$scoreboard3Value = compound.contains(SCOREBOARD_3_VALUE_KEY)
                ? compound.getInt(SCOREBOARD_3_VALUE_KEY)
                : 1;
        customnpcsExtendedCompat$refreshHasOptions();
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void customnpcsExtendedCompat$saveThirdScoreboard(
            HolderLookup.Provider lookupProvider,
            CompoundTag compound,
            CallbackInfoReturnable<CompoundTag> callbackInfo
    ) {
        compound.putString(SCOREBOARD_3_OBJECTIVE_KEY, this.customnpcsExtendedCompat$scoreboard3Objective);
        compound.putInt(SCOREBOARD_3_TYPE_KEY, this.customnpcsExtendedCompat$scoreboard3Type.ordinal());
        compound.putInt(SCOREBOARD_3_VALUE_KEY, this.customnpcsExtendedCompat$scoreboard3Value);
    }

    @Inject(method = "isAvailable", at = @At("RETURN"), cancellable = true)
    private void customnpcsExtendedCompat$checkThirdScoreboard(
            Player player,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        if (callbackInfo.getReturnValueZ()
                && !this.customnpcsExtendedCompat$scoreboardAvailable(
                player,
                this.customnpcsExtendedCompat$scoreboard3Objective,
                this.customnpcsExtendedCompat$scoreboard3Type,
                this.customnpcsExtendedCompat$scoreboard3Value
        )) {
            callbackInfo.setReturnValue(false);
        }
    }

    @Inject(method = "setScoreboard", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$setThirdScoreboard(
            int index,
            String objective,
            int type,
            int value,
            CallbackInfo callbackInfo
    ) {
        if (index != 2) {
            return;
        }

        this.customnpcsExtendedCompat$setScoreboard3Objective(objective);
        this.customnpcsExtendedCompat$setScoreboard3Type(
                EnumAvailabilityScoreboard.values()[
                        Math.max(0, Math.min(EnumAvailabilityScoreboard.values().length - 1, type))
                ]
        );
        this.customnpcsExtendedCompat$setScoreboard3Value(value);
        callbackInfo.cancel();
    }

    @Inject(method = "checkHasOptions", at = @At("RETURN"), cancellable = true)
    private void customnpcsExtendedCompat$checkThirdScoreboardHasOptions(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (!this.customnpcsExtendedCompat$scoreboard3Objective.isEmpty()) {
            callbackInfo.setReturnValue(true);
        }
    }

    @Unique
    private void customnpcsExtendedCompat$refreshHasOptions() {
        this.hasOptions = this.customnpcsExtendedCompat$checkHasOptions();
    }

    @Unique
    private static void customnpcsExtendedCompat$initScore(String objective) {
        if (objective.isEmpty() || Availability.scores.contains(objective)) {
            return;
        }

        Availability.scores.add(objective);
        if (CustomNpcs.Server == null) {
            return;
        }

        for (ServerLevel level : CustomNpcs.Server.getAllLevels()) {
            ServerScoreboard scoreboard = level.getScoreboard();
            Objective scoreboardObjective = scoreboard.getObjective(objective);
            if (scoreboardObjective != null
                    && !((ServerScoreboardAccessor) scoreboard)
                    .customnpcsExtendedCompat$getTrackedObjectives()
                    .contains(scoreboardObjective)) {
                scoreboard.startTrackingObjective(scoreboardObjective);
            }
        }
    }
}
