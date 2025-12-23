package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditorAbstract;
import joshie.enchiridion.gui.book.features.script.FeatureJSWrapper;
import joshie.enchiridion.gui.book.features.script.GraphicsJS;
import joshie.enchiridion.helpers.MCClientHelper;
import joshie.enchiridion.util.ITextEditable;
import joshie.enchiridion.util.TextEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import uk.joshiejack.penguinlib.scripting.Sandbox;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.scripting.ScriptLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSElement implements FeatureElement, ITextEditable {
    private static final ScriptLoader.ScriptLocation SCRIPT_LOCATION = new ScriptLoader.ScriptLocation("enchiridion/features", (rl) -> null);

    public static final Codec<JSElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("script", "").forGetter(f -> f.script),
        ResourceLocation.CODEC.optionalFieldOf("script_file").forGetter(f -> Optional.ofNullable(f.scriptFile)),
        Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.size),
        Codec.BOOL.optionalFieldOf("render_result", true).forGetter(f -> f.renderResult),
        Codec.BOOL.optionalFieldOf("use_callbacks", false).forGetter(f -> f.useCallbacks),
        Codec.STRING.optionalFieldOf("error_text", "§cScript Error").forGetter(f -> f.errorText)
    ).apply(instance, (script, scriptFile, size, renderResult, useCallbacks, errorText) -> new JSElement(script, scriptFile.orElse(null), size, renderResult, useCallbacks, errorText)));

    private String script;
    private ResourceLocation scriptFile;
    private float size;
    private boolean renderResult;
    private boolean useCallbacks;
    private String errorText;

    private transient boolean readTemp = false;
    private transient double cachedWidth = 0;
    private transient int wrap = 100;
    private transient String cachedResult;
    private transient String loadedScript;
    private transient boolean hasError;
    private transient long lastExecutionTime;
    private transient uk.joshiejack.penguinlib.scripting.Interpreter interpreter;
    private transient GraphicsJS graphicsWrapper;
    private transient FeatureJSWrapper featureWrapper;

    public JSElement(String script, ResourceLocation scriptFile, float size, boolean renderResult, boolean useCallbacks, String errorText) {
        this.script = script;
        this.scriptFile = scriptFile;
        this.size = size;
        this.renderResult = renderResult;
        this.useCallbacks = useCallbacks;
        this.errorText = errorText;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (useCallbacks && interpreter != null && graphicsWrapper != null) {
            try {
                graphicsWrapper.setGraphics(graphics, x, y, width, height);
                interpreter.callFunction("draw", graphicsWrapper, mouseX, mouseY, partialTicks, featureWrapper);
            } catch (Exception e) {
                Font font = Minecraft.getInstance().font;
                graphics.drawString(font, errorText, x, y, 0xFF0000);
            }
        } else if (script != null && !script.isEmpty() && renderResult) {
            String displayText = executeScript();
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.scale(size, size, size);
            Font font = Minecraft.getInstance().font;
            int color = hasError ? 0xFF0000 : 0x555555;
            graphics.drawWordWrap(font, Component.literal(displayText), (int)(x / size), (int)(y / size), wrap, color);
            poseStack.popPose();
        }
    }

    private String executeScript() {
        try {
            String scriptToExecute = scriptFile != null ? loadScriptFromFile() : script;
            if (hasError) return scriptToExecute;
            Context context = Sandbox.enter();
            Scriptable scope = context.initStandardObjects();
            Object result = context.evaluateString(scope, scriptToExecute, "inline", 1, null);
            hasError = false;
            return (result == null || result instanceof Undefined) ? "" : String.valueOf(result);
        } catch (Exception e) {
            hasError = true;
            return errorText + ": " + e.getMessage();
        }
    }

    private String loadScriptFromFile() {
        if (loadedScript != null) return loadedScript;
        try {
            Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(scriptFile);
            if (resourceOpt.isPresent()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceOpt.get().open(), StandardCharsets.UTF_8))) {
                    loadedScript = reader.lines().collect(Collectors.joining("\n"));
                    return loadedScript;
                }
            }
        } catch (Exception e) {}
        hasError = true;
        return errorText + ": Script file not found";
    }

    @Override
    public boolean onClick(GuiBook guiBook, int mouseX, int mouseY, int button) {
        if (useCallbacks && interpreter != null) {
            try {
                return ScriptFactory.getResult(interpreter, "onClick", false, mouseX, mouseY, button, featureWrapper);
            } catch (Exception e) {}
        }
        return false;
    }

    @Override
    public boolean onKeyPress(GuiBook guiBook, char character, int key) {
        if (MCClientHelper.isShiftPressed()) {
            if (key == 78) { size = Math.min(15F, Math.max(0.5F, size + 0.1F)); wrap = Math.max(50, (int)(cachedWidth / size) + 4); return true; }
            else if (key == 74) { size = Math.min(15F, Math.max(0.5F, size - 0.1F)); wrap = Math.max(50, (int)(cachedWidth / size) + 4); return true; }
        }
        return false;
    }

    @Override
    public GuiSimpleEditorAbstract getEditor(GuiBook guiBook) {
        TextEditor.INSTANCE.setEditable(this);
        return null; // Text editor mode
    }

    @Override
    public void onDeselected() {
        TextEditor.INSTANCE.setEditable(null);
    }

    @Override
    public String getTextField() { return script; }

    @Override
    public void setTextField(String text) {
        this.script = text;
        cachedResult = null;
        interpreter = null;
    }

    @Override
    public FeatureElement copy() {
        return new JSElement(script, scriptFile, size, renderResult, useCallbacks, errorText);
    }

    @Override
    public String getName() {
        return scriptFile != null ? "JavaScript: " + scriptFile.getPath() : "JavaScript";
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
