package test.data.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;

import java.util.Optional;

/**
 * Renders a clickable button that can trigger scripts
 * Position and size are stored in BookWidget, not here
 */
public class ButtonElement extends RenderElement {
    public static final Codec<ButtonElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("text").forGetter(e -> Optional.ofNullable(e.text)),
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(e -> Optional.ofNullable(e.texture)),
            Codec.INT.optionalFieldOf("u", 0).forGetter(e -> e.u),
            Codec.INT.optionalFieldOf("v", 0).forGetter(e -> e.v),
            Codec.INT.optionalFieldOf("hoverU", 0).forGetter(e -> e.hoverU),
            Codec.INT.optionalFieldOf("hoverV", 0).forGetter(e -> e.hoverV),
            Codec.STRING.optionalFieldOf("script").forGetter(ButtonElement::getScript),
            Codec.STRING.optionalFieldOf("function").forGetter(ButtonElement::getFunction)
    ).apply(instance, ButtonElement::new));

    private final String text;
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int hoverU;
    private final int hoverV;
    private final String script;
    private final String function;

    public ButtonElement(Optional<String> text, Optional<ResourceLocation> texture,
                        int u, int v, int hoverU, int hoverV,
                        Optional<String> script, Optional<String> function) {
        this.text = text.orElse(null);
        this.texture = texture.orElse(null);
        this.u = u;
        this.v = v;
        this.hoverU = hoverU;
        this.hoverV = hoverV;
        this.script = script.orElse(null);
        this.function = function.orElse(null);
    }

    @Override
    public Codec<? extends RenderElement> codec() {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

        if (texture != null) {
            // Render textured button
            int renderU = hovered ? hoverU : u;
            int renderV = hovered ? hoverV : v;
            graphics.blit(texture, x, y, renderU, renderV, width, height, 256, 256);
        } else {
            // Render simple colored button
            int color = hovered ? 0xFFAAAAAA : 0xFF888888;
            graphics.fill(x, y, x + width, y + height, color);
        }

        if (text != null) {
            Component component = Component.translatable(text);
            int textWidth = Minecraft.getInstance().font.width(component);
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height - 8) / 2;
            graphics.drawString(Minecraft.getInstance().font, component, textX, textY, 0xFFFFFF);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        triggerScript();
    }

    private void triggerScript() {
        if (script != null && function != null) {
            ResourceLocation scriptId = new ResourceLocation(script);
            uk.joshiejack.penguinlib.scripting.Interpreter<?> interpreter = ScriptFactory.getScript(scriptId);
            if (interpreter != null) {
                interpreter.callFunction(function, Minecraft.getInstance().player);
            }
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeByte(getType().ordinal());
        buf.writeBoolean(text != null);
        if (text != null) buf.writeUtf(text);
        buf.writeBoolean(texture != null);
        if (texture != null) buf.writeResourceLocation(texture);
        buf.writeInt(u);
        buf.writeInt(v);
        buf.writeInt(hoverU);
        buf.writeInt(hoverV);
        buf.writeBoolean(script != null);
        if (script != null) buf.writeUtf(script);
        buf.writeBoolean(function != null);
        if (function != null) buf.writeUtf(function);
    }

    public static ButtonElement fromNetwork(FriendlyByteBuf buf) {
        Optional<String> text = buf.readBoolean() ? Optional.of(buf.readUtf()) : Optional.empty();
        Optional<ResourceLocation> texture = buf.readBoolean() ? Optional.of(buf.readResourceLocation()) : Optional.empty();
        int u = buf.readInt();
        int v = buf.readInt();
        int hoverU = buf.readInt();
        int hoverV = buf.readInt();
        Optional<String> script = buf.readBoolean() ? Optional.of(buf.readUtf()) : Optional.empty();
        Optional<String> function = buf.readBoolean() ? Optional.of(buf.readUtf()) : Optional.empty();

        return new ButtonElement(text, texture, u, v, hoverU, hoverV, script, function);
    }

    @Override
    public Type getType() {
        return Type.BUTTON;
    }

    public Optional<String> getScript() {
        return Optional.ofNullable(script);
    }

    public Optional<String> getFunction() {
        return Optional.ofNullable(function);
    }
}
