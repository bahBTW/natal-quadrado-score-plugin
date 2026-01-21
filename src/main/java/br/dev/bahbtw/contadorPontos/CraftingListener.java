package br.dev.bahbtw.contadorPontos;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack result = event.getCurrentItem();

        if (result == null || result.getType() == Material.AIR) return;

        Material itemType = result.getType();

        // Verificar se é um item que monitoramos
        if (!isMonitoredItem(itemType)) {
            return;
        }

        // VERIFICAÇÃO: Clique de número (1-9) com slot ocupado
        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarSlot = event.getHotbarButton();
            ItemStack hotbarItem = player.getInventory().getItem(hotbarSlot);

            if (hotbarItem != null && hotbarItem.getType() != Material.AIR) {
                return; // Slot ocupado - não dar pontos
            }
        }

        // VERIFICAÇÃO: Shift+click com inventário cheio
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            if (!canAddItemToInventory(player, result)) {
                return; // Não pode adicionar item - não dar pontos
            }
        }

        // Tudo ok, dar pontos
        givePointsForCraft(player, itemType);
    }

    private void givePointsForCraft(Player player, Material itemType) {
        PointsManager pointsManager = ContadorPontos.getInstance().getPointsManager();
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();

        // Obter pontos base
        int basePoints = getBasePointsForItem(itemType);

        // Aplicar multiplicador e enviar mensagem
        double multiplier = multiplierManager.getPlayerMultiplier(player);
        String multiplierText = multiplierManager.getFormattedMultiplier(player);
        String teamName = getPlayerTeamName(player);

        switch (itemType) {
            case CAKE:
                pointsManager.addPoints(player, 35);
                player.sendMessage("§a+35 pontos por craftar bolo! " + multiplierText);
                if (multiplier != 1.0) {
                    player.sendMessage("§eTime " + teamName + ": " + basePoints + " × " + multiplier + " = " + (int)(basePoints * multiplier) + " pontos");
                }
                break;
            case COOKIE:
                pointsManager.addPoints(player, 8);
                player.sendMessage("§a+8 pontos por craftar cookie! " + multiplierText);
                break;
            case PUMPKIN_PIE:
                pointsManager.addPoints(player, 15);
                player.sendMessage("§a+15 pontos por craftar pumpkin pie! " + multiplierText);
                break;
            case RED_CANDLE:
                pointsManager.addPoints(player, 20);
                player.sendMessage("§a+20 pontos por craftar vela vermelha! " + multiplierText);
                break;
            case LANTERN:
                pointsManager.addPoints(player, 8);
                player.sendMessage("§a+8 pontos por craftar lanterna! " + multiplierText);
                break;
            case NOTE_BLOCK:
                pointsManager.addPoints(player, 5);
                player.sendMessage("§a+5 pontos por craftar bloco musical! " + multiplierText);
                break;
            case FIREWORK_ROCKET:
                pointsManager.addPoints(player, 5);
                player.sendMessage("§a+6 pontos por craftar foguete! " + multiplierText);
                break;
        }
    }

    private int getBasePointsForItem(Material material) {
        switch (material) {
            case CAKE: return 35;
            case COOKIE: return 8;
            case PUMPKIN_PIE: return 15;
            case RED_CANDLE: return 20;
            case LANTERN: return 8;
            case NOTE_BLOCK: return 5;
            case FIREWORK_ROCKET: return 5;
            default: return 0;
        }
    }

    private String getPlayerTeamName(Player player) {
        MultiplierManager multiplierManager = ContadorPontos.getInstance().getMultiplierManager();
        org.bukkit.scoreboard.Team team = multiplierManager.getPlayerTeam(player);
        return team != null ? team.getName() : "Sem time";
    }

    private boolean canAddItemToInventory(Player player, ItemStack itemToAdd) {
        if (itemToAdd == null) return false;

        Material itemType = itemToAdd.getType();
        int maxStackSize = itemToAdd.getMaxStackSize();

        // Verificar slots com o mesmo item que não estão cheios
        for (ItemStack slot : player.getInventory().getStorageContents()) {
            if (slot != null && slot.getType() == itemType && slot.getAmount() < maxStackSize) {
                return true;
            }
        }

        // Verificar slots vazios
        for (ItemStack slot : player.getInventory().getStorageContents()) {
            if (slot == null || slot.getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }

    private boolean isMonitoredItem(Material material) {
        switch (material) {
            case CAKE:
            case COOKIE:
            case PUMPKIN_PIE:
            case RED_CANDLE:
            case LANTERN:
            case NOTE_BLOCK:
            case FIREWORK_ROCKET:
                return true;
            default:
                return false;
        }
    }
}