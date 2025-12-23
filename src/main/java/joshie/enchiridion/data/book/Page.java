package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IBook;

import java.util.*;

public class Page {
    public static final Codec<Page> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("page_number", 0).forGetter(p -> p.pageNumber),
        Codec.BOOL.optionalFieldOf("is_scrollable", false).forGetter(p -> p.isScrollable),
            FeatureProvider.FEATURE.listOf().optionalFieldOf("features", new ArrayList<>()).forGetter(p -> new ArrayList<>(p.features))
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

    public IBook getBook() {
        return book;
    }

    public Page setBook(IBook book) {
        this.book = book;
        return this;
    }

    public boolean isScrollingEnabled() {
        return isScrollable;
    }

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

    public void setScrollPosition(int position) {
        if (isScrollable) {
            this.scrollAmount = position;
            validateScrollPosition();
        }
    }

    public void scroll(boolean down, int amount) {
        if (isScrollable) {
            if (down) {
                this.scrollAmount += amount;
            } else this.scrollAmount -= amount;

            validateScrollPosition();
        }
    }

    public int getScroll() {
        return isScrollable ? scrollAmount : 0;
    }

    public void addFeature(FeatureProvider feature, int x, int y, double width, double height, boolean isLocked, boolean isHidden, boolean isFromTemplate) {
        // Feature is a FeatureProvider
        // No wrapper needed - just configure
        FeatureProvider provider = feature;
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

    public void removeFeature(FeatureProvider selected) {
        features.remove(selected);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public ArrayList<FeatureProvider> getFeatures() {
        return new ArrayList<>(features);
    }

    public void setPageNumber(int number) {
        pageNumber = number;
    }

    private static final SortIndex SORTER = new SortIndex();

    private static class SortIndex implements Comparator {
        public int compare(Object o1, Object o2) {
            FeatureProvider provider1 = (FeatureProvider) o1;
            FeatureProvider provider2 = (FeatureProvider) o2;
            if (provider1.getLayerIndex() == provider2.getLayerIndex()) {
                return provider1.getTimeChanged() >= provider2.getTimeChanged() ? 1 : -1;
            } else return provider1.getLayerIndex() > provider2.getLayerIndex() ? 1 : -1;
        }
    }

    public int getScrollbarMax(int screenTop) {
        updateMaximumScroll(screenTop);
        return maximumScroll;
    }

    public void updateMaximumScroll(int screenTop) {
        int maxY = 0;
        for (FeatureProvider provider : features) {
            if (provider.getTop() + provider.getHeight() > maxY) {
                maxY = (int) (provider.getTop() + provider.getHeight());
            }
        }

        maximumScroll = maxY - screenTop;
    }

    public void sort() {
        Collections.sort(features, SORTER); //Sort everything out in to order

        int i = 0;
        for (FeatureProvider provider : features) { //Fix all the id numbers
            provider.setLayerIndex(i);
            i++;
        }
    }

    public void clear() {
        features = new ArrayList<>();
    }
}