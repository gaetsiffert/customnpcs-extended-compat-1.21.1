package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsscoreboardcompat.CustomMarkTypes;
import fr.narrnouille.customnpcsscoreboardcompat.client.gui.GuiMarkTypeSelection;
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
    private static final int customnpcsScoreboardCompat$PAGE_SIZE = 8;
    @Unique
    private static final int customnpcsScoreboardCompat$ROW_BUTTON_START = 1000;
    @Unique
    private static final int customnpcsScoreboardCompat$PREVIOUS_PAGE_BUTTON = 2000;
    @Unique
    private static final int customnpcsScoreboardCompat$PAGE_LABEL_BUTTON = 2001;
    @Unique
    private static final int customnpcsScoreboardCompat$NEXT_PAGE_BUTTON = 2002;
    @Unique
    private static final int customnpcsScoreboardCompat$ADD_BUTTON = 2003;

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
        customnpcsScoreboardCompat$clampPage();

        int y = this.guiTop + 14;
        int start = customnpcsScoreboardCompat$page * customnpcsScoreboardCompat$PAGE_SIZE;
        int end = Math.min(start + customnpcsScoreboardCompat$PAGE_SIZE, this.data.marks.size());

        for (int index = start; index < end; index++) {
            int row = index - start;
            int buttonBase = customnpcsScoreboardCompat$ROW_BUTTON_START + row * 10;
            MarkData.Mark mark = this.data.marks.get(index);

            this.addButton(new GuiButtonNop(this, buttonBase + 1, this.guiLeft + 6, y, 120, 20, customnpcsScoreboardCompat$displayKey(mark.type)));
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
        GuiButtonNop previous = new GuiButtonNop(this, customnpcsScoreboardCompat$PREVIOUS_PAGE_BUTTON, this.guiLeft + 6, controlsY, 40, 20, "<");
        previous.active = customnpcsScoreboardCompat$page > 0;
        this.addButton(previous);

        GuiButtonNop pageLabel = new GuiButtonNop(this, customnpcsScoreboardCompat$PAGE_LABEL_BUTTON, this.guiLeft + 48, controlsY, 64, 20, customnpcsScoreboardCompat$pageLabel());
        pageLabel.active = false;
        this.addButton(pageLabel);

        GuiButtonNop next = new GuiButtonNop(this, customnpcsScoreboardCompat$NEXT_PAGE_BUTTON, this.guiLeft + 114, controlsY, 40, 20, ">");
        next.active = customnpcsScoreboardCompat$page + 1 < customnpcsScoreboardCompat$pageCount();
        this.addButton(next);

        if (this.data.marks.size() < CustomMarkTypes.MAX_MARKS_PER_NPC) {
            this.addButton(new GuiButtonNop(this, customnpcsScoreboardCompat$ADD_BUTTON, this.guiLeft + 292, controlsY, 60, 20, "gui.add"));
        }
    }

    /**
     * @author Narrnouille
     * @reason Handle paged mark rows and allow up to 64 saved mark entries.
     */
    @Overwrite
    public void buttonEvent(GuiButtonNop button) {
        if (button.id == customnpcsScoreboardCompat$PREVIOUS_PAGE_BUTTON) {
            customnpcsScoreboardCompat$page = Math.max(0, customnpcsScoreboardCompat$page - 1);
            this.init();
            return;
        }

        if (button.id == customnpcsScoreboardCompat$NEXT_PAGE_BUTTON) {
            customnpcsScoreboardCompat$page = Math.min(customnpcsScoreboardCompat$pageCount() - 1, customnpcsScoreboardCompat$page + 1);
            this.init();
            return;
        }

        if (button.id == customnpcsScoreboardCompat$ADD_BUTTON) {
            if (this.data.marks.size() < CustomMarkTypes.MAX_MARKS_PER_NPC) {
                this.data.addMark(0);
                customnpcsScoreboardCompat$page = customnpcsScoreboardCompat$pageCount() - 1;
                this.init();
            }
            return;
        }

        if (button.id >= customnpcsScoreboardCompat$ROW_BUTTON_START && button.id < customnpcsScoreboardCompat$ROW_BUTTON_START + customnpcsScoreboardCompat$PAGE_SIZE * 10) {
            int row = (button.id - customnpcsScoreboardCompat$ROW_BUTTON_START) / 10;
            int action = button.id % 10;
            int index = customnpcsScoreboardCompat$page * customnpcsScoreboardCompat$PAGE_SIZE + row;
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
                customnpcsScoreboardCompat$clampPage();
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
    private int customnpcsScoreboardCompat$page;

    @Unique
    private int customnpcsScoreboardCompat$pageCount() {
        return Math.max(1, (this.data.marks.size() + customnpcsScoreboardCompat$PAGE_SIZE - 1) / customnpcsScoreboardCompat$PAGE_SIZE);
    }

    @Unique
    private void customnpcsScoreboardCompat$clampPage() {
        customnpcsScoreboardCompat$page = Math.max(0, Math.min(customnpcsScoreboardCompat$page, customnpcsScoreboardCompat$pageCount() - 1));
    }

    @Unique
    private String customnpcsScoreboardCompat$pageLabel() {
        return (customnpcsScoreboardCompat$page + 1) + "/" + customnpcsScoreboardCompat$pageCount();
    }

    @Unique
    private static String customnpcsScoreboardCompat$displayKey(int type) {
        String[] displayKeys = CustomMarkTypes.displayKeys();
        if (type >= 0 && type < displayKeys.length) {
            return displayKeys[type];
        }
        return displayKeys[0];
    }
}
