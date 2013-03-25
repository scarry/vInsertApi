import java.awt.Graphics2D;

import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;

import api.ScriptBase;



public class FightCave extends ScriptBase{
	
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
	public static int bankTokkulEvery;
	
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
		if (enemy != null) //TODO add enemy.getLocation().isWalkable()
			return true;
		return false;
	}
	
	public boolean isEnemyOnscreen() {
		Npc enemy = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(ENEMY_IDS));
		if (enemy != null && camera.isVisible(enemy)) //TODO add !isDead && enemy.getLocation().isWalkable()
			return true;
		return false;
	}
	
	public boolean needToBank() {
		if (inventory.getCount(true, TOKKUL_ID) > bankTokkulEvery)
			return true;
		return false;
	}
	
	public boolean isBankerOnscreen() {
		Npc banker = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(BANK_NPC_ID));
		if (banker != null && camera.isVisible(banker))
			return true;
		return false;
	}
	
	public boolean isBankerLoaded() {
		Npc banker = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(BANK_NPC_ID));
		if (banker != null)
			return true;
		return false;
	}
		
	public boolean isEntranceOnscreen() {
		GameObject entrance = objects.getNearest(Filters.objectId(ENTRANCE_ID));
		if (entrance != null && camera.isVisible(entrance) && localPlayer.getLocation().distanceTo(entrance.getLocation()) < 4)
			return true;
		return false;
		
	}
	
	public boolean isEntranceLoaded() {
		GameObject entrance = objects.getNearest(Filters.objectId(ENTRANCE_ID));
		if (entrance != null)
			return true;
		return false;
	}
	
	public boolean isInCombat() {
		return localPlayer.isInCombat();
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
	


}
