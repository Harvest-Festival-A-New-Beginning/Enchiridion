package joshie.enchiridion.api;

import joshie.enchiridion.api.edit.IEditHelper;
import joshie.enchiridion.api.library.ILibraryRegistry;

public class EnchiridionAPI {
    /** Instance of the enchiridion api **/
    public static IEnchiridionAPI instance;

    /** Reference to editing modes **/
    public static IEditHelper editor;

    /** Instance of the library registry **/
    public static ILibraryRegistry library;
}