package net.tipssi.dfmod.handlers;

import com.alrex.parcool.api.Stamina;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tipssi.dfmod.config.DfModConfig;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class BlockBreakStaminaHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new BlockBreakStaminaHandler());
    }

    private boolean blockBreakProcessed = false;

    @SubscribeEvent
    public void onBlockBreak(BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null || !player.getCommandSenderWorld().isClientSide()) {
            return;
        }

        Stamina stamina = Stamina.get(player);
        if (stamina == null) {
            return;
        }

        if (stamina.isExhausted()) {
            event.setNewSpeed(0);
            return;
        }

        int staminaConsumption = DfModConfig.STAMINA_CONSUMPTION_BLOCK.get();
        if (stamina.getValue() < staminaConsumption) {
            activateExhaustedState(stamina);
            event.setNewSpeed(0);
            return;
        }

        stamina.consume(staminaConsumption);
        checkAndActivateExhausted(stamina);
        blockBreakProcessed = true;
    }

    @SubscribeEvent
    public void onPostTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !event.player.getCommandSenderWorld().isClientSide()) {
            return;
        }

        if (blockBreakProcessed) {
            Stamina stamina = Stamina.get(event.player);
            if (stamina != null) {
                stamina.setValue(stamina.getValue());
            }
            blockBreakProcessed = false;
        }
    }

    private void activateExhaustedState(Stamina stamina) {
        stamina.setValue(0);
        stamina.consume(0);
    }

    private void checkAndActivateExhausted(Stamina stamina) {
        if (stamina.getValue() <= 0) {
            activateExhaustedState(stamina);
        }
    }
}
