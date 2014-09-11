package me.nortonw.prestigekit.Util;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class ExactNames
{
  private Plugin plugin;
  
  public ExactNames(Plugin instance)
  {
    this.plugin = instance;
    generateItemNames();
    generateEnchantNames();
    generateEffectNames();
  }
  
  @SuppressWarnings("deprecation")
private void generateItemNames()
  {
    File f = new File(this.plugin.getDataFolder(), "names-items.yml");
    if (f.exists()) {
      f.delete();
    }
    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
    cfg.options().header("List of ALL existing material names and their old ids.");
    for (Material mat : Material.values()) {
      cfg.set(String.valueOf(mat.getId()), mat.name());
    }
    try
    {
      cfg.save(f);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("deprecation")
private void generateEnchantNames()
  {
    File f = new File(this.plugin.getDataFolder(), "names-enchants.yml");
    if (f.exists()) {
      f.delete();
    }
    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
    cfg.options().header("List of ALL existing enchantment names and their old ids.");
    for (Enchantment en : Enchantment.values()) {
      cfg.set(String.valueOf(en.getId()), en.getName());
    }
    try
    {
      cfg.save(f);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("deprecation")
private void generateEffectNames()
  {
    File f = new File(this.plugin.getDataFolder(), "names-effects.yml");
    if (f.exists()) {
      f.delete();
    }
    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
    cfg.options().header("List of ALL existing potion-effect names and their old ids.");
    for (PotionEffectType pet : PotionEffectType.values()) {
      if (pet != null) {
        cfg.set(String.valueOf(pet.getId()), pet.getName());
      }
    }
    try
    {
      cfg.save(f);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
