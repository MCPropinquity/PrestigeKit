package me.nortonw.prestigekit;

import me.nortonw.prestigekit.Handler.CommandHandler;
import me.nortonw.prestigekit.Handler.ConfigHandler;
import me.nortonw.prestigekit.Handler.HistoryHandler;
import me.nortonw.prestigekit.Handler.KitHandler;
import me.nortonw.prestigekit.Listener.PlayerListener;
import me.nortonw.prestigekit.Util.ExactNames;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings("unused")
public class PrestigeKit
  extends JavaPlugin
{
  public Economy economy = null;
  public Logger logger = Logger.getLogger("Minecraft.PrestigeKit");
  private PluginDescriptionFile pdfFile;
  public String path = null;
  public boolean UPDATE = false;
  public String NAME = "";
  private ConfigHandler ch;
  private KitHandler kh;
  private PlayerListener pl;
  private HistoryHandler hl;
  private ExactNames en;
  
  public void onEnable()
  {
    this.ch = new ConfigHandler(this);
    this.kh = new KitHandler(this);
    this.pl = new PlayerListener(this);
    this.hl = new HistoryHandler(this);
    this.en = new ExactNames(this);
    setupVault();
    registerCommands();
    this.pdfFile = getDescription();
    this.logger.info("[KitPlugin] version " + this.pdfFile.getVersion() + " enabled!");
  }
  
  public void onDisable()
  {
    this.logger.info("[KitPlugin] version " + this.pdfFile.getVersion() + " disabled!");
  }
  
  private boolean setupVault()
  {
    Plugin vault = getServer().getPluginManager().getPlugin("Vault");
    if ((vault != null & vault instanceof Vault))
    {
      this.logger.info("[KitPlugin] Found Vault. Checking for economy plugin.");
      setupEconomy();
    }
    else
    {
      this.logger.info("[KitPlugin] Vault was NOT found! Running without economy!");
    }
    return vault != null;
  }
  
  private boolean setupEconomy()
  {
    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
    if (economyProvider != null)
    {
      this.economy = ((Economy)economyProvider.getProvider());
      this.logger.info("[KitPlugin] Found economy plugin. Using that.");
    }
    else
    {
      this.logger.info("[KitPlugin] No economy plugin found. Running without economy support.");
    }
    return this.economy != null;
  }
  
  private void registerCommands()
  {
    getCommand("kit").setExecutor(new CommandHandler(this));
  }
  
  public String timeUntil(Long sec)
  {
    if (sec.longValue() < 120L)
    {
      Integer buf = Integer.valueOf(Math.round((float)sec.longValue()));
      return buf + " second" + (buf.intValue() == 1 ? "" : "s");
    }
    if (sec.longValue() < 7200L)
    {
      Integer buf = Integer.valueOf(Math.round((float)(sec.longValue() / 60L)));
      return buf + " minute" + (buf.intValue() == 1 ? "" : "s");
    }
    if (sec.longValue() < 172800L)
    {
      Integer buf = Integer.valueOf(Math.round((float)(sec.longValue() / 3600L)));
      return buf + " hour" + (buf.intValue() == 1 ? "" : "s");
    }
    Integer buf = Integer.valueOf(Math.round((float)(sec.longValue() / 86400L)));
    return buf + " day" + (buf.intValue() == 1 ? "" : "s");
  }
  
  public void respond(Player player, String message)
  {
    if (player == null) {
      System.out.println(message);
    } else {
      player.sendMessage(message);
    }
  }
  
}
