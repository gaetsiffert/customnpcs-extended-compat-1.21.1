package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsscoreboardcompat.CustomNpcDialogSlots;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.advanced.GuiNPCDialogNpcOptions;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcDialogRemove;
import noppes.npcs.packets.server.SPacketNpcDialogSet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(GuiNPCDialogNpcOptions.class)
public abstract class GuiNPCDialogNpcOptionsMixin extends GuiNPCInterface2 implements GuiSelectionListener, IGuiData {
    @Unique
    private static final int customnpcsScoreboardCompat$PAGE_SIZE = 12;
    @Unique
    private static final int customnpcsScoreboardCompat$SLOT_BUTTON_START = 1000;
    @Unique
    private static final int customnpcsScoreboardCompat$REMOVE_BUTTON_START = 1100;
    @Unique
    private static final int customnpcsScoreboardCompat$PREVIOUS_PAGE_BUTTON = 2000;
    @Unique
    private static final int customnpcsScoreboardCompat$PAGE_LABEL_BUTTON = 2001;
    @Unique
    private static final int customnpcsScoreboardCompat$NEXT_PAGE_BUTTON = 2002;

    @Shadow
    private HashMap<Integer, DialogOption> data;
    @Shadow
    private int selectedSlot;

    protected GuiNPCDialogNpcOptionsMixin(EntityNPCInterface npc) {
        super(npc);
    }

    /**
     * @author Narrnouille
     * @reason CustomNPCs hard-codes 12 NPC dialog slots; the backing data and packets already support arbitrary slot ids.
     */
    @Overwrite
    public void init() {
        super.init();
        customnpcsScoreboardCompat$clampPage();

        int start = customnpcsScoreboardCompat$page * customnpcsScoreboardCompat$PAGE_SIZE;
        for (int row = 0; row < customnpcsScoreboardCompat$PAGE_SIZE; row++) {
            int slot = start + row;
            if (slot >= CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC) {
                break;
            }

            int offset = row >= 6 ? 200 : 0;
            int y = this.guiTop + 13 + row % 6 * 22;
            this.addButton(new GuiButtonNop(this, customnpcsScoreboardCompat$REMOVE_BUTTON_START + row, this.guiLeft + 20 + offset, y, 20, 20, "X"));
            this.addLabel(new GuiLabel(row, String.valueOf(slot), this.guiLeft + 6 + offset, this.guiTop + 18 + row % 6 * 22));

            String title = "dialog.selectoption";
            if (this.data.containsKey(slot)) {
                title = this.data.get(slot).title;
            }
            this.addButton(new GuiButtonNop(this, customnpcsScoreboardCompat$SLOT_BUTTON_START + row, this.guiLeft + 44 + offset, y, 140, 20, title));
        }

        int controlsY = this.guiTop + 154;
        GuiButtonNop previous = new GuiButtonNop(this, customnpcsScoreboardCompat$PREVIOUS_PAGE_BUTTON, this.guiLeft + 20, controlsY, 40, 20, "<");
        previous.active = customnpcsScoreboardCompat$page > 0;
        this.addButton(previous);

        GuiButtonNop pageLabel = new GuiButtonNop(this, customnpcsScoreboardCompat$PAGE_LABEL_BUTTON, this.guiLeft + 62, controlsY, 64, 20, customnpcsScoreboardCompat$pageLabel());
        pageLabel.active = false;
        this.addButton(pageLabel);

        GuiButtonNop next = new GuiButtonNop(this, customnpcsScoreboardCompat$NEXT_PAGE_BUTTON, this.guiLeft + 128, controlsY, 40, 20, ">");
        next.active = customnpcsScoreboardCompat$page + 1 < customnpcsScoreboardCompat$pageCount();
        this.addButton(next);
    }

    /**
     * @author Narrnouille
     * @reason Route paged buttons to actual dialog slots 0-63.
     */
    @Overwrite
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == customnpcsScoreboardCompat$PREVIOUS_PAGE_BUTTON) {
            customnpcsScoreboardCompat$page = Math.max(0, customnpcsScoreboardCompat$page - 1);
            this.init();
            return;
        }

        if (id == customnpcsScoreboardCompat$NEXT_PAGE_BUTTON) {
            customnpcsScoreboardCompat$page = Math.min(customnpcsScoreboardCompat$pageCount() - 1, customnpcsScoreboardCompat$page + 1);
            this.init();
            return;
        }

        if (id >= customnpcsScoreboardCompat$SLOT_BUTTON_START && id < customnpcsScoreboardCompat$SLOT_BUTTON_START + customnpcsScoreboardCompat$PAGE_SIZE) {
            int slot = customnpcsScoreboardCompat$slotFromRow(id - customnpcsScoreboardCompat$SLOT_BUTTON_START);
            if (slot >= CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC) {
                return;
            }
            this.selectedSlot = slot;
            int dialogID = -1;
            if (this.data.containsKey(slot)) {
                dialogID = this.data.get(slot).dialogId;
            }
            this.setSubGui(new GuiDialogSelection(dialogID));
            return;
        }

        if (id >= customnpcsScoreboardCompat$REMOVE_BUTTON_START && id < customnpcsScoreboardCompat$REMOVE_BUTTON_START + customnpcsScoreboardCompat$PAGE_SIZE) {
            int slot = customnpcsScoreboardCompat$slotFromRow(id - customnpcsScoreboardCompat$REMOVE_BUTTON_START);
            if (slot >= CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC) {
                return;
            }
            this.data.remove(slot);
            Packets.sendServer(new SPacketNpcDialogRemove(slot));
            this.init();
        }
    }

    /**
     * @author Narrnouille
     * @reason Preserve dialog selection behavior after replacing the paged screen.
     */
    @Overwrite
    public void selected(int id, String name) {
        Packets.sendServer(new SPacketNpcDialogSet(this.selectedSlot, id));
    }

    /**
     * @author Narrnouille
     * @reason Accept server dialog data for the expanded slot range.
     */
    @Overwrite
    public void setGuiData(CompoundTag compound) {
        int pos = compound.getInt("Position");
        if (pos < 0 || pos >= CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC) {
            return;
        }

        DialogOption dialog = new DialogOption();
        dialog.readNBT(compound);
        this.data.put(pos, dialog);
        this.init();
    }

    /**
     * @author Narrnouille
     * @reason Preserve empty save behavior after replacing the screen.
     */
    @Overwrite
    public void save() {
    }

    @Unique
    private int customnpcsScoreboardCompat$page;

    @Unique
    private int customnpcsScoreboardCompat$pageCount() {
        return Math.max(1, (CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC + customnpcsScoreboardCompat$PAGE_SIZE - 1) / customnpcsScoreboardCompat$PAGE_SIZE);
    }

    @Unique
    private int customnpcsScoreboardCompat$slotFromRow(int row) {
        return customnpcsScoreboardCompat$page * customnpcsScoreboardCompat$PAGE_SIZE + row;
    }

    @Unique
    private void customnpcsScoreboardCompat$clampPage() {
        customnpcsScoreboardCompat$page = Math.max(0, Math.min(customnpcsScoreboardCompat$page, customnpcsScoreboardCompat$pageCount() - 1));
    }

    @Unique
    private String customnpcsScoreboardCompat$pageLabel() {
        return (customnpcsScoreboardCompat$page + 1) + "/" + customnpcsScoreboardCompat$pageCount();
    }
}
