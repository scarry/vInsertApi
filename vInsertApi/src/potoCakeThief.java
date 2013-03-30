import java.awt.*;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;

import api.Node;
import api.ScriptBase;

@ScriptManifest(name = "potoCakeThief", authors = { "potofreak" }, description = "Power thieves cake stall", version = 0.2)

public class potoCakeThief extends ScriptBase{

	
	public static Tile CAKE_TILE =  	new Tile(2658, 3312);
	public static Tile SAFE_TILE =  	new Tile(2648, 3316);
	public static int[] STALL_ID = 		{2561};
	int CAKE_3 = 						1892;
	int CAKE_2 = 						1894;
	int CAKE_1 = 						1896;
	int CHOCOLATE_CAKE_ID = 			1902;
	int BREAD_ID = 						2310;
	int[] FOOD_ID = 					{CAKE_3,CAKE_2,CAKE_1,BREAD_ID,CHOCOLATE_CAKE_ID};
	public int health;
	
	public class StealFromStall extends Node{

		@Override
		public boolean activate() {
			return localPlayer.getLocation().distanceTo(CAKE_TILE) <= 2 && 
					objects.getNearest(Filters.objectId(STALL_ID)) != null &&
					!localPlayer.isInCombat() && 
					localPlayer.getLocation().getX() > 2656 &&
					!inventory.isFull();
 		}

		@Override
		public void execute() {
			GameObject stall = objects.getNearest(Filters.objectId(STALL_ID));
			if(stall != null){
				stall.interact("Steal-from");
				//sleep(50, 100);
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
					health >= localPlayer.getMaxHealth();
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
			return !localPlayer.isInCombat() && inventory.isFull(); 
		}

		@Override
		public void execute() {
			Item drop = inventory.getItem(FOOD_ID);
			if(drop != null){
				int slot = inventory.indexOf(drop);
				inventory.interact(slot, "Drop");
			}
			sleep(200, 400);			
		}
		
	}
	
	public class Heal extends Node{

		@Override
		public boolean activate() {
			return health < localPlayer.getMaxHealth() &&
					inventory.getItem(FOOD_ID) != null && 
					((localPlayer.getLocation().distanceTo(SAFE_TILE) < 6 &&
					!localPlayer.isInCombat()) || (localPlayer.isInCombat() && localPlayer.isMoving()));
		}

		@Override
		public void execute() {
			Item food;
			if(inventory.getItem(FOOD_ID) != null){
				food = inventory.getItem(FOOD_ID);
				int slot = inventory.indexOf(food);
				Point pFood = inventory.getClickPoint(slot);
				mouse.click(pFood.x, pFood.y);
				sleep(Utils.random(900, 1400));
			}
			health = players.getLocalPlayer().getHealth();
		}
	}

	
	@Override
	public boolean init() {
		submit(new StealFromStall());
        submit(new Heal());
		submit(new RunToSafeSpot());
		submit(new RunToStall());
		submit(new MakeRoom());
		return true;
	}

	@Override
	public void render(Graphics2D g) {
		health = players.getLocalPlayer().getHealth();
		g.drawString(String.format("Health: %d", health), 13, 240);
		utilities.renderNodes(this, g, 13, 205);
        utilities.drawTile(this,g,CAKE_TILE);
        utilities.drawTile(this,g,SAFE_TILE);
        Point progBar = new Point(10,300);
        utilities.drawProgressBar(g,skillData,Skills.THIEVING,progBar,500,22, Color.yellow,Color.red,Color.black);
    }

}
