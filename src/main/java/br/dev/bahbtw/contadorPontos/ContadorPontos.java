package br.dev.bahbtw.contadorPontos;

import org.bukkit.plugin.java.JavaPlugin;

public class ContadorPontos extends JavaPlugin {

    private PointsManager pointsManager;
    private MultiplierManager multiplierManager;
    private static ContadorPontos instance;

    @Override
    public void onEnable() {
        instance = this;

        // Inicializar gerenciadores
        pointsManager = new PointsManager(this);
        multiplierManager = new MultiplierManager(this);

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);
        getServer().getPluginManager().registerEvents(new FoodListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        // Registrar comando
        getCommand("contadorpontos").setExecutor(new PointsCommand());

        getLogger().info("ContadorPontos ativado com sucesso!");
        getLogger().info("Sistema de multiplicadores ativado!");
        getLogger().info("Times: tn_renas (1.5x), tn_pinguins (1.0x), tn_duendes (1.0x), tn_ursos (1.0x)");
    }

    @Override
    public void onDisable() {
        getLogger().info("ContadorPontos desativado!");
    }

    public PointsManager getPointsManager() {
        return pointsManager;
    }

    public MultiplierManager getMultiplierManager() {
        return multiplierManager;
    }

    public static ContadorPontos getInstance() {
        return instance;
    }
}