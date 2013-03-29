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
        return sc.npcs.getNearest(GENIE_ID) != null;
    }

    @Override
    public int pulse() {
        Genie = sc.npcs.getNearest(GENIE_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (Genie != null) {
            if (Genie.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(Genie.getLocation());
                Genie.interact("Talk-to Genie");
                Utils.sleep(Utils.random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
                    Utils.sleep(Utils.random(1000, 1200));
                    sc.mouse.click(true);
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
