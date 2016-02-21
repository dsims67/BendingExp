package main.java.com.aetheriumwars.bendingexp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.com.aetheriumwars.bendingexp.BendingExp;
import main.java.com.aetheriumwars.bendingexp.scoreboard.BendingScoreboard;

public class PlayerQuitListener implements Listener{
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent ev) {
		BendingExp.removePlayer(ev.getPlayer());
		if(BendingScoreboard.getScoreboard(ev.getPlayer()) != null) {
			BendingScoreboard.getScoreboard(ev.getPlayer()).removeScoreboard();;
		}
	}

}
