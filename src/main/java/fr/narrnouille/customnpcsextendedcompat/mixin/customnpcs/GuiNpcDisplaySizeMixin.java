package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcNameTagBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.mainmenu.GuiNpcDisplay;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiNpcDisplay.class, remap = false)
public abstract class GuiNpcDisplaySizeMixin extends GuiNPCInterface2 implements ITextfieldListener, IGuiData {
    @Unique
    private static final int MODEL_BUTTON_ID = 1;
    @Unique
    private static final int SIZE_LABEL_ID = 2;
    @Unique
    private static final int SIZE_FIELD_ID = 2;
    @Unique
    private static final int SIZE_RANGE_LABEL_ID = 3;
    @Unique
    private static final int NAME_TAG_Y_OFFSET_FIELD_ID = 1000;
    @Unique
    private static final int NAME_TAG_Y_OFFSET_LABEL_ID = 1001;
    @Unique
    private static final int NAME_VISIBILITY_DISTANCE_FIELD_ID = 1002;
    @Unique
    private static final int NAME_VISIBILITY_DISTANCE_LABEL_ID = 1003;
    @Unique
    private static final int TITLE_VISIBILITY_DISTANCE_FIELD_ID = 1004;
    @Unique
    private static final int TITLE_VISIBILITY_DISTANCE_LABEL_ID = 1005;
    @Unique
    private static final int VISIBILITY_RANGE_LABEL_ID = 1006;
    @Unique
    private static final int MARK_MOVEMENT_SECONDS_FIELD_ID = 1007;
    @Unique
    private static final int MARK_MOVEMENT_SECONDS_LABEL_ID = 1008;

    @Shadow
    private DataDisplay display;

    protected GuiNpcDisplaySizeMixin(EntityNPCInterface npc) {
        super(npc);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void customnpcsExtendedCompat$useFloatSizeField(CallbackInfo callbackInfo) {
        GuiButtonNop modelButton = this.getButton(MODEL_BUTTON_ID);
        if (modelButton != null) {
            modelButton.setWidth(64);
        }

        GuiLabel sizeLabel = this.getLabel(SIZE_LABEL_ID);
        if (sizeLabel != null) {
            sizeLabel.setX(this.guiLeft + 116);
        }

        GuiTextFieldNop sizeField = this.getTextField(SIZE_FIELD_ID);
        if (sizeField != null) {
            sizeField.setX(this.guiLeft + 143);
            sizeField.setWidth(32);
            sizeField.numbersOnly = false;
            sizeField.floatsOnly = true;
            sizeField.setMinMaxDefault(
                    CustomNpcSizeBridge.MIN_SIZE_SCALE,
                    CustomNpcSizeBridge.MAX_SIZE_SCALE,
                    CustomNpcSizeBridge.DEFAULT_SIZE_SCALE
            );
            sizeField.setValue(CustomNpcSizeBridge.formatSizeScale(CustomNpcSizeBridge.getSizeScale(this.display)));
        }

        GuiLabel rangeLabel = this.getLabel(SIZE_RANGE_LABEL_ID);
        if (rangeLabel != null) {
            rangeLabel.setX(this.guiLeft + 178);
            rangeLabel.setMessage(Component.literal("(0.2-6)"));
        }

        GuiTextFieldNop titleField = this.getTextField(11);
        if (titleField != null) {
            this.addLabel(new GuiLabel(
                    NAME_TAG_Y_OFFSET_LABEL_ID,
                    "Name Offset",
                    this.guiLeft + 242,
                    titleField.getY() + 5
            ));
            GuiTextFieldNop offsetField = new GuiTextFieldNop(
                    NAME_TAG_Y_OFFSET_FIELD_ID,
                    this,
                    this.guiLeft + 310,
                    titleField.getY(),
                    28,
                    20,
                    CustomNpcNameTagBridge.formatNameTagYOffset(CustomNpcNameTagBridge.getNameTagYOffset(this.display))
            );
            offsetField.numbersOnly = false;
            offsetField.floatsOnly = true;
            offsetField.setMinMaxDefault(
                    CustomNpcNameTagBridge.MIN_NAME_TAG_Y_OFFSET,
                    CustomNpcNameTagBridge.MAX_NAME_TAG_Y_OFFSET,
                    CustomNpcNameTagBridge.DEFAULT_NAME_TAG_Y_OFFSET
            );
            this.addTextField(offsetField);

            this.addLabel(new GuiLabel(
                    MARK_MOVEMENT_SECONDS_LABEL_ID,
                    "Time",
                    this.guiLeft + 344,
                    titleField.getY() + 5
            ));
            GuiTextFieldNop movementField = new GuiTextFieldNop(
                    MARK_MOVEMENT_SECONDS_FIELD_ID,
                    this,
                    this.guiLeft + 374,
                    titleField.getY(),
                    28,
                    20,
                    CustomNpcNameTagBridge.formatMarkMovementSeconds(CustomNpcNameTagBridge.getMarkMovementSeconds(this.display))
            );
            movementField.numbersOnly = false;
            movementField.floatsOnly = true;
            movementField.setMinMaxDefault(
                    CustomNpcNameTagBridge.MIN_MARK_MOVEMENT_SECONDS,
                    CustomNpcNameTagBridge.MAX_MARK_MOVEMENT_SECONDS,
                    CustomNpcNameTagBridge.DEFAULT_MARK_MOVEMENT_SECONDS
            );
            this.addTextField(movementField);

            int rangeY = titleField.getY() + 23;
            this.addLabel(new GuiLabel(
                    VISIBILITY_RANGE_LABEL_ID,
                    "Range:",
                    this.guiLeft + 238,
                    rangeY + 5
            ));
            this.customnpcsExtendedCompat$addVisibilityDistanceField(
                    NAME_VISIBILITY_DISTANCE_LABEL_ID,
                    NAME_VISIBILITY_DISTANCE_FIELD_ID,
                    "Name",
                    this.guiLeft + 282,
                    this.guiLeft + 313,
                    rangeY,
                    CustomNpcNameTagBridge.getNameVisibilityDistance(this.display)
            );
            this.customnpcsExtendedCompat$addVisibilityDistanceField(
                    TITLE_VISIBILITY_DISTANCE_LABEL_ID,
                    TITLE_VISIBILITY_DISTANCE_FIELD_ID,
                    "Title",
                    this.guiLeft + 350,
                    this.guiLeft + 376,
                    rangeY,
                    CustomNpcNameTagBridge.getTitleVisibilityDistance(this.display)
            );
        }
    }

    @Inject(method = "unFocused", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$saveFloatSizeField(GuiTextFieldNop textField, CallbackInfo callbackInfo) {
        if (textField.id == MARK_MOVEMENT_SECONDS_FIELD_ID) {
            float seconds = CustomNpcNameTagBridge.clampMarkMovementSeconds(textField.getFloat());
            if (this.display instanceof CustomNpcNameTagBridge nameTagBridge) {
                nameTagBridge.customnpcsExtendedCompat$setMarkMovementSeconds(seconds);
            }
            textField.setValue(CustomNpcNameTagBridge.formatMarkMovementSeconds(seconds));
            callbackInfo.cancel();
            return;
        }

        if (textField.id == NAME_VISIBILITY_DISTANCE_FIELD_ID || textField.id == TITLE_VISIBILITY_DISTANCE_FIELD_ID) {
            float distance = textField.id == NAME_VISIBILITY_DISTANCE_FIELD_ID
                    ? CustomNpcNameTagBridge.clampNameVisibilityDistance(textField.getFloat())
                    : CustomNpcNameTagBridge.clampTitleVisibilityDistance(textField.getFloat());
            if (this.display instanceof CustomNpcNameTagBridge nameTagBridge) {
                if (textField.id == NAME_VISIBILITY_DISTANCE_FIELD_ID) {
                    nameTagBridge.customnpcsExtendedCompat$setNameVisibilityDistance(distance);
                } else {
                    nameTagBridge.customnpcsExtendedCompat$setTitleVisibilityDistance(distance);
                }
            }
            textField.setValue(CustomNpcNameTagBridge.formatLabelVisibilityDistance(distance));
            callbackInfo.cancel();
            return;
        }

        if (textField.id == NAME_TAG_Y_OFFSET_FIELD_ID) {
            float yOffset = CustomNpcNameTagBridge.clampNameTagYOffset(textField.getFloat());
            if (this.display instanceof CustomNpcNameTagBridge nameTagBridge) {
                nameTagBridge.customnpcsExtendedCompat$setNameTagYOffset(yOffset);
            }
            textField.setValue(CustomNpcNameTagBridge.formatNameTagYOffset(yOffset));
            callbackInfo.cancel();
            return;
        }

        if (textField.id != SIZE_FIELD_ID) {
            return;
        }

        float sizeScale = CustomNpcSizeBridge.clampSizeScale(textField.getFloat());
        if (this.display instanceof CustomNpcSizeBridge sizeBridge) {
            sizeBridge.customnpcsExtendedCompat$setSizeScale(sizeScale);
        }
        textField.setValue(CustomNpcSizeBridge.formatSizeScale(sizeScale));
        callbackInfo.cancel();
    }

    @Unique
    private void customnpcsExtendedCompat$addVisibilityDistanceField(
            int labelId,
            int fieldId,
            String label,
            int labelX,
            int fieldX,
            int y,
            float value
    ) {
        this.addLabel(new GuiLabel(
                labelId,
                label,
                labelX,
                y + 5
        ));
        GuiTextFieldNop distanceField = new GuiTextFieldNop(
                fieldId,
                this,
                fieldX,
                y,
                34,
                20,
                CustomNpcNameTagBridge.formatLabelVisibilityDistance(value)
        );
        distanceField.numbersOnly = false;
        distanceField.floatsOnly = true;
        distanceField.setMinMaxDefault(
                CustomNpcNameTagBridge.MIN_LABEL_VISIBILITY_DISTANCE,
                CustomNpcNameTagBridge.MAX_LABEL_VISIBILITY_DISTANCE,
                value
        );
        this.addTextField(distanceField);
    }
}
