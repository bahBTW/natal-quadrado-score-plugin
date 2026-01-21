package br.dev.bahbtw.contadorPontos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                showPlayerInfo(player);
            } else {
                sender.sendMessage("§cEste comando só pode ser usado por jogadores!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("multipliers") && sender.hasPermission("contadorpontos.admin")) {
            showAllMultipliers(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("setmultiplier") && sender.hasPermission("contadorpontos.admin")) {
            if (args.length >= 3) {
                try {
                    String teamName = args[1];
                    double multiplier = Double.parseDouble(args[2]);

                    ContadorPontos.getInstance().getMultiplierManager().setTeamMultiplier(teamName, multiplier);
                    sender.sendMessage("§aMultiplicador do time " + teamName + " definido para: " + multiplier + "x");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cMultiplicador inválido! Use um número (ex: 1.5)");
                }
                return true;
            }
        }

        sender.sendMessage("§cUso: /contadorpontos [multipliers|setmultiplier]");
        return true;
    }

    private void showPlayerInfo(Player player) {
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();
        org.bukkit.scoreboard.Team team = multiplierManager.getPlayerTeam(player);

        player.sendMessage("§6=== §eContador de Pontos §6===");

        if (team != null) {
            String teamName = team.getName();
            double multiplier = multiplierManager.getPlayerMultiplier(player);
            String multiplierText = multiplierManager.getFormattedMultiplier(player);

            player.sendMessage("§7Time: §f" + teamName);
            player.sendMessage("§7Multiplicador: " + multiplierText);

            if (multiplier > 1.0) {
                player.sendMessage("§aParabéns! Seu time tem bônus de " + multiplier + "× nos pontos!");
            }
        } else {
            player.sendMessage("§7Time: §cNenhum time atribuído");
            player.sendMessage("§7Multiplicador: §7(1.0×)");
            player.sendMessage("§eEntre em um time para receber bônus!");
        }

        player.sendMessage("§6========================");
    }

    private void showAllMultipliers(CommandSender sender) {
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();
        Map<String, Double> multipliers = multiplierManager.getTeamMultipliers();

        sender.sendMessage("§6=== §eMultiplicadores dos Times §6===");

        for (Map.Entry<String, Double> entry : multipliers.entrySet()) {
            String teamName = entry.getKey();
            double multiplier = entry.getValue();

            if (multiplier == 1.5) {
                sender.sendMessage("§a" + teamName + ": §e" + multiplier + "× §a(META BATIDA!)");
            } else {
                sender.sendMessage("§7" + teamName + ": §f" + multiplier + "×");
            }
        }

        sender.sendMessage("§6================================");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("contadorpontos.admin")) {
                suggestions.add("multipliers");
                suggestions.add("setmultiplier");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setmultiplier")) {
            if (sender.hasPermission("contadorpontos.admin")) {
                suggestions.add("tn_renas");
                suggestions.add("tn_pinguins");
                suggestions.add("tn_duendes");
                suggestions.add("tn_ursos");
            }
        }

        return suggestions;
    }
}