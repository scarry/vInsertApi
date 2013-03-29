package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Utils;

import api.AntiRandom;

public class OldMan extends RandomEvent{

	ScriptContext sc;
	public static final int OLDMAN_ID = 410;
	Npc OldMan;

	@Override
	public boolean init() {
		return sc.npcs.getNearest(OLDMAN_ID) != null;
	}

	@Override
	public int pulse() {
		OldMan = sc.npcs.getNearest(OLDMAN_ID);
		Widget[] b = sc.widgets.get(241);
		Widget[] c = sc.widgets.get(242);
		Widget[] d = sc.widgets.get(243);
		Widget[] e = sc.widgets.get(244);
		if(OldMan != null) {
			if(OldMan.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2){
				sc.camera.rotateToTile(OldMan.getLocation());
				OldMan.interact("Talk-to Mysterious Old Man");
				Utils.sleep(Utils.random(1000,2500));
				if(b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
					sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
					Utils.sleep(Utils.random(1000,1200));
					sc.mouse.click(true);
				}
			}
			//log("OldMan Random");
		}

        return 0;
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }

}
