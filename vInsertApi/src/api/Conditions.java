package api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
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
        final Timer t = new Timer(timeout);
        while (t.isRunning() && !c.validate(ctx)) {
            Utils.sleep(timeout);
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

        public isNpcOnScreen(int npcId) {
            this.npcIds = new int[] {npcId};
        }

        public isNpcOnScreen(int[] npcIds) {
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

        public isNpcLoaded(int npcId) {
            this.npcIds = new int[] {npcId};
        }

        public isNpcLoaded(int[] npcIds) {
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
}
