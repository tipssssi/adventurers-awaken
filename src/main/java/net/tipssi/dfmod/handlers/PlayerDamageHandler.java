package net.tipssi.dfmod.handlers;

import com.alrex.parcool.api.Stamina;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tipssi.dfmod.config.DfModConfig;

@Mod.EventBusSubscriber
public class PlayerDamageHandler {

    // Метод регистрации
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new PlayerDamageHandler());
    }

    @SubscribeEvent
    public void onPlayerDamage(LivingHurtEvent event) {
        // Проверяем, является ли атакующим игроком
        if (!(event.getSource().getEntity() instanceof Player)) {
            return; // Игнорируем, если атакующий не игрок
        }

        Player player = (Player) event.getSource().getEntity();

        // Проверяем, выполняется ли код на серверной стороне
        if (player.getCommandSenderWorld().isClientSide()) {
            return; // Игнорируем клиентскую сторону
        }

        // Получаем объект стамины через API ParCool
        Stamina stamina = Stamina.get(player);
        if (stamina == null) {
            System.out.println("Stamina object is not initialized for player: " + player.getName().getString());
            return; // Игнорируем, если стамина не инициализирована
        }

        // Проверяем, находится ли стамина в состоянии Exhausted
        if (stamina.isExhausted()) {
            // Уменьшаем урон на коэффициент из конфигурации
            float originalDamage = event.getAmount();
            float reductionMultiplier = DfModConfig.EXHAUSTED_DAMAGE_REDUCTION.get().floatValue(); // Значение из конфига
            float reducedDamage = originalDamage * reductionMultiplier;
            event.setAmount(reducedDamage);

            // Логирование уменьшения урона
            System.out.println("Reduced damage for player " + player.getName().getString() + " from " + originalDamage + " to " + reducedDamage + " due to exhausted stamina.");
        }
    }
}
