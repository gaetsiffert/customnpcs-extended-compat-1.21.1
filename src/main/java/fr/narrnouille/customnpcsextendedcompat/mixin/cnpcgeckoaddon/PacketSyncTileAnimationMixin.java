package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.CnpcGeckoAnimationCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animation.RawAnimation;

import java.lang.reflect.Constructor;

@Pseudo
@Mixin(targets = "com.goodbird.cnpcgeckoaddon.network.PacketSyncTileAnimation", remap = false)
public abstract class PacketSyncTileAnimationMixin {
    private static final String PACKET_CLASS = "com.goodbird.cnpcgeckoaddon.network.PacketSyncTileAnimation";

    @Shadow(remap = false)
    private BlockPos pos;

    @Shadow(remap = false)
    private RawAnimation builder;

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true, remap = false)
    private void customnpcs_extended_compat$encodeFullAnimation(FriendlyByteBuf buffer, CallbackInfo callbackInfo) {
        buffer.writeBlockPos(this.pos);
        CnpcGeckoAnimationCodec.encode(buffer, this.builder);
        callbackInfo.cancel();
    }

    @Inject(method = "decode", at = @At("HEAD"), cancellable = true, remap = false)
    private static void customnpcs_extended_compat$decodeFullAnimation(
            FriendlyByteBuf buffer,
            CallbackInfoReturnable<CustomPacketPayload> callbackInfo
    ) {
        BlockPos pos = buffer.readBlockPos();
        RawAnimation animation = CnpcGeckoAnimationCodec.decode(buffer);
        callbackInfo.setReturnValue(createPacket(pos, animation));
    }

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

    private static CustomPacketPayload createPacket(BlockPos pos, RawAnimation animation) {
        try {
            Class<?> packetClass = Class.forName(PACKET_CLASS);
            Constructor<?> constructor = packetClass.getConstructor(BlockPos.class, RawAnimation.class);
            return (CustomPacketPayload) constructor.newInstance(pos, animation);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create CNPC Gecko tile animation sync packet", exception);
        }
    }
}
