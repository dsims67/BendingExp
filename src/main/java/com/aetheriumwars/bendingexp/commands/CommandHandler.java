package main.java.com.aetheriumwars.bendingexp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;

import main.java.com.aetheriumwars.bendingexp.BendingExp;
import main.java.com.aetheriumwars.bendingexp.scoreboard.BendingScoreboard;
import net.md_5.bungee.api.ChatColor;

public class CommandHandler implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { 
	
		//Player Only Commands
		/*
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (player.hasPermission(BendingExp.opPermission) && command.getName().equalsIgnoreCase("bendexp") && args != null && args.length >= 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						BendingExp.getPlugin().reloadConfig();
						player.sendMessage(ChatColor.GRAY+"BendingExp: Configuration reloaded!");
						return true;
					}
			}
			
		}*/
		
		if((sender instanceof ConsoleCommandSender) || (sender instanceof Player)) {
			boolean isPlayer = false;
			if(sender instanceof Player)
				isPlayer = true;
			
			//if player is console or has op permission and command is bendexp
			if( (!isPlayer || sender.hasPermission(BendingExp.opPermission)) 
					&& command.getName().equalsIgnoreCase("bendexp") && args != null && args.length >= 1) 
			{
				
				// /bendexp reload
				if (args[0].equalsIgnoreCase("reload")) {
					BendingExp.getPlugin().reloadConfig();
					sender.sendMessage(ChatColor.GRAY+"BendingExp: Configuration reloaded!");
					return true;
				}
				// /bendexp setlevel <player> <element> <level>
				else if (args[0].equalsIgnoreCase("setlevel") && args.length >= 4) {
					Player p = Bukkit.getPlayer(args[1]);
					Element e = Element.getElement(args[2]);
					int lvl = Integer.parseInt(args[3]);
					if(p != null && lvl >= 0 && BendingExp.hasPlayer(p)) {
						BendingExp.setLevel(p, e, lvl);
						sender.sendMessage(ChatColor.GOLD+p.getDisplayName()+"'s "+e.getName()+" bending level has been set to "+lvl);
						p.sendMessage(ChatColor.GOLD+"Your "+e.getName()+" bending level has been set to "+ChatColor.LIGHT_PURPLE+lvl);
						return true;
					}
				}
				// /bendexp addexp <player> <element> <experience>
				else if (args[0].equalsIgnoreCase("addexp") && args.length >= 4) {
					try {
						boolean all = false;
						Player p = Bukkit.getPlayer(args[1]);
						int exp = Integer.parseInt(args[3]);
						if(p != null && exp >= 0 && BendingExp.hasPlayer(p)) {
							if(args[2].toLowerCase().equals("all")) {
								for(Element e: Element.getMainElements()) {
									BendingExp.addExpNoMessage(p, e, exp);
								}
								p.sendMessage(ChatColor.GOLD+"You have received "+ChatColor.GREEN+exp+" Exp "+ChatColor.GOLD+"in "+ChatColor.LIGHT_PURPLE+"All Elements");
								return true;
							}
							else {
								Element e = Element.getElement(args[2]);
								BendingExp.addExp(p, e, exp);
								sender.sendMessage(ChatColor.GOLD+p.getDisplayName()+" has received "+exp+" "+e.getName()+" bending experience");
								p.sendMessage(ChatColor.GOLD+"You have received "+ChatColor.GREEN+exp+" Exp "+ChatColor.GOLD+"in "+e.getColor()+e.getName()+" Bending");
								return true;
							}
						}
					}
					catch(Exception e) {
						sender.sendMessage(ChatColor.RED+"Incorrect syntax. /bendexp addexp <player> <element> <exp>");
						return false;
					}
					
				}
			}
			
			if(isPlayer && command.getName().equalsIgnoreCase("bendexp") && args.length >= 1) {
				Player p = (Player) sender;
				if(sender.hasPermission(BendingExp.levelCmdPermissions) && args[0].equalsIgnoreCase("level") && !BendingPlayer.getBendingPlayer(p).getElements().isEmpty()) {
					Element e = BendingPlayer.getBendingPlayer(p).getElements().get(0);
					int lvl = BendingExp.getBendingLevel(p, e);
					int remainingExp = (int)(BendingExp.getExpForLevel(lvl+1) - BendingExp.getExperience(p, e));
					p.sendMessage(ChatColor.YELLOW+"BendingExp: "+e.getColor()+e.getName()+" Bending level: "+ChatColor.GREEN+lvl
						+ChatColor.GOLD+" | "+remainingExp+" exp till next level");
						//+(int)( ((BendingExp.getExpForLevel(lvl+1) - BendingExp.getExpForLevel(lvl)) - remainingExp) / (BendingExp.getExpForLevel(lvl+1) - BendingExp.getExpForLevel(lvl)) )+"%)" );
					return true;
				}
				else if(sender.hasPermission(BendingExp.scoreboardCmdPermissions) && args[0].equalsIgnoreCase("showboard")) {
					BendingScoreboard sb = BendingScoreboard.getScoreboard(p);
					if(sb != null) {
						sb.showScoreboard();
					}
					return true;
				}
				else if(sender.hasPermission(BendingExp.scoreboardCmdPermissions) && args[0].equalsIgnoreCase("hideboard")) {
					BendingScoreboard sb = BendingScoreboard.getScoreboard(p);
					if(sb != null) {
						sb.hideScoreboard();
					}
					return true;
				}
			}
			
			if(isPlayer && sender.hasPermission(BendingExp.skillsCmdPermissions) && command.getName().equalsIgnoreCase("skills")) {
				Player p = (Player) sender;
				for(Element e: BendingPlayer.getBendingPlayer(p).getElements()) {
					for(CoreAbility a: CoreAbility.getAbilitiesByElement(e)) {
						ChatColor c = ChatColor.RED;
						int playerLevel = BendingExp.getBendingLevel(p, e);
						int reqLevel = BendingExp.getRequiredLevel(a);
						if(playerLevel >= reqLevel)
							c = ChatColor.GREEN;
						sender.sendMessage(ChatColor.GRAY+"== "+e.getColor()+a.getName()+ChatColor.DARK_RED+" | "+c+"Level: "+reqLevel+ChatColor.GRAY+" ==");
					
					}
				}
				return true;
			}
			
			
		}
		
		return false;
	}
	
	
}
