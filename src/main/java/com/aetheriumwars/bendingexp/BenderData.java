package main.java.com.aetheriumwars.bendingexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.projectkorra.projectkorra.Element;

public class BenderData {
	private UUID playerID;
	private HashMap<String, Integer> expData = new HashMap<String, Integer>();
	
	public BenderData(UUID uuid) {
		//initialize or load expData
		this.playerID = uuid;
		loadBenderJSON();

	}
	
	public UUID getId() {
		return playerID;
	}
	
	//y=2x^2 * 1.3 (leveling algorithm) x=sqrt(5y / 13) y = exp, x = lvl
	public int getLevel(Element e) {
		return (int) Math.sqrt((5*getExperience(e))/13);
	}
	
	public int getExperience(Element e) {
		return expData.get(e.getName());
	}
	
	public int getExperience(String elementName) {
		return expData.get(elementName);
	}
	
	public void addExperience(Element e, int exp) {
		expData.put(e.getName(), this.getExperience(e)+exp);
	}
	
	public void setLevel(Element e, int lvl) {
		this.setExperience( e, (int) (2*Math.pow(lvl, 2)*1.3) );
	}
	
	public void setExperience(Element e, int exp) {
		expData.put(e.getName(), exp);
	}

	private void loadBenderJSON() {
		String fileLoc = BendingExp.getPlugin().getDataFolder()+File.separator+"PlayerData"+File.separator + playerID+".json";
		File f = new File(fileLoc);
		if(f.exists() && !f.isDirectory()) {
			Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
			try (BufferedReader reader = new BufferedReader(new FileReader(fileLoc))) {
				Gson gson = new Gson();
				this.expData = gson.fromJson(reader, type);
				//Map<String, String> myMap = gson.fromJson(reader, type);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else { 
			for(Element e: Element.getElements()) {
				expData.put(e.getName(), 0);
			}		
			//create file for the player
			saveBenderJSON();
		}
	
	}
	
	//saves player to json
	public void saveBenderJSON() {
		String fileLoc = BendingExp.getPlugin().getDataFolder()+File.separator+"PlayerData"+File.separator+playerID+".json";
		try(Writer writer = new OutputStreamWriter(new FileOutputStream(fileLoc) , "UTF-8")) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(expData, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
