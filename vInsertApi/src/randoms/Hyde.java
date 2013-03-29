package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;

@ScriptManifest(name = "zHyde", authors = { "potofreak" })
public class Hyde extends AntiRandom {

    ScriptContext sc;
    public static final int HYDE_ID = 2540;
    Npc Hyde;


    @Override
    public boolean init() {
        return sc.npcs.getNearest(HYDE_ID) != null;
    }

    @Override
    public int pulse() {
        Hyde = sc.npcs.getNearest(HYDE_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (Hyde != null) {
            if (Hyde.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(Hyde.getLocation());
                Hyde.interact("Talk-to Dr Jekyll");
                Utils.sleep(Utils.random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));

                    Utils.sleep(Utils.random(2000, 4000));
                    sc.mouse.click(true);
                    Utils.sleep(Utils.random(2000, 4000));
                    sc.mouse.click(true);
                    Utils.sleep(Utils.random(2000, 4000));
                    sc.mouse.click(true);
                    Utils.sleep(Utils.random(2000, 4000));
                    sc.mouse.click(true);
                    Utils.sleep(Utils.random(2000, 4000));
                }
            }
            //log("Dr Jekyll hyde random");
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
