package me.nortonw.prestigekit.Handler;

import me.nortonw.prestigekit.*;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class HistoryHandler
{
  private static File historyfile = null;
  private static FileConfiguration history = null;
  private static PrestigeKit plugin;
  
  public HistoryHandler(PrestigeKit instance)
  {
    plugin = instance;
  }
  
  public static void createPlayerfile(String player)
  {
    historyfile = new File(plugin.getDataFolder(), "/playerdb/" + player.toLowerCase() + ".yml");
    if (!historyfile.exists())
    {
      history = YamlConfiguration.loadConfiguration(historyfile);
      try
      {
        history.save(historyfile);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  private static void loadPlayerfile(String player)
  {
    historyfile = new File(plugin.getDataFolder(), "/playerdb/" + player.toLowerCase() + ".yml");
    history = YamlConfiguration.loadConfiguration(historyfile);
  }
  
  public static Long getCooldown(String player, String kitname)
  {
    loadPlayerfile(player.toLowerCase());
    return Long.valueOf(history.getLong(kitname + ".cooldown"));
  }
  
  public static Integer getUsages(String player, String kitname)
  {
    loadPlayerfile(player.toLowerCase());
    return Integer.valueOf(history.getInt(kitname + ".usages"));
  }
  
  public static void setHistory(String player, String kitname, Long cooldown, Integer usages)
  {
    loadPlayerfile(player.toLowerCase());
    history.set(kitname + ".cooldown", cooldown);
    history.set(kitname + ".usages", usages);
    try
    {
      history.save(historyfile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
