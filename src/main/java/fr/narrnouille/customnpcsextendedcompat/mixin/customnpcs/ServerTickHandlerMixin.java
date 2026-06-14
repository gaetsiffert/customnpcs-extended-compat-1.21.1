package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.DialogInteractionTracker;
import fr.narrnouille.customnpcsextendedcompat.ScoreboardObjectiveSync;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Pseudo
@Mixin(targets = "noppes.npcs.ServerTickHandler")
public abstract class ServerTickHandlerMixin {
    @Redirect(
            method = "playerLogin",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void customnpcsExtendedCompat$syncLoginObjectiveSafely(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        ScoreboardObjectiveSync.sendLoginObjectiveState(connection, packet);
    }

    @Redirect(
            method = "playerLogin",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;"
            )
    )
    private static <T> Optional<T> customnpcsExtendedCompat$allowNullLoginOptional(T value) {
        return Optional.ofNullable(value);
    }

    @Inject(
            method = "onServerTick(Lnet/neoforged/neoforge/event/tick/PlayerTickEvent$Pre;)V",
            at = @At("RETURN")
    )
    private void customnpcsExtendedCompat$keepNpcFacingDialogPlayer(PlayerTickEvent.Pre event, CallbackInfo callbackInfo) {
        DialogInteractionTracker.keepNpcFacingDialogPlayer(event.getEntity());
    }
}
