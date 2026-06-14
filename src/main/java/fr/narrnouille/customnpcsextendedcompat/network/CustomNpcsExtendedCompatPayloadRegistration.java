package fr.narrnouille.customnpcsextendedcompat.network;

import fr.narrnouille.customnpcsextendedcompat.CustomNpcsExtendedCompatMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.lang.reflect.Method;

public final class CustomNpcsExtendedCompatPayloadRegistration {
    private static final String DIALOG_LOOK_CLIENT_HANDLER_CLASS =
            "fr.narrnouille.customnpcsextendedcompat.client.DialogLookOverrideClient";
    private static final String GECKO_MANUAL_ANIMATION_CLIENT_HANDLER_CLASS =
            "fr.narrnouille.customnpcsextendedcompat.client.GeckoManualAnimationClient";
    private static Method dialogLookClientHandler;
    private static Method stopManualAnimationClientHandler;

    private CustomNpcsExtendedCompatPayloadRegistration() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(
                ClientboundDialogLookPayload.TYPE,
                ClientboundDialogLookPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleClientPayload(payload))
        );
        registrar.playToClient(
                ClientboundStopManualAnimationPayload.TYPE,
                ClientboundStopManualAnimationPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleClientPayload(payload))
        );
    }

    private static void handleClientPayload(ClientboundDialogLookPayload payload) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        try {
            Method handler = dialogLookClientHandler;
            if (handler == null) {
                handler = Class.forName(DIALOG_LOOK_CLIENT_HANDLER_CLASS).getMethod("handle", ClientboundDialogLookPayload.class);
                dialogLookClientHandler = handler;
            }
            handler.invoke(null, payload);
        } catch (ReflectiveOperationException | LinkageError exception) {
            CustomNpcsExtendedCompatMod.LOGGER.warn("Could not handle dialog look payload", exception);
        }
    }

    private static void handleClientPayload(ClientboundStopManualAnimationPayload payload) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        try {
            Method handler = stopManualAnimationClientHandler;
            if (handler == null) {
                handler = Class.forName(GECKO_MANUAL_ANIMATION_CLIENT_HANDLER_CLASS)
                        .getMethod("handle", ClientboundStopManualAnimationPayload.class);
                stopManualAnimationClientHandler = handler;
            }
            handler.invoke(null, payload);
        } catch (ReflectiveOperationException | LinkageError exception) {
            CustomNpcsExtendedCompatMod.LOGGER.warn("Could not handle stop manual animation payload", exception);
        }
    }
}
