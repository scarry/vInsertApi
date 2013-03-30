package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;

@ScriptManifest(name = "zPirate", authors = { "potofreak" })
public class Pirate extends AntiRandom {

    public static final int PIRATE_ID = 2539;
    Npc pirate;

    @Override
    public boolean init() {
        return sc.npcs.getNearest(PIRATE_ID) != null && sc.npcs.getNearest(PIRATE_ID).getLocation().distanceTo(localPlayer.getLocation()) <= 2;
    }

    @Override
    public int pulse() {
        if(!init()){
            requestExit();
            return 0;
        }
        pirate = sc.npcs.getNearest(PIRATE_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (pirate != null) {
            if (pirate.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(pirate.getLocation());
                utilities.interact(pirate,"Talk-to");
                //pirate.interact("Talk-to Cap'n Hand");
                Utils.sleep(random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    utilities.clickToContinue();
                }
            }
            //log("Pirate Random");
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
