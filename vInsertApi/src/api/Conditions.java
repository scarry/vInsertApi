package api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.*;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.util.Utils;

/**
 * Author: Fortruce
 */
public class Conditions {

    public static abstract class Condition {
        public abstract boolean validate(ScriptContext ctx);
    }

    public static boolean waitFor(final Condition c, final int timeout, ScriptContext ctx) {
        return waitFor(c, timeout, 0, ctx);
    }

    public static boolean waitFor(final Condition c, final int timeout, final int sleepTime, ScriptContext ctx) {
        final Timer t = new Timer(timeout);
        while (t.isRunning() && !c.validate(ctx)) {
            Utils.sleep(timeout);
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
}
