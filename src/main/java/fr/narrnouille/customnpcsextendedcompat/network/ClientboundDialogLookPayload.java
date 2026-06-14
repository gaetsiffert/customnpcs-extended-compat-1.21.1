package fr.narrnouille.customnpcsextendedcompat.network;

import fr.narrnouille.customnpcsextendedcompat.CustomNpcsExtendedCompatMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundDialogLookPayload(int npcId, int mode, double x, double y, double z, float speed)
        implements CustomPacketPayload {
    public static final int MODE_CLEAR = 0;
    public static final int MODE_LOOK_AT_PLAYER = 1;
    public static final int MODE_LOOK_AT_POSITION = 2;

    public static final Type<ClientboundDialogLookPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomNpcsExtendedCompatMod.MODID, "dialog_look")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDialogLookPayload> STREAM_CODEC =
            StreamCodec.of(ClientboundDialogLookPayload::encode, ClientboundDialogLookPayload::decode);

    private static void encode(RegistryFriendlyByteBuf buffer, ClientboundDialogLookPayload payload) {
        buffer.writeInt(payload.npcId);
        buffer.writeInt(payload.mode);
        buffer.writeDouble(payload.x);
        buffer.writeDouble(payload.y);
        buffer.writeDouble(payload.z);
        buffer.writeFloat(payload.speed);
    }

    private static ClientboundDialogLookPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ClientboundDialogLookPayload(
                buffer.readInt(),
                buffer.readInt(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readFloat()
        );
    }

    public static ClientboundDialogLookPayload clear(int npcId) {
        return new ClientboundDialogLookPayload(npcId, MODE_CLEAR, 0.0D, 0.0D, 0.0D, 0.0F);
    }

    public static ClientboundDialogLookPayload lookAtPlayer(int npcId, float speed) {
        return new ClientboundDialogLookPayload(npcId, MODE_LOOK_AT_PLAYER, 0.0D, 0.0D, 0.0D, speed);
    }

    public static ClientboundDialogLookPayload lookAtPosition(int npcId, double x, double y, double z, float speed) {
        return new ClientboundDialogLookPayload(npcId, MODE_LOOK_AT_POSITION, x, y, z, speed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
