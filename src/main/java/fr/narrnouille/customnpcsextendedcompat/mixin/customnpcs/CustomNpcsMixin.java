package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.ScoreboardObjectiveSync;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Pseudo
@Mixin(targets = "noppes.npcs.CustomNpcs")
public abstract class CustomNpcsMixin {
    @Redirect(
            method = "lambda$serverstart$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private static void customnpcsExtendedCompat$syncScoreUpdateObjectiveSafely(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        ScoreboardObjectiveSync.sendOrStartTracking(connection, packet);
    }

    @Redirect(
            method = "lambda$serverstart$2",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;"
            )
    )
    private static <T> Optional<T> customnpcsExtendedCompat$allowNullScoreUpdateOptional(T value) {
        return Optional.ofNullable(value);
    }
}
