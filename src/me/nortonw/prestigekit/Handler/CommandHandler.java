package me.nortonw.prestigekit.Handler;

import me.nortonw.prestigekit.PrestigeKit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandHandler
  implements CommandExecutor
{
  private PrestigeKit plugin;
  
  public CommandHandler(PrestigeKit instance)
  {
    this.plugin = instance;
  }
  
  @SuppressWarnings({ "rawtypes", "deprecation" })
public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
  {
    Player player = null;
    if ((sender instanceof Player)) {
      player = (Player)sender;
    }
    Player recipient = player;
    if (((commandLabel.equalsIgnoreCase("kit")) && (player == null)) || (player.hasPermission("kit.kit")))
    {
      Object localObject;
      if (args.length == 0)
      {
        if (player == null)
        {
          this.plugin.respond(player, "[Kit] ALL kits: " + ConfigHandler.getKitsConfig().getKeys(false));
          return false;
        }
        @SuppressWarnings("unchecked")
		ArrayList<String> kits = new ArrayList();
        for (localObject = ConfigHandler.getKitsConfig().getKeys(false).iterator(); ((Iterator)localObject).hasNext();)
        {
          @SuppressWarnings("rawtypes")
		String kit = (String)((Iterator)localObject).next();
          if ((player.hasPermission("kit." + kit)) && (
            (ConfigHandler.getKitsConfig().getInt(kit + ".usages") >= 0) || ((ConfigHandler.getKitsConfig().getInt(kit + ".usages") == -2) && (HistoryHandler.getUsages(recipient.getName(), kit).intValue() != -20)))) {
            kits.add(kit);
          }
        }
        if (kits.size() == 0) {
          this.plugin.respond(player, ChatColor.RED + "[Kit] There are no kits available for you!");
        } else {
          this.plugin.respond(player, ChatColor.GREEN + "[Kit] You have access to: " + kits);
        }
      }
      else if ((args.length == 1) || (args.length == 2))
      {
        if (args[0].equalsIgnoreCase("reload"))
        {
          if ((player == null) || (player.hasPermission("kit.reload")))
          {
            ConfigHandler.loadKits();
            this.plugin.reloadConfig();
            this.plugin.respond(player, ChatColor.GREEN + "[Kit] Kits and config reloaded!");
          }
          else
          {
            this.plugin.respond(player, ChatColor.RED + "[Kit] Permission denied!");
          }
          return false;
        }
        String kitname = args[0];
        if ((args.length == 1) && (player == null))
        {
          this.plugin.respond(player, "[Kit] The console can't request kits. Use '/kit <kitname> <playername>' if you want to give someone a kit!");
          return false;
        }
        if (ConfigHandler.getKitsConfig().contains(kitname))
        {
          if (ConfigHandler.getKitsConfig().getLong(kitname + ".usages") == -1L)
          {
            this.plugin.respond(player, ChatColor.RED + "[Kit] You can't request starterkits!");
            return false;
          }
          if ((player != null) && (!player.hasPermission("kit." + kitname)) && (!player.hasPermission("kit.proxy")))
          {
            this.plugin.respond(player, ChatColor.RED + "[Kit] You don't have the permission to request this kit!");
            return false;
          }
          if (args.length == 2) {
            if ((player == null) || (player.hasPermission("kit.proxy")))
            {
              if ((player == null) || (!player.getName().equalsIgnoreCase(args[1])))
              {
                if ((localObject = this.plugin.getServer().getOnlinePlayers()).length != 0)
                {
                  Player pl = localObject[0];
                  if (pl.getName().equalsIgnoreCase(args[1]))
                  {
                    recipient = pl;
                  }
                  else
                  {
                    this.plugin.respond(player, ChatColor.RED + "[Kit] Player not online!");
                    return false;
                  }
                }
              }
              else
              {
                this.plugin.respond(player, ChatColor.RED + "[Kit] You can't give yourself a kit. Use '/kit <kitname>' instead !");
                return false;
              }
            }
            else
            {
              this.plugin.respond(player, ChatColor.RED + "[Kit] Permission denied!");
              return false;
            }
          }
          KitHandler.giveKit(player, recipient, kitname);
        }
        else
        {
          this.plugin.respond(recipient, ChatColor.RED + "[Kit] Kit doesn't exist! Names are case sensitive!");
          return false;
        }
      }
    }
    else
    {
      this.plugin.respond(recipient, ChatColor.RED + "[Kit] Permission denied!");
    }
    return true;
  }
}
