package joshie.enchiridion.gui.book.buttons.actions;

import com.google.common.collect.Lists;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.gui.book.buttons.IButtonAction;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.data.book.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActionPreviousPage extends AbstractAction {
    public ActionPreviousPage() {
        super("previous");
    }

    @Override
    public IButtonAction copy() {
        return copyAbstract(new ActionPreviousPage());
    }

    @Override
    public IButtonAction create(GuiBook guiBook) {
        return new ActionPreviousPage();
    }

    @Override
    public boolean performAction(GuiBook guiBook) {
        try {
            List<Page> pages = guiBook.getBook().getPages();
            List<Integer> numbersTemp = pages.stream().map(Page::getPageNumber).collect(Collectors.toList());

            Collections.sort(numbersTemp, new SortNumerical());
            List<Integer> numbers = Lists.reverse(numbersTemp);

            int number = guiBook.getPage().getPageNumber();
            for (Integer integer : numbers) {
                if (integer < number) {
                    return guiBook.jumpToPageIfExists(integer);
                }
            }

            return guiBook.jumpToPageIfExists(numbers.get(0));
        } catch (Exception ignored) {
        }
        return false;
    }
}