package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.util.ITextEditable;
import joshie.enchiridion.util.TextEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;

import javax.script.ScriptException;

public class FeatureJS extends FeatureProvider implements ITextEditable {
    public static final Codec<FeatureJS> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("script", "").forGetter(f -> f.script),
        Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.size),
        Codec.BOOL.optionalFieldOf("render_result", true).forGetter(f -> f.renderResult),
        Codec.STRING.optionalFieldOf("error_text", "§cScript Error").forGetter(f -> f.errorText)
    ).apply(instance, (script, size, renderResult, errorText) -> {
        FeatureJS feature = new FeatureJS(script);
        feature.size = size;
        feature.renderResult = renderResult;
        feature.errorText = errorText;
        return feature;
    }));

    protected transient boolean readTemp = false;
    protected transient double cachedWidth = 0;
    public transient int wrap = 100;
    public String script = "";
    public float size = 1F;
    public boolean renderResult = true; // If true, renders the result of the script
    public String errorText = "§cScript Error";

    private transient String cachedResult = null;
    private transient boolean hasError = false;
    private transient long lastExecutionTime = 0;
    private static final long CACHE_DURATION = 1000; // Cache for 1 second

    public FeatureJS() {
        super(0, 0, 0, 0);
    }

    public FeatureJS(String script) {
        super(0, 0, 0, 0);
        this.script = script;
    }

    @Override
    public FeatureProvider copy() {
        FeatureJS js = new FeatureJS(this.script);
        js.size = size;
        js.renderResult = renderResult;
        js.errorText = errorText;
        return js;
    }

    @Override
    public String getName() {
        return "JavaScript: " + (script.length() > 20 ? script.substring(0, 20) + "..." : script);
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        cachedWidth = getWidth();
        wrap = Math.max(50, (int) (cachedWidth / size) + 4);
        // Clear cache when page updates
        cachedResult = null;
        lastExecutionTime = 0;
    }

    private String executeScript() {
        // Check cache first
        long currentTime = System.currentTimeMillis();
        if (cachedResult != null && (currentTime - lastExecutionTime) < CACHE_DURATION) {
            return cachedResult;
        }

        try {
            // Get the ScriptFactory from Penguin-Lib
            Object result = ScriptFactory.eval(script);

            // Convert result to string
            if (result == null) {
                cachedResult = "";
            } else {
                cachedResult = String.valueOf(result);
            }

            hasError = false;
            lastExecutionTime = currentTime;
            return cachedResult;

        } catch (ScriptException e) {
            hasError = true;
            cachedResult = errorText + ": " + e.getMessage();
            lastExecutionTime = currentTime;
            return cachedResult;
        } catch (Exception e) {
            hasError = true;
            cachedResult = errorText;
            lastExecutionTime = currentTime;
            return cachedResult;
        }
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (script != null && !script.isEmpty() && renderResult) {
            String displayText = executeScript();

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.scale(size, size, size);

            Font font = Minecraft.getInstance().font;
            int x = (int)(getLeft() / size);
            int y = (int)(getTop() / size);

            // Use red color for errors, normal color otherwise
            int color = hasError ? 0xFF0000 : 0x555555;

            guiGraphics.drawWordWrap(font, Component.literal(displayText), x, y, wrap, color);
            poseStack.popPose();
        }
    }

    @Override
    public boolean getAndSetEditMode() {
        // Use text editor for editing JavaScript code
        TextEditor.INSTANCE.setEditable(this);
        return true;
    }

    @Override
    public void onDeselected() {
        if (readTemp) {
            byte[] encoded;
            try {
                encoded = org.apache.commons.compress.utils.IOUtils.toByteArray(
                    new java.io.FileInputStream("enchiridion.temp.txt")
                );
                script = new String(encoded, java.nio.charset.Charset.defaultCharset());
                readTemp = false;
                // Clear cache when script changes
                cachedResult = null;
                lastExecutionTime = 0;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        TextEditor.INSTANCE.setEditable(null);
    }

    @Override
    public boolean keyTyped(char character, int key) {
        if (joshie.enchiridion.helpers.MCClientHelper.isShiftPressed()) {
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
        return script;
    }

    @Override
    public void setTextField(String text) {
        this.script = text;
        // Clear cache when script changes
        cachedResult = null;
        lastExecutionTime = 0;
    }

    @Override
    public Codec<? extends IFeature> codec() {
        return CODEC;
    }
}
