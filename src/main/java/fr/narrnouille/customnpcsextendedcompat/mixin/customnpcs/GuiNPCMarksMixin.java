package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.CustomMarkTypes;
import fr.narrnouille.customnpcsextendedcompat.client.gui.GuiMarkTypeSelection;
import net.minecraft.client.gui.screens.Screen;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.advanced.GuiNPCMarks;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiNPCMarks.class)
public abstract class GuiNPCMarksMixin extends GuiNPCInterface2 implements GuiSelectionListener {
    @Unique
    private static final int customnpcsExtendedCompat$PAGE_SIZE = 8;
    @Unique
    private static final int customnpcsExtendedCompat$ROW_BUTTON_START = 1000;
    @Unique
    private static final int customnpcsExtendedCompat$PREVIOUS_PAGE_BUTTON = 2000;
    @Unique
    private static final int customnpcsExtendedCompat$PAGE_LABEL_BUTTON = 2001;
    @Unique
    private static final int customnpcsExtendedCompat$NEXT_PAGE_BUTTON = 2002;
    @Unique
    private static final int customnpcsExtendedCompat$ADD_BUTTON = 2003;

    @Shadow
    private MarkData data;
    @Shadow
    private MarkData.Mark selectedMark;

    protected GuiNPCMarksMixin(EntityNPCInterface npc) {
        super(npc);
    }

    /**
     * @author Narrnouille
     * @reason CustomNPCs hard-codes mark options and uses button IDs that collide above 9 rows.
     */
    @Overwrite
    public void init() {
        super.init();
        customnpcsExtendedCompat$clampPage();

        int y = this.guiTop + 14;
        int start = customnpcsExtendedCompat$page * customnpcsExtendedCompat$PAGE_SIZE;
        int end = Math.min(start + customnpcsExtendedCompat$PAGE_SIZE, this.data.marks.size());

        for (int index = start; index < end; index++) {
            int row = index - start;
            int buttonBase = customnpcsExtendedCompat$ROW_BUTTON_START + row * 10;
            MarkData.Mark mark = this.data.marks.get(index);

            this.addButton(new GuiButtonNop(this, buttonBase + 1, this.guiLeft + 6, y, 120, 20, customnpcsExtendedCompat$displayKey(mark.type)));
            Object color = Integer.toHexString(mark.color);
            while (((String) color).length() < 6) {
                color = "0" + color;
            }
            this.addButton(new GuiButtonNop(this, buttonBase + 2, this.guiLeft + 128, y, 60, 20, (String) color));
            this.addButton(new GuiButtonNop(this, buttonBase + 3, this.guiLeft + 190, y, 120, 20, "availability.options"));
            this.addButton(new GuiButtonNop(this, buttonBase + 4, this.guiLeft + 312, y, 40, 20, "X"));
            y += 22;
        }

        int controlsY = this.guiTop + 192;
        GuiButtonNop previous = new GuiButtonNop(this, customnpcsExtendedCompat$PREVIOUS_PAGE_BUTTON, this.guiLeft + 6, controlsY, 40, 20, "<");
        previous.active = customnpcsExtendedCompat$page > 0;
        this.addButton(previous);

        GuiButtonNop pageLabel = new GuiButtonNop(this, customnpcsExtendedCompat$PAGE_LABEL_BUTTON, this.guiLeft + 48, controlsY, 64, 20, customnpcsExtendedCompat$pageLabel());
        pageLabel.active = false;
        this.addButton(pageLabel);

        GuiButtonNop next = new GuiButtonNop(this, customnpcsExtendedCompat$NEXT_PAGE_BUTTON, this.guiLeft + 114, controlsY, 40, 20, ">");
        next.active = customnpcsExtendedCompat$page + 1 < customnpcsExtendedCompat$pageCount();
        this.addButton(next);

        if (this.data.marks.size() < CustomMarkTypes.MAX_MARKS_PER_NPC) {
            this.addButton(new GuiButtonNop(this, customnpcsExtendedCompat$ADD_BUTTON, this.guiLeft + 292, controlsY, 60, 20, "gui.add"));
        }
    }

    /**
     * @author Narrnouille
     * @reason Handle paged mark rows and allow up to 64 saved mark entries.
     */
    @Overwrite
    public void buttonEvent(GuiButtonNop button) {
        if (button.id == customnpcsExtendedCompat$PREVIOUS_PAGE_BUTTON) {
            customnpcsExtendedCompat$page = Math.max(0, customnpcsExtendedCompat$page - 1);
            this.init();
            return;
        }

        if (button.id == customnpcsExtendedCompat$NEXT_PAGE_BUTTON) {
            customnpcsExtendedCompat$page = Math.min(customnpcsExtendedCompat$pageCount() - 1, customnpcsExtendedCompat$page + 1);
            this.init();
            return;
        }

        if (button.id == customnpcsExtendedCompat$ADD_BUTTON) {
            if (this.data.marks.size() < CustomMarkTypes.MAX_MARKS_PER_NPC) {
                this.data.addMark(0);
                customnpcsExtendedCompat$page = customnpcsExtendedCompat$pageCount() - 1;
                this.init();
            }
            return;
        }

        if (button.id >= customnpcsExtendedCompat$ROW_BUTTON_START && button.id < customnpcsExtendedCompat$ROW_BUTTON_START + customnpcsExtendedCompat$PAGE_SIZE * 10) {
            int row = (button.id - customnpcsExtendedCompat$ROW_BUTTON_START) / 10;
            int action = button.id % 10;
            int index = customnpcsExtendedCompat$page * customnpcsExtendedCompat$PAGE_SIZE + row;
            if (index < 0 || index >= this.data.marks.size()) {
                return;
            }

            this.selectedMark = this.data.marks.get(index);
            if (action == 1) {
                this.setSubGui(new GuiMarkTypeSelection(this.selectedMark.type));
            }
            if (action == 2) {
                this.setSubGui(new SubGuiColorSelector(this.selectedMark.color));
            }
            if (action == 3) {
                this.setSubGui(new SubGuiNpcAvailability(this.selectedMark.availability));
            }
            if (action == 4) {
                this.data.marks.remove(this.selectedMark);
                customnpcsExtendedCompat$clampPage();
                this.init();
            }
        }
    }

    /**
     * @author Narrnouille
     * @reason Preserve color selector behavior after replacing the mark screen.
     */
    @Overwrite
    public void subGuiClosed(Screen subgui) {
        if (subgui instanceof SubGuiColorSelector) {
            this.selectedMark.color = ((SubGuiColorSelector) subgui).color;
            this.init();
        }
    }

    @Override
    public void selected(int id, String name) {
        if (this.selectedMark != null && id >= 0 && id < CustomMarkTypes.TOTAL_MARK_TYPES) {
            this.selectedMark.type = id;
        }
    }

    /**
     * @author Narrnouille
     * @reason Preserve CustomNPCs save packet behavior after replacing the mark screen.
     */
    @Overwrite
    public void save() {
        Packets.sendServer(new SPacketMenuSave(EnumMenuType.MARK, this.data.getNBT()));
    }

    @Unique
    private int customnpcsExtendedCompat$page;

    @Unique
    private int customnpcsExtendedCompat$pageCount() {
        return Math.max(1, (this.data.marks.size() + customnpcsExtendedCompat$PAGE_SIZE - 1) / customnpcsExtendedCompat$PAGE_SIZE);
    }

    @Unique
    private void customnpcsExtendedCompat$clampPage() {
        customnpcsExtendedCompat$page = Math.max(0, Math.min(customnpcsExtendedCompat$page, customnpcsExtendedCompat$pageCount() - 1));
    }

    @Unique
    private String customnpcsExtendedCompat$pageLabel() {
        return (customnpcsExtendedCompat$page + 1) + "/" + customnpcsExtendedCompat$pageCount();
    }

    @Unique
    private static String customnpcsExtendedCompat$displayKey(int type) {
        String[] displayKeys = CustomMarkTypes.displayKeys();
        if (type >= 0 && type < displayKeys.length) {
            return displayKeys[type];
        }
        return displayKeys[0];
    }
}
