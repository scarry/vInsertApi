
import java.awt.Color;
import java.awt.Graphics2D;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Tile;

import api.Area;
import api.Node;
import api.Path;
import api.ScriptBase;
import api.Timer;
import api.Path.TraversableObject.Direction;


@ScriptManifest(authors = { "Fortruce" }, name = "Path Tester")
public class PathTester extends ScriptBase {

	Area fishingGuildArea = new Area(new Tile[] { new Tile(2589, 3397), new Tile(2594, 3397), new Tile(2599, 3397), 
			new Tile(2604, 3397), new Tile(2609, 3397), new Tile(2614, 3397), 
			new Tile(2619, 3397), new Tile(2624, 3397), new Tile(2624, 3384), 
			new Tile(2588, 3384), new Tile(2588, 3397), new Tile(2594, 3397) });

	Area ardyArea = new Area(new Tile[] { new Tile(2602, 3300), new Tile(2602, 3292), new Tile(2608, 3292), 
			new Tile(2608, 3300) });

	Tile[] fishToArdyPathTiles = new Tile[] { new Tile(2608, 3394), new Tile(2609, 3389), new Tile(2610, 3384), 
			new Tile(2611, 3379), new Tile(2611, 3374), new Tile(2611, 3370), 
			new Tile(2611, 3365), new Tile(2612, 3360), new Tile(2612, 3355), 
			new Tile(2612, 3350), new Tile(2612, 3345), new Tile(2612, 3340), 
			new Tile(2608, 3338), new Tile(2606, 3334), new Tile(2606, 3330), 
			new Tile(2607, 3325), new Tile(2607, 3320), new Tile(2606, 3315), 
			new Tile(2606, 3310), new Tile(2606, 3306), new Tile(2606, 3301), 
			new Tile(2606, 3296), new Tile(2607, 3296), new Tile(2607, 3297), 
			new Tile(2606, 3297) };
	
	Path fishToArdyPath;
	Path ardyToFishPath;
	boolean walkingToFish = false;
	boolean walkingToBank = false;
	
	
	public class InArea extends Node {
		@Override
		public boolean activate() {
			if ((fishingGuildArea.contains(localPlayer.getLocation()) ||
					ardyArea.contains(localPlayer.getLocation())))
					return true;
			return false;
		}
		@Override
		public void execute() {
			walkingToFish = false;
			walkingToBank = false;
		}
		
	}
	
	public class WalkToArdy extends Node {

		@Override
		public boolean activate() {
			if (!ardyArea.contains(localPlayer.getLocation())
					&& !walkingToFish) {
				walkingToBank = true;
				return true;
			}
			return false;
		}

		@Override
		public void execute() {
			log("traversing");
			fishToArdyPath.traverse(Direction.FORWARD);
			sleep(1000, 2000);
		}
		
	}
	
	public class WalkToFish extends Node {

		@Override
		public boolean activate() {
			if (!fishingGuildArea.contains(localPlayer.getLocation())
					&& !walkingToBank) {
				walkingToFish = true;
				return true;
			}
			return false;
		}

		@Override
		public void execute() {
			log("traversing");
			ardyToFishPath.traverse(Direction.FORWARD);
			sleep(1000, 2000);
		}
		
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		fishToArdyPath = new Path(fishToArdyPathTiles, getContext());
		ardyToFishPath = fishToArdyPath.reverse();
		
		submit(new WalkToFish());
		submit(new WalkToArdy());
		submit(new InArea());
		
		
		
//		fishToArdyPath.traverse(Direction.FORWARD);
		
		return true;
	}

	private static final Timer TIMER = new Timer(0);
	
	@Override
	public void render(Graphics2D g) {
        int[] point = {385, 2};
		
		//box
        g.setColor(new Color(63, 63, 43, 200));
        g.draw3DRect(375, 5, 139, 225, true);
        g.fill3DRect(375, 5, 139, 325, true);
       
        int height = g.getFontMetrics().getHeight();
		
        g.setColor(Color.WHITE);
        g.drawString("Fortruce - FightCave", point[0], point[1] += height);
        g.drawLine(383, 21, 495, 21);
		
        g.drawString("Run Time:  " + TIMER.toElapsedString(), point[0], point[1] += height);
        
        if (fishToArdyPath != null) {
        	g.drawString("in ardy: " + ardyArea.contains(localPlayer.getLocation()), point[0], point[1]+=height);
        	g.drawString("in guild: " + fishingGuildArea.contains(localPlayer.getLocation()), point[0], point[1]+=height);
        	g.drawString("walkingfish: " + walkingToFish, point[0], point[1]+=height);
        	g.drawString("walkingbank: " + walkingToBank, point[0], point[1]+=height);
        }
	}

}
