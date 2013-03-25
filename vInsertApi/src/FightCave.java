import java.awt.Graphics2D;

import org.vinsert.bot.script.StatefulScript;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;

import api.Node;
import api.Utilities;



public class FightCave extends StatefulScript{
	
	/**
	 * Ids
	 */
	public static final int ENTRANCE_ID = 9356;
	public static final int EXIT_ID = 9357;
	public static final int BANK_NPC_ID = 2619;
	public static final int TOKKUL_ID = 6530; 
	public static final int ENTRANCE_NPC_ID = 2617;
	public static final int[] ENEMY_IDS = {2734, 2735, 2736, 2737, 2738, 2739};
	public static final int DEATH_ANIMATION_ID = 836;
	
	/**
	 * Center of the fight cave
	 */
	public static Tile fightCaveCenter = null;
	
	/**
	 * Boolean helper methods
	 */
	private boolean isCaveCenterSet() {
		GameObject exit = objects.getNearest(Filters.objectId(EXIT_ID));
		if (exit != null && fightCaveCenter != null && 
				fightCaveCenter.equals(new Tile(exit.getLocation().getX() - 12, exit.getLocation().getY() - 30)))
			return true;
		return false;
	}
	
	public boolean isInCave() {
		GameObject exit = objects.getNearest(Filters.objectId(EXIT_ID));
		if (exit == null)
			return false;
		else
			return true;
	}
	
	public boolean isEnemyLoaded() {
		Npc enemy = npcs.getNearest(ENEMY_IDS);
		if (enemy != null && enemy.getLocation().isWalkable())
			return true;
		return false;
	}
	
	public boolean isEnemyOnscreen() {
		NPC enemy = Npcs.getNearest(ENEMY_IDS);
		if (enemy != null && Utilities.isOnScreen(enemy) && !enemy.isDead() && enemy.getLocation().isWalkable())
			return true;
		return false;
	}
	
	public boolean needToBank() {
		if (Inventory.getCount(TOKKUL_ID, true) > bankTokkulEvery)
			return true;
		return false;
	}
	
	public boolean isBankerOnscreen() {
		NPC banker = Npcs.getNearest(BANK_NPC_ID);
		if (banker != null && Utilities.isOnScreen(banker))
			return true;
		return false;
	}
	
	public boolean isBankerLoaded() {
		NPC banker = Npcs.getNearest(BANK_NPC_ID);
		if (banker != null)
			return true;
		return false;
	}
		
	public boolean isEntranceOnscreen() {
		GameObject entrance = Objects.getNearest(ENTRANCE_ID);
		if (entrance != null && ExUtilities.isOnScreen(entrance) && entrance.distance() < 4)
			return true;
		return false;
	}
	
	public boolean isEntranceLoaded() {
		GameObject entrance = Objects.getNearest(ENTRANCE_ID);
		if (entrance != null)
			return true;
		return false;
	}
	
	public boolean isInCombat() {
		return Players.getLocal().inCombat();
	}
	
	
	
	

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Enum determine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int handle(Enum state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub	
	}
	
	public class SetCaveCenter extends Node<ScriptState> {
		public SetCaveCenter() {
			super(ScriptState.SET_CAVE_CENTER);
		}
		
		@Override
		public boolean determine() {
			
			return false;
		}

		@Override
		public void handle() {
			// TODO Auto-generated method stub
			return;
		}
		
	}
	
	public static enum ScriptState {
		SET_CAVE_CENTER, WALK_TO_BANK, OPEN_BANK, DEPOSIT_BANK, WALK_TO_CENTER, WALK_TO_ENEMY, ATTACK_ENEMY, ENTER_CAVE, WALK_TO_ENTRANCE, ERROR
	}

}
