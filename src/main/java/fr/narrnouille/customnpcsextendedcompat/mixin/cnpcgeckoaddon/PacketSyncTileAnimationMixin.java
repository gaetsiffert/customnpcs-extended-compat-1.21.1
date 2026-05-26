package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "com.goodbird.cnpcgeckoaddon.network.PacketSyncTileAnimation", remap = false)
public abstract class PacketSyncTileAnimationMixin {
    /**
     * @author Narrnouille
     * @reason CNPC-Gecko-Addon 1.21.1-neo returns minecraft:cnpcgeckoaddonpacketsynctileanimation here,
     * but registers cnpcgeckoaddon:packetsynctileanimation. NeoForge cannot encode the packet when
     * those payload types differ.
     */
    @Overwrite(remap = false)
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("cnpcgeckoaddon", "packetsynctileanimation"));
    }
}
