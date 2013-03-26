package api;

import java.awt.Point;

import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.tools.Inventory;

public class Utilities {

	ScriptContext sc;
	
	public void Utilites(ScriptContext context){
		this.sc = context;
		return;
	}	
	
	public static Tile halveDistance(Tile a, Tile b) {
		int x, y;
		x = a.getX() + b.getX();
		x /= 2;
		y = a.getY() + b.getY();
		y /= 2;
		return new Tile(x, y);
	}
	
	public static boolean clickItem(Item item){
		int slot = sc.inventory.indexOf(item);
		Point point = sc.inventory.getClickPoint(slot);
		sc.mouse.click(point.x, point.y);
		return false;
	}
	
	
}
