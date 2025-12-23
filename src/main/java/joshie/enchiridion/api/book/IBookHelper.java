package joshie.enchiridion.api.book;

import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Page;

public interface IBookHelper {
    //GETTERS
    /** Whether or not this book is in edit mode **/
    boolean isEditMode();

    /** Returns the current book being displayed **/
    IBook getBook();

    /** Returns the current page being displayed **/
    Page getPage();

    /** Returns the current feature that is selected **/
    FeatureProvider getSelected();

    /** Whether this feature is selected in a group **/
    boolean isGroupSelected(FeatureProvider provider);

    //SETTERS
    /** Set the current book **/
    IBookHelper setBook(IBook book, boolean isEditing);

    /** Set the currently selected feature **/
    void setSelected(FeatureProvider provider);

    /** Jumps to the page number if it exists, returns true if we jumped **/
    boolean jumpToPageIfExists(int number);

    /** Creates the page if it doesn't exist
     *  @param number the page number
     *  @return returns null if the page existed,
     *  returns the page if it was created*/
    Page getPageIfNotExists(int number);
}