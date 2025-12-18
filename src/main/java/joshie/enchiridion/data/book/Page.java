package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.features.*;

import java.util.*;

public class Page implements IPage {
    // Simple map-based codec dispatch for features - no custom registry needed
    // This is similar to how Icon.CODEC works - just a static codec, not registered
    private static final Map<String, Codec<? extends FeatureProvider>> FEATURE_CODECS = Map.ofEntries(
        Map.entry("text", FeatureText.CODEC),
        Map.entry("image", FeatureImage.CODEC),
        Map.entry("item", FeatureItem.CODEC),
        Map.entry("icon", FeatureIcon.CODEC),
        Map.entry("recipe", FeatureRecipe.CODEC),
        Map.entry("button", FeatureButton.CODEC),
        Map.entry("box", FeatureBox.CODEC),
        Map.entry("line", FeatureLine.CODEC),
        Map.entry("shape", FeatureShape.CODEC),
        Map.entry("entity", FeatureEntity.CODEC),
        Map.entry("js", FeatureJS.CODEC),
        Map.entry("fluid", FeatureFluid.CODEC),
        Map.entry("model", FeatureModel.CODEC),
        Map.entry("sound", FeatureSound.CODEC),
        Map.entry("preview_window", FeaturePreviewWindow.CODEC)
    );

    // Reverse map for encoding (codec -> type name)
    private static final Map<Codec<? extends IFeature>, String> CODEC_TO_TYPE = new HashMap<>();
    static {
        FEATURE_CODECS.forEach((type, codec) -> CODEC_TO_TYPE.put(codec, type));
    }

    // Polymorphic codec for features using simple dispatch
    // Package-private for use in Template
    @SuppressWarnings("unchecked")
    static final Codec<FeatureProvider> FEATURE_CODEC = Codec.STRING.dispatch(
        "type",
        feature -> CODEC_TO_TYPE.getOrDefault(feature.codec(), "text"),
        type -> (Codec<FeatureProvider>) FEATURE_CODECS.getOrDefault(type, FeatureText.CODEC)
    );

    public static final Codec<Page> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("page_number", 0).forGetter(p -> p.pageNumber),
        Codec.BOOL.optionalFieldOf("is_scrollable", false).forGetter(p -> p.isScrollable),
        FEATURE_CODEC.listOf().optionalFieldOf("features", new ArrayList<>()).forGetter(p ->
            new ArrayList<>(p.features))
    ).apply(instance, (pageNumber, isScrollable, features) -> {
        Page page = new Page();
        page.pageNumber = pageNumber;
        page.isScrollable = isScrollable;
        page.features = new ArrayList<>(features);
        return page;
    }));
    public List<FeatureProvider> features = new ArrayList<>();
    public int pageNumber;
    public boolean isScrollable;
    public transient int scrollAmount;
    public transient int maximumScroll;
    public transient IBook book;

    public Page() {
    }

    public Page(int number) {
        this.pageNumber = number;
    }

    @Override
    public IBook getBook() {
        return book;
    }

    @Override
    public IPage setBook(IBook book) {
        this.book = book;
        return this;
    }

    @Override
    public boolean isScrollingEnabled() {
        return isScrollable;
    }

    @Override
    public void toggleScroll() {
        this.isScrollable = !isScrollable;
    }

    private void validateScrollPosition() {
        if (this.scrollAmount < 0) {
            this.scrollAmount = 0;
        }

        if (this.scrollAmount > maximumScroll) {
            this.scrollAmount = maximumScroll;
        }

        for (FeatureProvider provider : getFeatures()) {
            provider.update(this);
        }
    }

    @Override
    public void setScrollPosition(int position) {
        if (isScrollable) {
            this.scrollAmount = position;
            validateScrollPosition();
        }
    }

    @Override
    public void scroll(boolean down, int amount) {
        if (isScrollable) {
            if (down) {
                this.scrollAmount += amount;
            } else this.scrollAmount -= amount;

            validateScrollPosition();
        }
    }

    @Override
    public int getScroll() {
        return isScrollable ? scrollAmount : 0;
    }

    @Override
    public void addFeature(IFeature feature, int x, int y, double width, double height, boolean isLocked, boolean isHidden, boolean isFromTemplate) {
        // Feature is a FeatureProvider (implements IFeature)
        // No wrapper needed - just cast and configure
        FeatureProvider provider = (FeatureProvider) feature;
        provider.setX(x);
        provider.setY(y);
        provider.setWidth((int) width);
        provider.setHeight((int) height);
        provider.setLocked(isLocked);
        provider.setVisible(!isHidden);
        provider.setFromTemplate(isFromTemplate);
        provider.update(this);
        provider.setLayerIndex(features.size());
        features.add(provider);
    }

    @Override
    public void removeFeature(FeatureProvider selected) {
        features.remove(selected);
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public ArrayList<FeatureProvider> getFeatures() {
        return new ArrayList<>(features);
    }

    @Override
    public void setPageNumber(int number) {
        pageNumber = number;
    }

    private static final SortIndex SORTER = new SortIndex();

    private static class SortIndex implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            FeatureProvider provider1 = (FeatureProvider) o1;
            FeatureProvider provider2 = (FeatureProvider) o2;
            if (provider1.getLayerIndex() == provider2.getLayerIndex()) {
                return provider1.getTimeChanged() >= provider2.getTimeChanged() ? 1 : -1;
            } else return provider1.getLayerIndex() > provider2.getLayerIndex() ? 1 : -1;
        }
    }

    @Override
    public int getScrollbarMax(int screenTop) {
        updateMaximumScroll(screenTop);
        return maximumScroll;
    }

    @Override
    public void updateMaximumScroll(int screenTop) {
        int maxY = 0;
        for (FeatureProvider provider : features) {
            if (provider.getTop() + provider.getHeight() > maxY) {
                maxY = (int) (provider.getTop() + provider.getHeight());
            }
        }

        maximumScroll = maxY - screenTop;
    }

    @Override
    public void sort() {
        Collections.sort(features, SORTER); //Sort everything out in to order

        int i = 0;
        for (FeatureProvider provider : features) { //Fix all the id numbers
            provider.setLayerIndex(i);
            i++;
        }
    }

    @Override
    public void clear() {
        features = new ArrayList<>();
    }
}