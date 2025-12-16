package joshie.enchiridion.library;

import com.google.gson.JsonObject;
import joshie.enchiridion.EConfig;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nonnull;

public class LibraryConditionFactory implements ICondition {

    @Override
    public boolean test(@Nonnull IContext context) {
        return EConfig.SETTINGS.addWrittenBookRecipeForLibrary.get();
    }
}