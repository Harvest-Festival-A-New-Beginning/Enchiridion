package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Empty element for features that don't render (Jump, Sound, etc.)
 */
public class EmptyElement implements FeatureElement {
    public static final Codec<EmptyElement> CODEC = Codec.unit(EmptyElement::new);
    public static final EmptyElement INSTANCE = new EmptyElement();

    private EmptyElement() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        // No rendering
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
