package joshie.enchiridion.library;

import com.google.gson.JsonObject;
import joshie.enchiridion.EConfig;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nonnull;

// TODO: ICondition interface changed in 1.20.4 - codec() return type is wrong
// This class needs to be rewritten or removed
/*
public class LibraryConditionFactory implements ICondition {

    @Override
    public boolean test(@Nonnull IContext context) {
        return EConfig.SETTINGS.addWrittenBookRecipeForLibrary.get();
    }

    @Override
    public com.mojang.serialization.Codec<? extends net.neoforged.neoforge.common.conditions.ICondition> codec() {
        return null; // TODO: Implement proper codec
    }

}
*/

public class LibraryConditionFactory {
    public static boolean test() {
        return EConfig.SETTINGS.addWrittenBookRecipeForLibrary.get();
    }
}