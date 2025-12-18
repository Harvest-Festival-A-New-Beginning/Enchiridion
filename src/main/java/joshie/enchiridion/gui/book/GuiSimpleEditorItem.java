package joshie.enchiridion.gui.book;

import com.mojang.blaze3d.vertex.PoseStack;
import joshie.enchiridion.EConfig;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.gui.IBookEditorOverlay;
import joshie.enchiridion.helpers.ItemListHelper;
import joshie.enchiridion.util.ELocation;
import joshie.enchiridion.util.IItemSelectable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class GuiSimpleEditorItem extends AbstractGuiOverlay {
    public static final GuiSimpleEditorItem INSTANCE = new GuiSimpleEditorItem();
    private static final ResourceLocation CHECKED = new ELocation("check_selected");
    private static final ResourceLocation UNCHECKED = new ELocation("check_unselected");
    public IItemSelectable selectable = null;
    private ArrayList<ItemStack> sorted;
    private int position;

    protected GuiSimpleEditorItem() {
    }

    public IBookEditorOverlay setItem(IItemSelectable item) {
        selectable = item;
        return this;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int offsetX = GuiBook.INSTANCE.x;
        int offsetY = GuiBook.INSTANCE.y;
        int backgroundColor = 0xFFB0A483; //0xFF48453C
        int fontColor = 0xCE48433D;
        if (mouseX >= EConfig.SETTINGS.editorXPos + 2 && mouseX <= EConfig.SETTINGS.editorXPos + 83) {
            if (mouseY >= EConfig.SETTINGS.toolbarYPos.get() + 9 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 23) {
                fontColor = 0xFFB0A483;
                backgroundColor = 0xCE48433D;
            }
        }

        // Draw bordered rectangle
        int left = EConfig.SETTINGS.editorXPos + 2;
        int top = EConfig.SETTINGS.toolbarYPos.get() + 9;
        int right = EConfig.SETTINGS.editorXPos + 83;
        int bottom = EConfig.SETTINGS.toolbarYPos.get() + 23;
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, backgroundColor);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0xCE48433D);
        guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0xCE48433D);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0xCE48433D);
        guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0xCE48433D);

        // Draw scaled strings
        PoseStack poseStack = guiGraphics.pose();
        Font font = Minecraft.getInstance().font;
        left = EConfig.SETTINGS.editorXPos + 4;
        top = EConfig.SETTINGS.toolbarYPos.get() + 14;
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, 0);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        guiGraphics.drawWordWrap(font, Component.literal("[b]" + Enchiridion.format("tooltips") + ": [/b]"), (int)(left / 0.5F), (int)(top / 0.5F), 200, fontColor);
        poseStack.popPose();

        left = EConfig.SETTINGS.editorXPos + 4 + 55;
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, 0);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        guiGraphics.drawWordWrap(font, Component.literal("[b]" + selectable.getTooltipsEnabled() + "[/b]"), (int)(left / 0.5F), (int)(top / 0.5F), 200, fontColor);
        poseStack.popPose();

        if (sorted != null) {
            int j = 0;
            int k = 0;
            for (int i = position; i < position + 132; i++) {
                if (i >= 0 && i < sorted.size()) {
                    //1F > 0.75F, 4 > 5, 16 > 13 + EConfig.SETTINGS.editorXPos + 4, -30 > + EConfig.SETTINGS. toolBarYPos + 12
                    EnchiridionAPI.draw.drawStack(sorted.get(i), (j * 12) + EConfig.SETTINGS.editorXPos + 7, (k * 12) + EConfig.SETTINGS.toolbarYPos.get() + 26, 0.75F);

                    j++;

                    if (j > 5) {
                        j = 0;
                        k++;
                    }
                }
            }
        } else updateSearch(GuiSimpleEditor.INSTANCE.getText());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY) {
        if (mouseX >= EConfig.SETTINGS.editorXPos + 2 && mouseX <= EConfig.SETTINGS.editorXPos + 83) {
            if (mouseY >= EConfig.SETTINGS.toolbarYPos.get() + 9 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 23) {
                selectable.setTooltips(!selectable.getTooltipsEnabled());

                return true;
            }
        }

        if (sorted != null) {
            int j = 0;
            int k = 0;
            for (int i = position; i < position + 132; i++) {
                if (i >= 0 && i < sorted.size()) {
                    if (mouseX >= (j * 12) + EConfig.SETTINGS.editorXPos + 7 && mouseX <= (j * 12) + EConfig.SETTINGS.editorXPos + 19) {
                        if (mouseY >= (k * 12) + EConfig.SETTINGS.toolbarYPos.get() + 26 && mouseY <= (k * 12) + EConfig.SETTINGS.toolbarYPos.get() + 38) {
                            if (selectable != null) {
                                selectable.setItemStack(sorted.get(i));

                                return true;
                            }
                        }
                    }
                    j++;

                    if (j > 5) {
                        j = 0;
                        k++;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void scroll(boolean down, int mouseX, int mouseY) {
        if (mouseX >= EConfig.SETTINGS.editorXPos + 7 && mouseX <= EConfig.SETTINGS.editorXPos + 91) {
            if (mouseY >= EConfig.SETTINGS.toolbarYPos.get() + 14 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 302) {
                if (down) {
                    position = Math.min(sorted.size() - 200, position + 6);
                } else {
                    position = Math.max(0, position - 6);
                }
            }
        }
    }

    @Override
    public void updateSearch(String search) {
        ItemListHelper.addInventory();

        if (search == null || search.equals("")) {
            sorted = new ArrayList<>(ItemListHelper.allItems());
        } else {
            position = 0;
            sorted = new ArrayList<>();
            for (ItemStack stack : ItemListHelper.allItems()) {
                try {
                    if (!stack.isEmpty()) {
                        if (stack.getHoverName().getString().toLowerCase().contains(search.toLowerCase())) {
                            sorted.add(stack);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}