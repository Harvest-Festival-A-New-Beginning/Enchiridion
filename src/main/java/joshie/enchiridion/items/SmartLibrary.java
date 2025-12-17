package joshie.enchiridion.items;

import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventBusSubscriber;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = EInfo.MODID, value = Dist.CLIENT)
public class SmartLibrary implements BakedModel { //TODO
    private static BakedModel library;

    @Override
    @Nonnull
    public ItemOverrides getOverrides() {
        return LibraryOverride.INSTANCE;
    }

    private static class LibraryOverride extends ItemOverrides {
        private static LibraryOverride INSTANCE = new LibraryOverride();
        private ItemStack broken = new ItemStack(Items.ENCHANTED_BOOK);

        private ItemModelShaper mesher;

        public LibraryOverride() {
            super();
        }

        @Override
        @Nonnull
        public BakedModel resolve(@Nonnull BakedModel originalModel, @Nonnull ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity, int seed) {
            BakedModel ret;
            //Setup
            /*if (mesher == null) mesher = Minecraft.getInstance().getItemRenderer().getItemModelShaper(); //TODO
            library = mesher.getModelManager().getModel(EClientHandler.library);
            if (stack.getDamageValue() == 0) { //If we're a book
                ret = mesher.getModelManager().getModel(BookMeshDefinition.INSTANCE.getModelLocation(stack));
            } else {*/
                ItemStack book = LibraryHelper.getClientLibraryContents().getCurrentBookItem();
                ret = book.isEmpty() ? library : mesher.getItemModel(book);


            return ret == null ? mesher.getItemModel(broken) : ret;
        }
    }

    /**
     * Redundant crap below :/
     **/
    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
        return new ArrayList<>();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleIcon() {
        return library.getParticleIcon();
    }
}