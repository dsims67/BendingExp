package main.java.com.aetheriumwars.bendingexp.scoreboard;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

import main.java.com.aetheriumwars.bendingexp.BendingExp;
import net.md_5.bungee.api.ChatColor;

public class BendingScoreboard {
	
	private static HashMap<UUID, BendingScoreboard> scoreboards = new HashMap<UUID, BendingScoreboard>();
	private String title = ChatColor.BLUE+"== "+ChatColor.GREEN+""+ChatColor.BOLD+"Abilities "+ChatColor.BLUE+"==";
	private boolean hidden = true;
	private ScoreboardManager manager;
	private Scoreboard board;
	private Team team;
	private Objective objective;
	private UUID playerId;
	
	public BendingScoreboard(Player p) {
		this.playerId = p.getUniqueId();
		this.manager = Bukkit.getScoreboardManager();
		this.board = manager.getNewScoreboard();
		this.team = board.registerNewTeam("benders");
		this.objective = board.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR); //set the slot to show the scoreboard
		objective.setDisplayName(title); //set the scoreboard's title
		updateScoreboard(); //load the initial values
		showScoreboard();
		scoreboards.put(playerId, this);
	}
	
	public static BendingScoreboard getScoreboard(Player p) {
		return scoreboards.get(p.getUniqueId());
	}
	
	private Player getOwner() {
		return Bukkit.getPlayer(playerId);
	}
	
	public void showScoreboard() {
		if(!hidden)
			return;
		
		Player p = getOwner();
		if(!team.hasPlayer(p))
			team.addPlayer(p);
		
		p.setScoreboard(board);
		this.hidden = false;
	}
	
	public void hideScoreboard() {
		if(hidden)
			return;
		
		Player p = getOwner();
		p.setScoreboard(manager.getNewScoreboard()); //clears the scoreboard
		if(team.hasPlayer(p))
			team.removePlayer(p);
		this.hidden = true;
	}
	
	public void removeScoreboard() {
		this.hideScoreboard();
		//scheduler.cancelAllTasks();
		//BendingExp.unregisterListener(this.listener);
		scoreboards.remove(playerId);
	}
	
	public void updateScoreboard() {
		Score score;
		Player p = getOwner();
		BendingPlayer bp = BendingPlayer.getBendingPlayer(p);
		
		
		if(p == null || bp == null)
			return;
		
		clearBoard();
		
		if(bp.getAbilities().size() == 0)
			return;
		
		for(int i=9; i > 0; i--) {
			String ability = bp.getAbilities().get(i);
			if(ability == null || ability.equals("")) { //if empty slot
				continue;
			}
			else if(bp.isOnCooldown(ability)) { // if ability is on cooldown
				score = objective.getScore(CoreAbility.getAbility(ability).getElement().getColor()+""+ChatColor.STRIKETHROUGH+ability);
			}
			else { //if ability is available
				score = objective.getScore(CoreAbility.getAbility(ability).getElement().getColor()+ability);
			}
			score.setScore(i*-1);
		}
		
	}
	
	private void clearBoard() {
		objective.unregister();
		this.objective = board.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR); //set the slot to show the scoreboard
		objective.setDisplayName(title);
	}

}
