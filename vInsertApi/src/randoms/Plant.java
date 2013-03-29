package randoms;

import api.AntiRandom;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.util.Utils;

public class Plant extends AntiRandom {

    ScriptContext sc;
    public Npc plant;

    public Plant(ScriptContext context) {
        super(context);
        sc = context;
    }

    int[] plant2 = {407};

    @Override
    public boolean activate() {
        return sc.npcs.getNearest(plant2) != null;
    }

    @Override
    public void execute() {
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

    }
}
