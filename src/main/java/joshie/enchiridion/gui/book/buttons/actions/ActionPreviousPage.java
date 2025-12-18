package joshie.enchiridion.gui.book.buttons.actions;

import com.google.common.collect.Lists;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IButtonAction;
import joshie.enchiridion.api.book.IPage;

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
    public IButtonAction create(joshie.enchiridion.gui.book.GuiBook guiBook) {
        return new ActionPreviousPage();
    }

    @Override
    public boolean performAction(joshie.enchiridion.gui.book.GuiBook guiBook) {
        try {
            List<IPage> pages = guiBook.getBook().getPages();
            List<Integer> numbersTemp = pages.stream().map(IPage::getPageNumber).collect(Collectors.toList());

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