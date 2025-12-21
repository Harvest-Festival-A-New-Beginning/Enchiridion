package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.features.script.FeatureJSWrapper;
import joshie.enchiridion.gui.book.features.script.GraphicsJS;
import joshie.enchiridion.util.ITextEditable;
import joshie.enchiridion.util.TextEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import uk.joshiejack.penguinlib.scripting.Sandbox;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.scripting.ScriptLoader;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeatureJS extends FeatureProvider implements ITextEditable {
    private static final ScriptLoader.ScriptLocation SCRIPT_LOCATION = new ScriptLoader.ScriptLocation(
        "enchiridion/features",
        (rl) -> null  // FeatureJS creates interpreters directly, not via registry
    );

    public static final Codec<FeatureJS> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("script", "").forGetter(f -> f.script),
        ResourceLocation.CODEC.optionalFieldOf("script_file").forGetter(f -> Optional.ofNullable(f.scriptFile)),
        Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.size),
        Codec.BOOL.optionalFieldOf("render_result", true).forGetter(f -> f.renderResult),
        Codec.BOOL.optionalFieldOf("use_callbacks", false).forGetter(f -> f.useCallbacks),
        Codec.STRING.optionalFieldOf("error_text", "§cScript Error").forGetter(f -> f.errorText)
    ).apply(instance, (script, scriptFile, size, renderResult, useCallbacks, errorText) -> {
        FeatureJS feature = new FeatureJS(script);
        feature.scriptFile = scriptFile.orElse(null);
        feature.size = size;
        feature.renderResult = renderResult;
        feature.useCallbacks = useCallbacks;
        feature.errorText = errorText;
        return feature;
    }));

    protected transient boolean readTemp = false;
    protected transient double cachedWidth = 0;
    public transient int wrap = 100;
    public String script = "";
    public ResourceLocation scriptFile = null; // Optional: load script from resource file
    public float size = 1F;
    public boolean renderResult = true; // If true, renders the result of the script
    public boolean useCallbacks = false; // If true, use callback mode (draw, update, onClick)
    public String errorText = "§cScript Error";

    private transient String cachedResult = null;
    private transient String loadedScript = null;
    private transient boolean hasError = false;
    private transient long lastExecutionTime = 0;
    private static final long CACHE_DURATION = 1000; // Cache for 1 second

    // Callback mode fields
    private transient Interpreter interpreter = null;
    private transient ResourceLocation scriptId = null;
    private transient GraphicsJS graphicsWrapper = null;
    private transient FeatureJSWrapper featureWrapper = null;

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
        js.scriptFile = scriptFile;
        js.size = size;
        js.renderResult = renderResult;
        js.useCallbacks = useCallbacks;
        js.errorText = errorText;
        return js;
    }

    @Override
    public String getName() {
        if (scriptFile != null) {
            return "JavaScript: " + scriptFile.getPath();
        }
        return "JavaScript: " + (script.length() > 20 ? script.substring(0, 20) + "..." : script);
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        cachedWidth = getWidth();
        wrap = Math.max(50, (int) (cachedWidth / size) + 4);
        // Clear cache when page updates
        cachedResult = null;
        loadedScript = null;
        lastExecutionTime = 0;

        // Initialize interpreter in callback mode
        if (useCallbacks) {
            String scriptToLoad = loadScriptFromFile();
            if (!hasError) {
                try {
                    // Generate a unique ID for this script instance
                    scriptId = scriptFile != null ? scriptFile :
                        new ResourceLocation("enchiridion", "inline_" + System.identityHashCode(this));

                    // Create interpreter with the script
                    interpreter = new Interpreter(scriptId, this, scriptToLoad);

                    // Create reusable wrappers
                    graphicsWrapper = new GraphicsJS();
                    featureWrapper = new FeatureJSWrapper(this, page);

                    // Call the update callback if it exists
                    interpreter.callFunction("update", featureWrapper);
                } catch (Exception e) {
                    hasError = true;
                    e.printStackTrace();
                }
            }
        }
    }

    private String loadScriptFromFile() {
        if (scriptFile == null) return script;
        if (loadedScript != null) return loadedScript;

        try {
            Minecraft mc = Minecraft.getInstance();
            Optional<Resource> resourceOpt = mc.getResourceManager().getResource(scriptFile);

            if (resourceOpt.isPresent()) {
                Resource resource = resourceOpt.get();
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                    loadedScript = reader.lines().collect(Collectors.joining("\n"));
                    return loadedScript;
                }
            } else {
                hasError = true;
                return errorText + ": Script file not found: " + scriptFile;
            }
        } catch (Exception e) {
            hasError = true;
            return errorText + ": Failed to load script: " + e.getMessage();
        }
    }

    private String executeScript() {
        // Check cache first
        long currentTime = System.currentTimeMillis();
        if (cachedResult != null && (currentTime - lastExecutionTime) < CACHE_DURATION) {
            return cachedResult;
        }

        try {
            // Load script (either from file or inline)
            String scriptToExecute = loadScriptFromFile();

            if (hasError) {
                // Error occurred during loading
                cachedResult = scriptToExecute;
                lastExecutionTime = currentTime;
                return cachedResult;
            }

            // Evaluate script using Rhino Context directly
            Context context = Sandbox.enter();
            Scriptable scope = context.initStandardObjects();
            Object result = context.evaluateString(scope, scriptToExecute, "inline", 1, null);

            // Convert result to string
            if (result == null || result instanceof Undefined) {
                cachedResult = "";
            } else {
                cachedResult = String.valueOf(result);
            }

            hasError = false;
            lastExecutionTime = currentTime;
            return cachedResult;

        } catch (Exception e) {
            hasError = true;
            cachedResult = errorText + ": " + e.getMessage();
            lastExecutionTime = currentTime;
            return cachedResult;
        }
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Callback mode - call JavaScript draw() function
        if (useCallbacks && interpreter != null && graphicsWrapper != null) {
            try {
                // Update the graphics wrapper with current frame data
                graphicsWrapper.setGraphics(guiGraphics, getRenderX(), getRenderY(), getWidth(), getHeight());

                // Call the draw function with the reusable wrappers
                interpreter.callFunction("draw", graphicsWrapper, mouseX, mouseY, partialTicks, featureWrapper);

            } catch (Exception e) {
                // Display error
                Font font = Minecraft.getInstance().font;
                guiGraphics.drawString(font, errorText, getRenderX(), getRenderY(), 0xFF0000);
                guiGraphics.drawWordWrap(font, Component.literal("Error: " + e.getMessage()),
                    getRenderX(), getRenderY() + 10, getWidth(), 0xFF0000);
            }
            return;
        }

        // Simple mode - render script result as text
        if (script != null && !script.isEmpty() && renderResult) {
            String displayText = executeScript();

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.scale(size, size, size);

            Font font = Minecraft.getInstance().font;
            int x = (int)(getRenderX() / size);
            int y = (int)(getRenderY() / size);

            // Use red color for errors, normal color otherwise
            int color = hasError ? 0xFF0000 : 0x555555;

            guiGraphics.drawWordWrap(font, Component.literal(displayText), x, y, wrap, color);
            poseStack.popPose();
        }
    }

    @Override
    public boolean performClick(int mouseX, int mouseY, int button, Object gui) {
        // Callback mode - call JavaScript onClick() function
        if (useCallbacks && interpreter != null && featureWrapper != null && isOverFeature(mouseX, mouseY)) {
            try {
                return ScriptFactory.getResult(interpreter, "onClick", false,
                    mouseX, mouseY, button, featureWrapper);
            } catch (Exception e) {
                // Ignore errors
            }
        }
        return super.performClick(mouseX, mouseY, button, gui);
    }

    @Override
    public boolean getAndSetEditMode(Object gui) {
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
                // Reset interpreter and wrappers
                interpreter = null;
                graphicsWrapper = null;
                featureWrapper = null;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        TextEditor.INSTANCE.setEditable(null);
    }

    @Override
    public boolean keyTyped(char character, int key, Object gui) {
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
        // Reset interpreter and wrappers
        interpreter = null;
        graphicsWrapper = null;
        featureWrapper = null;
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }

    // Nested Interpreter class following Quest.java pattern
    public static class Interpreter extends uk.joshiejack.penguinlib.scripting.Interpreter<FeatureJS> {
        private final FeatureJS feature;

        public Interpreter(@Nonnull ResourceLocation id, @Nonnull FeatureJS feature, @Nonnull String javascript) {
            super(id, javascript, SCRIPT_LOCATION, feature);
            this.feature = feature;
        }

        @Override
        protected void addGlobals(FeatureJS data) {
            super.addGlobals(data);

            // Add the feature wrapper to the JavaScript scope as "feature"
            // Use the static context field from parent Interpreter class
            context.addToScope(localScope, "feature",
                Context.javaToJS(context, new FeatureJSWrapper(data, null), localScope));
        }
    }
}
