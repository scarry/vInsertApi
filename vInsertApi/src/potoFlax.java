import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Path;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.script.ScriptManifest;

//import randoms.SimpleRandoms;

import api.Node;
import api.ScriptBase;

@ScriptManifest(name = "potoFlax", authors = { "potofreak" }, description = "Picks and Banks Flax", version = 0.05)
public class potoFlax extends ScriptBase{

	public static final int FLAX_ID = 	17045;
	public static final int FLAX_INV = 	1780;
	public String 						status;
	public static Tile BANK_TILE = 		new Tile(2729,3493);
	public static Tile FLAX_TILE = 		new Tile(2741,3447);
	public static int BANK_ID = 		22836;
	
	Path pathToBank = new Path (
			new Tile(2739, 3441), new Tile(2740, 3446), new Tile(2737, 3443), new Tile(2733, 3443), new Tile(2730, 3445),
			new Tile(2737, 3443), new Tile(2732,3444), new Tile(2728, 3451), new Tile(2727, 3459), new Tile(2726, 3466),
			new Tile(2726, 3475), new Tile(2725, 3481), new Tile(2726, 3487), BANK_TILE
			);
	
	Path pathToFlax = pathToBank.reverse();
	
	public class pickFlax extends Node{

		@Override
		public boolean activate() {
			return inventory.freeSpace() > 0 && FLAX_TILE.distanceTo(players.getLocalPlayer().getLocation()) <= 7;
		}

		@Override
		public void execute() {
			pathToBank.reset();
			pathToFlax.reset();
			GameObject flax = objects.getNearest(new Filter<GameObject>(){
				public boolean accept(GameObject obj) {
					if (obj.getId() == FLAX_ID)
						return true;
					return false;
				}
			});
			if(flax != null){
				//Fancy math for single click on flax
				flax.interact("Pick");
				//mouse.click(flax.hullPoint(flax.hull()).x, flax.hullPoint(flax.hull()).y);
				//Point[] points = flax.getPoints();
				//log("Points: " + points.length);
				//mouse.click(flax.getPoints()[1].x, flax.getPoints()[1].y);
				sleep(200, 400);
			}
			else
				log("Null flax");
		}
		
	}
	
	public class walkToBank extends Node{

		@Override
		public boolean activate() {
			return inventory.freeSpace() == 0 && BANK_TILE.distanceTo(players.getLocalPlayer().getLocation()) >= 2; 
		}

		@Override
		public void execute() {
			//navigation.toggleRunning();
			navigation.navigate(pathToBank, 1, NavigationPolicy.MINIMAP);	
			sleep(500, 800);
		}
		
	}
	
	public class bankFlax extends Node{

		@Override
		public boolean activate() {
			return inventory.freeSpace() < 28 && BANK_TILE.distanceTo(players.getLocalPlayer().getLocation()) <= 1;
		}

		@Override
		public void execute() {
			GameObject bankObj = objects.getNearest(new Filter<GameObject>(){
				public boolean accept(GameObject obj) {
					if (obj.getId() == BANK_ID)
						return true;
					return false;
				}
			});
			if(bankObj != null && !bank.isOpen()){
				bankObj.interact("Bank");
				sleep(300, 600);
				if(!bank.isOpen())
					return;
			}
			
			if(inventory.freeSpace() < 28){
				bank.depositAll();
				sleep(800, 1200);
				if(inventory.freeSpace() < 28)
					return;
			}
			sleep(500, 800);
		}
		
	}
	
	public class walkToFlax extends Node{

		@Override
		public boolean activate() {
			return inventory.getCount(false, FLAX_INV) != 28 && FLAX_TILE.distanceTo(players.getLocalPlayer().getLocation()) > 7;
		}

		@Override
		public void execute() {
			navigation.navigate(pathToFlax, 1, NavigationPolicy.MINIMAP);
			sleep(500, 800);
		}
		
	}
	
	@Override
	public boolean init() {
		//submit(new SimpleRandoms(this.getContext(),new Tile(0,0)));
		submit(new pickFlax());
		submit(new walkToBank());
		submit(new bankFlax());
		submit(new walkToFlax());
		status = "Starting script";
		return true;
	}

	private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
	}
	
	private final Color COLOR_BLACK = new Color(0,0,0);
	private final Color COLOR_WHITE = new Color(255,255,255);
	
	private final Color COLOR_PINK = new Color(255,156,253,50);
	private Rectangle backgroundBox = new Rectangle(3, 341, 514, 475);
	private final Font statusFont = new Font("Garamond", 1, 20);
	private final Font expFont = new Font("Garamond",1,12);
	
	private final Image back = getImage("http://img824.imageshack.us/img824/9691/blargq.png");
	private final Image logo = getImage("http://img715.imageshack.us/img715/4545/logogyi.png");
	private final Font ShowHideFont = new Font("Garamond", 1, 16);
	
	private int ShowHideX = 450;
    private int ShowHideY = 420;
    private int ShowHideWidth = 60;
    private int ShowHideHeight = 15;
	
	@Override
	public void render(Graphics2D g) {
		//gp = inventory.getCount(true, GP_ID);
		
        g.drawImage(back, 4, 342, null);
	    g.drawImage(logo, 230, 305, null);
        
	    g.setColor(COLOR_WHITE);
	    g.setFont(expFont);
	    g.drawString("pickFlax: " + new pickFlax().activate(),110, 355);
	    g.drawString("walkToBank: " + new walkToBank().activate(),110,370);
	    g.drawString("bankFlax: " + new bankFlax().activate(),110,385);
	    g.drawString("walkToFlax: " + new walkToFlax().activate(),110,400);
	    //g.drawString("GP/HR: " + Utilities.perHour(gp-gpStart) + " (+" + (gp-gpStart) + ")",13,445);
	    g.drawString("Distance to BANK: " + BANK_TILE.distanceTo(players.getLocalPlayer().getLocation()),13,425);
	    g.drawString("Distance to FLAX: " + FLAX_TILE.distanceTo(players.getLocalPlayer().getLocation()),13,445);
	    g.drawString("Free space: " + inventory.freeSpace(), 13, 465);
	    g.drawString("Test: " + new Tile(1,1).distanceTo(new Tile(1,3)),13,405);
	    
	    
	    //g.setFont(statusFont);
	    //String time = String.format("Time: %02d:%02d:%02d",hours,minutes,seconds);
	    //g.drawString("Time: " + timer.toElapsedString(), 320, 410);
	    
	    g.drawString("Status: " , 340, 435);
	    //g.drawString(status, 260, 460);
	    if(ScriptBase.getActiveNode() != null)
	    	g.drawString("Node: " + ScriptBase.getActiveNode().toString(),260,460);
	    else
	    	g.drawString(status.toString(),260,475);
	    g.setColor(COLOR_PINK);	
	}

}
