# Enchiridion 1.20.4 NeoForge Migration - TODO List

**Status**: ✅ Compiles | ❌ Not Functional
**Version**: 1.20.4 → 4.0.0
**Last Updated**: 2025-12-17

---

## 🎯 QUICK START - What To Do First

### Step 1: Try to Build & Run
```bash
./gradlew build
./gradlew runClient
```

**Expected Result**: Build succeeds, client may crash or show errors
**Action**: Copy any errors/crashes and share with Claude for fixing

---

## 📊 Current Status Summary

| Component | Status | Blocks |
|-----------|--------|--------|
| ✅ Compilation | **DONE** | Nothing |
| ❌ Networking | **BROKEN** | Multiplayer, Syncing |
| ❌ Rendering | **BROKEN** | All visuals |
| ⚠️ Resource Loading | **STUBBED** | Custom textures |
| ⚠️ Recipe Display | **PARTIAL** | Furnace recipes |
| ✅ Config System | **DONE** | Nothing |
| ✅ Event Registration | **DONE** | Nothing |
| ⚠️ Library System | **MOSTLY WORKING** | Testing needed |
| ⚠️ GUI/Screens | **MIXED** | Some work, some broken |
| ❌ JEI Integration | **DISABLED** | JEI features |
| ⚠️ Keybinding | **INCOMPLETE** | Hotkey activation |

**Critical Path**: Networking → Rendering → Resource Loading → Testing

---

## 📝 SEQUENTIAL TODO LIST

### Phase 1: Initial Testing & Bug Fixing (Week 1)

#### ✅ COMPLETED
- [x] Update to NeoForge 1.20.4 (20.4.251)
- [x] Update Gradle to 8.10
- [x] Migrate 167 Java files to NeoForge APIs
- [x] Fix all compilation errors
- [x] Update build.gradle for NeoGradle 7.0.57
- [x] Update mods.toml for NeoForge
- [x] Fix import paths (ForgeRegistries → BuiltInRegistries, etc.)
- [x] Update inventory/container system (IInventory → Container, etc.)
- [x] Update text components (TranslationTextComponent → Component)
- [x] Update NBT tags (CompoundNBT → CompoundTag, etc.)
- [x] Update SavedData system
- [x] Proactive code search for common errors

#### 🔄 IN PROGRESS

##### 1. **Run Initial Build & Test** ⏱️ 30 min
**Priority**: CRITICAL
**Status**: NEXT STEP

**Actions**:
1. Run `./gradlew build` - should succeed
2. Run `./gradlew runClient` - will likely crash or show errors
3. Document ALL errors/warnings/crashes
4. Share with Claude for fixing

**Expected Issues**:
- Networking warnings
- Rendering issues
- Resource loading errors
- Possible crashes on book opening

**Files to Watch**:
- `logs/latest.log` - for runtime errors
- `logs/debug.log` - for detailed info

---

##### 2. **Fix Networking System** ⏱️ 6-8 hours
**Priority**: CRITICAL - Blocks multiplayer and syncing
**Depends On**: Nothing

**Problem**: Old SimpleChannel networking completely stubbed out

**Files Affected** (Priority Order):
1. `src/main/java/joshie/enchiridion/network/PacketHandler.java` - Main registration
2. `src/main/java/joshie/enchiridion/network/packet/PacketOpenBook.java`
3. `src/main/java/joshie/enchiridion/network/packet/PacketOpenLibrary.java`
4. `src/main/java/joshie/enchiridion/network/packet/PacketSyncFile.java`
5. `src/main/java/joshie/enchiridion/network/packet/PacketSyncLibraryContents.java`
6. `src/main/java/joshie/enchiridion/network/packet/PacketSyncLibraryAllowed.java`
7. `src/main/java/joshie/enchiridion/network/packet/PacketHandleBook.java`
8. `src/main/java/joshie/enchiridion/network/packet/PacketSetLibraryBook.java`
9. `src/main/java/joshie/enchiridion/network/packet/PacketLibraryCommand.java`
10. `src/main/java/joshie/enchiridion/network/packet/PacketSyncMD5.java`

**What Needs to Be Done**:

**Step 2.1**: Update PacketHandler.java
- Remove SimpleChannel usage
- Add RegisterPayloadHandlerEvent listener to Enchiridion constructor
- Create registration method

**Example Pattern**:
```java
// In Enchiridion.java constructor:
eventBus.addListener(this::registerPayloads);

private void registerPayloads(RegisterPayloadHandlerEvent event) {
    PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0");

    registrar.playToClient(
        PacketOpenBook.TYPE,
        PacketOpenBook.STREAM_CODEC,
        PacketOpenBook::handle
    );
    // ... register all other packets
}
```

**Step 2.2**: Convert Each Packet to CustomPacketPayload
For each packet, update to:
```java
public record PacketOpenBook(String bookID, int page) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketOpenBook> TYPE =
        new CustomPacketPayload.Type<>(new ResourceLocation(MODID, "open_book"));

    public static final StreamCodec<FriendlyByteBuf, PacketOpenBook> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketOpenBook::bookID,
            ByteBufCodecs.VAR_INT, PacketOpenBook::page,
            PacketOpenBook::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            // Handle packet on correct thread
        });
    }
}
```

**Step 2.3**: Update Packet Sending
Replace:
```java
PacketHandler.sendToServer(packet);
```
With:
```java
PacketDistributor.sendToServer(packet);
```

**Step 2.4**: Test
- Test client→server packets (PacketLibraryCommand)
- Test server→client packets (PacketOpenBook)
- Test server→all packets (PacketSyncLibraryContents)

**References**:
- NeoForge Networking Docs: https://docs.neoforged.net/docs/networking/
- Official 1.20.4 examples in NeoForge repo

**Testing Checklist**:
- [ ] PacketOpenBook works (server opens book on client)
- [ ] PacketOpenLibrary works
- [ ] PacketSyncLibraryContents syncs properly
- [ ] PacketLibraryCommand sends from client to server
- [ ] No networking errors in console

---

##### 3. **Fix Rendering System** ⏱️ 10-12 hours
**Priority**: CRITICAL - Blocks all visual features
**Depends On**: Nothing (can work in parallel with #2)

**Problem**: GuiBase uses old rendering APIs without GuiGraphics parameter

**Files Affected** (Priority Order):
1. `src/main/java/joshie/enchiridion/api/gui/IDrawHelper.java` - Interface
2. `src/main/java/joshie/enchiridion/gui/book/GuiBase.java` - Main implementation (15+ TODOs)
3. `src/main/java/joshie/enchiridion/helpers/ClientStackHelper.java` - Item rendering
4. `src/main/java/joshie/enchiridion/gui/book/GuiBook.java` - Uses GuiBase methods
5. `src/main/java/joshie/enchiridion/gui/book/features/FeaturePreviewWindow.java` - Complex rendering
6. All feature classes in `src/main/java/joshie/enchiridion/gui/book/features/`

**Step 3.1**: Update IDrawHelper Interface
Add GuiGraphics parameter to all methods:
```java
void drawTexturedRectangle(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, ...);
void drawString(GuiGraphics guiGraphics, String text, int x, int y, int color);
void drawStack(GuiGraphics guiGraphics, ItemStack stack, int x, int y, float size);
// ... etc for all methods
```

**Step 3.2**: Update GuiBase.java Implementation

**Lines 107-122** - `drawTexturedRectangle()`:
```java
@Override
public void drawTexturedRectangle(GuiGraphics guiGraphics, ResourceLocation resource,
                                  int x, int y, int u, int v, int w, int h, float size) {
    RenderSystem.setShaderTexture(0, resource);
    guiGraphics.pose().pushPose();
    guiGraphics.pose().scale(size, size, 1.0F);
    int x2 = (int) (x / size);
    int y2 = (int) (y / size);
    guiGraphics.blit(resource, x2, y2, u, v, w, h);
    guiGraphics.pose().popPose();
}
```

**Lines 144-151** - `drawSplitScaledString()`:
```java
@Override
public void drawSplitScaledString(GuiGraphics guiGraphics, String text, int x, int y,
                                  int wrap, int color, float scale) {
    guiGraphics.pose().pushPose();
    guiGraphics.pose().scale(scale, scale, scale);
    guiGraphics.drawWordWrap(font, Component.literal(text),
                            (int)(x/scale), (int)(y/scale), wrap, color);
    guiGraphics.pose().popPose();
}
```

**Lines 155-158** - `drawRectangle()`:
```java
@Override
public void drawRectangle(GuiGraphics guiGraphics, int x, int y, int left, int top,
                         int right, int bottom, int color) {
    guiGraphics.fill(x + left, y + top, x + right, y + bottom, color);
}
```

**Lines 252-267** - `renderTooltip()`:
```java
@Override
public void renderTooltip(GuiGraphics guiGraphics, List<String> textLines, int x, int y) {
    List<Component> components = textLines.stream()
        .map(Component::literal)
        .collect(Collectors.toList());
    guiGraphics.renderTooltip(font, components, x, y);
}
```

**Step 3.3**: Update ClientStackHelper.java
```java
public static void drawStack(GuiGraphics guiGraphics, ItemStack stack, int x, int y, float size) {
    guiGraphics.pose().pushPose();
    guiGraphics.pose().scale(size, size, 1.0F);
    guiGraphics.renderItem(stack, (int)(x/size), (int)(y/size));
    guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack,
                                     (int)(x/size), (int)(y/size));
    guiGraphics.pose().popPose();
}
```

**Step 3.4**: Update All GUI Classes
Find all calls to `EnchiridionAPI.draw.drawXXX()` and pass GuiGraphics:
- In `render()` methods, GuiGraphics is already provided as parameter
- Pass it through to all draw calls

**Step 3.5**: Fix Vertex Buffer Usage (Lines 193-210)
This is complex - may need to revisit if lines are still needed

**Testing Checklist**:
- [ ] Books display textures correctly
- [ ] Text renders properly (no scaling issues)
- [ ] Items display in books
- [ ] Tooltips show correctly
- [ ] Colored rectangles/backgrounds render
- [ ] Custom images load and display
- [ ] No rendering errors in console

---

##### 4. **Fix Resource Loading** ⏱️ 3-4 hours
**Priority**: HIGH - Blocks custom book textures/icons
**Depends On**: Nothing (can work in parallel)

**Problem**: Resource pack registration disabled (addResourcePack() removed)

**Files Affected**:
1. `src/main/java/joshie/enchiridion/EClientHandler.java` (lines 39-41)
2. `src/main/java/joshie/enchiridion/util/EResourcePack.java` (needs update)

**Step 4.1**: Register via AddPackFindersEvent
In EClientHandler.java setupClient():
```java
public void setupClient(final FMLClientSetupEvent event) {
    eventBus.addListener(this::addPackFinders);
    // ... rest of setup
}

private void addPackFinders(AddPackFindersEvent event) {
    if (event.getPackType() == PackType.CLIENT_RESOURCES) {
        event.addRepositorySource((consumer) -> {
            Pack pack = Pack.readMetaAndCreate(
                new PackLocationInfo(
                    "enchiridion_custom",
                    Component.literal("Enchiridion Custom Resources"),
                    PackSource.BUILT_IN,
                    Optional.empty()
                ),
                EResourcePack.INSTANCE,
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(false, Pack.Position.TOP, false)
            );
            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }
}
```

**Step 4.2**: Update EResourcePack.java
Verify it properly implements PackResources for 1.20.4

**Step 4.3**: Test Resource Loading
1. Place custom book texture in `config/enchiridion/images/`
2. Create book that references it
3. Verify texture loads and displays

**Testing Checklist**:
- [ ] Custom book icons load from config folder
- [ ] Custom book backgrounds load
- [ ] Template images display
- [ ] No resource loading errors in console

---

### Phase 2: Feature Completion (Week 2)

#### 5. **Fix Recipe System** ⏱️ 4-5 hours
**Priority**: MEDIUM-HIGH
**Depends On**: #3 (Rendering)

**Problem**: Furnace recipes can't be queried, potential server crashes

**Files Affected**:
1. `src/main/java/joshie/enchiridion/gui/book/features/recipe/RecipeHandlerFurnace.java`
2. `src/main/java/joshie/enchiridion/gui/book/features/FeatureRecipe.java`
3. `src/main/java/joshie/enchiridion/ECommonHandler.java` (condition system)

**Step 5.1**: Fix Furnace Recipe Handler (Lines 30-37)
```java
// Replace commented code with:
Level level = Minecraft.getInstance().level;
if (level != null) {
    RecipeManager recipeManager = level.getRecipeManager();
    for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
        if (holder.value() instanceof SmeltingRecipe smelting) {
            // Process smelting recipe
            // Get input: smelting.getIngredients()
            // Get output: smelting.getResultItem(level.registryAccess())
        }
    }
}
```

**Step 5.2**: Add Null Checks in FeatureRecipe (Line 43)
```java
// Change to:
ServerLevel serverLevel = ServerLifecycleHooks.getCurrentServer() != null
    ? ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)
    : null;
if (serverLevel != null) {
    handler.addRecipes(stack, recipes, serverLevel);
}
```

**Step 5.3**: Condition System (Optional)
Decide if library conditions are still needed:
- If yes: Implement via RegisterEvent
- If no: Remove TODO comment

**Testing Checklist**:
- [ ] Furnace recipes display in books
- [ ] Shaped recipes display correctly
- [ ] Shapeless recipes display correctly
- [ ] No crashes when opening recipe books
- [ ] Recipes show correct inputs/outputs

---

#### 6. **Test & Fix Library System** ⏱️ 3-4 hours
**Priority**: MEDIUM
**Depends On**: #2 (Networking), #3 (Rendering)

**Problem**: Tooltip crashes, mod book list outdated, syncing untested

**Files Affected**:
1. `src/main/java/joshie/enchiridion/items/ItemLibrary.java` (lines 78-83)
2. `src/main/java/joshie/enchiridion/library/ModSupport.java` (line 60)
3. `src/main/java/joshie/enchiridion/library/LibrarySavedData.java`

**Step 6.1**: Fix Tooltip Crash
Uncomment and fix with null check:
```java
if (world != null && world.isClientSide && LibraryHelper.getClientLibraryContents() != null) {
    int currentBook = LibraryHelper.getClientLibraryContents().getCurrentBook();
    if (currentBook >= 0 && currentBook < LibraryHelper.getClientLibraryContents().getContainerSize()) {
        ItemStack internal = LibraryHelper.getClientLibraryContents().getItem(currentBook);
        if (!internal.isEmpty() && Minecraft.getInstance().player != null) {
            tooltip.addAll(internal.getTooltip(Minecraft.getInstance().player, flag));
        }
    }
}
```

**Step 6.2**: Update Mod Book List
Review and update ModSupport.java line 60+ with 1.20.4 mods:
- Remove outdated mods
- Add modern equivalents
- Test with popular mod packs

**Step 6.3**: Test SavedData Persistence
1. Create library inventory
2. Save and exit world
3. Reload world
4. Verify library contents persist

**Step 6.4**: Test Library Syncing
1. Set up dedicated server
2. Join with client
3. Add books to library
4. Verify client receives updates

**Testing Checklist**:
- [ ] Library item shows current book tooltip
- [ ] Library inventory persists across world reloads
- [ ] Library syncs to clients in multiplayer
- [ ] Mod books load from other mods
- [ ] No crashes when using library

---

#### 7. **Implement Keybinding Registration** ⏱️ 1-2 hours
**Priority**: MEDIUM
**Depends On**: #2 (Networking)

**Problem**: Keybinding created but not registered

**Files Affected**:
1. `src/main/java/joshie/enchiridion/EClientHandler.java` (lines 98-101)

**Step 7.1**: Add Event Listener
```java
// In Enchiridion.java constructor:
if (dist == Dist.CLIENT) {
    eventBus.addListener(EClientHandler::registerKeyMappings);
}

// In EClientHandler.java:
public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    if (EConfig.SETTINGS.libraryAsHotkey.get() && libraryKeyBinding != null) {
        event.register(libraryKeyBinding);
    }
}
```

**Step 7.2**: Test Keybinding
1. Enable library hotkey in config
2. Press 'I' key
3. Verify library opens

**Testing Checklist**:
- [ ] Keybinding appears in controls menu
- [ ] Pressing key opens library
- [ ] Keybinding can be rebound
- [ ] No conflicts with vanilla keys

---

### Phase 3: Polish & Integration (Week 3)

#### 8. **Test All GUI Screens** ⏱️ 4-5 hours
**Priority**: MEDIUM
**Depends On**: #3 (Rendering)

**Files to Test**:
- `GuiBook.java` - Main book viewing
- `GuiBookCreate.java` - Book creation
- `GuiLibrary.java` - Library container
- `GuiToolbar.java` - Editor toolbar
- `GuiTimeLine.java` - Timeline overlay
- `GuiGrid.java` - Grid overlay
- `GuiLayers.java` - Layer management
- `GuiSimpleEditor.java` - Simple editor
- All feature editors

**Testing Checklist**:
- [ ] Books open and display correctly
- [ ] Book creation GUI works
- [ ] Library GUI shows items correctly
- [ ] Editor toolbar appears
- [ ] Timeline overlay functional
- [ ] Grid overlay shows/hides
- [ ] Layer management works
- [ ] Simple editor accepts input
- [ ] All feature editors work (text, item, image, etc.)

---

#### 9. **Re-enable JEI Integration** ⏱️ 2-3 hours
**Priority**: LOW (Optional)
**Depends On**: #5 (Recipe System)

**Problem**: Entire JEI plugin commented out

**Files Affected**:
1. `src/main/java/joshie/enchiridion/jei/LibraryPlugin.java`

**Step 9.1**: Update to JEI 1.20.4 API
- Uncomment all code
- Update to use modern JEI API
- Update recipe category registration
- Update GUI handlers

**Step 9.2**: Add JEI Dependency
In build.gradle:
```gradle
dependencies {
    implementation "net.neoforged:neoforge:${neo_version}"

    // JEI
    compileOnly "mezz.jei:jei-1.20.4-neoforge-api:${jei_version}"
    runtimeOnly "mezz.jei:jei-1.20.4-neoforge:${jei_version}"
}
```

Add to gradle.properties:
```
jei_version=17.3.0.49
```

**Testing Checklist**:
- [ ] JEI loads with mod
- [ ] Library GUI doesn't overlap JEI
- [ ] Recipes show in JEI
- [ ] No JEI errors in console

---

#### 10. **Review Item Registration** ⏱️ 2-3 hours
**Priority**: LOW-MEDIUM
**Depends On**: Nothing

**Files to Review**:
1. `src/main/java/joshie/enchiridion/items/ItemBook.java`
2. `src/main/java/joshie/enchiridion/items/ItemLibrary.java`
3. `src/main/java/joshie/enchiridion/items/SmartLibrary.java`
4. `src/main/java/joshie/enchiridion/util/EItemGroup.java`

**Step 10.1**: Check Stack Size Methods
ItemBook.java and ItemLibrary.java both override `getItemStackLimit()`:
- Verify this is still valid in 1.20.4
- If deprecated, move to Item.Properties in constructor

**Step 10.2**: Test SmartLibrary BakedModel
- Verify model system works
- Test library item shows correct book icons

**Step 10.3**: Update Creative Tab (if needed)
EItemGroup.java may need updating to CreativeModeTab.Builder

**Testing Checklist**:
- [ ] Items register correctly
- [ ] Items appear in creative tab
- [ ] Max stack sizes work (16 for books, 1 for library)
- [ ] Library shows correct icons for books
- [ ] Items have correct localization

---

### Phase 4: Final Testing & Release (Week 4)

#### 11. **Comprehensive Integration Testing** ⏱️ 8-10 hours
**Priority**: HIGH
**Depends On**: ALL previous steps

**Test Scenarios**:

**Singleplayer Tests**:
- [ ] Create new world
- [ ] Open/create books
- [ ] Use library
- [ ] Save and reload
- [ ] Verify persistence

**Multiplayer Tests**:
- [ ] Start dedicated server
- [ ] Client connects successfully
- [ ] Books sync to client
- [ ] Library syncs to client
- [ ] Create book on client
- [ ] Verify server receives it
- [ ] Test with multiple clients

**Editor Tests**:
- [ ] Create new book
- [ ] Add pages
- [ ] Add text features
- [ ] Add item features
- [ ] Add image features
- [ ] Add recipe features
- [ ] Add button features
- [ ] Save and test book

**Intermod Tests**:
- [ ] Test with JEI (if enabled)
- [ ] Test with other mods that might have books
- [ ] Verify IMC works

**Performance Tests**:
- [ ] Large books (100+ pages)
- [ ] Many books in library (100+ books)
- [ ] Complex pages (many features)

**Edge Cases**:
- [ ] Empty books
- [ ] Books with missing resources
- [ ] Invalid book data
- [ ] Server restart while editing
- [ ] Client disconnect/reconnect

---

#### 12. **Config Testing** ⏱️ 1-2 hours
**Priority**: MEDIUM
**Depends On**: Nothing

**Files to Test**:
1. `src/main/java/joshie/enchiridion/EConfig.java`

**Test All Config Options**:
- [ ] `enableEditing` - Verify editing can be disabled
- [ ] `resourceReload` - Test resource pack reloading
- [ ] `offlineMode` - Test offline functionality
- [ ] `defaultText` - Verify default text in editor
- [ ] `defaultItem` - Verify default item selection
- [ ] `toolbarYPos`, `timelineYPos`, `layersXPos` - Test positioning
- [ ] `allowDataAndImagesFromServers` - Test server content
- [ ] `syncDataAndImagesToClients` - Test syncing control
- [ ] `libraryAsItem` - Test library item enabled/disabled
- [ ] `libraryAsHotkey` - Test hotkey enabled/disabled
- [ ] `addWrittenBookRecipeForLibrary` - Test recipe presence

**Testing Checklist**:
- [ ] Config file generates on first run
- [ ] All values save/load correctly
- [ ] Changes take effect after restart
- [ ] Invalid values are handled gracefully

---

#### 13. **Documentation & Polish** ⏱️ 3-4 hours
**Priority**: LOW
**Depends On**: ALL previous steps

**Tasks**:

**Update README.md**:
- Minecraft 1.20.4 compatibility
- NeoForge requirement
- Breaking changes from 3.x
- New features (if any)
- Installation instructions

**Create CHANGELOG.md**:
- Version 4.0.0 changes
- API changes
- Breaking changes
- Migration guide

**Create MIGRATION.md** (for book authors):
- Changes to book format (if any)
- How to update existing books
- New features available
- Deprecated features

**Polish Code**:
- Remove all TODO comments (or convert to issues)
- Remove commented-out code
- Add JavaDoc where needed
- Clean up debug logging

**Testing Checklist**:
- [ ] README accurate
- [ ] CHANGELOG complete
- [ ] Migration guide clear
- [ ] Code cleaned up
- [ ] No debug spam in console

---

## 🚨 KNOWN ISSUES & WORKAROUNDS

### Issue 1: Networking Stubbed
**Status**: ❌ Broken
**Impact**: Multiplayer won't work
**Workaround**: None - must be fixed
**Fix**: See Step #2

### Issue 2: Rendering Uses Old APIs
**Status**: ❌ Broken
**Impact**: Books won't display properly
**Workaround**: None - must be fixed
**Fix**: See Step #3

### Issue 3: Resource Packs Not Loading
**Status**: ⚠️ Stubbed
**Impact**: Custom textures won't load
**Workaround**: None - must be fixed
**Fix**: See Step #4

### Issue 4: Furnace Recipes Don't Display
**Status**: ⚠️ Partial
**Impact**: Some recipes missing from books
**Workaround**: Use shaped/shapeless recipes only
**Fix**: See Step #5

### Issue 5: JEI Integration Disabled
**Status**: ❌ Disabled
**Impact**: No JEI integration
**Workaround**: Use without JEI
**Fix**: See Step #9 (optional)

---

## 📋 TESTING CHECKLIST

### Pre-Release Testing

#### Build & Launch
- [ ] `./gradlew build` succeeds with no errors
- [ ] `./gradlew runClient` launches successfully
- [ ] Mod appears in mods list
- [ ] No errors in latest.log on startup

#### Basic Functionality
- [ ] Can create new book
- [ ] Can open existing book
- [ ] Library item exists
- [ ] Library opens
- [ ] Config file generates

#### Book Features
- [ ] Text displays
- [ ] Items display
- [ ] Images display
- [ ] Recipes display (shaped, shapeless, furnace)
- [ ] Buttons work
- [ ] Page navigation works

#### Editor Features
- [ ] Can add text
- [ ] Can add items
- [ ] Can add images
- [ ] Can add recipes
- [ ] Can add buttons
- [ ] Can save changes

#### Multiplayer
- [ ] Server starts
- [ ] Client connects
- [ ] Books sync
- [ ] Library syncs
- [ ] No sync errors

#### Performance
- [ ] No lag with large books
- [ ] No memory leaks
- [ ] Reasonable FPS

---

## 🎯 SUCCESS CRITERIA

### Minimum Viable Product (MVP)
- ✅ Mod compiles
- ⬜ Mod loads in game
- ⬜ Books can be created
- ⬜ Books can be viewed
- ⬜ Library works in singleplayer
- ⬜ No crashes in normal use

### Full Release Criteria
- ⬜ All MVP criteria met
- ⬜ Networking functional
- ⬜ Rendering works correctly
- ⬜ Resources load from config
- ⬜ Multiplayer fully functional
- ⬜ All config options work
- ⬜ All editor features work
- ⬜ Documentation complete
- ⬜ Tested with 10+ mods

### Optional Features
- ⬜ JEI integration working
- ⬜ CraftTweaker support
- ⬜ Performance optimizations
- ⬜ Additional book features

---

## 📞 GETTING HELP

When reporting issues to Claude:

### Include This Information:
1. **What you were doing** - Step number from this TODO
2. **What happened** - Exact error message or crash log
3. **Latest.log** - Last 100 lines from logs/latest.log
4. **Build output** - If compilation error, full output
5. **Stack trace** - If crash, full stack trace

### Format:
```
Working on: Step #X - [Step Name]

Error:
[paste error here]

Latest.log (last 100 lines):
[paste log here]

What I tried:
- [list what you tried]
```

---

## 📊 PROGRESS TRACKER

**Overall Progress**: █░░░░░░░░░ 10% (Compilation Complete)

### Phase 1: Testing & Core Fixes
- [x] Compilation (100%)
- [ ] Initial Testing (0%)
- [ ] Networking (0%)
- [ ] Rendering (0%)
- [ ] Resource Loading (0%)

### Phase 2: Features
- [ ] Recipe System (0%)
- [ ] Library System (0%)
- [ ] Keybinding (0%)

### Phase 3: Polish
- [ ] GUI Testing (0%)
- [ ] JEI Integration (0%)
- [ ] Item Registration Review (0%)

### Phase 4: Release
- [ ] Integration Testing (0%)
- [ ] Config Testing (0%)
- [ ] Documentation (0%)

---

## 🔄 QUICK REFERENCE

### Useful Commands
```bash
# Build
./gradlew build

# Run client (development)
./gradlew runClient

# Run server (development)
./gradlew runServer

# Clean build
./gradlew clean build

# Generate IDE files
./gradlew eclipse  # or 'idea'

# Check for errors
./gradlew compileJava
```

### Important Files
- **Config**: `.minecraft/config/enchiridion-common.toml`
- **Books**: `.minecraft/config/enchiridion/books/`
- **Images**: `.minecraft/config/enchiridion/images/`
- **Templates**: `.minecraft/config/enchiridion/templates/`
- **Logs**: `.minecraft/logs/latest.log`

### Key Directories
- **Source**: `src/main/java/joshie/enchiridion/`
- **Resources**: `src/main/resources/`
- **Build Output**: `build/libs/`

---

## 📝 NOTES

- Remember to backup your config folder before testing
- Test frequently to catch issues early
- Document any workarounds you discover
- Keep this TODO updated as you progress
- Celebrate small wins! 🎉

---

**Last Updated**: 2025-12-17
**Next Update**: After Step #1 completion
**Maintained By**: Claude & User
