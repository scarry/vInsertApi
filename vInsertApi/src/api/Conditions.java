package api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.util.Utils;

/**
 * Author: Fortruce
 */
public class Conditions {

    public static abstract class Condition {
        public abstract boolean validate(ScriptContext ctx);
    }

    /**
     * Waits for condition to be true.
     * @param c
     *      Condition to test.
     * @param timeout
     * @param ctx
     * @return
     *      Condition met.
     */
    public static boolean waitFor(final Condition c, final int timeout, ScriptContext ctx) {
        return waitFor(c, timeout, 0, ctx);
    }

    /**
     * Waits for condition to be true with sleep.
     * @param c
     *      Condition to test.
     * @param timeout
     * @param sleepTime
     *      Time to sleep on success.
     * @param ctx
     * @return
     *      Condition met.
     */
    public static boolean waitFor(final Condition c, final int timeout, final int sleepTime, ScriptContext ctx) {
        final Timer t = new Timer(timeout);
        while (t.isRunning() && !c.validate(ctx)) {
            //Utils.sleep(timeout);
            Utils.sleep(10);
        }
        if (c.validate(ctx)) {
            Utils.sleep(sleepTime);
            return true;
        }
        return c.validate(ctx);
    }

    public static class isBankOpen extends Condition {
        @Override
        public boolean validate(ScriptContext ctx) {
            return ctx.bank.isOpen();
        }
    }

    public static class isNpcOnScreen extends Condition {

        private int[] npcIds;

        public isNpcOnScreen(int... npcIds) {
            this.npcIds = npcIds;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            Npc npc = ctx.npcs.getNearest(ctx.players.getLocalPlayer().getLocation(), Filters.npcId(this.npcIds));
            return npc != null && ctx.camera.isVisible(npc);
        }
    }

    public static class isNpcLoaded extends Condition {

        private int[] npcIds;

        public isNpcLoaded(int... npcIds) {
            this.npcIds = npcIds;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            Npc npc = ctx.npcs.getNearest(ctx.players.getLocalPlayer().getLocation(), Filters.npcId(this.npcIds));
            return npc != null;
        }
    }

    public static class isGameObjectLoaded extends Condition {

        private int[] gameObjects;

        public isGameObjectLoaded(int... gameObjects) {
            this.gameObjects = gameObjects;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            GameObject object = ctx.objects.getNearest(Filters.objectId(this.gameObjects));
            return object != null;
        }
    }

    public static class isInteracting extends Condition {
        @Override
        public boolean validate(ScriptContext ctx) {
            return ctx.players.getLocalPlayer().getInteracting() != null;
        }
    }

    public static class isNearTile extends Condition {
        private int distance;
        private Tile tile;

        public isNearTile(Tile tile, int distance) {
            this.distance = distance;
            this.tile = tile;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            return this.tile.distanceTo(ctx.players.getLocalPlayer().getLocation()) < distance;
        }
    }

    public static class isVisible extends Condition {
        private Actor actor;
        private GameObject gameObject;
        private Tile tile;
        private GroundItem groundItem;

        public isVisible(Actor actor) {
            this.actor = actor;
        }

        public isVisible(GameObject gameObject) {
            this.gameObject = gameObject;
        }

        public isVisible(Tile tile) {
            this.tile = tile;
        }

        public isVisible(GroundItem groundItem) {
            this.groundItem = groundItem;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            if (this.actor != null) return ctx.camera.isVisible(this.actor);
            if (this.gameObject != null) return ctx.camera.isVisible(gameObject);
            if (this.tile != null) return ctx.camera.isVisible(this.tile);
            if (this.groundItem != null) return ctx.camera.isVisible(this.groundItem);
            return false;
        }
    }

    public static class isMoving extends Condition {
        @Override
        public boolean validate(ScriptContext ctx) {
            return ctx.players.getLocalPlayer().isMoving();
        }
    }

    public static class isNotMoving extends Condition {
        @Override
        public boolean validate(ScriptContext ctx) {
            return !ctx.players.getLocalPlayer().isMoving();
        }
    }

    public static class inventoryContains extends Condition {
        private int[] itemIds;

        public inventoryContains(int... itemIds) {
            this.itemIds = itemIds;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            return ctx.inventory.contains(Filters.itemId(itemIds));
        }
    }

    public static class inventoryNotContains extends Condition {
        private int[] itemIds;

        public inventoryNotContains(int... itemIds) {
            this.itemIds = itemIds;
        }

        @Override
        public boolean validate(ScriptContext ctx) {
            return !ctx.inventory.contains(Filters.itemId(itemIds));
        }
    }

    public static class isInArea extends Condition {
        private Area area;
        public isInArea(Area area) {
            this.area = area;
        }
        @Override
        public boolean validate(ScriptContext ctx) {
            return this.area.contains(ctx.players.getLocalPlayer().getLocation());
        }
    }
}
