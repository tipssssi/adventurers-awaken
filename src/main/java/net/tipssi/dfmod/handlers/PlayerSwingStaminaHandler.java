package net.tipssi.dfmod.handlers;

import com.alrex.parcool.api.Stamina;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tipssi.dfmod.config.DfModConfig;

@Mod.EventBusSubscriber
public class PlayerSwingStaminaHandler {

    // Register the handler
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new PlayerSwingStaminaHandler());
    }

    private boolean swingProcessed = false;

    @SubscribeEvent
    public void onPlayerSwing(LeftClickEmpty event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }


        if (!player.getCommandSenderWorld().isClientSide()) {
            return; // Ignore the server side
        }


        Stamina stamina = Stamina.get(player);
        if (stamina == null) {
            return;
        }


        if (stamina.isExhausted()) {
            return;
        }


        ItemStack itemInHand = player.getMainHandItem();


        int minStaminaConsumption = 10;
        if (itemInHand.isEmpty() || !isWeapon(itemInHand)) {
            if (stamina.getValue() < minStaminaConsumption) {
                activateExhaustedState(stamina);
                return;
            }

            stamina.consume(minStaminaConsumption);
            checkAndActivateExhausted(stamina);
            swingProcessed = true;
            return;
        }


        double attackSpeed = getAttackSpeed(itemInHand);


        double staminaModifier;
        if (attackSpeed < 1.5) {
            staminaModifier = DfModConfig.STAMINA_MODIFIER_LOW_ATTACK_SPEED.get();
        } else if (attackSpeed < 1.9) {
            staminaModifier = DfModConfig.STAMINA_MODIFIER_NORMAL_ATTACK_SPEED.get();
        } else {
            staminaModifier = DfModConfig.STAMINA_MODIFIER_HIGH_ATTACK_SPEED.get();
        }


        int baseConsumption = DfModConfig.STAMINA_CONSUMPTION_SWING.get();
        int staminaConsumption = (int) (baseConsumption * staminaModifier);


        if (stamina.getValue() < staminaConsumption) {
            activateExhaustedState(stamina);
            return;
        }


        stamina.consume(staminaConsumption);
        checkAndActivateExhausted(stamina);
        swingProcessed = true;
    }

    @SubscribeEvent
    public void onPostTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !event.player.getCommandSenderWorld().isClientSide()) {
            return;
        }

        if (swingProcessed) {
            Stamina stamina = Stamina.get(event.player);
            if (stamina != null) {
                stamina.setValue(stamina.getValue());
            }
            swingProcessed = false;
        }
    }

    private boolean isWeapon(ItemStack itemStack) {
        return itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    private double getAttackSpeed(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 4.0;
        }

        return itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND)
                .get(Attributes.ATTACK_SPEED)
                .stream()
                .findFirst()
                .map(modifier -> modifier.getAmount() + 4.0) // Add base attack speed
                .orElse(4.0); // Default value if no modifier
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
