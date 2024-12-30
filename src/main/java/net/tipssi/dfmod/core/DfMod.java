package net.tipssi.dfmod.core;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tipssi.dfmod.handlers.BlockBreakStaminaHandler;
import net.tipssi.dfmod.handlers.PlayerAttackStaminaHandler;
import net.tipssi.dfmod.handlers.PlayerDamageHandler;
import net.tipssi.dfmod.handlers.PlayerSwingStaminaHandler;
import net.tipssi.dfmod.config.DfModConfig;
import net.tipssi.dfmod.item.ModCreativeModTabs;
import net.tipssi.dfmod.item.ModItems;
import org.slf4j.Logger;

// Основной класс мода
@Mod(DfMod.MOD_ID)
public class DfMod {
    public static final String MOD_ID = "adventurers_awaken";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DfMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        ModLoadingContext.get().registerConfig(Type.COMMON, DfModConfig.CONFIG, "dfmod-common.toml");


        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);


        PlayerSwingStaminaHandler.register();
        PlayerAttackStaminaHandler.register();
        BlockBreakStaminaHandler.register();
        PlayerDamageHandler.register();


        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("DfMod: Client settings configuration start");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.LEVCHIK);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("DfMod: Server starting...");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("DfMod: Client settings configuration end");
        }
    }
}
