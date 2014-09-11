package me.nortonw.prestigekit.Handler;

import me.nortonw.prestigekit.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitHandler
{
  private static PrestigeKit plugin;
  
  public KitHandler(PrestigeKit instance)
  {
    plugin = instance;
  }
  
  @SuppressWarnings("deprecation")
public static void giveKit(Player player, Player recipient, String kitname)
  {
    Integer price = Integer.valueOf(ConfigHandler.getKitsConfig().getInt(kitname + ".price"));
    Integer cooldown = Integer.valueOf(ConfigHandler.getKitsConfig().getInt(kitname + ".cooldown"));
    Boolean clearinv = Boolean.valueOf(ConfigHandler.getKitsConfig().getBoolean(kitname + ".clearinv"));
    Boolean cleararmor = Boolean.valueOf(ConfigHandler.getKitsConfig().getBoolean(kitname + ".cleararmor"));
    String kitmessage = ConfigHandler.getKitsConfig().getString(kitname + ".message");
    Integer givemoney = Integer.valueOf(ConfigHandler.getKitsConfig().getInt(kitname + ".givemoney"));
    Integer givexp = Integer.valueOf(ConfigHandler.getKitsConfig().getInt(kitname + ".givexp"));
    Boolean removeeffects = Boolean.valueOf(ConfigHandler.getKitsConfig().getBoolean(kitname + ".removeeffects"));
    Integer usages = Integer.valueOf(ConfigHandler.getKitsConfig().getInt(kitname + ".usages"));
    List<String> effects = ConfigHandler.getKitsConfig().getStringList(kitname + ".effects");
    Long historycd = HistoryHandler.getCooldown(recipient.getName(), kitname);
    Integer historyusages = HistoryHandler.getUsages(recipient.getName(), kitname);
    Long now = Long.valueOf(new Date().getTime() / 1000L);
    Long seconds = Long.valueOf(0L);
    String playername = "(Console)";
    if (player != null) {
      playername = player.getName();
    }
    if ((historyusages.intValue() == 0) && 
      (usages.intValue() > 0)) {
      historyusages = usages;
    }
    if (historyusages.intValue() == -1) {
      return;
    }
    if (historyusages.intValue() == -10) {
      if ((player == null) || (player.hasPermission("kit.proxy")) || (player.isOp()))
      {
        plugin.logger.info("[KitPlugin] Ignoring limit restriction for " + playername + " because of 'kit.proxy' permission");
      }
      else
      {
        plugin.respond(player, ChatColor.RED + "[Kit] You can't request this kit! Limit reached.");
        plugin.logger.info("[KitPlugin] Refused kit for " + playername + ": " + kitname + " (limit reached)");
        return;
      }
    }
    if (historyusages.intValue() == -20) {
      if ((player == null) || (player.hasPermission("kit.proxy")) || (player.isOp()))
      {
        plugin.logger.info("[KitPlugin] Ignoring deathkit restriction for " + playername + " because of 'kit.proxy' permission");
      }
      else
      {
        plugin.respond(player, ChatColor.RED + "[Kit] You already requested this kit. Wait until you die again.");
        plugin.logger.info("[KitPlugin] Refused kit for " + playername + ": " + kitname + " (deathkit)");
        return;
      }
    }
    if (cooldown.intValue() != 0)
    {
      seconds = Long.valueOf(historycd.longValue() + cooldown.intValue() - now.longValue());
      if (((player == null) || (player.hasPermission("kit.proxy")) || (player.isOp())) && (seconds.longValue() > 0L))
      {
        plugin.logger.info("[KitPlugin] Ignoring cooldown for " + playername + " (" + seconds + " seconds) because of 'kit.proxy' permission");
        seconds = Long.valueOf(0L);
      }
    }
    if (seconds.longValue() > 0L)
    {
      plugin.respond(player, ChatColor.RED + "[Kit] " + "Please try again in " + plugin.timeUntil(seconds) + ".");
      plugin.logger.info("[KitPlugin] Refused kit for " + playername + ": " + kitname + " (cooldown)");
      return;
    }
    if (clearinv.booleanValue()) {
      recipient.getInventory().clear();
    }
    if (cleararmor.booleanValue())
    {
      recipient.getInventory().setHelmet(null);
      recipient.getInventory().setChestplate(null);
      recipient.getInventory().setLeggings(null);
      recipient.getInventory().setBoots(null);
    }
    if ((price.intValue() > 0) && (player != null) && (plugin.economy != null)) {
      if ((player.hasPermission("kit.proxy")) || (player.isOp()))
      {
        plugin.logger.info("[KitPlugin] Ignoring cost of " + plugin.economy.format(price.intValue()) + " for " + playername + " (" + seconds + " seconds) because of 'kit.proxy' permission");
      }
      else
      {
        if (plugin.economy.getBalance(playername) < price.intValue())
        {
          plugin.respond(player, ChatColor.RED + "[Kit] Not enough money!");
          plugin.logger.info("[KitPlugin] " + playername + " can't afford the kit '" + kitname + "'");
          return;
        }
        if (plugin.economy.getBalance(playername) >= price.intValue())
        {
          plugin.economy.withdrawPlayer(playername, price.intValue());
          plugin.respond(player, ChatColor.GREEN + "[Kit] " + plugin.economy.format(price.intValue()) + " deducted.");
          plugin.logger.info("[KitPlugin] Deducted " + plugin.economy.format(price.intValue()) + " from " + playername);
        }
      }
    }
    int j;
    int i;
    for (@SuppressWarnings("rawtypes")
	Iterator localIterator = ConfigHandler.getKitsConfig().getConfigurationSection(kitname + ".items").getKeys(false).iterator(); localIterator.hasNext(); i < j)
    {
      String identifier = (String)localIterator.next();
      String slot = ConfigHandler.getKitsConfig().getString(kitname + ".items." + identifier + "." + "slot");
      Integer amount = Integer.valueOf(ConfigHandler.getKitsConfig().getInt(kitname + ".items." + identifier + "." + "amount"));
      Short damage = Short.valueOf((short)ConfigHandler.getKitsConfig().getInt(kitname + ".items." + identifier + "." + "data"));
      String color = ConfigHandler.getKitsConfig().getString(kitname + ".items." + identifier + "." + "color");
      List<String> enchant = ConfigHandler.getKitsConfig().getStringList(kitname + ".items." + identifier + "." + "enchantments");
      String[] enchants = new String[enchant.size()];
      enchant.toArray(enchants);
      List<String> lore = processLore(ConfigHandler.getKitsConfig().getStringList(kitname + ".items." + identifier + "." + "lore"), recipient.getName());
      String name = ConfigHandler.getKitsConfig().getString(kitname + ".items." + identifier + "." + "name");
      try
      {
        ItemStack stack = new ItemStack(Material.valueOf(ConfigHandler.getKitsConfig().getString(kitname + ".items." + identifier + "." + "id").toUpperCase()), amount.intValue(), damage.shortValue());
      }
      catch (IllegalArgumentException e)
      {
        ItemStack stack;
        plugin.respond(player, ChatColor.RED + "[Kit] Error: Please contact the server admins and tell them to check the server log/console for more details!");
        plugin.logger.severe("[Kit] Error at -> id: " + ConfigHandler.getKitsConfig().getString(new StringBuilder(String.valueOf(kitname)).append(".items.").append(identifier).append(".").append("id").toString()) + " <- Don't use item IDs and make sure the material name is correct. Check the links in your config file for explanation."); return;
      }
      ItemStack stack;
      if (enchants.length > 0) {
        for (int i1 = 0; i1 < enchants.length; i1++) {
          if (Enchantment.getByName(enchants[i1].toUpperCase().split(",")[0]) != null)
          {
            stack.addUnsafeEnchantment(Enchantment.getByName(enchants[i1].toUpperCase().split(",")[0]), Integer.parseInt(enchants[i1].split(",")[1]));
          }
          else
          {
            plugin.logger.severe("[KitPlugin] Illegal enchantment found (" + enchants[i1].split(",")[0] + "," + enchants[i1].split(",")[1] + "). Make sure you are using names instead of IDs. Check the links in your config for explanation!");
            plugin.respond(player, ChatColor.RED + "[Kit] Error: Please contact the server admins and tell them to check the server log/console for more details!");
            return;
          }
        }
      }
      if (lore.size() > 0)
      {
        ItemMeta im = stack.getItemMeta();
        im.setLore(lore);
        stack.setItemMeta(im);
      }
      if ((name != null) && (!name.isEmpty()))
      {
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(colorizeString(name).replace("(player)", recipient.getName()));
        stack.setItemMeta(im);
      }
      ItemStack[] arrayOfItemStack;
      j = (arrayOfItemStack = splitStacks(stack)).length;i = 0; continue;ItemStack stacks = arrayOfItemStack[i];
      if (stacks.getAmount() > 0)
      {
        if (((stacks.getType().equals(Material.LEATHER_CHESTPLATE)) || (stacks.getType().equals(Material.LEATHER_BOOTS)) || (stacks.getType().equals(Material.LEATHER_HELMET)) || (stacks.getType().equals(Material.LEATHER_LEGGINGS))) && (color != null)) {
          colorizeArmor(stacks, color);
        }
        if ((slot == null) || (slot.equalsIgnoreCase("")) || (slot.equalsIgnoreCase("inventory")))
        {
          if (recipient.getInventory().firstEmpty() != -1) {
            recipient.getInventory().addItem(new ItemStack[] { stacks });
          } else {
            recipient.getWorld().dropItemNaturally(recipient.getLocation(), stacks);
          }
        }
        else if (slot.equalsIgnoreCase("chestplate"))
        {
          if (recipient.getInventory().getChestplate() != null) {
            if (recipient.getInventory().firstEmpty() != -1) {
              recipient.getInventory().addItem(new ItemStack[] { recipient.getInventory().getChestplate() });
            } else {
              recipient.getWorld().dropItemNaturally(recipient.getLocation(), recipient.getInventory().getChestplate());
            }
          }
          recipient.getInventory().setChestplate(stacks);
        }
        else if (slot.equalsIgnoreCase("leggings"))
        {
          if (recipient.getInventory().getLeggings() != null) {
            if (recipient.getInventory().firstEmpty() != -1) {
              recipient.getInventory().addItem(new ItemStack[] { recipient.getInventory().getLeggings() });
            } else {
              recipient.getWorld().dropItemNaturally(recipient.getLocation(), recipient.getInventory().getLeggings());
            }
          }
          recipient.getInventory().setLeggings(stacks);
        }
        else if (slot.equalsIgnoreCase("boots"))
        {
          if (recipient.getInventory().getBoots() != null) {
            if (recipient.getInventory().firstEmpty() != -1) {
              recipient.getInventory().addItem(new ItemStack[] { recipient.getInventory().getBoots() });
            } else {
              recipient.getWorld().dropItemNaturally(recipient.getLocation(), recipient.getInventory().getBoots());
            }
          }
          recipient.getInventory().setBoots(stacks);
        }
        else if (slot.equalsIgnoreCase("helmet"))
        {
          if (recipient.getInventory().getHelmet() != null) {
            if (recipient.getInventory().firstEmpty() != -1) {
              recipient.getInventory().addItem(new ItemStack[] { recipient.getInventory().getHelmet() });
            } else {
              recipient.getWorld().dropItemNaturally(recipient.getLocation(), recipient.getInventory().getHelmet());
            }
          }
          recipient.getInventory().setHelmet(stacks);
        }
      }
      i++;
    }
    for (String str : ConfigHandler.getKitsConfig().getStringList(kitname + ".commands")) {
      plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), str.replace("(player)", recipient.getName()));
    }
    if ((givemoney.intValue() > 0) && (plugin.economy != null)) {
      plugin.economy.depositPlayer(recipient.getName(), givemoney.intValue());
    }
    if (givexp.intValue() > 0) {
      recipient.giveExp(givexp.intValue());
    }
    if ((kitmessage == null) || (kitmessage == "")) {
      kitmessage = plugin.getConfig().getString("KitMessage");
    }
    if (removeeffects.booleanValue()) {
      for (PotionEffect effect : recipient.getActivePotionEffects()) {
        recipient.removePotionEffect(effect.getType());
      }
    }
    if (effects.size() > 0) {
      for (String effect : effects) {
        try
        {
          recipient.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect.toUpperCase().split(",")[0]), Integer.valueOf(effect.split(",")[1]).intValue() * 20, Integer.valueOf(effect.split(",")[2]).intValue()));
        }
        catch (IllegalArgumentException e)
        {
          plugin.logger.severe("[Kit] Error at EFFECT -> " + effect + " <- Don't use effect IDs and make sure the material name is correct. Check the links in your config file for explanation.");
          plugin.respond(player, ChatColor.RED + "[Kit] Error: Please contact the server admins and tell them to check the server log/console for more details!");
          return;
        }
      }
    }
    plugin.respond(recipient, ChatColor.GREEN + "[Kit] " + ChatColor.RESET + colorizeString(kitmessage));
    if ((player != null) && (recipient.equals(player))) {
      if (usages.intValue() > 0)
      {
        if (historyusages.intValue() - 1 == 0) {
          HistoryHandler.setHistory(recipient.getName(), kitname, now, Integer.valueOf(-10));
        } else {
          HistoryHandler.setHistory(recipient.getName(), kitname, now, Integer.valueOf(historyusages.intValue() - 1));
        }
      }
      else if (usages.intValue() == -2) {
        HistoryHandler.setHistory(recipient.getName(), kitname, now, Integer.valueOf(-20));
      } else if (usages.intValue() == -1) {
        HistoryHandler.setHistory(recipient.getName(), kitname, now, Integer.valueOf(-1));
      } else {
        HistoryHandler.setHistory(recipient.getName(), kitname, now, Integer.valueOf(0));
      }
    }
  }
  
  private static ItemStack[] splitStacks(ItemStack item)
  {
    Integer maxStacksize = Integer.valueOf(item.getMaxStackSize());
    Integer remainder = Integer.valueOf(item.getAmount() % maxStacksize.intValue());
    Integer fullStacks = Integer.valueOf((int)Math.floor(item.getAmount() / item.getMaxStackSize()));
    
    ItemStack fullStack = item.clone();
    ItemStack finalStack = item.clone();
    fullStack.setAmount(maxStacksize.intValue());
    finalStack.setAmount(remainder.intValue());
    
    ItemStack[] items = new ItemStack[fullStacks.intValue() + 1];
    for (int i = 0; i < fullStacks.intValue(); i++) {
      items[i] = fullStack;
    }
    items[(items.length - 1)] = finalStack;
    
    return items;
  }
  
  private static List<String> processLore(List<String> lores, String recipient)
  {
    for (int i = 0; i < lores.size(); i++)
    {
      lores.set(i, colorizeString((String)lores.get(i)));
      lores.set(i, ((String)lores.get(i)).replace("(player)", recipient));
    }
    return lores;
  }
  
  private static String colorizeString(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  private static ItemStack colorizeArmor(ItemStack is, String color)
  {
    LeatherArmorMeta lam = (LeatherArmorMeta)is.getItemMeta();
    lam.setColor(Color.fromRGB(Integer.valueOf(color.split(" ")[0]).intValue(), Integer.valueOf(color.split(" ")[1]).intValue(), Integer.valueOf(color.split(" ")[2]).intValue()));
    is.setItemMeta(lam);
    return is;
  }
}
