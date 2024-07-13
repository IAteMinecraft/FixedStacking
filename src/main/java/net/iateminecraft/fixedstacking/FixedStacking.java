package net.iateminecraft.fixedstacking;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("fixedstacking")
public class FixedStacking
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public FixedStacking()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HAIIIIIIIIIIIIIIII (from Fixed Stacking");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo("fixedstacking", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void clearEmptyTags(EntityJoinWorldEvent evt) {
            if (evt.getEntity() instanceof ItemEntity) {
                ItemStack is = ((ItemEntity) evt.getEntity()).getItem();
                if (is.hasTag() && is.getTag().isEmpty()) {
                    is.setTag(null);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void clearEmptyTags(PlayerEvent.ItemPickupEvent evt) {
            ItemStack is = evt.getStack();
            if (is.hasTag() && is.getTag().isEmpty()) {
                is.setTag(null);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void clearEmptyTags(PlayerEvent.ItemCraftedEvent evt) {
            ItemStack is = evt.getCrafting();
            if (is.hasTag() && is.getTag().isEmpty()) {
                is.setTag(null);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void clearEmptyTags(PlayerEvent.PlayerLoggedInEvent evt) {
            Player player = evt.getPlayer();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack is = player.getInventory().getItem(i);
                if (is.hasTag() && is.getTag().isEmpty()) {
                    is.setTag(null);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void clearEmptyTags(PlayerInteractEvent evt) {
            ItemStack is = evt.getPlayer().getMainHandItem();
            if (is.hasTag() && is.getTag().isEmpty()) {
                is.setTag(null);
            }
        }
    }

}
