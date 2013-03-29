package randoms;

import api.AntiRandom;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;

public class Guard extends AntiRandom {

    ScriptContext sc;
    public static final int GUARD_ID = 4375;
    Npc Guard;

    public Guard(ScriptContext context) {
        super(context);
        sc = context;
    }

    @Override
    public boolean activate() {
        return sc.npcs.getNearest(GUARD_ID) != null;
    }

    @Override
    public void execute() {
        Guard = sc.npcs.getNearest(GUARD_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (Guard != null) {
            if (Guard.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(Guard.getLocation());
                Guard.interact("Talk-to Security Guard");
                Utils.sleep(Utils.random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
                    Utils.sleep(Utils.random(1000, 1200));
                    sc.mouse.click(true);
                }
            }
            //log("Guard Random");
        }

    }

}
