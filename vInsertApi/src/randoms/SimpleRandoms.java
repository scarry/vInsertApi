package randoms;

import java.awt.Graphics2D;
import java.util.Random;

import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.StatefulScript;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.tools.Game;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.api.tools.Objects;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Utils;
//import org.vinsert.bot.script.api.tools.sc.npcs;
//import org.vinsert.bot.script.api.tools.sc.widgets;

import api.Node;

public class SimpleRandoms extends Node {

	ScriptContext sc;
	
	public SimpleRandoms(ScriptContext newSc, Tile safeLoc){
		SimpleRandoms.safeLoc = safeLoc;
		sc = newSc;		
	}

	public SimpleRandoms(ScriptContext newSc, Tile safeLoc, int objectId, String actionToPerform){
		sc = newSc;
		SimpleRandoms.safeLoc = safeLoc;
		SimpleRandoms.objectId = objectId;
		SimpleRandoms.actionToPerform = actionToPerform;
	}
	
	public static Tile safeLoc = null;
	public static int objectId = -1;
	public static String actionToPerform = null;
	public static int currentPlane = -1;
	
	public static final int[] Npc_ACTIVATE = {4375, 2540, 410, 409, 956, 2539, 407, 411, 2470, 2476}; //removed 2538 id - Giles
	int[] spirit2 = { 438, 439, 440, 441, 442, 443 };
	int[] chicken2 = { 2463, 2464, 2465, 2466, 2467, 2468 };
	int[] rick2 = {2476};  //removed 2538 id - Giles
	int[] plant2 = {407};
	int[] frog2 = {2469, 2470};

	/*
	
	public class hasChangedPlane implements IConditional{
		@Override
		public boolean execute() {
			return Game.getPlane() != currentPlane;
		}
		
	}
	
	public class isCloseToEscape implements IConditional{
		@Override
		public boolean execute() {
			return Calculations.distanceTo(safeLoc) < 1;
		}		
	}
	
	*/
	
	@Override
	public boolean activate() {
		Npc n = sc.npcs.getNearest(Npc_ACTIVATE);
		return (n != null && (n.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) < 4 || n.getId() == 2470));
		//return true;
	}

	/*
	public void walkToSafeLocation() {
		if(SimpleRandoms.safeLoc != null && objectId == -1 && actionToPerform == null)
		{
			Tile current_location = sc.players.getLocalPlayer().getLocation();
			while(sc.players.getLocalPlayer().isInCombat()) {
				sc.navigation.navigate(safeLoc, NavigationPolicy.MINIMAP);
				//Walking.walkTo(safeLoc);
				Utils.sleep(Utils.random(1400, 1800));
			}
			while(current_location.distanceTo(sc.players.getLocalPlayer().getLocation()) > 3)
			{
				sc.navigation.navigate(current_location, NavigationPolicy.MINIMAP);
				//Walking.walkTo(current_location);
				Utils.sleep(Utils.random(1400, 1800));
			}
		}
		else
		{
			//currentPlane = sc.game.getPlane();
			//use object to plane shift
			GameObject safeObject = Objects.getNearest(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject arg0) {
					if (arg0.getLocation().equals(safeLoc))
						return true;
					return false;
				}
			});
			
			if (safeObject != null) {
				if (sc.camera.isVisible(safeObject)) {
					if(actionToPerform != null){
						safeObject.interact(actionToPerform);
						//while(!ExConditions.waitFor(new hasChangedPlane(), 8000));
					}
				}
				else {
					sc.navigation.navigate(safeObject.getLocation(), NavigationPolicy.MINIMAP);
					//while(!ExConditions.waitFor(new isCloseToEscape(), 8000));
				}
			}
		} 
	}
	*/
	@Override
	public void execute() {
		Npc Chicken = sc.npcs.getNearest(chicken2);
		Npc pirate = sc.npcs.getNearest(2539);
		Npc plant = sc.npcs.getNearest(plant2);
		Npc genie = sc.npcs.getNearest(409);
		Npc OldMan = sc.npcs.getNearest(410);
		Npc Guard = sc.npcs.getNearest(4375);
		Npc Hyde = sc.npcs.getNearest(2540);
		Npc Swarm = sc.npcs.getNearest(411);
		Npc drunkenDwarf = sc.npcs.getNearest(956);
		Npc Rick = sc.npcs.getNearest(rick2);
		Npc frog = sc.npcs.getNearest(frog2);
		//GameObject frogobj = sc.objects.getNearest(5955);
		GameObject frogobj = sc.objects.getNearest(new Filter<GameObject>(){
			public boolean accept(GameObject obj) {
				if (obj.getId() == 5955)
					return true;
				return false;
			}		
		});
		Tile frogtile = new Tile(2464, 4776);
		Widget[] b = sc.widgets.get(241);
		Widget[] c = sc.widgets.get(242);
		Widget[] d = sc.widgets.get(243);
		Widget[] e = sc.widgets.get(244);
		
		if(Chicken != null && sc.players.getLocalPlayer().isInCombat()) {
			
			//walkToSafeLocation();
		}

		if(Swarm != null && sc.players.getLocalPlayer().isInCombat()) {
			//walkToSafeLocation();
		}

		if(plant != null) {
			if(plant.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(plant.getLocation());
				plant.interact("Pick");
				Utils.sleep(Utils.random(2000,4000));
				sc.mouse.click(300,455);
				Utils.sleep(Utils.random(2000,4000));
				//log("Strange plant :o");

			}
		}

		if(frogobj != null) {
			//log("starting frog");
			if(frog != null){
				sc.navigation.navigate(frog.getLocation(), NavigationPolicy.MINIMAP);
				Utils.sleep(4000);
				sc.camera.rotateToTile(frog.getLocation());
				Utils.sleep(300);
				frog.interact("Talk-to Frog");
				Utils.sleep(Utils.random(2000,4000));
				sc.mouse.click(300,455);
				Utils.sleep(Utils.random(2000,4000));
				sc.mouse.click(300,455);
				Utils.sleep(Utils.random(2000,4000));
				sc.mouse.click(300,455);
				Utils.sleep(Utils.random(2000,4000));
				sc.mouse.click(300,455);
				Utils.sleep(Utils.random(8000,10000));                
				//log("Frawgie random :D");
			} else {
				sc.navigation.navigate(frogtile, NavigationPolicy.MINIMAP);
			}
		}
		
		if(pirate != null) {
			if(pirate.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(pirate.getLocation());
				pirate.interact("Talk-to Cap'n Hand");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("Pirate Random");
		}
		
		
		if(genie != null) {
			if(genie.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(genie.getLocation());
				genie.interact("Talk-to Genie");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("Genie Random");
		}
		
		if(OldMan != null) {
			if(OldMan.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(OldMan.getLocation());
				OldMan.interact("Talk-to Mysterious Old Man");
				Utils.sleep(Utils.random(1800,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("Mysterious old man random");
		}
		
		if(Guard != null) {
			if(Guard.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(Guard.getLocation());
				Guard.interact("Talk-to Security Guard");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("Guard Random");
		}
		
		if(Hyde != null) {
			if(Hyde.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(Hyde.getLocation());
				Hyde.interact("Talk-to Dr Jekyll");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					
					Utils.sleep(Utils.random(2000,4000));
					sc.mouse.click(true);
					Utils.sleep(Utils.random(2000,4000));
					sc.mouse.click(true);
					Utils.sleep(Utils.random(2000,4000));
					sc.mouse.click(true);
					Utils.sleep(Utils.random(2000,4000));
					sc.mouse.click(true);
					Utils.sleep(Utils.random(2000,4000));
				}
			}
			//log("Dr Jekyll hyde random");
		}
		
		if(drunkenDwarf != null) {
			if(drunkenDwarf.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
				sc.camera.rotateToTile(drunkenDwarf.getLocation());
				drunkenDwarf.interact("Talk-to Drunken Dwarf");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("Drunken Dwarf Random");
		}
		
		if(Rick != null) {
			if(Rick.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
				sc.camera.rotateToTile(Rick.getLocation());
				Rick.interact("Talk-to Rick Turpentine");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("Rick Turpentine Random");
		}
	}

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}
}
