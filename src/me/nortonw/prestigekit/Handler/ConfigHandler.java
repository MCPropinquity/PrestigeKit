package me.nortonw.prestigekit.Handler;

import me.nortonw.prestigekit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class ConfigHandler
{
  private static File kitsfile = null;
  private static FileConfiguration kits = null;
  private static PrestigeKit plugin;
  
  public ConfigHandler(PrestigeKit instance)
  {
    plugin = instance;
    loadConfig();
    loadKits();
    loadDefaultKits();
    updateKits();
  }
  
  private void loadConfig()
  {
    if (plugin.getConfig().contains("UpdateCheck")) {
      plugin.getConfig().set("UpdateCheck", null);
    }
    plugin.getConfig().addDefault("KitMessage", "&aEnjoy your Kit ;)");
    plugin.getConfig().addDefault("CheckUpdate", Boolean.valueOf(true));
    plugin.getConfig().addDefault("DownloadUpdate", Boolean.valueOf(false));
    plugin.getConfig().options().copyDefaults(true);
    plugin.saveConfig();
  }
  
  public static void loadKits()
  {
    kitsfile = new File(plugin.getDataFolder(), "kits.yml");
    if (!kitsfile.exists()) {
      plugin.getConfig().addDefault("ConfigVersion", Integer.valueOf(2));
    } else {
      plugin.getConfig().addDefault("ConfigVersion", Integer.valueOf(1));
    }
    plugin.saveConfig();
    kits = YamlConfiguration.loadConfiguration(kitsfile);
  }
  
  private void loadDefaultKits()
  {
    kits.options().header("***************************\nConfig explanation: http://dev.bukkit.org/bukkit-plugins/kitplugin/pages/kits-config-v7-0/\n***************************\nIMPORTANT: v7.0 or higher no longer allows numeric IDs! You have to use EXACT NAMES! \nCheck your KitPlugin folder for all the names! It contains 3 files (names-items.yml, names-enchants.yml, names-effects.yml).\n***************************\n");
    kits.options().copyDefaults(true);
    try
    {
      kits.save(kitsfile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static FileConfiguration getKitsConfig()
  {
    return kits;
  }
  
  public static void saveKits()
  {
    try
    {
      kits.save(kitsfile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("deprecation")
public void updateKits()
  {
    if ((plugin.getServer().getBukkitVersion().startsWith("1.7.")) && 
      (plugin.getConfig().getInt("ConfigVersion") < 2))
    {
      for (String kitname : kits.getKeys(false))
      {
        List<String> effects = new ArrayList<String>();
        List<String> enchants = new ArrayList<String>();
        for (String identifier : getKitsConfig().getConfigurationSection(kitname + ".items").getKeys(false))
        {
          kits.set(kitname + ".items." + identifier + "." + "id", Material.getMaterial(kits.getInt(kitname + ".items." + identifier + "." + "id")).name());
          for (String enchant : getKitsConfig().getStringList(kitname + ".items." + identifier + "." + "enchantments")) {
            enchants.add(Enchantment.getById(Integer.valueOf(enchant.split(",")[0]).intValue()).getName() + "," + enchant.split(",")[1]);
          }
          kits.set(kitname + ".items." + identifier + "." + "enchantments", enchants.toArray());
        }
        for (String pes : kits.getStringList(kitname + ".effects")) {
          effects.add(PotionEffectType.getById(Integer.valueOf(pes.split(",")[0]).intValue()).getName() + "," + pes.split(",")[1] + "," + pes.split(",")[2]);
        }
        kits.set(kitname + ".effects", effects.toArray());
      }
      saveKits();
      plugin.getConfig().set("ConfigVersion", Integer.valueOf(2));
      plugin.saveConfig();
      plugin.logger.info("[KitPlugin] Successfully converted Item IDs, Enchantment IDs and Effect IDs to Names.");
    }
  }
}
