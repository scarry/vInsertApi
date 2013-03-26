package randoms;

import java.awt.Graphics2D;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Tile;


import api.Node;
import api.ScriptBase;

public class RandomHandler extends ScriptBase {
	
	private Tile safeLoc = null;
	private int lampSkill = -1;
	private int objectId = -1;
	private String actionToPerform = null;
	private boolean noRandoms = false;
	private static boolean noCombat = false;
	private ScriptContext sc;
/*
	Node anticombat;
	Node drilldemon;
	Node evilbob;
	Node freakyforester;
	Node lampsolver;
	Node mime;
	Node molly;
	Node niles;
	Node pillory;
	Node pinball;
	Node quiz;
	Node sandwichlady;
	Node simplerandoms;
	Node strangecube;
*/
	public RandomHandler(boolean noRandoms, ScriptBase script) {
		this(null, null, -1, -1, noRandoms, noCombat, script);
	}
	
	public RandomHandler(Tile safeLoc, int lampSkill, ScriptBase script) {
		this(safeLoc, null, -1, lampSkill, false, script);
	}
	
	public RandomHandler(Tile safeLoc, String actionToPerform, int objectId, int lampSkill, ScriptBase script) {
		this(safeLoc, actionToPerform, objectId, lampSkill, false, script);
	}

	public RandomHandler(Tile safeLoc, String actionToPerform, int objectId, int lampSkill, boolean noCombat, ScriptBase script) {
		this(safeLoc, actionToPerform, objectId, lampSkill, false, noCombat, script);
	}
	
	public RandomHandler(Tile safeLoc, int lampSkill, boolean noCombat, ScriptBase script) {
		this(safeLoc, null, -1, lampSkill,noCombat, script);
	}
	
	private RandomHandler(Tile loc, String action, int object, int lamp, boolean noRand, boolean noComb, ScriptBase script) {
		safeLoc = loc;
		actionToPerform = action;
		objectId = object;
		lampSkill = lamp;
		noRandoms = noRand;
		noCombat = noComb;
		sc = script.getContext();
	}
	
	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean init() {
		submit(new DrunkenDwarf(sc));
		submit(new Frog(sc));
		submit(new Genie(sc));
		submit(new Guard(sc));
		submit(new Hyde(sc));
		submit(new OldMan(sc));
		submit(new Pirate(sc));
		submit(new Plant(sc));
		submit(new Rick(sc));
		return true;
	}
	 
}