package br.dev.bahbtw.contadorPontos;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.isCancelled()) return;

        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) return;

        // Matar mob hostil - pontos POSITIVOS com multiplicador
        if (isHostileMob(entity)) {
            givePointsForKill(killer, entity, 10, true); // +10 pontos (com multiplicador)
        }
        // Matar mob pacífico - pontos NEGATIVOS com multiplicador
        else if (isPeacefulMob(entity)) {
            givePointsForKill(killer, entity, 250, false); // -250 pontos (com multiplicador)
        }
        // Matar jogador - pontos NEGATIVOS com multiplicador
        else if (entity instanceof Player) {
            Player victim = (Player) entity;
            givePointsForKill(killer, entity, 1500, false); // -1500 pontos (com multiplicador)
        }
    }

    /**
     * Dar ou remover pontos por matar uma entidade
     * @param killer Jogador que matou
     * @param entity Entidade morta
     * @param basePoints Pontos base (sempre positivo)
     * @param isPositive true para adicionar pontos, false para remover
     */
    private void givePointsForKill(Player killer, LivingEntity entity, int basePoints, boolean isPositive) {
        PointsManager pointsManager = ContadorPontos.getInstance().getPointsManager();
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();

        String entityName = getEntityName(entity);
        double multiplier = multiplierManager.getPlayerMultiplier(killer);
        String multiplierText = multiplierManager.getFormattedMultiplier(killer);
        String teamName = getPlayerTeamName(killer);

        // Aplicar multiplicador (tanto para ganhos quanto perdas)
        int finalPoints = multiplierManager.calculatePointsWithMultiplier(killer, basePoints);

        if (isPositive) {
            // Ganhar pontos (mob hostil)
            pointsManager.addPoints(killer, basePoints);

            if (multiplier != 1.0) {
                killer.sendMessage("§a+" + basePoints + " pontos por matar " + entityName + "! " + multiplierText);
                killer.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + finalPoints + " pontos");
            } else {
                killer.sendMessage("§a+" + basePoints + " pontos por matar " + entityName + "!");
            }
        } else {
            // Perder pontos (mob pacífico ou jogador)
            // Para penalidades, também aplicamos o multiplicador
            // Se quiser penalidades fixas, use: int finalPoints = basePoints;
            pointsManager.removePoints(killer, basePoints);

            if (multiplier != 1.0) {
                killer.sendMessage("§c-" + basePoints + " pontos por matar " + entityName + "! " + multiplierText);
                killer.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + finalPoints + " pontos perdidos");
            } else {
                killer.sendMessage("§c-" + basePoints + " pontos por matar " + entityName + "!");
            }
        }
    }

    private boolean isHostileMob(Entity entity) {
        return entity instanceof Monster ||
                entity instanceof Slime ||
                entity instanceof Phantom ||
                entity instanceof Ghast ||
                entity instanceof Shulker ||
                entity instanceof Hoglin ||
                entity instanceof PiglinBrute ||
                entity instanceof Blaze ||
                entity instanceof Creeper ||
                entity instanceof Enderman ||
                entity instanceof Endermite ||
                entity instanceof Evoker ||
                entity instanceof Guardian ||
                entity instanceof ElderGuardian ||
                entity instanceof Husk ||
                entity instanceof MagmaCube ||
                entity instanceof PigZombie ||
                entity instanceof Pillager ||
                entity instanceof Ravager ||
                entity instanceof Silverfish ||
                entity instanceof Skeleton ||
                entity instanceof Stray ||
                entity instanceof Vex ||
                entity instanceof Vindicator ||
                entity instanceof Witch ||
                entity instanceof WitherSkeleton ||
                entity instanceof Zoglin ||
                entity instanceof Zombie ||
                entity instanceof ZombieVillager ||
                entity instanceof Spider ||
                entity instanceof CaveSpider ||
                entity instanceof Drowned ||
                entity instanceof Warden;
    }

    private boolean isPeacefulMob(Entity entity) {
        return (entity instanceof Animals && !(entity instanceof Monster)) ||
                entity instanceof Squid ||
                entity instanceof GlowSquid ||
                entity instanceof Dolphin ||
                entity instanceof Bat ||
                entity instanceof Villager ||
                entity instanceof IronGolem ||
                entity instanceof Snowman ||
                entity instanceof Allay ||
                entity instanceof Frog ||
                entity instanceof Axolotl ||
                entity instanceof Tadpole ||
                entity instanceof Camel ||
                entity instanceof Sniffer ||
                entity instanceof Bee ||
                entity instanceof Cat ||
                entity instanceof Chicken ||
                entity instanceof Cow ||
                entity instanceof Donkey ||
                entity instanceof Fox ||
                entity instanceof Horse ||
                entity instanceof Llama ||
                entity instanceof Mule ||
                entity instanceof Ocelot ||
                entity instanceof Panda ||
                entity instanceof Parrot ||
                entity instanceof Pig ||
                entity instanceof PolarBear ||
                entity instanceof Rabbit ||
                entity instanceof Sheep ||
                entity instanceof SkeletonHorse ||
                entity instanceof Strider ||
                entity instanceof TraderLlama ||
                entity instanceof TropicalFish ||
                entity instanceof Turtle ||
                entity instanceof Wolf ||
                entity instanceof ZombieHorse;
    }

    private String getEntityName(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getName();
        } else {
            String name = entity.getType().name().toLowerCase();
            return name.replace("_", " ");
        }
    }

    private String getPlayerTeamName(Player player) {
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();
        org.bukkit.scoreboard.Team team = multiplierManager.getPlayerTeam(player);
        return team != null ? team.getName() : "Sem time";
    }
}