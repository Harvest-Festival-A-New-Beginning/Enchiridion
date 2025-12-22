package joshie.enchiridion.helpers;

import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.Page;

public class JumpHelper {
    public static Page getPageByNumber(IBook book, int number) {
        for (Page page : book.getPages()) {
            if (page.getPageNumber() == number) {
                return page;
            }
        }

        return null;
    }

    public static void insertPage(IBook book, int pageNumber, Page dragged) {
        if (dragged.getPageNumber() != pageNumber) {
            if (getPageByNumber(book, pageNumber) != null) {
                dragged.setPageNumber(pageNumber);
                for (Page page : book.getPages()) {
                    if (page == dragged) continue;
                    else {
                        //Increase any page numbers to come after the new insertion to their new value
                        int original = page.getPageNumber();
                        if (original >= pageNumber) {
                            page.setPageNumber(original + 1);
                        }
                    }
                }
            } else dragged.setPageNumber(pageNumber);
        }
    }
}