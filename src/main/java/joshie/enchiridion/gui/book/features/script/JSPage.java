package joshie.enchiridion.gui.book.features.script;

import joshie.enchiridion.api.book.IPage;

/**
 * JavaScript wrapper for IPage to provide safe page information access
 */
public class JSPage {
    private final IPage page;

    public JSPage(IPage page) {
        this.page = page;
    }

    /**
     * Get the page number (0-indexed)
     */
    public int getPageNumber() {
        return page != null ? page.getPageNumber() : 0;
    }

    /**
     * Get the total number of pages in the book
     */
    public int getTotalPages() {
        return page != null && page.getBook() != null ? page.getBook().getPageCount() : 0;
    }

    /**
     * Check if this is the first page
     */
    public boolean isFirstPage() {
        return getPageNumber() == 0;
    }

    /**
     * Check if this is the last page
     */
    public boolean isLastPage() {
        return getPageNumber() >= getTotalPages() - 1;
    }

    /**
     * Get the raw IPage object (for advanced usage)
     */
    public IPage getRaw() {
        return page;
    }
}
