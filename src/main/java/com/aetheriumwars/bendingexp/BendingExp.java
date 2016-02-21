package main.java.com.aetheriumwars.bendingexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;

import main.java.com.aetheriumwars.bendingexp.commands.CommandHandler;
import main.java.com.aetheriumwars.bendingexp.listeners.BindAbilityListener;
import main.java.com.aetheriumwars.bendingexp.listeners.DeathByBendingListener;
import main.java.com.aetheriumwars.bendingexp.listeners.PlayerJoinListener;
import main.java.com.aetheriumwars.bendingexp.listeners.PlayerQuitListener;
import main.java.com.aetheriumwars.bendingexp.scoreboard.BendingScoreboard;
import net.md_5.bungee.api.ChatColor;

public class BendingExp extends JavaPlugin {
	
	private static BendingExp bxp;
	private static FileConfiguration config;
	private static Plugin plugin;
	
	private static HashMap<UUID, BenderData> playerData = new HashMap<UUID, BenderData>(); //player json data maintained by server
	public static String allAbilitiesPermission = "bendingexp.nolimit"; //TODO: move these to enum
	public static String opPermission = "bendingexp.op";
	public static String skillsCmdPermissions = "bendingexp.skills";
	public static String levelCmdPermissions = "bendingexp.level";
	public static String scoreboardCmdPermissions = "bendingexp.scoreboard";
	
	/* Ideas */
	/* 1. Add Ability leveling up I - IV (adds damage boost, or knockback, ect, depending on ability) */
	
	@Override
	public void onEnable() {
		bxp = this;	
		
		//init config
		//this.saveDefaultConfig();
		config = this.getConfig();
		this.getConfig().options().copyDefaults(true);
		
		File configFile = new File(BendingExp.getPlugin().getDataFolder()+File.separator+"config.yml");
		if(!configFile.exists())
			initConfig();
		
		//make playerdata folder
		boolean successful = new File(BendingExp.getPlugin().getDataFolder()+File.separator+"PlayerData").mkdir();
		if(successful)
			getLogger().info("BendingExp: Created PlayerData Directory");
		
		//register listeners
		registerListener(new BindAbilityListener());
		registerListener(new PlayerJoinListener());
		registerListener(new PlayerQuitListener());
		registerListener(new DeathByBendingListener());
		//register commands
        getCommand("bendexp").setExecutor(new CommandHandler());
        getCommand("skills").setExecutor(new CommandHandler());
		
        //for aliases
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("bxp");
        getCommand("bendexp").setAliases(arrayList);
        
		getLogger().info("BendingExp has been enabled");
		
		/*auto-save scheduler*/
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	//save every 5 mins
            	if(playerData.size() > 0) {
	            	getLogger().info("Saving player data..");
	            	for(BenderData b: playerData.values()) {
	            		b.saveBenderJSON();
	            	}
            	}
            }
        }, 0L, 6000L);
        
		/*scoreboard scheduler*/
        scheduler.scheduleSyncRepeatingTask(BendingExp.getPlugin(), new Runnable() {
            @Override
            public void run() {
            	if(Bukkit.getOnlinePlayers().size() < 1)
            		return;
            	
            	for(Player p: Bukkit.getOnlinePlayers()) {
            		try {
            			BendingScoreboard bs = BendingScoreboard.getScoreboard(p);
            			if(bs != null) {
            				bs.updateScoreboard();
            			}
            		}
            		catch(Exception e) {
            			BendingScoreboard.getScoreboard(p).removeScoreboard();
            			Bukkit.getLogger().info("Encountered scoreboard for missing player. Removing...");
            		}
            	}
            }
        }, 10L, 3L);
	}
	
	@Override
	public void onDisable() {
    	for(BenderData b: playerData.values()) {
    		b.saveBenderJSON();
    	}
		this.saveConfig();
		getLogger().info("BendingExp has been disabled!");
	}
	
	public static Plugin getPlugin() {
		return bxp;
	}
	
	
	public static void registerListener(Listener lc) {
		Bukkit.getServer().getPluginManager().registerEvents(lc, getPlugin());
	}
	
	public static void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
	}
	
	public void initConfig() {
		for(Element e: Element.getElements()) {
			for(CoreAbility ability : CoreAbility.getAbilitiesByElement(e))
				config.addDefault("Element."+e.getName()+"."+ability.getName(), 10);
		}
		getLogger().info("Initialized Configuration");
		this.saveConfig();
	}
	
	//returns the level required to use an ability
	public static int getRequiredLevel(CoreAbility ability) {
		return config.getInt("Element."+ability.getElement().getName()+"."+ability.getName());
	}
	
	//return a player's bending level for a specific element
	public static int getBendingLevel(Player p, Element e) {
		if(playerData.containsKey(p.getUniqueId()))
			return playerData.get(p.getUniqueId()).getLevel(e);
		else
			return 0;
	}
	
	//adds experience to the player for a specific element
	public static void addExp(Player p, Element e, int amount) {
		int preLvl;
		int postLvl;
		preLvl = playerData.get(p.getUniqueId()).getLevel(e);
		playerData.get(p.getUniqueId()).addExperience(e, amount);
		postLvl = playerData.get(p.getUniqueId()).getLevel(e);
		p.sendMessage(ChatColor.GRAY+"You've gained "+ChatColor.GREEN+amount+" "+e.getColor()+e.getName()+" bending"+ChatColor.GRAY+" experience!");
		if(postLvl > preLvl) {
			p.sendMessage(ChatColor.AQUA+"Level Up! "+ChatColor.YELLOW+"You're now a level "+postLvl+" "+e.getName()+" bender!");
		}
	}

	public static void addExpNoMessage(Player p, Element e, int amount) {
		int preLvl;
		int postLvl;
		preLvl = playerData.get(p.getUniqueId()).getLevel(e);
		playerData.get(p.getUniqueId()).addExperience(e, amount);
		postLvl = playerData.get(p.getUniqueId()).getLevel(e);
		if(postLvl > preLvl) {
			p.sendMessage(ChatColor.AQUA+"Level Up! "+ChatColor.YELLOW+"You're now a level "+postLvl+" "+e.getName()+" bender!");
		}
	}
	
	public static int getExperience(Player p, Element e) {
		if(playerData.containsKey(p.getUniqueId()))
			return playerData.get(p.getUniqueId()).getExperience(e);
		else
			return 0;
	}
	
	//returns the exp needed for a specific level
	public static int getExpForLevel(int lvl) {
		return (int) (2*Math.pow(lvl, 2)*1.3);
	}
	
	public static void setLevel(Player p, Element e, int lvl) {
		if(playerData.containsKey(p.getUniqueId()) && lvl >= 0) {
			playerData.get(p.getUniqueId()).setLevel(e, lvl);
			p.sendMessage(ChatColor.YELLOW+"Your level has been set to "+ChatColor.AQUA+lvl);
		}
	}
	
	public static void addPlayer(Player p) {
		playerData.put(p.getUniqueId(), new BenderData(p.getUniqueId()));
		new BendingScoreboard(p); //create a scoreboard for that player
	}
	
	public static void removePlayer(Player p) {
		UUID pid = p.getUniqueId();
		try {
			if(playerData.containsKey(pid)) {
				playerData.get(p.getUniqueId()).saveBenderJSON();
				playerData.remove(p.getUniqueId());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean hasPlayer(Player p) {
		return playerData.containsKey(p.getUniqueId());
	}
	

}
