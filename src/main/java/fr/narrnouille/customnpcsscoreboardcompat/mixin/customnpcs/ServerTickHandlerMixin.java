package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
    private void customnpcsScoreboardCompat$skipLoginObjectiveSync(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        if (!(packet instanceof ClientboundSetObjectivePacket)) {
            connection.send(packet);
        }
    }

    @Redirect(
            method = "playerLogin",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;"
            )
    )
    private static <T> Optional<T> customnpcsScoreboardCompat$allowNullLoginOptional(T value) {
        return Optional.ofNullable(value);
    }
}
