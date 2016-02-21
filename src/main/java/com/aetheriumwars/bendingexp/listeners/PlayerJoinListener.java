package main.java.com.aetheriumwars.bendingexp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import main.java.com.aetheriumwars.bendingexp.BendingExp;

public class PlayerJoinListener implements Listener{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		BendingExp.addPlayer(ev.getPlayer());
	}

}
