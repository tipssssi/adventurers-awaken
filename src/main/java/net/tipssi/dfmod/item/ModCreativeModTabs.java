package net.tipssi.dfmod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.tipssi.dfmod.core.DfMod;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DfMod.MOD_ID);

public static final RegistryObject<CreativeModeTab> DfMod_Tab = CREATIVE_MODE_TABS.register("df_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.LEVCHIK.get()))
                .title(Component.translatable("creativetab.df_tab"))
                .displayItems((itemDisplayParameters, output) -> {
                    output.accept(ModItems.LEVCHIK.get());
                })
                .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
