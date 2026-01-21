package br.dev.bahbtw.contadorPontos;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Base penalty: 100 pontos
        applyDeathPoints(player, 100, false); // false = remover pontos
    }

    /**
     * Método genérico para aplicar pontos (ganho ou perda) com multiplicador
     */
    private void applyDeathPoints(Player player, int basePoints, boolean addPoints) {
        PointsManager pointsManager = ContadorPontos.getInstance().getPointsManager();
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();

        double multiplier = multiplierManager.getPlayerMultiplier(player);
        String multiplierText = multiplierManager.getFormattedMultiplier(player);
        String teamName = getPlayerTeamName(player);

        // Aplicar multiplicador
        int finalPoints = (int) Math.round(basePoints * multiplier);

        if (addPoints) {
            // Ganhar pontos (não usado para morte, mas mantido para consistência)
            pointsManager.addPoints(player, basePoints);

            if (multiplier != 1.0) {
                player.sendMessage("§a+" + basePoints + " pontos! " + multiplierText);
                player.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + finalPoints + " pontos");
            } else {
                player.sendMessage("§a+" + basePoints + " pontos!");
            }
        } else {
            // Perder pontos (caso da morte)
            pointsManager.removePoints(player, basePoints);

            if (multiplier != 1.0) {
                player.sendMessage("§c-" + basePoints + " pontos por morrer! " + multiplierText);
                player.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + finalPoints + " pontos perdidos");
            } else {
                player.sendMessage("§c-" + basePoints + " pontos por morrer!");
            }
        }
    }

    private String getPlayerTeamName(Player player) {
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();
        org.bukkit.scoreboard.Team team = multiplierManager.getPlayerTeam(player);
        return team != null ? team.getName() : "Sem time";
    }
}