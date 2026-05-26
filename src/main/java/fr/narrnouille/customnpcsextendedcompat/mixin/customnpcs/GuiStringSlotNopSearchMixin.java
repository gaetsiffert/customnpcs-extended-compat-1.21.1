package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiStringSlotNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.common.util.NaturalOrderComparator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Mixin(GuiStringSlotNop.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class GuiStringSlotNopSearchMixin extends ObjectSelectionList {
    @Unique
    private GuiTextFieldNop customnpcsExtendedCompat$searchField;
    @Unique
    private List<String> customnpcsExtendedCompat$values = new ArrayList<>();
    @Unique
    private Map<String, Integer> customnpcsExtendedCompat$colors;
    @Unique
    private String customnpcsExtendedCompat$lastSearch = "";

    @Shadow
    private GuiBasic parent;

    private GuiStringSlotNopSearchMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void customnpcsExtendedCompat$initSearch(Collection<String> list, GuiBasic parent, boolean multiSelect, CallbackInfo ci) {
        int fieldWidth = Math.max(80, Math.min(300, parent.width - 40));
        this.customnpcsExtendedCompat$searchField = new GuiTextFieldNop(-1000, null, (parent.width - fieldWidth) / 2, 32, fieldWidth, 20, "");
        parent.addTextField(this.customnpcsExtendedCompat$searchField);
        this.setRectangle(parent.width, parent.height - 120, 0, 56);
    }

    /**
     * @author Narrnouille
     * @reason Keep the full source list so GuiStringSlotNop can filter entries from its global search field.
     */
    @Overwrite
    public void setList(Collection<String> l) {
        this.customnpcsExtendedCompat$colors = null;
        this.customnpcsExtendedCompat$values = new ArrayList<>(l);
        Collections.sort(this.customnpcsExtendedCompat$values, new NaturalOrderComparator());
        this.customnpcsExtendedCompat$refreshEntries(null);
    }

    /**
     * @author Narrnouille
     * @reason Keep the full colored source list so GuiStringSlotNop can filter entries from its global search field.
     */
    @Overwrite
    public void setColoredList(Map<String, Integer> m) {
        List<String> values = new ArrayList<>(m.keySet());
        Collections.sort(values, new NaturalOrderComparator());

        this.customnpcsExtendedCompat$values = values;
        this.customnpcsExtendedCompat$colors = new LinkedHashMap<>();
        for (String value : values) {
            this.customnpcsExtendedCompat$colors.put(value, m.get(value));
        }
        this.customnpcsExtendedCompat$refreshEntries(null);
    }

    /**
     * @author Narrnouille
     * @reason Clear the cached source list in addition to the visible filtered entries.
     */
    @Overwrite
    public void clear() {
        this.customnpcsExtendedCompat$values.clear();
        if (this.customnpcsExtendedCompat$colors != null) {
            this.customnpcsExtendedCompat$colors.clear();
        }
        this.clearEntries();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.customnpcsExtendedCompat$searchField != null) {
            boolean inSearch = this.customnpcsExtendedCompat$searchField.isMouseOver(mouseX, mouseY);
            this.customnpcsExtendedCompat$searchField.mouseClicked(mouseX, mouseY, button);
            if (inSearch) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (this.customnpcsExtendedCompat$searchField != null && this.customnpcsExtendedCompat$searchField.isFocused()) {
            boolean handled = this.customnpcsExtendedCompat$searchField.charTyped(codePoint, modifiers);
            this.customnpcsExtendedCompat$refreshIfSearchChanged();
            return handled;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.customnpcsExtendedCompat$searchField != null && this.customnpcsExtendedCompat$searchField.isFocused()) {
            boolean handled = this.customnpcsExtendedCompat$searchField.keyPressed(keyCode, scanCode, modifiers);
            this.customnpcsExtendedCompat$refreshIfSearchChanged();
            return handled;
        }
        return false;
    }

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void customnpcsExtendedCompat$updateSearch(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (this.customnpcsExtendedCompat$searchField == null) {
            return;
        }

        int fieldWidth = Math.max(80, Math.min(300, this.parent.width - 40));
        this.customnpcsExtendedCompat$searchField.setX((this.parent.width - fieldWidth) / 2);
        this.customnpcsExtendedCompat$searchField.setY(32);
        this.customnpcsExtendedCompat$searchField.setWidth(fieldWidth);
        this.customnpcsExtendedCompat$refreshIfSearchChanged();
    }

    @Unique
    private void customnpcsExtendedCompat$refreshIfSearchChanged() {
        String search = this.customnpcsExtendedCompat$searchField.getValue();
        if (!search.equals(this.customnpcsExtendedCompat$lastSearch)) {
            this.customnpcsExtendedCompat$lastSearch = search;
            this.customnpcsExtendedCompat$refreshEntries(((GuiStringSlotNop) (Object) this).getSelectedString());
        }
    }

    @Unique
    private void customnpcsExtendedCompat$refreshEntries(String selected) {
        this.clearEntries();
        GuiStringSlotNop<?> self = (GuiStringSlotNop<?>) (Object) this;
        for (String value : this.customnpcsExtendedCompat$values) {
            if (!this.customnpcsExtendedCompat$matchesSearch(value)) {
                continue;
            }

            Integer color = this.customnpcsExtendedCompat$colors == null ? null : this.customnpcsExtendedCompat$colors.get(value);
            GuiStringSlotNop<?>.ListEntry entry = color == null ? self.new ListEntry(value) : self.new ListEntry(value, color);
            this.addEntry((ObjectSelectionList.Entry) entry);
        }
        if (selected != null) {
            ((GuiStringSlotNop) (Object) this).setSelected(selected);
        } else {
            this.setSelected(null);
        }
        this.setScrollAmount(0.0);
    }

    @Unique
    private boolean customnpcsExtendedCompat$matchesSearch(String value) {
        String search = this.customnpcsExtendedCompat$lastSearch.trim();
        if (search.isEmpty()) {
            return true;
        }

        String raw = value.toLowerCase(Locale.ROOT);
        String translated = I18n.get(value).toLowerCase(Locale.ROOT);
        for (String word : search.toLowerCase(Locale.ROOT).split("\\s+")) {
            if (!raw.contains(word) && !translated.contains(word)) {
                return false;
            }
        }
        return true;
    }
}
