import api.*;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.*;
import api.Path.TraversableObject;
import api.Path.TraversableObject.Direction;

import java.awt.*;

/**
 * Author: Fortruce
 */
public class RcBot extends ScriptBase {

    public enum Rune {
        AIR, NATURE
    }

    public enum NatureState {
        GNOMETREE_BANK, GNOMETREE_GLIDER, KARAMJA_PRE_OBS, KARAMJA_POST_OBS, ALKHARID_TELE, ALKHARID_GLIDER
    }

    public static class RcAltar {
        public int entranceId;
        public int altarId;
        public int exitId;
        public int runeId;
        public int tiaraId;
        public Area bankArea;
        public Path path;
        public Rune rune;
        public int essenceId;
        public Enum state;

        public RcAltar(int entrance, int altar, int exit, int runeId, int tiara, Area bank,
                       Path path, Rune rune, int essenceId, Enum state) {
            this.entranceId = entrance;
            this.altarId = altar;
            this.exitId = exit;
            this.runeId = runeId;
            tiaraId = tiara;
            this.bankArea = bank;
            this.path = path;
            this.rune = rune;
            this.essenceId = essenceId;
            this.state = state;

        }
    }

    /**
     * Altar Constants
     */
    private static final int AIR_TIARA_ID = 5528;
    private static final int NATURE_TIARA_ID = 5542;
    private static final int AIR_RUNE_ID = 557;
    private static final int NATURE_RUNE_ID = 562;
    private static final int AIR_ENTRANCE_ID = 2452;
    private static final int NATURE_ENTRANCE_ID = 2460;
    private static final int AIR_ALTAR_ID = 2478;
    private static final int NATURE_ALTAR_ID = 2486;
    private static final int AIR_EXIT_ID = 2465;
    private static final int NATURE_EXIT_ID = 2473;

    /**
     * Tile Paths & TraversableObjects
     */
    private static final Tile[] AIR_PATH_TILES = {
            new Tile(2984, 3288), new Tile(2990, 3291), new Tile(2991, 3296),
            new Tile(2993, 3301), new Tile(2995, 3305), new Tile(2998, 3310),
            new Tile(3000, 3315), new Tile(3004, 3319), new Tile(3007, 3324),
            new Tile(3007, 3329), new Tile(3007, 3334), new Tile(3007, 3339),
            new Tile(3007, 3345), new Tile(3007, 3352), new Tile(3008, 3358),
            new Tile(3013, 3357)
    };
    private static final Tile[] GNOMETREE_BANK_PATH_TILES = new Tile[] {
            new Tile(2465, 3501), new Tile(2466, 3496), new Tile(2461, 3495),
            new Tile(2457, 3493), new Tile(2457, 3488), new Tile(2452, 3488),
            new Tile(2449, 3486), new Tile(2449, 3483), new Tile(2449, 3482)
    };

    private static final Tile[] ALKHARID_GLIDER_PATH_TILES = new Tile[] { new Tile(3320, 3234), new Tile(3315, 3235), new Tile(3310, 3235),
            new Tile(3305, 3236), new Tile(3300, 3237), new Tile(3295, 3240),
            new Tile(3290, 3240), new Tile(3286, 3237), new Tile(3283, 3233),
            new Tile(3280, 3229), new Tile(3278, 3224), new Tile(3277, 3219),
            new Tile(3278, 3214), new Tile(3277, 3214) };

    private static final Tile[] KARAMJA_PATH_TO_OBS_TILES = new Tile[] {
            new Tile(2971, 2969), new Tile(2968, 2973), new Tile(2963, 2976),
            new Tile(2958, 2977), new Tile(2953, 2979), new Tile(2950, 2980),
            new Tile(2947, 2984), new Tile(2945, 2989), new Tile(2942, 2993),
            new Tile(2939, 2997), new Tile(2937, 3002), new Tile(2934, 3006),
            new Tile(2931, 3010), new Tile(2929, 3015), new Tile(2927, 3020),
            new Tile(2926, 3025), new Tile(2924, 3030), new Tile(2923, 3035),
            new Tile(2921, 3040), new Tile(2918, 3044), new Tile(2913, 3047),
            new Tile(2910, 3049) };

    private static final Tile[] KARAMJA_PATH_TO_ALTAR_TILES = new Tile[] {
            new Tile(2906, 3050), new Tile(2901, 3051), new Tile(2896, 3051),
            new Tile(2891, 3052), new Tile(2887, 3049), new Tile(2884, 3045),
            new Tile(2881, 3041), new Tile(2878, 3037), new Tile(2875, 3033),
            new Tile(2873, 3028), new Tile(2870, 3024), new Tile(2868, 3019),
            new Tile(2867, 3016) };

    private static TraversableObject[] GNOMETREE_BANK_TOBJS = new TraversableObject[] {
            new TraversableObject(1746, new Tile(2466, 3495), 3, "Climb-down", Direction.FORWARD),
            new TraversableObject(2884,  new Tile(2466, 3495),  2, "Climb-down", Direction.FORWARD),
            new TraversableObject(1748, new Tile(2466, 3495), 1, "Climb-up", Direction.REVERSE),
            new TraversableObject(2884, new Tile(2466, 3495), 2, "Climb-up", Direction.REVERSE)
    };

    private static TraversableObject[] ALKHARID_PATH_TOBJS = new TraversableObject[] {
            new TraversableObject(3197, new Tile(3311, 3234), 0, "Open", Direction.FORWARD)
    };

    private static final Tile KARAMJA_LOG = new Tile(2909, 3049);

    /**
     * Path Declarations
     */
    private static Path ALKHARID_PATH;
    private static Path GNOMETREE_BANK_PATH;
    private static Path KARAMJA_PATH_TO_ALTAR;
    private static Path KARAMJA_PATH_TO_OBS;
    private static Path AIR_PATH;


    /**
     * Areas
     */
    private static final Area FALADOR_BANK = new Area(new Tile(3009, 3355), new Tile(3016, 3358));

    private static final Area GNOMETREE_BANK_AREA = new Area(new Tile(2447, 3481), new Tile(2452, 3485));

    private static final Area ALKHARID_TELE_AREA = new Area(new Tile[] { new Tile(3328, 3247), new Tile(3323, 3247), new Tile(3310, 3247),
            new Tile(3310, 3221), new Tile(3328, 3221) });

    private static final Area KARAMJA_PRE_OBSTACLE = new Area(new Tile[] { new Tile(2908, 3054), new Tile(2908, 3049), new Tile(2910, 3044),
            new Tile(2915, 3044), new Tile(2918, 3044), new Tile(2918, 3053),
            new Tile(2918, 3054) });

    private static final Area KARAMJA_POST_OBSTACLE = new Area(new Tile[] { new Tile(2907, 3053), new Tile(2907, 3045), new Tile(2900, 3045),
            new Tile(2900, 3053) });

    private static final Area KARAMJA_GLIDER = new Area(new Tile[] { new Tile(2959, 2987), new Tile(2979, 2987), new Tile(2979, 2959),
            new Tile(2962, 2962) });

    private static final Area GNOMETREE_GLIDER = new Area(new Tile(2463, 3499), new Tile(2467, 3502));


    /**
     * Widgets/Component Ids
     */
    private static final int GLIDER_WIDGET = 138;
    private static final int GLIDER_GNOMETREE_COMPONENT = 21;
    private static final int GLIDER_KARAMJA_COMPONENT = 20;

    /**
     * Inventory Ids
     */
    private static final int RUNE_ESSENCE_ID = 1437;
    private static final int PURE_RUNE_ESSENCE_ID = 7937;
    private static final int RING_OF_DUELING_8 = 2553;

    /**
     * Misc Ids
     */
    public static final int[] BANK_STALL_ID = {2213, 11758};
    private static final int GNOMETREE_GLIDER_NPC_ID = 3811;  //interact  "Glider"
    private static final int ALKHARID_GLIDER_NPC_ID = 3809;

    private static RcAltar AIR;
    private static RcAltar NATURE;

    private static RcAltar currAltar;


    private boolean hasEssence() {
        return inventory.contains(Filters.itemId(currAltar.essenceId));
    }

    private boolean hasRunes() {
        return inventory.contains(Filters.itemId(currAltar.runeId));
    }

    private boolean inAltar() {
        GameObject altar = objects.getNearest(Filters.objectId(currAltar.altarId));
        return altar != null;
    }

    private boolean altarNearby() {
        GameObject entrance = objects.getNearest(Filters.objectId(currAltar.entranceId));
        return entrance != null && camera.isVisible(entrance);
    }

    private boolean inBank() {
        return currAltar.bankArea.contains(localPlayer.getLocation());
    }

//    public class WalkToBank extends Node {
//        @Override
//        public boolean activate() {
//            if (!hasEssence() && !inAltar() && !inBank())
//                return true;
//            return false;
//        }
//
//        @Override
//        public void execute() {
//
//            if(currAltar.rune == Rune.NATURE) {
//                Npc gliderNpc;
//                switch(currAltar.state) {
//                    case ALKHARID_TELE:
//                        ALKHARID_PATH.traverse(true, false, 3);
//                        if (Conditions.waitFor(new Conditions.Condition() {
//                            @Override
//                            public boolean validate(ScriptContext ctx) {
//                                return ctx.npcs.getNearest(ALKHARID_GLIDER_NPC_ID) != null;
//                            }
//                        }, random(1200, 1500), getContext()));
//                            natureState = NatureState.ALKHARID_GLIDER;
//                        break;
//                    case ALKHARID_GLIDER:
//                        if (GNOMETREE_GLIDER.contains(localPlayer.getLocation()))
//                            currAltar.state = NatureState.GNOMETREE_BANK;
//                        gliderNpc = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(ALKHARID_GLIDER_NPC_ID));
//                        if (gliderNpc != null) {
//                            if (!camera.isVisible(gliderNpc)) {
//                                navigation.navigate(gliderNpc.getLocation(), Navigation.NavigationPolicy.MINIMAP);
//                                ExConditions.waitFor(new ExConditions.isEntityOnScreen(gliderNpc), 1500);
//                            }
//                            else {
//                                gliderNpc.interact("Glider");
//                                if (!ExConditions.waitFor(new ExConditions.isComponentOpen(GLIDER_WIDGET), 1500))
//                                    return;
//                                Widget karamja = widgets.getWidget(GLIDER_WIDGET, GLIDER_GNOMETREE_COMPONENT);
//                                if (karamja == null)
//                                    return;
//                                karamja.interact("Ok");
//                                ExConditions.waitFor(new ExConditions.isWidgetNotOpen(GLIDER_WIDGET), 7000);
//                            }
//                        }
//                        break;
//                    case GNOMETREE_BANK:
//                        GNOMETREE_BANK_PATH.traverseObjects(GNOMETREE_BANK_TOBJS, Direction.FORWARD, false, true);
//                        ExConditions.waitFor(new ExConditions.isInArea(GNOMETREE_BANK_AREA), 1500);
//                        break;
//                    default:
//                        log("ERROR: DEFAULT CASE - WALK TO BANK");
//                }
//            }
//            else if (currAltar.rune == Rune.AIR) {
//                path.traverse(true, true);
//                ExConditions.waitFor(new ExConditions.isNearLocation(path.getEnd(), 2), 2000);
//            }
//        }
//    }


    @Override
    public boolean init() {
        ALKHARID_PATH = new Path(ALKHARID_GLIDER_PATH_TILES, ALKHARID_PATH_TOBJS, getContext());
        GNOMETREE_BANK_PATH = new Path(GNOMETREE_BANK_PATH_TILES, GNOMETREE_BANK_TOBJS, getContext());
        KARAMJA_PATH_TO_ALTAR = new Path(KARAMJA_PATH_TO_ALTAR_TILES, getContext());
        KARAMJA_PATH_TO_OBS = new Path(KARAMJA_PATH_TO_OBS_TILES, getContext());
        AIR_PATH = new Path(AIR_PATH_TILES, getContext());


        NATURE = new RcAltar(NATURE_ENTRANCE_ID, NATURE_ALTAR_ID, NATURE_EXIT_ID, NATURE_RUNE_ID,
                NATURE_TIARA_ID, GNOMETREE_BANK_AREA, null, Rune.NATURE, PURE_RUNE_ESSENCE_ID, NatureState.GNOMETREE_BANK);
        AIR = new RcAltar(AIR_ENTRANCE_ID, AIR_ALTAR_ID, AIR_EXIT_ID, AIR_RUNE_ID, AIR_TIARA_ID,
                FALADOR_BANK, AIR_PATH, Rune.AIR, PURE_RUNE_ESSENCE_ID, null);

        currAltar = NATURE;

        return true;
    }

    @Override
    public void render(Graphics2D arg0) {
    }
}
