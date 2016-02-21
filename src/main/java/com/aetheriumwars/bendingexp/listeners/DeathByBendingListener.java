package main.java.com.aetheriumwars.bendingexp.listeners;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.EntityBendingDeathEvent;

import main.java.com.aetheriumwars.bendingexp.BendingExp;

public class DeathByBendingListener implements Listener{
	
	@EventHandler
	public void onEntityBendingDeath(EntityBendingDeathEvent event) {
		Entity v = event.getVictim();
		Element e = CoreAbility.getAbility(event.getAbility()).getElement();
		int xp;
		
		if(v == null || e == null)
			return;
		
		if(v instanceof Player) {
			xp = 25 + (int)(Math.random() * ((35 - 25) + 1));
		}
		else if(v instanceof Zombie || v instanceof Creeper || v instanceof Witch || v instanceof Skeleton || v instanceof Guardian || v instanceof Spider) {
			xp = 13 + (int)(Math.random() * ((22 - 13) + 1));
		}
		else {
			xp = 3 + (int)(Math.random() * ((8 - 3) + 1));
		}
		BendingExp.addExp(event.getAttacker(), e, xp);
	}

}
