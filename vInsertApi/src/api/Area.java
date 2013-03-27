package api;

import java.awt.*;
import java.util.ArrayList;

import org.vinsert.bot.script.api.Tile;


/**
 *
 * @author Jewtage
 *
 */

public class Area{
	private final Polygon area;
	private final int plane;

	public Area(final Tile[] tiles, final int plane){
		area = tilesToPolygon(tiles);
		this.plane = plane;
	}

	public Area(final Tile[] tiles){
		this(tiles, 0);
	}

	public Area(final Tile southwest, final Tile northeast){
		this(southwest, northeast, 0);
	}

	public Area(final int swX, final int swY, final int neX, final int neY){
		this(new Tile(swX, swY), new Tile(neX, neY), 0);
	}

	public Area(final int swX, final int swY, final int neX, final int neY, final int plane){
		this(new Tile(swX, swY), new Tile(neX, neY), plane);
	}

	public Area(final Tile southwest, final Tile northeast, final int plane){
		this(new Tile[]{southwest, new Tile(northeast.getX() + 1, southwest.getY()), new Tile(northeast.getX() + 1, northeast.getY() + 1), new Tile(southwest.getX(), northeast.getY() + 1)}, plane);
	}

	public boolean contains(final Tile... tiles){
		final Tile[] areaTiles = getTiles();
		for(final Tile check: tiles){
			for(final Tile space: areaTiles){
				if(check.equals(space)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean contains(final int x, final int y){
		return this.contains(new Tile(x, y));
	}

	public boolean contains(final int plane, final Tile... tiles){
		return this.plane == plane && this.contains(tiles);
	}

	public Rectangle getDimensions(){
		return new Rectangle(area.getBounds().x + 1, area.getBounds().y + 1, getWidth(), getHeight());
	}

	public Tile getNearestTile(final Tile base){
		Tile tempTile = null;
		for(final Tile tile : getTiles()){
			if(tempTile == null || base.distanceTo(tile) < tempTile.distanceTo(tile)){
				tempTile = tile;
			}
		}
		return tempTile;
	}

	public int getPlane(){
		return plane;
	}

	public Polygon getPolygon(){
		return area;
	}

	public Tile[] getTiles(){
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for(int x = getX(); x <= getX() + getWidth(); x++){
			for(int y = getY(); y <= getY() + getHeight(); y++){
				if(area.contains(x, y)){
					tiles.add(new Tile(x, y));
				}
			}
		}
		return tiles.toArray(new Tile[tiles.size()]);
	}

	public int getWidth(){
		return area.getBounds().width;
	}

	public int getHeight(){
		return area.getBounds().height;
	}

	public int getX(){
		return area.getBounds().x;
	}

	public int getY(){
		return area.getBounds().y;
	}

	public Polygon tilesToPolygon(final Tile[] tiles){
		final Polygon polygon = new Polygon();
		for(final Tile t : tiles){
			polygon.addPoint(t.getX(), t.getY());
		}
		return polygon;
	}
}