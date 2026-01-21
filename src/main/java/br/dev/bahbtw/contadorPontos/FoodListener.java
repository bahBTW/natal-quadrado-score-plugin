package br.dev.bahbtw.contadorPontos;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;

public class FoodListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material foodType = event.getItem().getType();

        // Verificar se é um alimento que monitoramos
        if (!isMonitoredFood(foodType)) {
            return;
        }

        // Dar pontos para o alimento
        givePointsForFood(player, foodType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCakeEat(PlayerInteractEvent event) {
        // Verificar se é um clique direito em um bloco
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Verificar se foi com a mão principal
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        // Verificar se o bloco é um bolo
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CAKE) {
            return;
        }

        Player player = event.getPlayer();

        // VERIFICAÇÃO IMPORTANTE: Só dar pontos se o jogador puder realmente comer
        // (não está com fome cheia e tem permissão de interagir)

        // Verificar se o jogador está com fome cheia
        if (player.getFoodLevel() >= 20) {
            // Fome cheia, não pode comer, não dar pontos
            return;
        }

        // Verificar se o jogador está em modo sobrevivência ou aventura (pode comer)
        String gameMode = player.getGameMode().toString();
        if (!gameMode.contains("SURVIVAL") && !gameMode.contains("ADVENTURE")) {
            return;
        }

        // Adicionar pontos por comer bolo
        givePointsForCake(player);
    }

    /**
     * Dar pontos por comer alimentos normais
     */
    private void givePointsForFood(Player player, Material foodType) {
        PointsManager pointsManager = ContadorPontos.getInstance().getPointsManager();
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();

        // Obter pontos base e mensagem
        int basePoints = 0;
        String foodName = "";
        String action = "comer";

        switch (foodType) {
            case PUMPKIN_PIE:
                basePoints = 2;
                foodName = "pumpkin pie";
                break;
            case COOKIE:
                basePoints = 1;
                foodName = "cookie";
                break;
            default:
                return; // Não é um alimento monitorado
        }

        // Aplicar multiplicador
        double multiplier = multiplierManager.getPlayerMultiplier(player);
        String multiplierText = multiplierManager.getFormattedMultiplier(player);
        String teamName = getPlayerTeamName(player);

        // Dar pontos
        pointsManager.addPoints(player, basePoints);

        // Enviar mensagem
        if (multiplier != 1.0) {
            int finalPoints = (int) Math.round(basePoints * multiplier);
            player.sendMessage("§a+" + basePoints + " pontos por " + action + " " + foodName + "! " + multiplierText);
            player.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + finalPoints + " pontos");
        } else {
            player.sendMessage("§a+" + basePoints + " pontos por " + action + " " + foodName + "!");
        }
    }

    /**
     * Dar pontos por comer bolo (bloco especial)
     */
    private void givePointsForCake(Player player) {
        PointsManager pointsManager = ContadorPontos.getInstance().getPointsManager();
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();

        int basePoints = 5;
        double multiplier = multiplierManager.getPlayerMultiplier(player);
        String multiplierText = multiplierManager.getFormattedMultiplier(player);
        String teamName = getPlayerTeamName(player);

        // Dar pontos
        pointsManager.addPoints(player, basePoints);

        // Enviar mensagem
        if (multiplier != 1.0) {
            int finalPoints = (int) Math.round(basePoints * multiplier);
            player.sendMessage("§a+" + basePoints + " pontos por comer bolo! " + multiplierText);
            player.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + finalPoints + " pontos");
        } else {
            player.sendMessage("§a+" + basePoints + " pontos por comer bolo!");
        }
    }

    /**
     * Verifica se um alimento está sendo monitorado
     */
    private boolean isMonitoredFood(Material material) {
        switch (material) {
            case PUMPKIN_PIE:
            case COOKIE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtém o nome do time do jogador
     */
    private String getPlayerTeamName(Player player) {
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();
        org.bukkit.scoreboard.Team team = multiplierManager.getPlayerTeam(player);
        return team != null ? team.getName() : "Sem time";
    }
}