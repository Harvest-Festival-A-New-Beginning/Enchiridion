# 1.20.4 Migration Remaining Errors

## Current Status
- ~100 compilation errors remaining
- Multiple deep API changes required

## Major API Changes Needed

### 1. Tooltip API (2 files affected)
```java
// OLD (1.14.3):
stack.getTooltip(player, flag)

// NEW (1.20.4):
stack.getTooltipLines(player, flag)
```
Files: FeatureItem.java, RecipeHandlerBase.java

### 2. Container/Inventory Methods (10+ files)
```java
// OLD:
container.getStackInSlot(index)
container.setInventorySlotContents(index, stack)

// NEW:
container.getItem(index)
container.setItem(index, stack)
```

### 3. FriendlyByteBuf Methods (8+ files)
```java
// OLD:
buf.writeString(str)
buf.readString(maxLength)
buf.writeItemStack(stack)
buf.readItemStack()

// NEW:
buf.writeUtf(str)
buf.readUtf(maxLength)
ItemStack.STREAM_CODEC.encode(buf, stack)
ItemStack.STREAM_CODEC.decode(buf)
```

### 4. ModelResourceLocation Constructor
```java
// OLD:
new ModelResourceLocation("minecraft:book", "inventory")

// NEW:
new ModelResourceLocation(new ResourceLocation("minecraft", "book"), "inventory")
```

### 5. mouseScrolled Signature
```java
// OLD:
public boolean mouseScrolled(double mX, double mY, double wheel)

// NEW:
public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY)
```

### 6. Abstract Method Implementations Needed
- `BakedModel.usesBlockLight()`
- `SavedData.save(CompoundTag)`
- `Recipe.assemble(CraftingContainer, RegistryAccess)`
- `ICondition.codec()`
- `PackResources.packId()`
- `Container.clearContent()`

### 7. Other Method Changes
- `Minecraft.level()` doesn't exist, use `Minecraft.level` (field)
- `Player.getUUID()` takes no args
- `ItemStack.isDamageable()` → `stack.isDamageableItem()`
- `MinecraftServer.getServerHostname()` → different API
- `player.openBook()` removed
- `LibraryHelper.markDirty()` needs implementation

## Recommendation
Due to the depth of these API changes, consider:
1. Fixing errors in batches by category
2. Some features may need to be temporarily stubbed out
3. Testing after each major batch of fixes
