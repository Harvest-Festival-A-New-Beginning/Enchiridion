package joshie.enchiridion.gui.book.buttons;

import com.google.common.collect.Lists;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;

import java.util.List;
import java.util.stream.Collectors;

public class ButtonDeletePage extends ButtonAbstract {
    public ButtonDeletePage() {
        super("delete");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        IPage currentPage = guiBook.getPage();
        int numberOfPages = guiBook.getBook().getPages().size();
        int pageNumber;
        if (numberOfPages > 1) {
            pageNumber = getPreviousPage(guiBook);
            guiBook.jumpToPageIfExists(pageNumber); //Jump to the previous page
            //Delete the older page
            guiBook.getBook().removePage(currentPage);
        } else {
            guiBook.getPage().clear();
        }
    }

    public int getPreviousPage(GuiBook guiBook) {
        List<IPage> pages = guiBook.getBook().getPages();
        List<Integer> numbersTemp = pages.stream().map(IPage::getPageNumber).sorted(Integer::compareTo).collect(Collectors.toList());

        List<Integer> numbers = Lists.reverse(numbersTemp);
        int number = guiBook.getPage().getPageNumber();
        for (Integer integer : numbers) {
            if (integer < number) {
                return integer;
            }
        }

        return numbers.get(0);
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }
}