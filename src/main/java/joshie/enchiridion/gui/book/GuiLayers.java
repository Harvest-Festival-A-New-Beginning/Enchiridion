package joshie.enchiridion.gui.book;

import com.mojang.blaze3d.vertex.PoseStack;
import joshie.enchiridion.EConfig;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class GuiLayers extends AbstractGuiOverlay {
    private static final ResourceLocation LOCK_DFLT = new ResourceLocation(EInfo.MODID, "textures/books/lock_dftl.png");
    private static final ResourceLocation LOCK_HOVER = new ResourceLocation(EInfo.MODID, "textures/books/lock_hover.png");
    private static final ResourceLocation VISIBLE_DFLT = new ResourceLocation(EInfo.MODID, "textures/books/layer_dftl.png");
    private static final ResourceLocation VISIBLE_HOVER = new ResourceLocation(EInfo.MODID, "textures/books/layer_hover.png");
    private final GuiBook guiBook;
    private FeatureProvider dragged = null;
    private int held = 0;
    private int yStart = 0;
    private int layerPosition = 0;

    public GuiLayers(GuiBook guiBook) {
        this.guiBook = guiBook;
    }

    public boolean isDragging() {
        return held != 0;
    }

    private boolean isOverLayer(int layerY, int mouseX, int mouseY) {
        if (mouseX > EConfig.SETTINGS.layersXPos.get() + 20 && mouseX <= EConfig.SETTINGS.layersXPos.get() + 83) {
            return mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 3 + layerY && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 8 + layerY;
        }
        return false;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, GuiBook guiBookParam) {
        int offsetX = guiBook.x;
        int offsetY = guiBook.y;

        // Draw SIDEBAR image
        int left = EConfig.SETTINGS.layersXPos.get() - 3;
        int top = EConfig.SETTINGS.toolbarYPos.get() - 7;
        int right = EConfig.SETTINGS.layersXPos.get() + 87;
        int bottom = EConfig.SETTINGS.timelineYPos.get() + 13;
        int w = right - left;
        int h = bottom - top;
        guiGraphics.blit(SIDEBAR, offsetX + left, offsetY + top, 0, 0, w, h, w, h);

        // Draw bordered rectangle
        left = EConfig.SETTINGS.layersXPos.get();
        top = EConfig.SETTINGS.toolbarYPos.get() + 7;
        right = EConfig.SETTINGS.layersXPos.get() + 85;
        bottom = EConfig.SETTINGS.timelineYPos.get() + 11;
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0xFF312921);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0xFF191511);
        guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0xFF191511);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0xFF191511);
        guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0xFF191511);

        // Draw "layers" text
        left = EConfig.SETTINGS.layersXPos.get() + 20;
        top = EConfig.SETTINGS.toolbarYPos.get() - 2;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, 0);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawWordWrap(font, Component.literal(Enchiridion.format("layers")), left, top, 250, 0xFFFFFFFF);
        poseStack.popPose();
        int layerY = 0;
        int hoverY = 0;
        ArrayList<FeatureProvider> features = guiBookParam.getPage().getFeatures();
        for (int i = layerPosition; i < Math.min(features.size(), layerPosition + 24); i++) {
            layerY += 12;
            FeatureProvider feature = features.get(i);
            /* LOCK ICON */
            //Setup the defaults for the lock icon
            ResourceLocation resource = LOCK_DFLT;
            int color1 = 0xFFE4D6AE;
            int color2 = 0x5579725A;

            if (feature.isFromTemplate()) {
                color1 = 0xFFDEAE3B;
                color2 = 0xFF785820;
            }

            //Switch over to the hover if it applies
            if (mouseX >= EConfig.SETTINGS.layersXPos.get() + 4 && mouseX <= EConfig.SETTINGS.layersXPos.get() + 9 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 1 + layerY && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + layerY + 5) {
                resource = LOCK_HOVER;
                if (feature.isFromTemplate()) {
                    color1 = 0xFFA5812C;
                    color2 = 0xFF543D16;
                } else {
                    color1 = 0xFFB0A483;
                    color2 = 0xFF48453C;
                }
            }

            //Draw the lock icons
            left = EConfig.SETTINGS.layersXPos.get() + 2;
            top = EConfig.SETTINGS.toolbarYPos.get() - 3 + layerY;
            right = EConfig.SETTINGS.layersXPos.get() + 11;
            bottom = EConfig.SETTINGS.toolbarYPos.get() + 7 + layerY;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, color1);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, color2);
            guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, color2);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, color2);
            guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, color2);
            if (feature.isLocked()) {
                left = EConfig.SETTINGS.layersXPos.get() + 4;
                top = EConfig.SETTINGS.toolbarYPos.get() - 1 + layerY;
                right = EConfig.SETTINGS.layersXPos.get() + 9;
                bottom = EConfig.SETTINGS.toolbarYPos.get() + layerY + 5;
                w = right - left;
                h = bottom - top;
                guiGraphics.blit(resource, offsetX + left, offsetY + top, 0, 0, w, h, w, h);
            }
            /* END LOCK ICON */

            /* VISIBILITY ICON */
            //Reset
            resource = VISIBLE_DFLT;
            if (feature.isFromTemplate()) {
                color1 = 0xFFDEAE3B;
                color2 = 0xFF785820;
            } else {
                color1 = 0xFFE4D6AE;
                color2 = 0x5579725A;
            }

            if (mouseX > EConfig.SETTINGS.layersXPos.get() + 9 && mouseX < EConfig.SETTINGS.layersXPos.get() + 20 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 1 + layerY && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + layerY + 5) {
                resource = VISIBLE_HOVER;
                if (feature.isFromTemplate()) {
                    color1 = 0xFFA5812C;
                    color2 = 0xFF543D16;
                } else {
                    color1 = 0xFFB0A483;
                    color2 = 0xFF48453C;
                }
            }

            left = EConfig.SETTINGS.layersXPos.get() + 11;
            top = EConfig.SETTINGS.toolbarYPos.get() - 3 + layerY;
            right = EConfig.SETTINGS.layersXPos.get() + 20;
            bottom = EConfig.SETTINGS.toolbarYPos.get() + 7 + layerY;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, color1);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, color2);
            guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, color2);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, color2);
            guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, color2);
            if (feature.isVisible()) {
                left = EConfig.SETTINGS.layersXPos.get() + 12;
                top = EConfig.SETTINGS.toolbarYPos.get() - 1 + layerY;
                right = EConfig.SETTINGS.layersXPos.get() + 19;
                bottom = EConfig.SETTINGS.toolbarYPos.get() + layerY + 5;
                w = right - left;
                h = bottom - top;
                guiGraphics.blit(resource, offsetX + left, offsetY + top, 0, 0, w, h, w, h);
            }
            /* END VISIBILITY ICON */

            /* Layer itself */
            left = EConfig.SETTINGS.layersXPos.get() + 20;
            top = EConfig.SETTINGS.toolbarYPos.get() - 3 + layerY;
            right = EConfig.SETTINGS.layersXPos.get() + 83;
            bottom = EConfig.SETTINGS.toolbarYPos.get() + 7 + layerY;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, color1);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, color2);
            guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, color2);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, color2);
            guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, color2);

            if (isOverLayer(layerY, mouseX, mouseY) || guiBookParam.isGroupSelected(feature)) {
                hoverY = layerY;
                if (feature.isFromTemplate()) {
                    color1 = 0xFFA5812C;
                    color2 = 0xFF543D16;
                } else {
                    color1 = 0xFFB0A483;
                    color2 = 0xFF48453C;
                }
                left = EConfig.SETTINGS.layersXPos.get() + 20;
                top = EConfig.SETTINGS.toolbarYPos.get() - 3 + layerY;
                right = EConfig.SETTINGS.layersXPos.get() + 83;
                bottom = EConfig.SETTINGS.toolbarYPos.get() + 7 + layerY;
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, color1);
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, color2);
                guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, color2);
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, color2);
                guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, color2);
            }
            /* End Layer */

            String name = feature.getFeature().getName();
            String truncated = (name.substring(0, Math.min(name.length(), 20))).replace("\n", " ");
            left = EConfig.SETTINGS.layersXPos.get() + 25;
            top = EConfig.SETTINGS.toolbarYPos.get() + layerY;
            poseStack.pushPose();
            poseStack.translate(offsetX, offsetY, 0);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            guiGraphics.drawWordWrap(font, Component.literal(truncated), (int)(left / 0.5F), (int)(top / 0.5F), 250, 0xFF000000);
            poseStack.popPose();
        }


        //Dragging!
        if (dragged != null && hoverY != 0) {
            if (held < 20) {
                held++;
            } else {
                String name = dragged.getFeature().getName();
                String truncated = name.substring(0, Math.min(name.length(), 20));
                left = EConfig.SETTINGS.layersXPos.get() + 20;
                top = EConfig.SETTINGS.toolbarYPos.get() - 3 + hoverY;
                right = EConfig.SETTINGS.layersXPos.get() + 83;
                bottom = EConfig.SETTINGS.toolbarYPos.get() + 7 + hoverY;
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0xFFEEEEEE);
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0xFF48453C);
                guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0xFF48453C);
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0xFF48453C);
                guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0xFF48453C);
                left = EConfig.SETTINGS.layersXPos.get() + 25;
                top = EConfig.SETTINGS.toolbarYPos.get() + hoverY;
                poseStack.pushPose();
                poseStack.translate(offsetX, offsetY, 0);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                guiGraphics.drawWordWrap(font, Component.literal(truncated), (int)(left / 0.5F), (int)(top / 0.5F), 250, 0xFF000000);
                poseStack.popPose();
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, GuiBook guiBookParam) {
        int layerY = 0;
        ArrayList<FeatureProvider> features = guiBookParam.getPage().getFeatures();
        for (int i = layerPosition; i < Math.min(features.size(), layerPosition + 20); i++) {
            FeatureProvider provider = features.get(i);
            layerY += 12;
            if (!provider.isLocked() && isOverLayer(layerY, mouseX, mouseY)) {
                yStart = mouseY;
                dragged = features.get(i);
                guiBook.selectLayer(dragged);
                return true;
            }
        }

        dragged = null;
        return false;
    }

    private void insertLayerAt(int mouseY, int layerNumber, GuiBook guiBookParam) {
        int change = 0;
        int difference = mouseY - yStart;
        if (difference > 0) change = 0;
        else if (difference < 0) change = -1;

        if (dragged.getLayerIndex() != layerNumber) {
            dragged.setLayerIndex(layerNumber + change);
            //Resort
            guiBookParam.getPage().sort();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, GuiBook guiBookParam) {
        boolean placing = held >= 20;
        int layerY = 0;
        ArrayList<FeatureProvider> features = guiBookParam.getPage().getFeatures();
        for (int i = layerPosition; i < Math.min(features.size(), layerPosition + 24); i++) {
            layerY += 12;
            if (isOverLayer(layerY, mouseX, mouseY)) {
                if (placing) {
                    insertLayerAt(mouseY, features.get(i).getLayerIndex(), guiBookParam);
                } else {
                    FeatureProvider selected = guiBookParam.getSelected();
                    if (selected != null) selected.deselect();
                    guiBookParam.setSelected(features.get(i));
                    selected = guiBookParam.getSelected();
                    selected.select(mouseX, mouseY);
                    selected.select(mouseX, mouseY);
                    selected.mouseReleased(mouseX, mouseY, 0);
                }
            }

            /* LOCK */
            if (mouseX >= EConfig.SETTINGS.layersXPos.get() + 4 && mouseX <= EConfig.SETTINGS.layersXPos.get() + 9 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 1 + layerY && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + layerY + 5) {
                FeatureProvider feature = features.get(i);
                feature.setLocked(!feature.isLocked());
            }
            /* END LOCK */
            /* VISIBLE */
            if (mouseX > EConfig.SETTINGS.layersXPos.get() + 9 && mouseX < EConfig.SETTINGS.layersXPos.get() + 20 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 1 + layerY && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + layerY + 5) {
                FeatureProvider feature = features.get(i);
                feature.setVisible(!feature.isVisible());
            }
            /* END VISIBLE */
        }


        //Reset
        dragged = null;
        yStart = 0;
        held = 0;
    }

    @Override
    public void scroll(boolean down, int mouseX, int mouseY) {
        if (mouseX >= EConfig.SETTINGS.layersXPos.get()) {
            if (down) {
                this.layerPosition++;
            } else {
                this.layerPosition--;
                this.layerPosition = Math.max(layerPosition, 0);
            }
        }
    }
}