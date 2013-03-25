

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.StatefulScript;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Path;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.Area;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Utils;

import api.Timer;

@ScriptManifest(name = "FishBot", authors = {"Fortruce"}, description = "fishing bot", version = 1.0)
public class FishBot extends StatefulScript<FishBot.ScriptState> {

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
		for(int i = 0; i < LURE_ANIMATION_IDS.size(); i++) {
			add(LURE_ANIMATION_IDS.get(i));
		}
		}};
	
	/**
	 * Fish Ids
	 */
		public static final int SHRIMP_ID = 318;
		public static final int ANCHOVIE_ID = 322;
		public static final int TROUT_ID = 336;
		public static final int SALMON_ID = 332;
		public static final int LOBSTER_ID = 378;
		public static final int SWORDFISH_ID = 372;
		public static final int TUNA_ID = 360;
		public static final int SHARK_ID = 384;
		@SuppressWarnings("serial")
		public static final ArrayList<Integer> FISH_IDS = new ArrayList<Integer>() {{
			add(SHRIMP_ID);
			add(ANCHOVIE_ID);
			add(TROUT_ID);
			add(SALMON_ID);
			add(LOBSTER_ID);
			add(SWORDFISH_ID);
			add(TUNA_ID);
			add(SHARK_ID);
			}};
	
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
	private static final int BANK_STALL = 16937;
	
	/**
	 * Bank Area
	 */
	private Area bankArea = new Area(new Tile(2586, 3418), new Tile(2589, 3422));
	
	/**
	 * Path to the fish from bank
	 */
	private Path fishPath = new Path(
			new Tile(2586, 3420),
			new Tile(2591, 3416),
			new Tile(2595, 3414)
			);
	private Path bankPath = fishPath.reverse();
	
	private Timer lastFishTimer = new Timer(0);
	
	private boolean needToBank() {
		if (inventory.isFull() || needEquipment())
			return true;
		return false;
	}
	private boolean needEquipment() {
		if (inventory.contains(new Filter<Item>() {
			@Override
			public boolean accept(Item item) {
				if (item.getId() == CAGE_ID)
					return true;
				return false;
			}
			}))
			return false;
		return true;
	}
	
	private boolean isAtBank() {
		return bankArea.contains(localPlayer);
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
		if (fish != null)
			return true;
		return false;
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
		if (fish != null)
			return true;
		return false;
	}
	
	private boolean canFish() {
		if (!needEquipment() && !inventory.isFull())
			return true;
		return false;
	}
	
	private boolean isFishing() {
		if (FISH_ANIMATION_IDS.contains(localPlayer.getAnimation()))
			return true;
		return false;
	}
	
	private boolean needToDeposit() {
		if (inventory.isFull() || (bank.isOpen() && inventory.contains(new Filter<Item>() {
			@Override
			public boolean accept(Item item) {
				if (FISH_IDS.contains(item.getId()))
					return true;
				return false;
			}
		})))
			return true;
		return false;
	}
	
	private boolean needToWithdraw() {
		if (needEquipment())
			return true;
		return false;
	}
	
	private boolean actionsContain(GameObject object, String action) {
		Point point = object.hullPoint(object.hull());
		mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(50, 250));
		int index = menu.getIndex(action);
		if (index == -1) return false;
		else return true;
	}
	
	@Override
	public ScriptState determine() {
		Player player = localPlayer;
		
		if (needToBank() && !isAtBank())
			return ScriptState.WALK_TO_BANK;
		else if (!isFishLoaded() && canFish())
			return ScriptState.WALK_TO_FISH;
		else if ((isFishLoaded() && !isFishClose()) && canFish() && player.getInteracting() == null)
			return ScriptState.APPROACH_FISH;
		else if (isFishClose() && canFish() && 
				(!isFishing() || TimeUnit.SECONDS.convert(lastFishTimer.getElapsed(), TimeUnit.MILLISECONDS) > random(75, 250)))
			return ScriptState.FISH;
		else if (!bank.isOpen() && isAtBank() && (needToDeposit() || needToWithdraw()))
			return ScriptState.OPEN_BANK;
		else if (bank.isOpen() && needToDeposit())
			return ScriptState.DEPOSIT_BANK;
		else if (bank.isOpen() && needToWithdraw())
			return ScriptState.WITHDRAW_BANK;
		else if (isFishing())
			return ScriptState.FISHING;
		
		
		return ScriptState.ERROR;
	}
	
	@Override
	public int handle(ScriptState state) {
		log("state: " + state.name());
		
		Npc fish;
		
		switch(state) {
		case WALK_TO_BANK:
			navigation.navigate(bankPath, 1, NavigationPolicy.MINIMAP);
			break;
			
		case WALK_TO_FISH:
			navigation.navigate(fishPath, 1, NavigationPolicy.MINIMAP);
			break;
			
		case APPROACH_FISH:
			fish = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
				@Override
				public boolean accept(Npc element) {
					for (int id : CAGE_HARPOON_FISH_ID) {
						if (id == element.getId() && element.containsAction("Cage"))
							return true;
					}
					return false;
				}
			});
			if (fish != null) {
				 navigation.navigate(fish.getLocation(), NavigationPolicy.MINIMAP);
				 sleep(random(1000, 1500));
			}
			break;
			
		case FISH:
			fish = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
				@Override
				public boolean accept(Npc element) {
					for (int id : CAGE_HARPOON_FISH_ID) {
						if (id == element.getId() && element.containsAction("Cage"))
							return true;
					}
					return false;
				}
			});
			if (fish != null) {
				fish.interact("Cage");
				lastFishTimer = new Timer(0);
				sleep(800, 1500);
			}
			break;
			
		case OPEN_BANK:
			GameObject bankStall = objects.getNearest(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject element) {
					return element.getId() == BANK_STALL && actionsContain(element, "Bank");
				}
			});
			if (bankStall != null) {
				log("bank found...");
				if (!camera.isVisible(bankStall)) {
					log("rotating to bank");
					camera.rotateToObject(bankStall);
					sleep(1200, 1600);
				}
				log("interacting with bank");
				bankStall.interact("Bank");
				sleep(800, 1200);
			}
			else
				log("bank not found");
			break;
			
		case DEPOSIT_BANK:
			bank.depositAllExcept(Filters.itemId(CAGE_ID));
			sleep(random(250, 350));
			break;
			
		case WITHDRAW_BANK:
			bank.withdraw(Filters.itemId(CAGE_ID), 1);
			sleep(random(250, 350));
			break;
			
		case FISHING:
			sleep(random(100, 200));
			break;
			
		case ERROR:
			log("Entered ERROR state - something went wrong...");
			break;
			
		}
		return random(50, 100);
	}

	@Override
	public boolean init() {
		return true;
	}
	
	@Override
	public void close() {
		log("FisBot finished.");
	}

	@Override
	public void render(Graphics2D g) {
		g.drawString("State: "+determine(), 50, 100);
		
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
        
        if (bankArea != null) {
        g.drawString("nToBank:    " + String.valueOf(needToBank()), point[0], point[1]+=height);
        g.drawString("nEquip:     " + String.valueOf(needEquipment()), point[0], point[1]+=height);
        g.drawString("isAtBank:   " + String.valueOf(isAtBank()), point[0], point[1]+=height);
        g.drawString("isFishLoad:  " + String.valueOf(isFishLoaded()), point[0], point[1]+=height);
        g.drawString("isFClose:   " + String.valueOf(isFishClose()), point[0], point[1]+=height);
        g.drawString("canFish:    " + String.valueOf(canFish()), point[0], point[1]+=height);
        g.drawString("isFishing:  " + String.valueOf(isFishing()), point[0], point[1]+=height);
        g.drawString("nToDeposit: " + String.valueOf(needToDeposit()), point[0], point[1]+=height);
        g.drawString("nToWithdrw: " + String.valueOf(needToWithdraw()), point[0], point[1]+=height);
        }
	}
	
	public static enum ScriptState {
		WALK_TO_BANK, WALK_TO_FISH, APPROACH_FISH, FISH, OPEN_BANK, DEPOSIT_BANK, WITHDRAW_BANK, FISHING, ERROR
	}

}
