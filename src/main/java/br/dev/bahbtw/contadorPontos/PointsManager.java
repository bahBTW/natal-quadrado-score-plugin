package br.dev.bahbtw.contadorPontos;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PointsManager {

    private final ContadorPontos plugin;

    public PointsManager(ContadorPontos plugin) {
        this.plugin = plugin;
    }

    /**
     * Adiciona pontos COM multiplicador aplicado
     */
    public void addPoints(Player player, int baseAmount) {
        if (baseAmount <= 0) return;

        // Aplicar multiplicador do time
        MultiplierManager multiplierManager = plugin.getMultiplierManager();
        int finalAmount = multiplierManager.calculatePointsWithMultiplier(player, baseAmount);

        if (finalAmount <= 0) return;

        // IMPORTANTE: Passar finalAmount para o comando, não baseAmount
        String command = "addpoints " + player.getName() + " " + finalAmount;
        executeCommand(command);

        // Log para console
        double multiplier = multiplierManager.getPlayerMultiplier(player);
        plugin.getLogger().info(player.getName() + " ganhou " + finalAmount + " pontos (base: " + baseAmount + " × " + multiplier + ")");
    }

    /**
     * Remove pontos COM multiplicador aplicado
     */
    public void removePoints(Player player, int baseAmount) {
        if (baseAmount <= 0) return;

        // Aplicar multiplicador do time (também para remoção)
        MultiplierManager multiplierManager = plugin.getMultiplierManager();
        int finalAmount = multiplierManager.calculatePointsWithMultiplier(player, baseAmount);

        // IMPORTANTE: Passar finalAmount para o comando, não baseAmount
        String command = "removepoints " + player.getName() + " " + finalAmount;
        executeCommand(command);

        // Log para console
        double multiplier = multiplierManager.getPlayerMultiplier(player);
        plugin.getLogger().info(player.getName() + " perdeu " + finalAmount + " pontos (base: " + baseAmount + " × " + multiplier + ")");
    }

    /**
     * Executa um comando como console
     */
    private void executeCommand(String command) {
        // Executa o comando como console
        boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        if (!success) {
            plugin.getLogger().warning("Falha ao executar comando: " + command);
        } else {
            plugin.getLogger().info("Comando executado: " + command);
        }
    }
}