

import api.Conditions;
import api.Node;
import api.ScriptBase;
import api.Timer;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.*;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.util.Filter;

import api.Path;
import api.Area;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "FishBot", authors = {"Fortruce"}, description = "fishing bot", version = 1.0)
public class FishBot extends ScriptBase {

    /**
     * Animation Ids
     */
    public static final int CAGE_ANIMATION_ID = 619;
    public static final int HARPOON_ANIMATION_ID = 618;
    public static final int SMALL_NET_ANIMATION_ID = 621;
    public static final int BIG_NET_ANIMATION_ID = 620;
    public static final int LURE_ANIMATION_ID = 623;
    public static final int LURE_CAST_ANIMTION_ID = 622;
    @SuppressWarnings("serial")
    public static final ArrayList<Integer> LURE_ANIMATION_IDS = new ArrayList<Integer>() {{
        add(LURE_ANIMATION_ID);
        add(LURE_CAST_ANIMTION_ID);
    }};
    @SuppressWarnings("serial")
    public static final ArrayList<Integer> FISH_ANIMATION_IDS = new ArrayList<Integer>() {{
        add(CAGE_ANIMATION_ID);
        add(HARPOON_ANIMATION_ID);
        add(SMALL_NET_ANIMATION_ID);
        add(BIG_NET_ANIMATION_ID);
        for (int i = 0; i < LURE_ANIMATION_IDS.size(); i++) {
            add(LURE_ANIMATION_IDS.get(i));
        }
    }};

    /**
     * Fish Ids
     */
    public static final int SHRIMP_ID = 318;
    public static final int ANCHOVY_ID = 322;
    public static final int TROUT_ID = 336;
    public static final int SALMON_ID = 332;
    public static final int LOBSTER_ID = 378;
    public static final int SWORDFISH_ID = 372;
    public static final int TUNA_ID = 360;
    public static final int SHARK_ID = 384;
    @SuppressWarnings("serial")
    public static final int[] FISH_IDS = new int[] {
        SHRIMP_ID,
        ANCHOVY_ID,
        TROUT_ID,
        SALMON_ID,
        LOBSTER_ID,
        SWORDFISH_ID,
        TUNA_ID,
        SHARK_ID
    };

    /**
     * Equipment Ids
     */
    public static final int HARPOON_ID = 312;
    public static final int CAGE_ID = 302;

    /**
     * Fish Spot Ids
     */
    public static final int[] CAGE_HARPOON_FISH_ID = {321, 312};

    /**
     * Bank Ids
     */
    private static final int BANK_STALL = 2213;

    /**
     * Bank Area
     */
    private Area FISHING_GUILD_BANK = new Area(new Tile(2586, 3418), new Tile(2589, 3422));

    /**
     * Path to the fish from bank
     */
    private Path FISHING_GUILD_PATH;


    private Timer lastFishTimer = new Timer(0);
    private FishSpot fishSpot;


    private class FishSpot {
        public Path fishPath;
        public Area bankArea;
        public int[] fishType;
        public int equipment;
        public String interact;

        public FishSpot(int[] fishType, String interact, int equipment, Path fishPath, Area bankArea) {
            this.fishPath = fishPath;
            this.bankArea = bankArea;
            this.fishType = fishType;
            this.equipment = equipment;
            this.interact = interact;
        }
    }


    private boolean needToBank() {
        return inventory.isFull() || needEquipment();
    }

    private boolean needEquipment() {
        return !inventory.contains(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                return item.getId() == CAGE_ID;
            }
        });
    }

    private boolean isAtBank() {
        return fishSpot.bankArea.contains(localPlayer.getLocation());
    }

    private boolean isFishLoaded() {
        Npc fish = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                for (int id : CAGE_HARPOON_FISH_ID) {
                    if (id == npc.getId() && npc.containsAction("Cage"))
                        return true;
                }
                return false;
            }
        });
        return fish != null;
    }

    private boolean isFishClose() {
        Npc fish = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                for (int id : CAGE_HARPOON_FISH_ID) {
                    if (id == npc.getId() && npc.containsAction("Cage") && camera.isVisible(npc))
                        return true;
                }
                return false;
            }
        });
        return fish != null;
    }

    private boolean canFish() {
        return !needEquipment() && !inventory.isFull();
    }

    private boolean isFishing() {
        return FISH_ANIMATION_IDS.contains(localPlayer.getAnimation());
    }

    private boolean needToDeposit() {
        return inventory.isFull() || (bank.isOpen() && inventory.contains(Filters.itemId(FISH_IDS)));
    }

    private boolean needToWithdraw() {
        return needEquipment();
    }

//	@Override
//	public ScriptState determine() {
//		Player player = localPlayer;
//		
//		if (needToBank() && !isAtBank())
//			return ScriptState.WALK_TO_BANK;
//		else if (!isFishLoaded() && canFish())
//			return ScriptState.WALK_TO_FISH;
//		else if ((isFishLoaded() && !isFishClose()) && canFish() && player.getInteracting() == null)
//			return ScriptState.APPROACH_FISH;
//		else if (isFishClose() && canFish() && 
//				(!isFishing() || TimeUnit.SECONDS.convert(lastFishTimer.getElapsed(), TimeUnit.MILLISECONDS) > random(75, 250)))
//			return ScriptState.FISH;
//		else if (!bank.isOpen() && isAtBank() && (needToDeposit() || needToWithdraw()))
//			return ScriptState.OPEN_BANK;
//		else if (bank.isOpen() && needToDeposit())
//			return ScriptState.DEPOSIT_BANK;
//		else if (bank.isOpen() && needToWithdraw())
//			return ScriptState.WITHDRAW_BANK;
//		else if (isFishing())
//			return ScriptState.FISHING;
//		
//		
//		return ScriptState.ERROR;
//	}
//	
//	@Override
//	public int handle(ScriptState state) {
//		log("state: " + state.name());
//		
//		Npc fish;
//		
//		switch(state) {
//		case WALK_TO_BANK:
//			navigation.navigate(bankPath, 1, NavigationPolicy.MINIMAP);
//			break;
//			
//		case WALK_TO_FISH:
//			navigation.navigate(fishPath, 1, NavigationPolicy.MINIMAP);
//			break;
//			
//		case APPROACH_FISH:
//			fish = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
//				@Override
//				public boolean accept(Npc element) {
//					for (int id : CAGE_HARPOON_FISH_ID) {
//						if (id == element.getId() && element.containsAction("Cage"))
//							return true;
//					}
//					return false;
//				}
//			});
//			if (fish != null) {
//				 navigation.navigate(fish.getLocation(), NavigationPolicy.MINIMAP);
//				 sleep(random(1000, 1500));
//			}
//			break;
//			
//		case FISH:
//			fish = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
//				@Override
//				public boolean accept(Npc element) {
//					for (int id : CAGE_HARPOON_FISH_ID) {
//						if (id == element.getId() && element.containsAction("Cage"))
//							return true;
//					}
//					return false;
//				}
//			});
//			if (fish != null) {
//				fish.interact("Cage");
//				lastFishTimer = new Timer(0);
//				sleep(800, 1500);
//			}
//			break;
//			
//		case OPEN_BANK:
//			GameObject bankStall = objects.getNearest(new Filter<GameObject>() {
//				@Override
//				public boolean accept(GameObject element) {
//					return element.getId() == BANK_STALL && actionsContain(element, "Bank");
//				}
//			});
//			if (bankStall != null) {
//				log("bank found...");
//				if (!camera.isVisible(bankStall)) {
//					log("rotating to bank");
//					camera.rotateToObject(bankStall);
//					sleep(1200, 1600);
//				}
//				log("interacting with bank");
//				bankStall.interact("Bank");
//				sleep(800, 1200);
//			}
//			else
//				log("bank not found");
//			break;
//			
//		case DEPOSIT_BANK:
//			bank.depositAllExcept(Filters.itemId(CAGE_ID));
//			sleep(random(250, 350));
//			break;
//			
//		case WITHDRAW_BANK:
//			bank.withdraw(Filters.itemId(CAGE_ID), 1);
//			sleep(random(250, 350));
//			break;
//			
//		case FISHING:
//			sleep(random(100, 200));
//			break;
//			
//		case ERROR:
//			log("Entered ERROR state - something went wrong...");
//			break;
//			
//		}
//		return random(50, 100);
//	}

    public class OpenBank extends Node {
        @Override
        public boolean activate() {
            return !bank.isOpen() && isAtBank() && (needToDeposit() || needToWithdraw());
        }

        @Override
        public void execute() {
            GameObject bankStall = objects.getNearest(Filters.objectId(BANK_STALL));
            if (bankStall == null)
                return;
            if (!camera.isVisible(bankStall)) {
                camera.rotateToObject(bankStall);
                sleep(100, 300);
            }
            if (camera.isVisible(bankStall)) {
                bankStall.interact("Bank");
                Conditions.waitFor(new Conditions.isBankOpen(), random(500, 1000), random(100, 300), getContext());
            }
        }
    }

    public class DepositBank extends Node {
        @Override
        public boolean activate() {
            return bank.isOpen() && needToDeposit();
        }

        @Override
        public void execute() {
            bank.depositAllExcept(Filters.itemId(fishSpot.equipment));
            Conditions.waitFor(new Conditions.inventoryNotContains(FISH_IDS), random(400, 800), getContext());
        }
    }

    public class WithdrawBank extends Node {
        @Override
        public boolean activate() {
            if (bank.isOpen() && needToWithdraw())
                return true;
            return false;
        }

        @Override
        public void execute() {
            utilities.withdraw(Filters.itemId(fishSpot.equipment), 1);
        }
    }

    public class Fish extends Node {
        @Override
        public boolean activate() {
            if (isFishClose() && canFish() &&
                    (!isFishing() || TimeUnit.SECONDS.convert(lastFishTimer.getElapsed(), TimeUnit.MILLISECONDS) > random(75, 250)))
                return true;
            return false;
        }

        @Override
        public void execute() {
            Npc fish = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(fishSpot.fishType));
            if (fish == null)
                return;
            if (utilities.interact(fish, fishSpot.interact))
                lastFishTimer = new Timer(0);
            Conditions.waitFor(new Conditions.Condition() {
                @Override
                public boolean validate(ScriptContext ctx) {
                    return isFishing();
                }
            }, random(300, 700), random(400, 600), getContext());
        }
    }

    public class ApproachFish extends Node {
        @Override
        public boolean activate() {
            if ((isFishLoaded() && !isFishClose()) && canFish() && localPlayer.getInteracting() == null)
                return true;
            return false;
        }

        @Override
        public void execute() {
            Npc fish = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(fishSpot.fishType));
            if (fish == null)
                return;
            navigation.navigate(fish.getLocation(), Navigation.NavigationPolicy.MINIMAP);
            Conditions.waitFor(new Conditions.isVisible(fish), random(600, 1000), getContext());
        }
    }

    public class WalkToBank extends Node {
        @Override
        public boolean activate() {
            if (needToBank() && !isAtBank())
                return true;
            return false;
        }

        @Override
        public void execute() {
            fishSpot.fishPath.traverse(true, true, 1);
            Conditions.waitFor(new Conditions.isInArea(fishSpot.bankArea), random(800, 1500), getContext());
        }
    }

    public class WalkToFish extends Node {
        @Override
        public boolean activate() {
            if (!isFishLoaded() && canFish())
                return true;
            return false;
        }

        @Override
        public void execute() {
            fishSpot.fishPath.traverse(false);
            Conditions.waitFor(new Conditions.isNpcLoaded(fishSpot.fishType), random(800, 1500), getContext());
        }
    }

    @Override
    public boolean init() {

//        FISHING_GUILD_PATH = new Path(new Tile[]{
//                new Tile(2586, 3420),
//                new Tile(2591, 3416),
//                new Tile(2595, 3414)}, getContext());
        
        FISHING_GUILD_PATH = new Path(new Tile[] { new Tile(2603, 3413), new Tile(2603, 3409), new Tile(2603, 3405),
                new Tile(2598, 3408), new Tile(2596, 3412), new Tile(2595, 3416),
                new Tile(2593, 3420), new Tile(2598, 3421), new Tile(2601, 3421),
                new Tile(2596, 3421), new Tile(2593, 3418), new Tile(2590, 3417),
                new Tile(2587, 3419), new Tile(2587, 3419) }, getContext());

        fishSpot = new FishSpot(CAGE_HARPOON_FISH_ID, "Cage", CAGE_ID, FISHING_GUILD_PATH, FISHING_GUILD_BANK);

        submit(new WalkToBank());
        submit(new ApproachFish());
        submit(new DepositBank());
        submit(new WithdrawBank());
        submit(new WalkToFish());
        submit(new Fish());
        submit(new OpenBank());

        return true;
    }

    //START: Code generated using Enfilade's Easel
    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    final Color color1 = new Color(0, 0, 0);
    final Color color2 = new Color(51, 51, 255);
    final Color color3 = new Color(153, 255, 255);

    final BasicStroke stroke1 = new BasicStroke(1);

    final Font font1 = new Font("DialogInput", 0, 20);
    final Font font2 = new Font("DialogInput", 0, 18);
    final Font font3 = new Font("DialogInput", 0, 14);
    final Font font4 = new Font("DialogInput", 0, 10);

    final Image img1 = getImage("http://puu.sh/2q9sZ/1a25387d57");

    @Override
    public void render(Graphics2D g) {
        g.setColor(color1);
        g.fillRect(2, 340, 514, 137);
        g.setStroke(stroke1);
        g.drawRect(2, 340, 514, 137);
        g.drawImage(img1, 3, 342, null);
        g.setFont(font1);
        g.setColor(color2);
        g.drawString("fortruce", 106, 365);
        g.setFont(font2);
        g.drawString("- FishBot", 214, 365);
        g.setFont(font4);
        g.setColor(color3);
        utilities.drawProgressBar(g, skillData, Skills.FISHING, new Point(120, 440), 380, 20,
                color3, Color.black, Color.blue, Color.white, new Color(222, 0, 6, 123), new Point(10,10), new Point(30,30));
        g.setFont(font3);
        if (ScriptBase.getActiveNode() != null)
            g.drawString(ScriptBase.getActiveNode(), 120, 425);
        g.drawString("Run Time: " + runTime.toElapsedString(), 120, 395);
    }
    //END: Code generated using Enfilade's Easel

}
