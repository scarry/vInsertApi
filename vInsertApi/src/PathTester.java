
import api.*;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Tile;

import java.awt.*;


@ScriptManifest(authors = {"Fortruce"}, name = "Path Tester")
public class PathTester extends ScriptBase {

    Area fishingGuildArea = new Area(new Tile[]{new Tile(2598, 3394), new Tile(2618, 3394), new Tile(2618, 3382),
            new Tile(2598, 3382)});

    Area ardyArea = new Area(new Tile[]{new Tile(2602, 3302), new Tile(2602, 3292), new Tile(2610, 3292),
            new Tile(2610, 3302)});

    Tile[] fishToArdyPathTiles = new Tile[]{new Tile(2609, 3392), new Tile(2609, 3387), new Tile(2609, 3382),
            new Tile(2611, 3377), new Tile(2613, 3372), new Tile(2614, 3367),
            new Tile(2613, 3362), new Tile(2613, 3357), new Tile(2612, 3352),
            new Tile(2612, 3347), new Tile(2612, 3342), new Tile(2610, 3339),
            new Tile(2608, 3336), new Tile(2607, 3332), new Tile(2607, 3327),
            new Tile(2607, 3322), new Tile(2607, 3317), new Tile(2606, 3312),
            new Tile(2606, 3307), new Tile(2607, 3302), new Tile(2607, 3297)};

    Path fishToArdyPath;
    boolean walkingToFish = false;
    boolean walkingToBank = false;


    public class InArea extends Node {
        @Override
        public boolean activate() {
            if ((fishingGuildArea.contains(localPlayer.getLocation()) ||
                    ardyArea.contains(localPlayer.getLocation())))
                return true;
            return false;
        }

        @Override
        public void execute() {
            walkingToFish = false;
            walkingToBank = false;
        }

    }

    public class WalkToArdy extends Node {

        @Override
        public boolean activate() {
            if (!ardyArea.contains(localPlayer.getLocation())
                    && !walkingToFish) {
                walkingToBank = true;
                return true;
            }
            return false;
        }

        @Override
        public void execute() {
            log("traversing");
            fishToArdyPath.traverse(true);
            sleep(1000, 2000);
        }

    }

    public class WalkToFish extends Node {

        @Override
        public boolean activate() {
            if (!fishingGuildArea.contains(localPlayer.getLocation())
                    && !walkingToBank) {
                walkingToFish = true;
                return true;
            }
            return false;
        }

        @Override
        public void execute() {
            log("traversing");
            fishToArdyPath.traverse(false);
            sleep(1000, 2000);
        }

    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub

        fishToArdyPath = new Path(fishToArdyPathTiles, getContext());

        submit(new WalkToFish());
        submit(new WalkToArdy());
        submit(new InArea());


//		fishToArdyPath.traverse(Direction.FORWARD);

        return true;
    }

    private static final Timer TIMER = new Timer(0);

    @Override
    public void render(Graphics2D g) {
        int[] point = {385, 2};

        //box
        g.setColor(new Color(63, 63, 43, 200));
        g.draw3DRect(375, 5, 139, 225, true);
        g.fill3DRect(375, 5, 139, 325, true);

        int height = g.getFontMetrics().getHeight();

        g.setColor(Color.WHITE);
        g.drawString("Fortruce - FightCave", point[0], point[1] += height);
        g.drawLine(383, 21, 495, 21);

        g.drawString("Run Time:  " + TIMER.toElapsedString(), point[0], point[1] += height);

        if (fishToArdyPath != null) {
            g.drawString("in ardy: " + ardyArea.contains(localPlayer.getLocation()), point[0], point[1] += height);
            g.drawString("in guild: " + fishingGuildArea.contains(localPlayer.getLocation()), point[0], point[1] += height);
            g.drawString("walkingfish: " + walkingToFish, point[0], point[1] += height);
            g.drawString("walkingbank: " + walkingToBank, point[0], point[1] += height);
        }
    }

}
