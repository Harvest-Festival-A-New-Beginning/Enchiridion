package joshie.enchiridion.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ClientStackHelper {
    public static void drawStack(@Nonnull ItemStack stack, int x, int y, float size) {
        // TODO: This method needs GuiGraphics parameter for proper item rendering in 1.20.4
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.scale(size, size, size);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F); //Forge: Reset color in case Items change it.
        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.

        Minecraft mc = Minecraft.getInstance();
        // TODO: Item rendering changed in 1.20.4 - needs GuiGraphics
        // guiGraphics.renderItem(stack, x, y);
        // guiGraphics.renderItemDecorations(mc.font, stack, x, y, null);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}