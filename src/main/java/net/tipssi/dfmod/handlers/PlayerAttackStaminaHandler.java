package net.tipssi.dfmod.handlers;

import com.alrex.parcool.api.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tipssi.dfmod.config.DfModConfig;

@Mod.EventBusSubscriber
public class PlayerAttackStaminaHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new PlayerAttackStaminaHandler());
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }


        if (!player.getCommandSenderWorld().isClientSide()) {
            return;
        }


        Stamina stamina = Stamina.get(player);
        if (stamina == null) {
            return;
        }


        if (stamina.isExhausted()) {
            return;
        }


        ItemStack itemInHand = player.getMainHandItem();


        if (itemInHand.isEmpty() || !isWeapon(itemInHand)) {
            int fixedConsumption = DfModConfig.STAMINA_CONSUMPTION_HAND.get();
            if (stamina.getValue() < fixedConsumption) {
                updateStamina(player, stamina, 0);
                return;
            }

            updateStamina(player, stamina, -fixedConsumption);
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

        int baseConsumption = DfModConfig.STAMINA_CONSUMPTION_ATTACK.get();
        int staminaConsumption = (int) (baseConsumption * staminaModifier);


        if (stamina.getValue() < staminaConsumption) {
            updateStamina(player, stamina, 0);
            return;
        }


        updateStamina(player, stamina, -staminaConsumption);
    }

    @SubscribeEvent
    public void onPostTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END || !event.player.getCommandSenderWorld().isClientSide()) {
            return;
        }

        Player player = event.player;


        Stamina stamina = Stamina.get(player);
        if (stamina == null) {
            return;
        }


        if (stamina.getValue() < 0) {
            stamina.setValue(0);
        }


        updateStaminaHUD(player, stamina);
    }

    private void updateStaminaHUD(Player player, Stamina stamina) {

        if (Minecraft.getInstance().player != player) {
            return;
        }


        int currentStamina = stamina.getValue();
        stamina.setValue(currentStamina);
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
                .map(modifier -> modifier.getAmount() + 4.0)
                .orElse(4.0);
    }

    private void updateStamina(Player player, Stamina stamina, int change) {

        if (player != Minecraft.getInstance().player) {
            return;
        }

        if (change < 0) {
            stamina.consume(-change);
        } else {
            stamina.setValue(change);
        }
    }
}
