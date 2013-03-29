

import api.Conditions;
import api.Node;
import api.ScriptBase;
import api.Timer;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.*;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.util.Filter;

import api.Path;
import api.Area;

import java.awt.*;
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
    private Path FISHING_GUILD_PATH = new Path(new Tile[] {
            new Tile(2586, 3420),
            new Tile(2591, 3416),
            new Tile(2595, 3414)}, getContext());



    private Timer lastFishTimer = new Timer(0);
    private FishSpot fishSpot = new FishSpot(CAGE_HARPOON_FISH_ID, CAGE_ID, FISHING_GUILD_PATH, FISHING_GUILD_BANK);



    private class FishSpot {
        public Path fishPath;
        public Area bankArea;
        public int[] fishType;
        public int equipment;

        public FishSpot(int[] fishType, int equipment, Path fishPath, Area bankArea) {
            this.fishPath = fishPath;
            this.bankArea = bankArea;
            this.fishType = fishType;
            this.equipment = equipment;
        }
    }


    private boolean needToBank() {
        return inventory.isFull() || needEquipment();
    }

    private boolean needEquipment() {
        return !inventory.contains(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                if (item.getId() == CAGE_ID)
                    return true;
                return false;
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
            if (!bank.isOpen() && isAtBank() && (needToDeposit() || needToWithdraw()))
                return true;
            return false;
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
            if (bank.isOpen() && needToDeposit())
                return true;
            return false;
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
            //TODO add withdraw
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
            fish.interact("Cage");
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
            fishSpot.fishPath.traverse(false);
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
            fishSpot.fishPath.traverse(true);
            Conditions.waitFor(new Conditions.isNpcLoaded(fishSpot.fishType), random(800, 1500), getContext());
        }
    }

    @Override
    public boolean init() {
        submit(new WalkToBank());
        submit(new ApproachFish());
        submit(new DepositBank());
        submit(new WithdrawBank());
        submit(new WalkToFish());
        submit(new Fish());

        return true;
    }

    @Override
    public void render(Graphics2D g) {
        //box
        g.setColor(new Color(63, 63, 43, 200));
        g.draw3DRect(375, 5, 139, 300, true);
        g.fill3DRect(375, 5, 139, 300, true);

        int[] point = {385, 2};
        int height = g.getFontMetrics().getHeight();

        //text
        g.setColor(Color.WHITE);
        g.drawString("Fortruce - FishBot", point[0] + 5, point[1] += height);
        g.drawLine(389, 21, 499, 21);

        if (fishSpot != null) {
            g.drawString("nToBank:    " + String.valueOf(needToBank()), point[0], point[1] += height);
            g.drawString("nEquip:     " + String.valueOf(needEquipment()), point[0], point[1] += height);
            g.drawString("isAtBank:   " + String.valueOf(isAtBank()), point[0], point[1] += height);
            g.drawString("isFishLoad:  " + String.valueOf(isFishLoaded()), point[0], point[1] += height);
            g.drawString("isFClose:   " + String.valueOf(isFishClose()), point[0], point[1] += height);
            g.drawString("canFish:    " + String.valueOf(canFish()), point[0], point[1] += height);
            g.drawString("isFishing:  " + String.valueOf(isFishing()), point[0], point[1] += height);
            g.drawString("nToDeposit: " + String.valueOf(needToDeposit()), point[0], point[1] += height);
            g.drawString("nToWithdrw: " + String.valueOf(needToWithdraw()), point[0], point[1] += height);
        }
    }

}
