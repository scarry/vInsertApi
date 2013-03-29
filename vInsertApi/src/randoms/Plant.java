package randoms;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Utils;

public class Plant extends RandomEvent {

    ScriptContext sc;
    public Npc plant;


    int[] plant2 = {407};

    @Override
    public boolean init() {
        return sc.npcs.getNearest(plant2) != null;
    }

    @Override
    public int pulse() {
        plant = sc.npcs.getNearest(plant2);
        if (plant != null) {
            if (plant.getLocation().distanceTo(sc.players.getLocalPlayer().getLocation()) <= 2) {
                sc.camera.rotateToTile(plant.getLocation());
                plant.interact("Pick");
                Utils.sleep(Utils.random(2000, 4000));
                sc.mouse.click(300, 455);
                Utils.sleep(Utils.random(2000, 4000));
                //log("Strange plant :o");
            }
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
