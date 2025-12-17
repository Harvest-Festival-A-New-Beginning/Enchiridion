package joshie.enchiridion.gui.library;

import com.mojang.blaze3d.systems.RenderSystem;
import joshie.enchiridion.util.ELocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiLibrary extends AbstractContainerScreen<ContainerLibrary> {
    private static final ResourceLocation LOCATION = new ELocation("library");
    public final int imageWidth = 430;
    public final int imageHeight = 217;
    public Container library;
    public int x, y;

    public GuiLibrary(ContainerLibrary containerLibrary, Inventory playerInventory, Component name) {
        super(containerLibrary, playerInventory, name);
    }

    @Override
    protected void renderBg(net.minecraft.client.gui.GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;
        drawImage(guiGraphics, LOCATION, -10, -10, 440, 240);
    }

    //Helper
    private void drawImage(net.minecraft.client.gui.GuiGraphics guiGraphics, ResourceLocation resource, int left, int top, int right, int bottom) {
        //Fix the position in even scale factors
        if (Minecraft.getInstance().getWindow().getGuiScale() % 2 == 0) {
            top--;
            bottom--;
        }

        guiGraphics.blit(resource, x + left, y + top, 0, 0, right - left, bottom - top, right - left, bottom - top);
    }
}