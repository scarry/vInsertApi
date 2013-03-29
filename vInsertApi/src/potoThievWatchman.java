


import api.Node;
import api.ScriptBase;
import api.Utilities;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Utils;
import randoms.RandomHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;


//import nl.wbot.bot.L;
/*
import api.ExConditions.IConditional;
import api.ExUtilities;
import api.ExConditions;
import api.Node;
import api.SkillData.Rate;
import api.ScriptBase;
import api.SkillData;
import api.Skills;
import api.Time;
import api.Timer;
import api.Utilities;
import api.randoms.RandomHandler;
*/
//import bot.script.BotScript;
//import bot.script.enums.Skill;
//import bot.script.methods.Bank;
//import bot.script.methods.Calculations;
//import bot.script.methods.Camera;
//import bot.script.methods.Game;
//import bot.script.methods.Game;
//import bot.script.methods.Game;
//import bot.script.methods.GroundItems;
//import bot.script.methods.Inventory;
//import bot.script.methods.Methods;
//import bot.script.methods.Npcs;
//import bot.script.methods.Objects;
//import bot.script.methods.Players;
//import bot.script.methods.Skills;
//import bot.script.methods.Walking;
//import bot.script.methods.Widgets;
//import bot.script.util.Random;
//import bot.script.wrappers.GameObject;
//import bot.script.wrappers.Item;
//import bot.script.wrappers.NPC;
//import bot.script.wrappers.Tile;
//import bot.script.wrappers.Component;

@ScriptManifest(name = "potoThievWatchman", authors = {"potofreak"}, description = "Thieves Watchman Guards", version = 0.04)
public class potoThievWatchman extends ScriptBase {

    public static enum ScriptState {
        GET_WATCHMAN, HEAL, PICKPOCKET, DEFAULT;
    }

    //private final Timer timer = new Timer(0);
    //private final SkillData sd = new SkillData(timer);

    long start_time = 0;
    public String status = "Working on status";
    boolean showPaint = true;


    int[] BANK_ID = {2213};
    int[] DROPABLES_ID = {5100, 5306, 5105, 5098, 5283, 5106, 5282, 5310, 5307, 5325, 5320, 5281, 5099, 5308, 5312, 5311, 5104, 5102, 5309, 5097, 5103, 5319, 5292, 5293, 5294};
    static int[] WATCHMAN_ID = {34};
    static int BREAD_ID[] = {2310};
    private Utilities utilities;

    Tile TRELLIS_TILE = new Tile(2548, 3120);

    Item bread;

    int NumOfFood = 15;
    int foodHeal = 4;
    int CAKE_3 = 1892;
    int CAKE_2 = 1894;
    int CAKE_1 = 1896;
    int GP_ID = 996;

    static int gpStart;
    int gp;

    //int STUN_ANIMATION = 424;
    int STUN_ANIMATION = 420;
    //int STEAL_ANIMATION = 881;

    Npc watchman;

    boolean foodAvailable = true;

    //int expPerHour = 0;
    int health;
    //int currentState = 0;

    //int thievExp, thievStartExp;
/*
    private final String generateString(int index){
		final StringBuilder sb = new StringBuilder();
		sb.append(Skills.getName(index));
		sb.append(" - Level: ");
		sb.append(sd.getLevel(index));
		sb.append("(+" + sd.level(index) + ")");
		sb.append(" - Exp/hr: ");
		sb.append(Utilities.perHour(sd.experience(index)));
		sb.append("(+" + sd.experience(index) + ")");
		sb.append(" - TTL: ");
		sb.append(Time.format(sd.timeToLevel(Rate.HOUR, index)));
		return sb.toString();
	}

	public class isStealing implements IConditional{

		@Override
		public boolean execute() {
			return Players.getLocalPlayer().getAnimation() == STUN_ANIMATION || Players.getLocalPlayer().getAnimation() == STEAL_ANIMATION;
		}
		
	}
	
	public class isOnTrellisTile implements IConditional{

		@Override
		public boolean execute() {
			return Calculations.distanceTo(TRELLIS_TILE) <= 2;
		}
		
	}
	
	public class isInThievingRoom implements IConditional{

		@Override
		public boolean execute() {
			return Game.getPlane() == 1;
		}
		
	}
	
	public class ClimbIntoRoom extends Node{

		@Override
		public boolean activate() {
			return Game.getPlane() == 0 && Game.isLoggedIn();
		}

		@Override
		public void execute() {
			Methods.log("Ground floor - Logging out");
			Game.logout();
			Methods.sleep(6000, 9000);
			/*
			status = "Walking to Trellis";
			Walking.walkTo(TRELLIS_TILE);
			//If not on Tile, return out of function
			if(!ExConditions.makeCondition(new isOnTrellisTile(), 12000))
				return;
			
			status = "Made it to Trellis";
			Methods.sleep(Random.nextInt(5000, 7000));	
			
			
		}
		
	}
	*/
	/*
	public class didEat implements IConditional{

		private int currInventoryCount;
		
		public didEat(int currinventcount){
			currInventoryCount = currinventcount;
		}
		
		@Override
		public boolean execute() {
			return (currInventoryCount - 1) == Inventory.getCount();
		}
		
	}
	
*/

    public class walkToWatchman extends Node {

        @Override
        public boolean activate() {
            return watchman != null && players.getLocalPlayer().getLocation().distanceTo(watchman.getLocation()) > 1;
        }

        @Override
        public void execute() {
            navigation.navigate(watchman.getLocation(), NavigationPolicy.MINIMAP);
            sleep(500, 900);
        }

    }

    public class getHealth extends Node {

        @Override
        public boolean activate() {
            return true;
        }

        @Override
        public void execute() {

        }

    }

    public class getWatchman extends Node {

        @Override
        public boolean activate() {
            return watchman == null && npcs.getNearest(WATCHMAN_ID) != null;
        }

        @Override
        public void execute() {
            status = "Getting watchman";
            watchman = npcs.getNearest(WATCHMAN_ID);
            health = players.getLocalPlayer().getHealth();
            return;
        }

    }

    public class heal extends Node {

        @Override
        public boolean activate() {
            return health <= 7 && inventory.getCount(false, BREAD_ID) > 0;
        }

        @Override
        public void execute() {
            //if(game.getCurrentTab() != Game.Tabs.INVENTORY)
            //	game.openTab(Game.Tabs.INVENTORY);
            status = "Healing";
            bread = inventory.getItem(BREAD_ID);
            int slot = inventory.indexOf(bread);
            Point food = inventory.getClickPoint(slot);
            mouse.click(food.x, food.y);
            //if(!ExConditions.waitFor(new didEat(Inventory.getCount()), 3000))
            //	return;
            sleep(700, 1000);
            health = players.getLocalPlayer().getHealth();
        }

    }

    public class pickpocket extends Node {

        @Override
        public boolean activate() {
            return watchman != null && (health >= 6 || inventory.getCount(false, BREAD_ID) > 0);
        }

        @Override
        public void execute() {
            if (Utils.random(1, 10) == 4)
                camera.rotateToTile(watchman.getLocation());
            status = "Pickpocket";
            mouse.click(watchman.getPoints()[0].x, watchman.getPoints()[0].y);
            //watchman.interact("Pickpocket");
            sleep(300, 500);
            //if(!ExConditions.waitFor(new isStealing(),1500))
            //	return;
            health = players.getLocalPlayer().getHealth();
        }

    }

    @Override
    public boolean init() {
        health = players.getLocalPlayer().getHealth();
        new RandomHandler(new Tile(0, 0), 0, this);
        submit(new getWatchman());
        submit(new heal());
        submit(new pickpocket());
        //submit(new walkToWatchman());
        return true;
    }

    public void doGetWatchman() {
        status = "Getting watchman";
        watchman = npcs.getNearest(WATCHMAN_ID);
        return;
    }


    public void doHeal() {
        status = "Healing";
        int slot = inventory.indexOf(bread);
        Point food = inventory.getClickPoint(slot);
        mouse.click(food.x, food.y);
        //if(!ExConditions.waitFor(new didEat(Inventory.getCount()), 3000))
        //	return;
        sleep(700, 1000);
        health = players.getLocalPlayer().getHealth();
    }

    public void doPickpocket() {
        status = "Pickpocket";
        watchman.interact("Pickpocket");
        //mouse.click(watchman);
        //watchman.interact("Pickpocket");
        sleep(300, 500);
        //if(!ExConditions.waitFor(new isStealing(),1500))
        //	return;

        if (players.getLocalPlayer().getAnimation() == STUN_ANIMATION) {
            status = "STUNNED";
            sleep(200, 400);
        }
        health = players.getLocalPlayer().getHealth();

    }
	
	/*
	 * 
	 * PROGGY PAINT
	 * 
	 */


    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    private final Color COLOR_BLACK = new Color(0, 0, 0);
    private final Color COLOR_WHITE = new Color(255, 255, 255);

    private final Color COLOR_PINK = new Color(255, 156, 253, 30);
    private Rectangle backgroundBox = new Rectangle(3, 341, 514, 475);
    private final Font statusFont = new Font("Garamond", 1, 20);
    private final Font expFont = new Font("Garamond", 1, 12);

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
        g.drawString("Health%:" + health, 13, 365);
        g.drawString("Bread Count: " + inventory.getCount(false, BREAD_ID), 13, 385);
        if (bread == null)
            g.drawString("Bread: NO", 13, 425);
        else
            g.drawString("Bread: YES", 13, 425);
        //g.drawString("GP/HR: " + Utilities.perHour(gp-gpStart) + " (+" + (gp-gpStart) + ")",13,445);
        g.drawString("Test test123", 13, 465);

        //g.setFont(statusFont);
        //String time = String.format("Time: %02d:%02d:%02d",hours,minutes,seconds);
        //g.drawString("Time: " + timer.toElapsedString(), 320, 410);

        g.drawString("Status: ", 340, 435);
        g.drawString(status, 260, 460);

        g.setColor(COLOR_PINK);
        Polygon[] poly = null;
        if (watchman != null) {
            g.fillPolygon(watchman.hull());
        }
    }


    @Override
    public void close() {
        log("potoThievWatchman finished.");
    }

}



