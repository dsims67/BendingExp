package main.java.com.aetheriumwars.bendingexp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.BindingUpdateEvent;

import main.java.com.aetheriumwars.bendingexp.BenderData;
import main.java.com.aetheriumwars.bendingexp.BendingExp;
import net.md_5.bungee.api.ChatColor;

public class BindAbilityListener implements Listener {
	
	@EventHandler
	public void onBindEvent(BindingUpdateEvent event) {
		if(event.isBinding()) {
			Player p = event.getPlayer();
			Element e = CoreAbility.getAbility(event.getAbility()).getElement();
			int reqLevel = BendingExp.getRequiredLevel(CoreAbility.getAbility(event.getAbility()));
			int playerLevel = BendingExp.getBendingLevel(p, e);
			if(playerLevel < reqLevel && !p.hasPermission(BendingExp.allAbilitiesPermission)) {
				p.sendMessage(ChatColor.RED+"You must be level "+ChatColor.LIGHT_PURPLE+reqLevel+ChatColor.RED+" to use this ability");
				event.setCancelled(true);
			}
		}
	}

}
