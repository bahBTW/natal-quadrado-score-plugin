package br.dev.bahbtw.contadorPontos;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class MultiplierManager {

    private final ContadorPontos plugin;
    private final Map<String, Double> teamMultipliers;

    public MultiplierManager(ContadorPontos plugin) {
        this.plugin = plugin;
        this.teamMultipliers = new HashMap<>();

        // Configurar multiplicadores dos times
        setupTeamMultipliers();
    }

    private void setupTeamMultipliers() {
        teamMultipliers.put("tn_renas", 3.0);
        teamMultipliers.put("tn_pinguins", 3.0);
        teamMultipliers.put("tn_duendes", 3.0);
        teamMultipliers.put("tn_ursos", 3.0);

        plugin.getLogger().info("Multiplicadores configurados:");
        for (Map.Entry<String, Double> entry : teamMultipliers.entrySet()) {
            plugin.getLogger().info("  " + entry.getKey() + ": " + entry.getValue() + "x");
        }
    }

    /**
     * Obtém o multiplicador de pontos de um jogador baseado no time
     */
    public double getPlayerMultiplier(Player player) {
        Team team = getPlayerTeam(player);

        if (team != null) {
            String teamName = team.getName().toLowerCase(); // Converter para minúsculas
            Double multiplier = teamMultipliers.get(teamName);

            if (multiplier != null) {
                return multiplier;
            }
        }

        // Multiplicador padrão se não estiver em time ou time não registrado
        return 1.0;
    }

    /**
     * Obtém o multiplicador formatado para exibição
     */
    public String getFormattedMultiplier(Player player) {
        double multiplier = getPlayerMultiplier(player);

        if (multiplier == 1.0) {
            return "§7(1.0×)";
        } else if (multiplier > 1.0) {
            // Formatar para 1 casa decimal
            String formatted = String.format("%.1f", multiplier).replace(",", ".");
            return "§a(" + formatted + "×)";
        } else {
            String formatted = String.format("%.1f", multiplier).replace(",", ".");
            return "§c(" + formatted + "×)";
        }
    }

    /**
     * Calcula os pontos com o multiplicador aplicado
     * CORRIGIDO: Garante que retorna valor inteiro
     */
    public int calculatePointsWithMultiplier(Player player, int basePoints) {
        double multiplier = getPlayerMultiplier(player);

        // Calcular pontos com multiplicador
        double multipliedPoints = basePoints * multiplier;

        // Arredondar para inteiro
        int finalPoints = (int) Math.round(multipliedPoints);

        // Garantir pelo menos 1 ponto se basePoints > 0
        if (basePoints > 0 && finalPoints <= 0) {
            finalPoints = 1;
        }

        // Log para debug (desative em produção se quiser)
        if (multiplier != 1.0) {
            plugin.getLogger().info("DEBUG: " + player.getName() +
                    " - " + basePoints + " × " + multiplier +
                    " = " + finalPoints + " pontos");
        }

        return finalPoints;
    }

    /**
     * Obtém o time do jogador
     */
    public Team getPlayerTeam(Player player) {
        try {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null) {
                scoreboard = player.getServer().getScoreboardManager().getMainScoreboard();
            }

            return scoreboard.getEntryTeam(player.getName());
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao obter time do jogador " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica se um jogador está em um time específico
     */
    public boolean isPlayerInTeam(Player player, String teamName) {
        Team team = getPlayerTeam(player);
        return team != null && team.getName().equalsIgnoreCase(teamName);
    }

    /**
     * Retorna todos os multiplicadores configurados
     */
    public Map<String, Double> getTeamMultipliers() {
        return new HashMap<>(teamMultipliers);
    }

    /**
     * Atualiza o multiplicador de um time (pode ser usado por admins)
     */
    public void setTeamMultiplier(String teamName, double multiplier) {
        teamMultipliers.put(teamName.toLowerCase(), multiplier);
        plugin.getLogger().info("Multiplicador do time " + teamName + " atualizado para: " + multiplier + "x");
    }
}