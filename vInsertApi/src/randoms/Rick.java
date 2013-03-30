package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;

@ScriptManifest(name = "zRick", authors = { "potofreak" })
public class Rick extends AntiRandom {

    public static final int RICK_ID = 2476;
    Npc Rick;


    @Override
    public boolean init() {
        return sc.npcs.getNearest(RICK_ID) != null;
    }

    @Override
    public int pulse() {
        if(!init()){
            requestExit();
            return 0;
        }
        Rick = sc.npcs.getNearest(RICK_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (Rick != null) {
            if (Rick.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(Rick.getLocation());
                //Rick.interact("Talk-to Rick Turpentine");
                utilities.interact(Rick,"Talk-to");
                Utils.sleep(random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    utilities.clickToContinue();
                }
            }
            //log("Rick Random");
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
