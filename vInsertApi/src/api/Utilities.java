package api;

import java.awt.Point;

import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
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
	
	public Tile walkableLocation(Tile loc) {
		if (localPlayer.getLocation().distanceTo(loc) < 17)
			return loc;
		log(String.format("[%d, %d]", loc.getX(), loc.getY()));
		return walkableLocation(halveDistance(localPlayer.getLocation(), loc));
	}
}
