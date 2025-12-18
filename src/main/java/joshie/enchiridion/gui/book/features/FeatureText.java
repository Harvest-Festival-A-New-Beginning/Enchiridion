package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.helpers.MCClientHelper;
import joshie.enchiridion.util.ITextEditable;
import joshie.enchiridion.util.TextEditor;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.compress.utils.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FeatureText extends FeatureProvider implements ITextEditable {
    public static final Codec<FeatureText> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("text", "").forGetter(f -> f.text),
        Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.size)
    ).apply(instance, (text, size) -> {
        FeatureText feature = new FeatureText(text);
        feature.size = size;
        return feature;
    }));

    protected transient boolean readTemp = false;
    protected transient double cachedWidth = 0;
    public transient int wrap = 100; //Default wrap to 100 to avoid errors
    public String text = "";
    public float size = 1F;

    public FeatureText() {
        super(0, 0, 0, 0);
    }

    public FeatureText(String text) {
        super(0, 0, 0, 0);
        this.text = text;
    }

    @Override
    public FeatureProvider copy() {
        FeatureText text = new FeatureText(this.text);
        text.size = size;
        return text;
    }

    @Override
    public String getName() {
        return text;
    }

    @Override
    public void update(IPage page) {
        super.update(page);
        cachedWidth = getWidth();
        wrap = Math.max(50, (int) (cachedWidth / size) + 4);
    }

    @Override
    protected void drawFeature(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (text != null) {
            String displayText = TextEditor.INSTANCE.getText(this);
            com.mojang.blaze3d.vertex.PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.scale(size, size, size);
            net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
            int x = (int)(getLeft() / size);
            int y = (int)(getTop() / size);
            // TODO: Handle text formatting codes (e.g., [b] for bold) - might need custom formatting parser
            graphics.drawWordWrap(font, net.minecraft.network.chat.Component.literal(displayText), x, y, wrap, 0x555555);
            poseStack.popPose();
        }
    }

    @Override
    public boolean getAndSetEditMode() {
        if (MCClientHelper.isShiftPressed()) {
            try {
                readTemp = true;

                File file = new File("enchiridion.temp.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }

                IOUtils.readFully(new FileInputStream("enchiridion.temp.txt"), text.getBytes());
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        } else {
            TextEditor.INSTANCE.setEditable(this);
            return true;
        }
    }

    @Override
    public void onDeselected() {
        if (readTemp) {
            byte[] encoded;
            try {
                encoded = IOUtils.toByteArray(new FileInputStream("enchiridion.temp.txt"));
                text = (new String(encoded, Charset.defaultCharset()));
                //File file = new File("enchiridion.temp.txt");
                //file.delete();
                readTemp = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TextEditor.INSTANCE.setEditable(null);
    }

    @Override
    public boolean keyTyped(char character, int key) {
        if (MCClientHelper.isShiftPressed()) {
            if (key == 78) {
                size = Math.min(15F, Math.max(0.5F, size + 0.1F));
                wrap = Math.max(50, (int) ((cachedWidth) / size) + 4);
                return true;
            } else if (key == 74) {
                size = Math.min(15F, Math.max(0.5F, size - 0.1F));
                wrap = Math.max(50, (int) ((cachedWidth) / size) + 4);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTextField() {
        return text;
    }

    @Override
    public void setTextField(String text) {
        this.text = text;
    }

    @Override
    public Codec<? extends joshie.enchiridion.api.book.IFeature> codec() {
        return CODEC;
    }
}