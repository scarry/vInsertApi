package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;

@ScriptManifest(name = "zGuard", authors = { "potofreak" })
public class Guard extends AntiRandom {

    public static final int GUARD_ID = 4375;
    Npc Guard;

    @Override
    public boolean init() {
        return sc.npcs.getNearest(GUARD_ID) != null && sc.npcs.getNearest(GUARD_ID).getLocation().distanceTo(localPlayer.getLocation()) <= 2;
    }

    @Override
    public int pulse() {
        if(!init()){
            requestExit();
            return 0;
        }
        Guard = sc.npcs.getNearest(GUARD_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (Guard != null) {
            if (Guard.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(Guard.getLocation());
                utilities.interact(Guard,"Talk-to");
                //Guard.interact("Talk-to Security Guard");
                Utils.sleep(random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    utilities.clickToContinue();
                }
            }
            //log("Guard Random");
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
