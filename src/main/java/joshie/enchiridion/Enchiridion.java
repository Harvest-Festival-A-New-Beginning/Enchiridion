package joshie.enchiridion;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.helpers.FileHelper;
import joshie.enchiridion.helpers.SyncHelper;
import joshie.enchiridion.lib.EGuis;
import joshie.enchiridion.lib.EnchiridionRegistries;
import joshie.enchiridion.library.LibraryCommand;
import joshie.enchiridion.library.LibraryHelper;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.util.EItemGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static joshie.enchiridion.lib.EInfo.MODID;
import static joshie.enchiridion.lib.EInfo.MODNAME;

@Mod(MODID)
public class Enchiridion {
    private static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public static File root = new File(FMLPaths.CONFIGDIR.get().toFile(), MODID);

    public Enchiridion(IEventBus eventBus) {
        // Register DeferredRegisters
        EnchiridionRegistries.register(eventBus);
        EGuis.MENUS.register(eventBus);
        EItemGroup.CREATIVE_MODE_TABS.register(eventBus);
        // Feature codecs use simple dispatch in Page.java - no registration needed

        eventBus.addListener(this::setupCommon);
        eventBus.addListener(this::setupClient);
        eventBus.addListener(this::handleIMCMessages);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EConfig.spec, FileHelper.getConfigFile().getAbsolutePath());
    }

    public void setupCommon(final FMLCommonSetupEvent event) {
        ECommonHandler.init();
        PacketHandler.registerPackets();
    }

    public void setupClient(final FMLClientSetupEvent event) {
        EClientHandler.setupClient();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LibraryHelper.resetServer(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD));
        SyncHelper.resetSyncing();

        //Register commands
        LibraryCommand.register(event.getServer().getCommands().getDispatcher());
    }

    public void handleIMCMessages(final InterModProcessEvent event) {
        event.getIMCStream().filter(message -> message.getMethod().equalsIgnoreCase("registerBook")).forEach(message -> { //TODO Test
            CompoundTag tag = new CompoundTag();
            String handlerType = tag.getString("handlerType");
            ItemStack stack = ItemStack.of(tag.getCompound("stack"));
            boolean matchNBT = tag.contains("matchNBT") && tag.getBoolean("matchNBT");
            EnchiridionAPI.library.registerBookHandlerForStack(handlerType, stack, matchNBT);
        });
    }

    //Universal log helper
    public static void log(org.apache.logging.log4j.Level level, String message) {
        LOGGER.log(level, message);
    }

    //Universal helper translation
    public static String format(String string, Object... format) {
        return Component.translatable("enchiridion." + string, format).getString();
    }
}