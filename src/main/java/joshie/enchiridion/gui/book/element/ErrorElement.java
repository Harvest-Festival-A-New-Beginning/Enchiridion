package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ErrorElement implements FeatureElement {
    public static final Codec<ErrorElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("error", "Error").forGetter(f -> f.error)
    ).apply(instance, ErrorElement::new));

    private final String error;

    public ErrorElement(String error) {
        this.error = error;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        graphics.fill(x, y, x + width, y + height, 0xFFFF0000);
        graphics.drawString(font, Component.literal("ERROR"), x + 2, y + 2, 0xFFFFFFFF);
        if (error != null && !error.isEmpty()) {
            graphics.drawWordWrap(font, Component.literal(error), x + 2, y + 12, width - 4, 0xFFFFFFFF);
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    @Override
    public FeatureElement copy() {
        return new ErrorElement(error);
    }
}
