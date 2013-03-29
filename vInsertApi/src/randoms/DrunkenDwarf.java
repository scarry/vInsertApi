package randoms;

import api.AntiRandom;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;

public class DrunkenDwarf extends AntiRandom {

    ScriptContext sc;
    public static final int DWARF_ID = 956;
    Npc DrunkenDwarf;

    public DrunkenDwarf(ScriptContext context) {
        super(context);
        sc = context;
    }

    @Override
    public boolean activate() {
        return sc.npcs.getNearest(DWARF_ID) != null;
    }

    @Override
    public void execute() {
        DrunkenDwarf = sc.npcs.getNearest(DWARF_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (DrunkenDwarf != null) {
            if (DrunkenDwarf.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(DrunkenDwarf.getLocation());
                DrunkenDwarf.interact("Talk-to Drunken Dwarf");
                Utils.sleep(Utils.random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    sc.mouse.move(Utils.random(196, 404), Utils.random(445, 455));
                    Utils.sleep(Utils.random(1000, 1200));
                    sc.mouse.click(true);
                }
            }
            //log("DrunkenDwarf Random");
        }

    }

}
