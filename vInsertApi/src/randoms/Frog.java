package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Utils;

import api.AntiRandom;

public class Frog extends AntiRandom{

	ScriptContext sc;
	Npc frog;
	int[] frog2 = {2469, 2470};
	Tile frogtile = new Tile(2464, 4776);
	
	public Frog(ScriptContext context) {
		super(context);
		sc = context;
	}
	
	@Override
	public boolean activate() {
		return sc.npcs.getNearest(frog2) != null;
	}

	@Override
	public void execute() {
		frog = sc.npcs.getNearest(frog2);
		if(frog != null) {
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
		
	}

}
