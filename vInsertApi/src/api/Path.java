package api;

import api.Path.TraversableObject.Direction;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Utils;

import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Path {
    private final Tile[] tiles;
    private final Tile[] reverseTiles;

    private final TraversableObject[] objects;
    private ScriptContext context;
    private Utilities utilities;
    private Player localPlayer;

    public static class TraversableObject {
        private final Tile location;
        private final int objectId;
        private final int plane;
        private final String interaction;
        private final Direction direction;

        public enum Direction {
            FORWARD, REVERSE, BOTH
        }

        public TraversableObject(int objectId, Tile location, int plane, String interaction, Direction direction) {
            this.objectId = objectId;
            this.location = location;
            this.plane = plane;
            this.interaction = interaction;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return String.format("%d", this.objectId) + " " + this.location.toString();
        }
    }

    public Path(final Tile[] tiles, TraversableObject[] objects, ScriptContext context) {
        this.tiles = tiles;
        this.reverseTiles = reverseTiles(tiles);

        if (objects.length == 0)
            this.objects = null;
        else
            this.objects = objects;

        this.context = context;
        this.utilities = new Utilities(context);
        this.localPlayer = context.players.getLocalPlayer();
    }

    public Path(final Tile[] tiles, ScriptContext context) {
        this(tiles, new TraversableObject[]{}, context);
    }

    private Tile[] reverseTiles(final Tile[] tiles) {
        Tile[] reverse = new Tile[this.tiles.length];
        for (int i = 0; i < this.tiles.length; i++) {
            reverse[i] = this.tiles[this.tiles.length - 1 - i];
        }
        return reverse;
    }

    public Tile[] getTiles() {
        return this.tiles;
    }

    public Tile getStart(boolean forward) {
        if (forward && tiles.length > 0)
            return tiles[0];
        else if (reverseTiles.length > 0)
            return reverseTiles[0];
        return null;
    }

    /**
     * Gets the last tile in the path.
     *
     * @param forward True for last tile in forward direction, False for last tile in reversed direction.
     * @return Last tile in path
     */
    public Tile getEnd(boolean forward) {
        if (forward && tiles.length > 0)
            return tiles[tiles.length - 1];
        else if (reverseTiles.length > 0)
            return reverseTiles[reverseTiles.length - 1];
        return null;
    }

    /**
     * Traverses path in specified direction.
     *
     * @param forward Direction to traverse the path.
     */
    public void traverse(boolean forward) {
        traverse(forward, true);
    }

    /**
     * Traverses the path in specified direction.
     *
     * @param forward Direction to traverse the path.
     * @param run     Toggle run.
     */
    public void traverse(boolean forward, final boolean run) {
        final Tile next = next(forward);
        traverse(next, forward, run, 3);
    }

    /**
     * Traverses path in specified direction.
     *
     * @param forward   Direction to traverse the path.
     * @param run       Toggle run.
     * @param deviation Amount of deviation.
     */
    public void traverse(boolean forward, final boolean run, int deviation) {
        final Tile next = next(forward);
        traverse(next, forward, run, deviation);
    }

    private boolean directionCheck(Direction direction, boolean forward) {
        if (direction == Direction.BOTH)
            return true;
        if (direction == Direction.FORWARD && forward)
            return true;
        if (direction == Direction.REVERSE && !forward)
            return true;
        return false;
    }

    private void traverse(final Tile next, boolean forward, final boolean run, int deviation) {
        boolean traversingObject = false;
        if (objects != null) {
            for (TraversableObject obj : objects) {
                GameObject gameObj = context.objects.getNearest(Filters.objectId(obj.objectId));
                if (gameObj != null && gameObj.getLocation().equals(obj.location) &&
                        context.getClient().getPlane() == obj.plane &&
                        directionCheck(obj.direction, forward)) {
                    //found obj - traverse
                    traversingObject = true;
                    if (context.camera.isVisible(gameObj)) {
                        gameObj.interact(obj.interaction);
                        Utils.sleep(Utils.random(1000, 1500));
                    } else {
                        context.navigation.navigate(gameObj.getLocation(), NavigationPolicy.MINIMAP);
                        Utils.sleep(Utils.random(1000, 1500));
                    }
                }
            }
        }
        if (context.game.getGameState().id() == 30
                && next != null && !isAtEnd(next, deviation, forward)
                && !traversingObject) { //TODO check the this.getEnd()
            if (run) {
                context.keyboard.press(KeyEvent.VK_CONTROL);
                Utils.sleep(Utils.random(80, 150));
            }
            context.navigation.navigate(next, NavigationPolicy.MINIMAP);
            if (run) {
                context.keyboard.press(KeyEvent.VK_CONTROL);
                Utils.sleep(Utils.random(1000, 2000));
            }
        }
    }

    private boolean isAtEnd(Tile next, int deviation, boolean forward) {
        return localPlayer.getLocation().distanceTo(next) < deviation
                || localPlayer.getLocation().distanceTo(this.getEnd(forward)) < deviation; //TODO check getEnd()
    }

    private Tile next(boolean forward) {
        Tile[] temp;
        if (forward)
            temp = tiles;
        else
            temp = reverseTiles;

        for (int i = temp.length - 1; i >= 0; --i) {
            if (utilities.isOnMinimap(temp[i])) { //TODO add isWalkable check
                return temp[i];
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Arrays.toString(tiles);
    }

    private void log(String string) {
        this.context.getBot().log("Path", string);
    }

}