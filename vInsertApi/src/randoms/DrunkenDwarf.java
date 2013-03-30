package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.util.Utils;

//import randoms.AntiRandom;
@ScriptManifest(name = "zDrunkenDwarf", authors = { "potofreak" })

public class DrunkenDwarf extends AntiRandom {

    public static final int DWARF_ID = 956;
    Npc DrunkenDwarf;

    @Override
    public boolean init() {
        return sc.npcs.getNearest(DWARF_ID) != null && sc.npcs.getNearest(DWARF_ID).getLocation().distanceTo(localPlayer.getLocation()) <= 2;
    }

    @Override
    public int pulse() {
        if(!init()){
            requestExit();
            return 0;
        }
        DrunkenDwarf = sc.npcs.getNearest(DWARF_ID);
        Widget[] b = sc.widgets.get(241);
        Widget[] c = sc.widgets.get(242);
        Widget[] d = sc.widgets.get(243);
        Widget[] e = sc.widgets.get(244);
        if (DrunkenDwarf != null) {
            if (DrunkenDwarf.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(DrunkenDwarf.getLocation());
                utilities.interact(DrunkenDwarf,"Talk-to");
                //DrunkenDwarf.interact("Talk-to Drunken Dwarf");
                Utils.sleep(random(1000, 2500));
                if (b != null && b.length > 0 || c.length > 0 || d.length > 0 || e.length > 0) {
                    utilities.clickToContinue();
                }
            }
            //log("DrunkenDwarf Random");
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
