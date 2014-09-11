package me.nortonw.prestigekit.Listener;

import me.nortonw.prestigekit.Handler.ConfigHandler;
import me.nortonw.prestigekit.Handler.HistoryHandler;
import me.nortonw.prestigekit.Handler.KitHandler;
import me.nortonw.prestigekit.PrestigeKit;
import me.nortonw.prestigekit.PrestigeKit;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;

@SuppressWarnings("unused")
public class PlayerListener
  implements Listener
{
  private static PrestigeKit plugin;
  
  public PlayerListener(PrestigeKit instance)
  {
    plugin = instance;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    Player player = event.getPlayer();
    for (String kits : ConfigHandler.getKitsConfig().getKeys(false)) {
      if ((ConfigHandler.getKitsConfig().getLong(kits + ".usages") == -1L) && 
        (player.hasPermission("kit." + kits))) {
        KitHandler.giveKit(player, player, kits);
      }
    }
    if ((event.getPlayer().hasPermission("kit.version")) && (plugin.UPDATE)) {
      if (!plugin.getConfig().getBoolean("DownloadUpdate"))
      {
        player.sendMessage(ChatColor.DARK_PURPLE + "There is an update available for KitPlugin.");
        player.sendMessage(ChatColor.DARK_PURPLE + "Version: " + ChatColor.GRAY + plugin.NAME);
        player.sendMessage(ChatColor.DARK_PURPLE + "Visit " + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-mods/kitplugin/" + ChatColor.DARK_PURPLE + " if you would like to download it.");
        player.sendMessage(ChatColor.DARK_PURPLE + "If you want to use the auto-updater in the future, check the config.yml of KitPlugin.");
      }
      else
      {
        player.sendMessage(ChatColor.DARK_PURPLE + "KitPlugin was automatically updated to " + ChatColor.GRAY + plugin.NAME);
        player.sendMessage(ChatColor.DARK_PURPLE + "Don't forget to restart your server if you want to use the new version.");
      }
    }
    HistoryHandler.createPlayerfile(player.getName());
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    Player player = event.getEntity();
    for (String kits : ConfigHandler.getKitsConfig().getKeys(false)) {
      if ((ConfigHandler.getKitsConfig().getLong(kits + ".usages") == -2L) && 
        (player.hasPermission("kit." + kits))) {
        HistoryHandler.setHistory(player.getName(), kits, HistoryHandler.getCooldown(player.getName(), kits), Integer.valueOf(-2));
      }
    }
  }
}
