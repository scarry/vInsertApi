package api;

import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.tools.*;
import org.vinsert.bot.script.api.tools.Menu;
import org.vinsert.bot.util.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Utilities {

    ScriptContext ctx;
    protected Bank bank;
    protected Camera camera;
    protected Inventory inventory;
    protected Keyboard keyboard;
    protected Menu menu;
    protected Mouse mouse;
    protected Navigation navigation;
    protected Npcs npcs;
    protected Objects objects;
    protected Players players;
    protected Widgets widgets;
    protected Player localPlayer;
    protected Game game;
    protected Skills skills;
    protected Settings settings;
    protected Bot bot;

    public Utilities(ScriptContext ctx) {
        this.ctx = ctx;

        game = ctx.game;
        bank = ctx.bank;
        camera = ctx.camera;
        inventory = ctx.inventory;
        keyboard = ctx.keyboard;
        menu = ctx.menu;
        mouse = ctx.mouse;
        navigation = ctx.navigation;
        npcs = ctx.npcs;
        objects = ctx.objects;
        players = ctx.players;
        widgets = ctx.widgets;
        skills = ctx.skills;
        settings = ctx.settings;
        localPlayer = ctx.players.getLocalPlayer();
        bot = ctx.getBot();
    }

    public void log(String string) {
        bot.log("Util", string);
    }

    public Tile halveDistance(Tile a, Tile b) {
        int x, y;
        x = a.getX() + b.getX();
        x /= 2;
        y = a.getY() + b.getY();
        y /= 2;
        return new Tile(x, y);
    }

    public boolean clickItem(Item item) {
        int slot = inventory.indexOf(item);
        Point point = inventory.getClickPoint(slot);
        mouse.click(point.x, point.y);
        return false;
    }

    /**
     * Creates a path between src and dest tiles.
     *
     * @param src      The start tile of the path.
     * @param dest     The destination tile of the path.
     * @param distance The desired distance between path tiles.
     * @return The created path.
     */
    private Path createPath(Tile src, Tile dest, int distance) {
        if (!(distance > 0) || src == null || dest == null)
            return null;

        int totalDistance = src.distanceTo(dest);
        int numberOfTiles = totalDistance / distance;

        Tile[] pathTiles = new Tile[numberOfTiles + 1];

        int srcX = src.getX();
        int srcY = src.getY();

        int deltaX = dest.getX() - srcX;
        int deltaY = dest.getY() - srcY;

        int adjustX = deltaX / numberOfTiles;
        int adjustY = deltaY / numberOfTiles;

        pathTiles[0] = src;
        pathTiles[pathTiles.length - 1] = dest;
        for (int i = 1; i < pathTiles.length - 1; i++)
            pathTiles[i] = new Tile(srcX + (adjustX * i), srcY + (adjustY * i));

        return new Path(pathTiles, this.ctx);
    }

    public Path createPath(int distance, Tile... tiles) {
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Tile> cumulativeTiles = new ArrayList<>();
        for (int i = 0; i < tiles.length - 1; i++) {
            paths.add(createPath(tiles[i], tiles[i + 1], distance));
        }
        for (Path p : paths) {
            Collections.addAll(cumulativeTiles, p.getTiles());
        }
        return new Path(cumulativeTiles.toArray(new Tile[cumulativeTiles.size()]), this.ctx);
    }

    public boolean interact(Actor actor, String action)
    {
        if (actor == null)
            return false;

        int speed = this.ctx.mouse.getSpeed();
        this.ctx.mouse.setSpeed(speed - 4);

        Point point = actor.hullPoint(actor.hull());
        this.ctx.mouse.move(point.x, point.y);
        Utils.sleep(Utils.random(15, 35));

        int index = this.ctx.menu.getIndex(action);

        if (index == 0) {
            this.ctx.mouse.click();
//            Utils.sleep(Utils.random(200, 400));
            this.ctx.mouse.setSpeed(speed);
            return true;
        }

        if (index != -1) {
            this.ctx.mouse.click(true);
            Point menuPoint = this.ctx.menu.getClickPoint(index);
            this.ctx.mouse.click(menuPoint.x, menuPoint.y);
//            Utils.sleep(Utils.random(350, 650));
            this.ctx.mouse.setSpeed(speed);
            return true;
        }

        this.ctx.mouse.setSpeed(speed);
        return false;
    }

    public Tile walkableLocation(Tile loc) {
        if (isOnMinimap(loc))
            return loc;
        Tile halfway = halveDistance(localPlayer.getLocation(), loc);
        return walkableLocation(halfway);
    }

    public boolean isOnMinimap(Tile t) {
        return localPlayer.getLocation().distanceTo(t) < 17;
    }
}
