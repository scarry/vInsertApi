package randoms;

import java.awt.Graphics2D;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Tile;


import api.Node;
import api.ScriptBase;

public class RandomHandler{
	
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
		script.submit(new DrunkenDwarf(sc));
		script.submit(new Frog(sc));
		script.submit(new Genie(sc));
		script.submit(new Guard(sc));
		script.submit(new Hyde(sc));
		script.submit(new OldMan(sc));
		script.submit(new Pirate(sc));
		script.submit(new Plant(sc));
		script.submit(new Rick(sc));
	}
}
	
	 
