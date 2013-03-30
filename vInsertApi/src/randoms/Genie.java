package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;


@ScriptManifest(name = "zGenie", authors = { "potofreak" })
public class Genie extends AntiRandom {

    public static final int GENIE_ID = 409;
    Npc Genie;

    @Override
    public boolean init() {
        return sc.npcs.getNearest(GENIE_ID) != null && sc.npcs.getNearest(GENIE_ID).getLocation().distanceTo(localPlayer.getLocation()) <= 2;
    }

    @Override
    public int pulse() {
        if(!init()){
            requestExit();
            return 0;
        }
        Genie = sc.npcs.getNearest(GENIE_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (Genie != null) {
            if (Genie.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(Genie.getLocation());
                utilities.interact(Genie,"Talk-to");
                //Genie.interact("Talk-to Genie");
                Utils.sleep(random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    utilities.clickToContinue();
                }
            }

            //log("Genie Random");
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
