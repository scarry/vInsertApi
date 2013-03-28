import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Utils;

import api.Node;
import api.ScriptBase;

@ScriptManifest(name = "potoCakeThief", authors = { "potofreak" }, description = "Power thieves cake stall", version = 0.1)

public class potoCakeThief extends ScriptBase{

	
	public static Tile CAKE_TILE =  	new Tile(2657, 3312);
	public static Tile SAFE_TILE =  	new Tile(2671, 3306);
	public static int[] STALL_ID = 		{2561};
	int[] CAKE_3 = 						{1892};
	int[] CAKE_2 = 						{1894};
	int[] CAKE_1 = 						{1896};
	int[] CHOCOLATE_CAKE_ID = 			{1902};
	int[] BREAD_ID = 					{2310};
	public int health;
	
	public class StealFromStall extends Node{

		@Override
		public boolean activate() {
			return localPlayer.getLocation().distanceTo(CAKE_TILE) <= 2 && 
					objects.getNearest(Filters.objectId(STALL_ID)) != null &&
					!localPlayer.isInCombat() && 
					localPlayer.getLocation().getX() > 2656;
 		}

		@Override
		public void execute() {
			GameObject stall = objects.getNearest(Filters.objectId(STALL_ID));
			if(stall != null){
				stall.interact("Steal-from");
				sleep(200, 400);
			}
			
		}
		
	}
	
	public class RunToSafeSpot extends Node{

		@Override
		public boolean activate() {
			return localPlayer.isInCombat();
		}

		@Override
		public void execute() {
			if(!localPlayer.isMoving())
				navigation.navigate(SAFE_TILE, NavigationPolicy.MINIMAP);
			sleep(200, 400);	
			health = players.getLocalPlayer().getHealth();
		}
		
	}
	
	public class RunToStall extends Node{

		@Override
		public boolean activate() {
			return (localPlayer.getLocation().distanceTo(CAKE_TILE) > 2 ||
					localPlayer.getLocation().getX() <= 2656) && 
					!localPlayer.isInCombat() && 
					health >= 8;
		}

		@Override
		public void execute() {
			if(!localPlayer.isMoving()){
				if(localPlayer.getLocation().distanceTo(CAKE_TILE) > 4)
					navigation.navigate(CAKE_TILE, NavigationPolicy.MINIMAP);
				else{ 
					navigation.navigate(CAKE_TILE, NavigationPolicy.SCREEN);
					sleep(600, 800);
				}
				sleep(800,1200);
			}
		}
		
	}
	
	public class MakeRoom extends Node{

		@Override
		public boolean activate() {
			return localPlayer.getLocation().distanceTo(CAKE_TILE) <= 2 && 
					objects.getNearest(Filters.objectId(STALL_ID)) == null &&
					!localPlayer.isInCombat(); 
		}

		@Override
		public void execute() {
			for(int i = 1; i < 28; i++){
				Item drop = inventory.getItem(i);
				if(drop != null){
					int slot = inventory.indexOf(drop);
					inventory.interact(slot, "Drop");
					break;
				}
			}
			sleep(200, 400);			
		}
		
	}
	
	public class Heal extends Node{

		@Override
		public boolean activate() {
			return  health < 8 && 
					(inventory.getCount(false, CAKE_1) > 0 ||
					 inventory.getCount(false, CAKE_2) > 0 ||
					 inventory.getCount(false, CAKE_3) > 0 ||
					 inventory.getCount(false, CHOCOLATE_CAKE_ID) > 0 ||
					 inventory.getCount(false, BREAD_ID) > 0) &&
					 localPlayer.getLocation().distanceTo(SAFE_TILE) < 4;			
		}

		@Override
		public void execute() {
			Item food;
			if(inventory.getCount(false, CHOCOLATE_CAKE_ID) > 0){
				food = inventory.getItem(CHOCOLATE_CAKE_ID);
				int slot = inventory.indexOf(food);
				Point pFood = inventory.getClickPoint(slot);
				mouse.click(pFood.x, pFood.y);
				sleep(Utils.random(900, 1400));
			}
			else if(inventory.getCount(false, BREAD_ID) > 0){
				food = inventory.getItem(BREAD_ID);
				if(food != null){
					int slot = inventory.indexOf(food);
					Point pFood = inventory.getClickPoint(slot);
					mouse.click(pFood.x, pFood.y);
					sleep(Utils.random(900, 1400));
				}
			}
			else if(inventory.getCount(false, CAKE_3) > 0){
				food = inventory.getItem(CAKE_3);
				if(food != null){
					int slot = inventory.indexOf(food);
					Point pFood = inventory.getClickPoint(slot);
					mouse.click(pFood.x, pFood.y);
					sleep(Utils.random(900, 1400));
				}
			}
			else if(inventory.getCount(false, CAKE_2) > 0){
				food = inventory.getItem(CAKE_2);
				if(food != null){
					int slot = inventory.indexOf(food);
					Point pFood = inventory.getClickPoint(slot);
					mouse.click(pFood.x, pFood.y);
					sleep(Utils.random(900, 1400));
				}
			}
			else if(inventory.getCount(false, CAKE_1) > 0){
				food = inventory.getItem(CAKE_1);
				if(food != null){
					int slot = inventory.indexOf(food);
					Point pFood = inventory.getClickPoint(slot);
					mouse.click(pFood.x, pFood.y);
					sleep(Utils.random(900, 1400));
				}
			}
			health = players.getLocalPlayer().getHealth();
			}	
		}
	
	@Override
	public boolean init() {
		submit(new StealFromStall());
		submit(new RunToSafeSpot());
		submit(new RunToStall());
		submit(new MakeRoom());
		return true;
	}

	@Override
	public void render(Graphics2D g) {
		health = players.getLocalPlayer().getHealth();
		g.drawString("Health: " + health, 13, 240);
		g.drawString("StealFromStall: " + new StealFromStall().activate(),13, 255);
		g.drawString("RunToSafeSpot: " + new RunToSafeSpot().activate(),13,270);
		g.drawString("RunToStall: " + new RunToStall().activate(), 13, 285);
		g.drawString("MakeRoom: " + new MakeRoom().activate(), 13, 300);
		g.drawString("Heal: " + new Heal().activate(), 13, 315);
	}
	

}
