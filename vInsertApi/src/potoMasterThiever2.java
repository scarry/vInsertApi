import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.generic.Interactable;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.util.Utils;

import randoms.RandomHandler;

import api.Node;
import api.SkillData.Rate;
import api.ScriptBase;
import api.SkillData;
import api.Time;
import api.Timer;
import api.Utilities;

@ScriptManifest(name = "potoMasterThiver", authors = { "potofreak" }, description = "Thieves Master Farmer", version = 0.3)

public class potoMasterThiever2 extends ScriptBase{
	
	private final Timer timer = new Timer(0);
	//private final SkillData sd = new SkillData(timer);
	
	long     start_time = 0;
	public String status = "Working on status";
	boolean showPaint;	
	
	int[] BANK_ID = {494};
	int[] DROPABLES_ID = {5100, 5306, 5105, 5098, 5283, 5106, 5282, 5310, 5307, 5325, 5320, 5281, 5099, 5308, 5312, 5311, 5104, 5102, 5309, 5097, 5103, 5319, 5292, 5293, 5294};
	static int FARMER_ID = 2234;
	
	Tile BANK_TILE = new Tile(Utils.random(3092,3095), Utils.random(3242,3244));
	Tile FARMER_TILE = new Tile(Utils.random(3078,3081),Utils.random(3249,3251));
	
	int NumOfFood = 6;
	int foodHeal = 4;
	int[] CAKE_3 = {1892};
	int[] CAKE_2 = {1894};
	int[] CAKE_1 = {1896};
	int[] BREAD_ID = {2310};
	
	int STUN_ANIMATION = 424;
	int STEAL_ANIMATION = 881;

    Utilities utilities;
	
	public Npc farmer;
	
	boolean foodAvailable = true;
	
	int expPerHour = 0;
	int health = 10;
	int currentState = 0;
	
	int thievExp, thievStartExp;

	/*
	private final String generateString(int index){
		final StringBuilder sb = new StringBuilder();
		//sb.append(Skills.getName(index));
		sb.append(" - Level: ");
		sb.append(sd.getLevel(index));
		sb.append("(+" + sd.level(index) + ")");
		sb.append(" - Exp/hr: ");
		sb.append(sd.experience(Rate.HOUR, index));
		sb.append("(+" + sd.experience(index) + ")");
		sb.append(" - TTL: ");
		sb.append(Time.format(sd.timeToLevel(Rate.HOUR, index)));
		return sb.toString();
	}
	*/
	/*
	public class DropTrash extends Node{
		
		@Override
		public boolean activate(){
			return inventory.isFull() || Utils.random(1, 1000) == 69;
		}
		
		@Override
		public void execute(){
			status = "Dropping Trash";
			for(int i : DROPABLES_ID){
				if(inventory.contains(Filters.itemId({i}))){
					Item drop = inventory.getItem(i);
					
					drop.interact("Drop");
					sleep(Utils.random(100, 250));
				}
			}
		}
	}
	*/
	public class WalkToBank extends Node{

		@Override
		public boolean activate() {
			return BANK_TILE.distanceTo(players.getLocalPlayer().getLocation()) > 2 && 
					(inventory.getCount(false, CAKE_1) == 0 &&
					inventory.getCount(false, CAKE_2) == 0 &&
					inventory.getCount(false, BREAD_ID) == 0);
		}

		@Override
		public void execute() {
			status = "Walking to Bank";
			navigation.navigate(BANK_TILE, NavigationPolicy.MINIMAP);
			sleep(Utils.random(700, 1000));			
		}
		
	}
	
	public class OpenBank extends Node{

		@Override
		public boolean activate() {
			return !bank.isOpen() && 
					BANK_TILE.distanceTo(players.getLocalPlayer().getLocation()) <= 2 && 
					inventory.getCount(false, BREAD_ID) != NumOfFood;
		}

		@Override
		public void execute() {
			status = "Opening Bank";
			Npc banker = npcs.getNearest(localPlayer.getLocation(),Filters.npcId(BANK_ID));
			if(banker != null){
				camera.rotateToActor(banker);
				sleep(Utils.random(300, 500));
				banker.interact("Bank");
				sleep(Utils.random(800, 1300));
			}			
		}
	}
	
	public class DepositAll extends Node{

		@Override
		public boolean activate() {
			return bank.isOpen() && inventory.getCount(false, BREAD_ID) != NumOfFood && !inventory.isEmpty();
		}

		@Override
		public void execute() {
			status = "Depositing all";
			bank.depositAll();	
			sleep(400,800);
		}
		
	}
	
	public class WithdrawFood extends Node{

		@Override
		public boolean activate() {
			return bank.isOpen() && inventory.getCount(false, BREAD_ID) != NumOfFood && inventory.isEmpty();
		}

		@Override
		public void execute() {
			status = "Withdrawing food";
			bank.withdraw(Filters.itemId(BREAD_ID), NumOfFood);
			sleep(Utils.random(500,800));
			foodAvailable = true;			
		}		
	}
	
	public class WalkToFarmerTile extends Node{

		@Override
		public boolean activate() {
			return FARMER_TILE.distanceTo(localPlayer.getLocation()) > 7 && inventory.getCount(false,BREAD_ID) == NumOfFood;
		}

		@Override
		public void execute() {
			status = "Walking to Farmer Tile";
			navigation.navigate(FARMER_TILE,NavigationPolicy.MINIMAP);
			sleep(Utils.random(300, 700));			
		}
		
	}
	
	public class GetFarmer extends Node{

		@Override
		public boolean activate() {
			return (farmer == null || Utils.random(1, 20) == 4) && npcs.getNearest(FARMER_ID) != null;
		}

		@Override
		public void execute() {
			status = "Getting farmer";
			farmer = npcs.getNearest(FARMER_ID);
		}
		
	}
	
	public class WalkToFarmer extends Node{

		@Override
		public boolean activate() {
			return farmer != null && health > 7 && 
					(inventory.getCount(false,CAKE_1) > 0 ||
					 inventory.getCount(false,CAKE_2) > 0 ||
					 inventory.getCount(false,BREAD_ID) > 0) &&
					 !camera.isVisible(farmer);
					 //&& !farmer.isVisible();
		}

		@Override
		public void execute() {
			status = "walking to farmer";
			if(Utils.random(1, 6) == 3)
				camera.rotateToTile(farmer.getLocation());
			if(localPlayer.getLocation().distanceTo(farmer.getLocation()) > 1){
				navigation.navigate(farmer.getLocation(),NavigationPolicy.MINIMAP);
				sleep(Utils.random(300, 500));
				}
		}
	}
	

	public class MoveMouseRandom extends Node{

		@Override
		public boolean activate() {
			return Utils.random(1, 10) == 4;
		}

		@Override
		public void execute() {
			mouse.move(Utils.random(100, 300), Utils.random(100, 300));
			
		}
		
	}
		
	
	public class PickpocketFarmer extends Node{
		@Override
		public boolean activate() {
			return farmer != null && health >= 7 && 
					(inventory.getCount(false,CAKE_1) > 0 || 
						inventory.getCount(false,CAKE_2) > 0 || 
						inventory.getCount(false,BREAD_ID) > 0) &&
                    camera.isVisible(farmer);
						//&& farmer.isVisible();
		}

		@Override
		public void execute() {
			if(Utils.random(1, 10) == 4){
				camera.rotateToActor(farmer);
				//mouse.move(Utils.random(100, 500), Utils.random(100, 500));
			}
			status = "Stealing from Farmer";

            utilities.interact(farmer,"Pickpocket");
			sleep(Utils.random(100, 200));
			health = players.getLocalPlayer().getHealth();
			}
	}



	/*
	public class CheckStun extends Node{

		@Override
		public boolean activate() {
			return players.getLocalPlayer().getAnimation() != -1;
		}

		@Override
		public void execute() {
			status = "Checking Stun";
			if(players.getLocalPlayer().getAnimation() == STUN_ANIMATION){
				status = "STUNNED";
				health = getHealth();
				sleep(Utils.random(4000, 6000));
			}			
		}
	
	}
*/
	
	public class Heal extends Node{

		@Override
		public boolean activate() {
			return  health < 7 && 
					(inventory.getCount(false, CAKE_1) > 0 ||
					 inventory.getCount(false, CAKE_2) > 0 ||
					 inventory.getCount(false, BREAD_ID) > 0);			
		}

		@Override
		public void execute() {
			status = "Healing";
			Item food = inventory.getItem(BREAD_ID);
			if(food != null){
				int slot = inventory.indexOf(food);
				Point pFood = inventory.getClickPoint(slot);
				mouse.click(pFood.x, pFood.y);
				sleep(Utils.random(900, 1400));
			}
			else{
				food = inventory.getItem(CAKE_2);
				if(food != null){
					int slot = inventory.indexOf(food);
					Point pFood = inventory.getClickPoint(slot);
					mouse.click(pFood.x, pFood.y);
					sleep(Utils.random(900, 1400));
				}
				else{
					food = inventory.getItem(CAKE_3);
						if(food != null){
							int slot = inventory.indexOf(food);
							Point pFood = inventory.getClickPoint(slot);
							mouse.click(pFood.x, pFood.y);
							sleep(Utils.random(900, 1400));
						}
						}
					}
			health = players.getLocalPlayer().getHealth();
			}	
		}
	
					
	@Override
	public boolean init() {
		//submit(new TestCastle());
		//new RandomHandler(BANK_TILE,Skills.HITPOINTS,this);
		utilities = new Utilities(getContext());
        submit(new Heal());
		submit(new GetFarmer());
		submit(new PickpocketFarmer());
		//submit(new DropTrash());		
		submit(new WalkToBank());
		submit(new OpenBank());
		submit(new DepositAll());
		submit(new WithdrawFood());
		submit(new WalkToFarmerTile());
		submit(new WalkToFarmer());
		//submit(new MoveMouseRandom());
		//submit(new CheckStun());
		return true;
	}

	
	
	/*
	@Override
	public boolean onStart() {
		start_time = System.currentTimeMillis();
		//thievStartExp = Skills.getXp(Skill.THIEVING);
		showPaint = true;
		health = 10;
		status = "Testing status";
		farmer = Npcs.getNearest(FARMER_ID);
		return true; 
	}
*/
	/*
	 * 
	 * PROGGY PAINT
	 * 
	 */
	
	
	private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
	}
	
	private final Color COLOR_BLACK = new Color(0,0,0);
	private final Color COLOR_WHITE = new Color(255,255,255);
	private final Color COLOR_RANDOM = new Color(227, 72, 15, 238);
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
		//START: Code generated using Enfilade's Easel
	    //Graphics2D g = (Graphics2D)g1;
		
		//if(showPaint){
	    
	    //long millis = System.currentTimeMillis() - start_time;
        //long hours = millis / (1000*60*60);
        //millis -= hours * (1000 * 60 * 60);
        //long minutes = millis / (1000*60);
        //millis -= minutes * (1000*60);
        //long seconds = millis / 1000;
        /*
        float expPerSec = 0;
        if((minutes > 0 || hours > 0 || seconds > 0) && expPerSec > 0){
        	expPerSec = ((float) BoltCounter)/(float)(seconds + (minutes*60) + (hours*60*60));
        }
        float expPerMin = expPerSec * 60;
        float expPerHour = expPerMin * 60;
        */
        //Draw Custom Background and Si


        //g.drawImage(back, 4, 342, null);
	    //g.drawImage(logo, 230, 305, null);
        
	    g.setColor(COLOR_WHITE);
	    g.setFont(expFont);

        g.drawString("PickpocketFarmer: " + new PickpocketFarmer().activate(),13,150);
        g.drawString("Heal: " + new Heal().activate(),13,165);
        g.drawString("WalkToBank: " + new WalkToBank().activate(),13,180);
        g.drawString("OpenBank: " + new OpenBank().activate(),13,195);
        g.drawString("DepositAll: " + new DepositAll().activate(),13,210);
        g.drawString("WithdrawFood: " + new WithdrawFood().activate(),13,225);
        g.drawString("WalkToFarmerTile: " + new WalkToFarmerTile().activate(),13,240);
        g.drawString("WalkToFarmer: " + new WalkToFarmer().activate(),13,255);
        g.drawString("Farmer visible: " + camera.isVisible(farmer), 13, 270);

	    g.drawString("Health:" + health, 13, 365);
	    //g.drawString("Animation: " + players.getLocalPlayer().getAnimation(), 13, 385);
	   // g.drawString(generateString(Skills.THIEVING), 13, 405);
	    //g.drawString("Capacity: " + inventory.getCapacity(), 13, 425);
	    //g.drawString("Test test", 13, 445);
	    //g.drawString("Test test123", 13, 465);
	    //g.setFont(statusFont);
	    //String time = String.format("Time: %02d:%02d:%02d",hours,minutes,seconds);
	    //g.drawString("Time: " + timer.toElapsedString(), 320, 410);
	    
	    //g.drawString("Status: ", 340, 435);
	    //if(this.getActiveNode() != null)
	    	//g.drawString(status, 260, 460);
	    
          /*
	    if(farmer != null){
            Polygon[] drawing = farmer.getModel().getPolygons();
            g.setColor(COLOR_RANDOM);
	     	//g.fillPolygon(farmer.hull());
            if(drawing.length > 0 ) {
                for(Polygon i : drawing)
                    g.fillPolygon(i);
            }
            g.setColor(COLOR_BLACK);
            g.drawPolygon(farmer.hull());
		}
		  */
		/*
	    if(showPaint){
			g.setColor(Color.GREEN);
			g.fillRect(ShowHideX, ShowHideY, ShowHideWidth, ShowHideHeight);
			g.setColor(Color.BLACK);
			g.setFont(ShowHideFont);
			g.drawString("Hide", 458, 433);
		}
	    else{
			g.setColor(Color.RED);
			g.fillRect(ShowHideX, ShowHideY, ShowHideWidth, ShowHideHeight);
			g.setColor(Color.BLACK);
			g.setFont(ShowHideFont);
			g.drawString("Show", 458, 433);
	    }
	   */
	    
	} 
}



