# Remaining Compilation Errors to Fix

Due to the scope of changes, these items need manual implementation:

## Critical Fixes Needed (prevent compilation)

### 1. Tooltip API - 2 files
- **FeatureItem.java:67** - `Item.TooltipContext.EMPTY` doesn't exist
- **RecipeHandlerBase.java:36** - Same issue

**Fix:** Use `getTooltipLines()` method instead or find correct API

### 2. LibrarySavedData - Missing save() method
- **LibrarySavedData.java:16** - Must implement `save(CompoundTag)`
- Current has `write()`, needs to be renamed/adapted

### 3. LibraryRecipe - Missing assemble() method
- **LibraryRecipe.java:22** - Must implement `assemble(CraftingContainer, RegistryAccess)`
- Needs implementation for crafting system

### 4. InventoryStorage - Method signature issue
- **InventoryStorage.java:77** - Some @Override doesn't match

### 5. LibraryInventory - Missing clearContent()
- **LibraryInventory.java:18** - Must implement `clearContent()` from Clearable
- Simple stub: `public void clearContent() { /* TODO */ }`

### 6. LibraryConditionFactory - Missing codec()
- **LibraryConditionFactory.java:10** - Must implement `codec()` method
- Returns `MapCodec<? extends ICondition>`

### 7. EResourcePack - Missing packId()
- **EResourcePack.java:25** - Must implement `packId()` method
- Returns String ID

### 8. EItemGroup - Constructor issue
- **EItemGroup.java:18** - String cannot be converted to Builder
- Needs CreativeModeTab.Builder pattern

### 9. GuiBase - renderTooltip() override issue
- **GuiBase.java:267** - Method signature doesn't match superclass

### 10. LibraryProxyServer - computeIfAbsent signature
- **LibraryProxyServer.java:13** - Wrong number of arguments

### 11. Writable/Written Book Handlers
- **WritableBookHandler.java** - Private field access in BookEditScreen
- **WrittenBookHandler.java:19** - `player.openBook()` doesn't exist

### 12. Various Network packet handlers
- All packet Handler classes reference `NetworkEvent.Context`
- Networking system was stubbed, needs proper CustomPacketPayload implementation

### 13. EffectiveSide
- **LibraryInventory.java:109** - `EffectiveSide` class doesn't exist
- Need to use FMLEnvironment or different API

### 14. LibraryHelper.markDirty()
- Multiple files call this - method doesn't exist
- Needs implementation

### 15. MCServerHelper
- **MCServerHelper.java:10** - `getServerHostname()` doesn't exist
- API changed

## Non-Critical (can be stubbed for now)

- Tessellator issues in GuiBase/GuiLibrary - rendering system needs refactor
- Various deprecated method warnings

## Strategy

Add minimal stub implementations to get it compiling:
```java
// Example stubs
@Override
public void clearContent() { }

@Override
public CompoundTag save(CompoundTag tag) { return tag; }

@Override
public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
    return ItemStack.EMPTY;
}
```

Then incrementally implement proper functionality.
