package api;

import java.awt.Point;
import java.util.ArrayList;

import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.tools.Bank;
import org.vinsert.bot.script.api.tools.Camera;
import org.vinsert.bot.script.api.tools.Game;
import org.vinsert.bot.script.api.tools.Inventory;
import org.vinsert.bot.script.api.tools.Keyboard;
import org.vinsert.bot.script.api.tools.Menu;
import org.vinsert.bot.script.api.tools.Mouse;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.script.api.tools.Npcs;
import org.vinsert.bot.script.api.tools.Objects;
import org.vinsert.bot.script.api.tools.Players;
import org.vinsert.bot.script.api.tools.Settings;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.script.api.tools.Widgets;

public class Utilities {

	ScriptContext context;
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
	
	public Utilities(ScriptContext context) {
		this.context = context;
		
	    game = context.game;
	    bank = context.bank;
	    camera = context.camera;
	    inventory = context.inventory;
	    keyboard = context.keyboard;
	    menu = context.menu;
	    mouse = context.mouse;
	    navigation = context.navigation;
	    npcs = context.npcs;
	    objects = context.objects;
	    players = context.players;
	    widgets = context.widgets;
	    skills = context.skills;
	    settings = context.settings;
	    localPlayer = context.players.getLocalPlayer();
	    bot = context.getBot();
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
	
	public boolean clickItem(Item item){
		int slot = inventory.indexOf(item);
		Point point = inventory.getClickPoint(slot);
		mouse.click(point.x, point.y);
		return false;
	}

    /**
     * Creates a path between src and dest tiles.
     * @param src
     * 		The start tile of the path.
     * @param dest
     * 		The destination tile of the path.
     * @param distance
     * 		The desired distance between path tiles.
     * @return
     * 		The created path.
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

        return new Path(pathTiles, this.context);
    }

    public Path createPath(int distance, Tile ... tiles) {
        ArrayList<Path> paths = new ArrayList<>();
        for (int i = 0; i < tiles.length - 1; i++) {
            paths.add(createPath(tiles[i], tiles[i+1], distance));
        }
        return new Path(this.context, paths.toArray(new Path[paths.size()]));
    }
	
	public void interact(Actor actor, String action) {
		if (actor == null)
			return;
		Point point = actor.hullPoint(actor.hull());
		mouse.move(point.x, point.y);
		int index = menu.getIndex(action);
		if (index == -1)
			return;
		if (index == 0) {
			mouse.click();
			return;
		}
		actor.interact(action);
	}
	
	public Tile walkableLocation(Tile loc) {
		if (localPlayer.getLocation().distanceTo(loc) < 17)
			return loc;
		Tile halfway = halveDistance(localPlayer.getLocation(), loc);
		return walkableLocation(halfway);
	}
	
	public boolean isOnMinimap(Tile t) {
		if (localPlayer.getLocation().distanceTo(t) < 17)
			return true;
		return false;
	}
}
