package fr.narrnouille.customnpcsscoreboardcompat.client.gui;

import com.google.common.collect.Lists;
import fr.narrnouille.customnpcsscoreboardcompat.CustomMarkTypes;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.HashMap;
import java.util.Map;

public class GuiMarkTypeSelection extends GuiBasic implements ICustomScrollListener {
    private final Map<String, Integer> markTypes = new HashMap<>();
    private final int currentType;
    private GuiCustomScrollNop scroll;
    private GuiSelectionListener listener;

    public GuiMarkTypeSelection(int currentType) {
        this.currentType = currentType;
        this.drawDefaultBackground = false;
        this.title = "";
        this.setBackground("menubg.png");
        this.imageWidth = 220;
        this.imageHeight = 220;
    }

    @Override
    public void init() {
        super.init();
        if (this.wrapper.parent instanceof GuiSelectionListener) {
            this.listener = (GuiSelectionListener) this.wrapper.parent;
        }

        this.addLabel(new GuiLabel(0, "mark.select", this.guiLeft + 8, this.guiTop + 4));
        this.addButton(new GuiButtonNop(this, 1, this.guiLeft + this.imageWidth - 26, this.guiTop + 4, 20, 20, "X"));

        String[] displayKeys = CustomMarkTypes.displayKeys();
        this.markTypes.clear();
        for (int type = 0; type < displayKeys.length; type++) {
            this.markTypes.put(displayKeys[type], type);
        }

        if (this.scroll == null) {
            this.scroll = new MarkTypeScroll(this, 0);
            this.scroll.setSize(210, 188);
        }

        this.scroll.setUnsortedList(Lists.newArrayList(displayKeys));
        if (this.currentType >= 0 && this.currentType < displayKeys.length) {
            this.scroll.setSelected(displayKeys[this.currentType]);
            this.scroll.scrollTo(displayKeys[this.currentType]);
        }
        this.scroll.guiLeft = this.guiLeft + 5;
        this.scroll.guiTop = this.guiTop + 24;
        this.addScroll(this.scroll);
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
        String selected = scroll.getSelected();
        Integer type = this.markTypes.get(selected);
        if (type != null && this.listener != null) {
            this.listener.selected(type, selected);
            this.close();
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 1) {
            this.close();
        }
    }

    private static final class MarkTypeScroll extends GuiCustomScrollNop {
        private MarkTypeScroll(GuiMarkTypeSelection parent, int id) {
            super(parent, id);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            return super.mouseScrolled(mouseX, mouseY, scrollY, scrollX);
        }
    }
}
