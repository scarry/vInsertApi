package api;

import org.vinsert.bot.script.api.Tile;

public class Utilities {
	public static Tile halveDistance(Tile a, Tile b) {
		int x, y;
		x = a.getX() + b.getX();
		x /= 2;
		y = a.getY() + b.getY();
		y /= 2;
		return new Tile(x, y);
	}
}
