package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.helpers.DefaultHelper;
import joshie.enchiridion.helpers.MCClientHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Book implements ReloadableRegistry.PenguinRegistry<Book>, IBook {
    public static final Codec<Book> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(Book::id),
        Codec.STRING.optionalFieldOf("displayName", "").forGetter(book -> book.displayName),
        Codec.STRING.optionalFieldOf("displayInfo", "").forGetter(book -> book.displayInfo),
        Codec.STRING.optionalFieldOf("colorHex", "FFFFFFFF").forGetter(book -> book.colorHex),
        Codec.STRING.optionalFieldOf("language", "en_us").forGetter(book -> book.language),
        Codec.BOOL.optionalFieldOf("hasCustomIcon", true).forGetter(book -> book.hasCustomIcon),
        Codec.BOOL.optionalFieldOf("showBackground", true).forGetter(book -> book.showBackground),
        Codec.BOOL.optionalFieldOf("legacyTexture", false).forGetter(book -> book.legacyTexture),
        Codec.STRING.optionalFieldOf("backgroundResource", "enchiridion:textures/books/rustic2.png").forGetter(book -> book.backgroundResource),
        Codec.INT.optionalFieldOf("defaultPage", 0).forGetter(book -> book.defaultPage),
        Codec.BOOL.optionalFieldOf("isLocked", false).forGetter(book -> book.isLocked),
        Codec.BOOL.optionalFieldOf("forgetPageOnClose", false).forGetter(book -> book.forgetPageOnClose),
        Page.CODEC.listOf().optionalFieldOf("pages", new ArrayList<>()).forGetter(book -> book.book != null ? (List<Page>) (List<?>) book.book : new ArrayList<>()),
        Codec.STRING.listOf().optionalFieldOf("defaultIDs", new ArrayList<>()).forGetter(book -> book.defaultIDs != null ? book.defaultIDs : new ArrayList<>())
    ).apply(instance, (bookId, displayName, displayInfo, colorHex, language, hasCustomIcon,
                       showBackground, legacyTexture, backgroundResource, defaultPage, isLocked, forgetPageOnClose, pages, defaultIDs) -> {
        Book book = new Book();
        book.bookId = bookId;
        book.displayName = displayName;
        book.displayInfo = displayInfo;
        book.colorHex = colorHex;
        book.language = language;
        book.hasCustomIcon = hasCustomIcon;
        book.showBackground = showBackground;
        book.legacyTexture = legacyTexture;
        book.backgroundResource = backgroundResource;
        book.defaultPage = defaultPage;
        book.isLocked = isLocked;
        book.forgetPageOnClose = forgetPageOnClose;
        book.book = new ArrayList<>(pages);
        book.defaultIDs = new ArrayList<>(defaultIDs);
        return book;
    }));
    /**
     * VARIABLES
     **/
    //Internal Information - bookId is the primary identifier (replaces modid, uniqueName, saveName)
    private ResourceLocation bookId;

    //Display Information
    private String displayName;
    private String displayInfo;
    private String colorHex;
    private String language;
    private boolean hasCustomIcon;

    //Background, with default texture
    private boolean showBackground; //Whether to show any background at all
    private boolean legacyTexture; //Use the legacy texture for the books instead
    private String backgroundResource = "enchiridion:textures/books/rustic2.png";
    private final int BACKGROUND_START_X = -10;
    private final int BACKGROUND_START_Y = -10;
    private final int BACKGROUND_END_X = 440;
    private final int BACKGROUND_END_Y = 240;

    //Extra Information
    private int defaultPage;
    private boolean isLocked;
    private boolean forgetPageOnClose;

    //Book itself
    private List<IPage> book;
    //Default Features UniqueIDs
    private List<String> defaultIDs;

    //Legacy information
    private transient int color;
    private transient boolean mc189book;
    private transient boolean showArrows;
    private transient String iconPass1;

    //Cached information
    private transient ResourceLocation resourceLocation;
    private transient boolean convertedColor;
    private transient List<Component> information;

    /**
     * CONSTRUCTOR
     **/
    public Book() {
    }

    public Book(ResourceLocation bookId, String display) {
        this.bookId = bookId;
        this.displayName = display;
        this.colorHex = "FFFFFFFF";
        this.language = MCClientHelper.getLang();
        this.hasCustomIcon = true;
        this.showBackground = true;
        this.book = new ArrayList<>();
        this.book.add(DefaultHelper.addDefaults(this, new Page(0).setBook(this)));
        this.defaultIDs = new ArrayList<>();
        this.defaultIDs.add("enchiridion_default_buttons");
    }

    /**
     * PENGUINREGISTRY IMPLEMENTATION
     **/
    @Override
    public ResourceLocation id() {
        return bookId != null ? bookId : new ResourceLocation("enchiridion", "default");
    }

    @Override
    public Book fromNetwork(FriendlyByteBuf buf) {
        // For now, use a simple implementation - can be expanded later if needed
        // This would typically deserialize the book data from the network buffer
        // TODO: Implement proper network serialization when needed
        return this;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        // For now, use a simple implementation - can be expanded later if needed
        // This would typically serialize the book data to the network buffer
        // TODO: Implement proper network serialization when needed
    }

    /**
     * METHODS
     **/
    @Override
    public String getModID() {
        return id().getNamespace();
    }

    @Override
    public String getUniqueName() {
        return id().getPath();
    }

    @Override
    public String getSaveName() {
        return id().getPath();
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getColorAsInt() {
        if (!convertedColor && colorHex != null && !colorHex.equals("")) {
            try {
                convertedColor = true;
                color = (int) Long.parseLong(colorHex, 16);
            } catch (Exception ignored) {
            }
        }
        return color;
    }

    @Override
    public String getLanguageKey() {
        return language;
    }

    @Override
    public boolean isBackgroundVisible() {
        return showBackground;
    }

    @Override
    public boolean isBackgroundLegacy() {
        return legacyTexture;
    }

    @Override
    public ResourceLocation getBackgroundResource() {
        if (resourceLocation == null) {
            resourceLocation = new ResourceLocation(backgroundResource);
        }
        return resourceLocation;
    }

    @Override
    public int getBackgroundStartX() {
        return BACKGROUND_START_X;
    }

    @Override
    public int getBackgroundStartY() {
        return BACKGROUND_START_Y;
    }

    @Override
    public int getBackgroundEndX() {
        return BACKGROUND_END_X;
    }

    @Override
    public int getBackgroundEndY() {
        return BACKGROUND_END_Y;
    }

    @Override
    public int getDefaultPage() {
        return defaultPage;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public boolean doesBookForgetClose() {
        return forgetPageOnClose;
    }

    @Override
    public List<IPage> getPages() {
        return new ArrayList<>(book);
    }

    @Override
    public boolean isLegacyBook() {
        return !mc189book;
    }

    @Override
    public boolean wereArrowsVisible() {
        return showArrows;
    }

    @Override
    public String getIconPass1() {
        return iconPass1;
    }

    @Override
    public IBook setModID(String modID) {
        // Update the namespace of bookId
        String currentPath = (bookId != null) ? bookId.getPath() : "default";
        this.bookId = new ResourceLocation(modID, currentPath);
        return this;
    }

    @Override
    public void setSaveName(String name) {
        // Update the path of bookId
        String currentNamespace = (bookId != null) ? bookId.getNamespace() : "enchiridion";
        this.bookId = new ResourceLocation(currentNamespace, name);
    }

    @Override
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public void setColorAsInt(int color) {
        this.color = color;
        this.colorHex = Integer.toHexString(color);
    }

    @Override
    public void setLanguageKey(String language) {
        this.language = language;
    }

    @Override
    public void setBackgroundResource(String string) {
        backgroundResource = string;
        resourceLocation = new ResourceLocation(string);
    }

    @Override
    public void setLegacy() {
        mc189book = false;
        legacyTexture = true;
    }

    @Override
    public void setArrowVisibility(boolean isVisible) {
        showArrows = isVisible;
    }

    @Override
    public void setIconPass1(String pass1) {
        iconPass1 = pass1;
    }

    @Override
    public void setMadeIn189() {
        mc189book = true;
    }

    @Override
    public void create() {
        book = new ArrayList<>();
    }

    @Override
    public void addPage(IPage page) {
        book.add(page);
    }

    @Override
    public void removePage(IPage page) {
        book.removeIf(iPage -> iPage.getPageNumber() == page.getPageNumber());
    }

    @Override
    public void addInformation(List<Component> tooltip) {
        if (information == null) {
            if (displayInfo == null) displayInfo = "";
            String[] split = displayInfo.split("/n");
            information = new ArrayList<>();
            for (String s : split) {
                if (!s.equals("")) information.add(Component.literal(s));
            }
        }
        tooltip.addAll(information);
    }

    @Override
    public List<String> getDefaultFeatures() {
        if (defaultIDs == null) {
            defaultIDs = new ArrayList<>();
            defaultIDs.add("enchiridion_default_buttons");
        }
        return defaultIDs;
    }

    @Override
    public void setDefaultFeatures(Collection<String> features) {
        this.defaultIDs = new ArrayList<>(features);
    }
}