package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomAvailabilityScoreboardBridge;
import noppes.npcs.client.gui.SubGuiNpcAvailabilityScoreboard;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SubGuiNpcAvailabilityScoreboard.class, remap = false)
public abstract class SubGuiNpcAvailabilityScoreboardMixin extends GuiBasic implements ITextfieldListener {
    @Unique
    private static final int SCOREBOARD_3_TYPE_BUTTON_ID = 2;
    @Unique
    private static final int SCOREBOARD_3_OBJECTIVE_FIELD_ID = 12;
    @Unique
    private static final int SCOREBOARD_3_VALUE_FIELD_ID = 22;

    @Shadow
    private Availability availabitily;

    @Inject(method = "init", at = @At("RETURN"))
    private void customnpcsExtendedCompat$addThirdScoreboardField(CallbackInfo callbackInfo) {
        if (!(this.availabitily instanceof CustomAvailabilityScoreboardBridge bridge)) {
            return;
        }

        int y = this.guiTop + 58;
        this.addTextField(new GuiTextFieldNop(
                SCOREBOARD_3_OBJECTIVE_FIELD_ID,
                this,
                this.guiLeft + 4,
                y,
                140,
                20,
                bridge.customnpcsExtendedCompat$getScoreboard3Objective()
        ));
        this.addButton(new GuiButtonNop(
                this,
                SCOREBOARD_3_TYPE_BUTTON_ID,
                this.guiLeft + 148,
                y,
                90,
                20,
                new String[]{"availability.smaller", "availability.equals", "availability.bigger"},
                bridge.customnpcsExtendedCompat$getScoreboard3Type().ordinal()
        ));
        this.addTextField(new GuiTextFieldNop(
                SCOREBOARD_3_VALUE_FIELD_ID,
                this,
                this.guiLeft + 244,
                y,
                60,
                20,
                bridge.customnpcsExtendedCompat$getScoreboard3Value() + ""
        ));
        this.getTextField(SCOREBOARD_3_VALUE_FIELD_ID).numbersOnly = true;
    }

    @Inject(method = "buttonEvent", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$handleThirdScoreboardButton(
            GuiButtonNop button,
            CallbackInfo callbackInfo
    ) {
        if (button.id != SCOREBOARD_3_TYPE_BUTTON_ID
                || !(this.availabitily instanceof CustomAvailabilityScoreboardBridge bridge)) {
            return;
        }

        bridge.customnpcsExtendedCompat$setScoreboard3Type(EnumAvailabilityScoreboard.values()[button.getValue()]);
        callbackInfo.cancel();
    }

    @Inject(method = "unFocused", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$saveThirdScoreboardField(
            GuiTextFieldNop textField,
            CallbackInfo callbackInfo
    ) {
        if (!(this.availabitily instanceof CustomAvailabilityScoreboardBridge bridge)) {
            return;
        }

        if (textField.id == SCOREBOARD_3_OBJECTIVE_FIELD_ID) {
            bridge.customnpcsExtendedCompat$setScoreboard3Objective(textField.getValue());
            callbackInfo.cancel();
        }

        if (textField.id == SCOREBOARD_3_VALUE_FIELD_ID) {
            bridge.customnpcsExtendedCompat$setScoreboard3Value(NoppesStringUtils.parseInt(textField.getValue(), 0));
            callbackInfo.cancel();
        }
    }
}
