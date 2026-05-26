package fr.narrnouille.customnpcsextendedcompat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.lang.reflect.Method;

public final class CnpcGeckoPayloadRegistration {
    private static final String MODID = "cnpcgeckoaddon";

    private CnpcGeckoPayloadRegistration() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        if (!ModList.get().isLoaded(MODID)) {
            return;
        }

        PayloadRegistrar registrar = event.registrar("1").optional();
        registerClientbound(registrar, "packetsyncanimation", "com.goodbird.cnpcgeckoaddon.network.PacketSyncAnimation");
        registerClientbound(registrar, "packetsynctileanimation", "com.goodbird.cnpcgeckoaddon.network.PacketSyncTileAnimation");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerClientbound(PayloadRegistrar registrar, String path, String className) {
        try {
            Class<?> packetClass = Class.forName(className);
            Method encode = packetClass.getMethod("encode", FriendlyByteBuf.class);
            Method decode = packetClass.getMethod("decode", FriendlyByteBuf.class);
            Method handle = packetClass.getMethod("handle", packetClass);

            CustomPacketPayload.Type type = new CustomPacketPayload.Type(
                    ResourceLocation.fromNamespaceAndPath(MODID, path)
            );
            StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload> codec = StreamCodec.of(
                    (buffer, packet) -> invokeEncode(encode, packet, buffer),
                    buffer -> (CustomPacketPayload) invokeDecode(decode, buffer)
            );

            registrar.playToClient(type, codec, (packet, context) -> invokeHandle(handle, packet));
            CustomNpcsExtendedCompatMod.LOGGER.debug("Registered CNPC Gecko payload {}", type.id());
        } catch (ReflectiveOperationException | LinkageError exception) {
            CustomNpcsExtendedCompatMod.LOGGER.warn("Could not register CNPC Gecko payload {}", className, exception);
        }
    }

    private static void invokeEncode(Method encode, CustomPacketPayload packet, RegistryFriendlyByteBuf buffer) {
        try {
            encode.invoke(packet, buffer);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not encode CNPC Gecko payload " + packet.type().id(), exception);
        }
    }

    private static Object invokeDecode(Method decode, RegistryFriendlyByteBuf buffer) {
        try {
            return decode.invoke(null, buffer);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not decode CNPC Gecko payload", exception);
        }
    }

    private static void invokeHandle(Method handle, CustomPacketPayload packet) {
        try {
            handle.invoke(null, packet);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not handle CNPC Gecko payload " + packet.type().id(), exception);
        }
    }
}
