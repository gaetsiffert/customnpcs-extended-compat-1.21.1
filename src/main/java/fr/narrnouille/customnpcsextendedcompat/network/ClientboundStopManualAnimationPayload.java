package fr.narrnouille.customnpcsextendedcompat.network;

import fr.narrnouille.customnpcsextendedcompat.CustomNpcsExtendedCompatMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundStopManualAnimationPayload(int npcId) implements CustomPacketPayload {
    public static final Type<ClientboundStopManualAnimationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomNpcsExtendedCompatMod.MODID, "stop_manual_animation")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStopManualAnimationPayload> STREAM_CODEC =
            StreamCodec.of(ClientboundStopManualAnimationPayload::encode, ClientboundStopManualAnimationPayload::decode);

    private static void encode(RegistryFriendlyByteBuf buffer, ClientboundStopManualAnimationPayload payload) {
        buffer.writeInt(payload.npcId);
    }

    private static ClientboundStopManualAnimationPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ClientboundStopManualAnimationPayload(buffer.readInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
