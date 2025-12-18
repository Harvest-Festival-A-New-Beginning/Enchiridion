package joshie.enchiridion.gui.book.features.script;

import uk.joshiejack.penguinlib.scripting.ScriptFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages JavaScript callback execution for feature scripts
 */
public class ScriptCallbackManager {
    private final String script;
    private ScriptEngine engine;
    private boolean initialized = false;
    private boolean hasError = false;
    private String errorMessage = "";
    private final Map<String, Boolean> methodCache = new HashMap<>();

    public ScriptCallbackManager(String script) {
        this.script = script;
    }

    /**
     * Initialize the script engine and evaluate the script
     */
    public boolean initialize() {
        if (initialized) return !hasError;
        initialized = true;

        try {
            // Get a script engine from ScriptFactory
            engine = ScriptFactory.getEngine();
            if (engine == null) {
                hasError = true;
                errorMessage = "Failed to create script engine";
                return false;
            }

            // Evaluate the script to define functions/objects
            engine.eval(script);
            hasError = false;
            return true;

        } catch (ScriptException e) {
            hasError = true;
            errorMessage = "Script error: " + e.getMessage();
            return false;
        } catch (Exception e) {
            hasError = true;
            errorMessage = "Initialization error: " + e.getMessage();
            return false;
        }
    }

    /**
     * Call a JavaScript function if it exists
     */
    public Object call(String functionName, Object... args) {
        if (!initialize()) return null;
        if (hasError) return null;

        try {
            // Check if we've already determined this method doesn't exist
            if (methodCache.containsKey(functionName) && !methodCache.get(functionName)) {
                return null;
            }

            if (engine instanceof Invocable invocable) {
                try {
                    Object result = invocable.invokeFunction(functionName, args);
                    methodCache.put(functionName, true);
                    return result;
                } catch (NoSuchMethodException e) {
                    // Method doesn't exist - cache this fact and return null
                    methodCache.put(functionName, false);
                    return null;
                }
            }
        } catch (ScriptException e) {
            hasError = true;
            errorMessage = "Error calling " + functionName + ": " + e.getMessage();
        } catch (Exception e) {
            hasError = true;
            errorMessage = "Error: " + e.getMessage();
        }

        return null;
    }

    /**
     * Set a global variable in the script engine
     */
    public void setGlobal(String name, Object value) {
        if (!initialize()) return;
        if (hasError) return;

        try {
            engine.put(name, value);
        } catch (Exception e) {
            hasError = true;
            errorMessage = "Error setting global " + name + ": " + e.getMessage();
        }
    }

    /**
     * Get a global variable from the script engine
     */
    public Object getGlobal(String name) {
        if (!initialize()) return null;
        if (hasError) return null;

        try {
            return engine.get(name);
        } catch (Exception e) {
            hasError = true;
            errorMessage = "Error getting global " + name + ": " + e.getMessage();
            return null;
        }
    }

    /**
     * Check if the script has an error
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * Get the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Reset the script engine (for script changes)
     */
    public void reset() {
        engine = null;
        initialized = false;
        hasError = false;
        errorMessage = "";
        methodCache.clear();
    }
}
