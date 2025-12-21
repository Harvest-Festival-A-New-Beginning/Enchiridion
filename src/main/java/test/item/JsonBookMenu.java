package test.item;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;

/**
 * Menu for JSON-defined books
 * Stores which book definition to display and whether to start in edit mode
 */
public class JsonBookMenu extends AbstractBookMenu {
    private final ResourceLocation bookId;
    private final boolean startInEditMode;

    // Client-side constructor (called from network)
    public JsonBookMenu(int windowID, Inventory inventory, FriendlyByteBuf buf) {
        super(TestMenus.JSON_BOOK.get(), windowID);
        this.bookId = buf.readResourceLocation();
        this.startInEditMode = buf.readBoolean();
    }

    // Server-side constructor
    public JsonBookMenu(int windowID, ResourceLocation bookId, boolean editMode) {
        super(TestMenus.JSON_BOOK.get(), windowID);
        this.bookId = bookId;
        this.startInEditMode = editMode;
    }

    // Backwards compatibility constructor
    public JsonBookMenu(int windowID, ResourceLocation bookId) {
        this(windowID, bookId, false);
    }

    public ResourceLocation getBookId() {
        return bookId;
    }

    public boolean shouldStartInEditMode() {
        return startInEditMode;
    }
}
