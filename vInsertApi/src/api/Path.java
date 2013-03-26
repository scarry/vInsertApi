package api;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
 
import api.Path.TraversableObject.Direction;
 
public class Path {
	private final Tile[] tiles;
	private final TraversableObject[] objects;
	private Path reversed;
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
	}
 
	public Path(final Tile[] tiles, TraversableObject[] objects, ScriptContext context) {
		this.tiles = tiles;
		this.objects = objects;
		this.context = context;
		this.utilities = new Utilities(context);
		this.localPlayer = context.players.getLocalPlayer();
	}
	
	public Path(final Tile[] tiles, ScriptContext context) {
		this(tiles, new TraversableObject[] {}, context);
	}

	public Tile getStart() {
		return tiles[0];
	}
 
	public Tile getEnd() {
		return tiles[tiles.length - 1];
	}
 
	/**
	 * Loops through the path to completely traverse it.
	 */
	public void traverseCompletely() {
		traverseCompletely(true);
	}
 
	public void traverseCompletely(final boolean run) {
		Tile next;
		while ((next = next()) != null && !isAtEnd(next)) {
			traverse(next, run);
		}
	}
 
	/**
	 * Traverses the path. This method must be looped.
	 */
	public void traverse() {
		traverse(true);
	}
 
	public void traverse(final boolean run) {
		final Tile next = next();
		traverse(next, run);
	}

	/**
	 * Traverses a path walking thru objects (doors, ladders, caves) that are identified.
	 * @param objects
	 * 		Array of TraversableObjects to be walked through.
	 * @param direction
	 * 		The direction relative to the path that the object should be traversed (forward if you would call
	 * 		with path, reverse if you would call with path.reverse, or both)
	 * @param run
	 * 		Toggle run on/off
	 * @param force
	 * 		Force a closer distance check for destination.
	 */
	public void traverseObjects(TraversableObject[] objects, final Direction direction, final boolean run, final boolean force) {
		final Tile next = next();

		for (int i = 0; i < objects.length; i++) {
			GameObject object = Objects.getNearest(objects[i].objectId);
			if(object != null && object.getLocation().equals(objects[i].location) 
					&& Game.getPlane() == objects[i].plane &&
					(objects[i].direction == direction || objects[i].direction == Direction.BOTH)) {
				if (object.isVisible()) {
				//found matching object
				Methods.log("Found matching object -- interacting");
				//traverse thru object
				object.interact(objects[i].interaction);
				}
				else {
					log("walking to object");
					Walking.walkTo(objects[i].location);
					ExConditions.waitFor(new ExConditions.isObjectVisible(objects[i].objectId), 2000);
				}
			}
			else
				traverse(next, run, force);
		}
	}
 
	/**
	 * Walks one step in path.
	 * @param run
	 * 			Toggle run on off.
	 * @param force
	 * 			Toggle close distance check for end checking.
	 */
	public void traverse(final boolean run, final boolean force) {
		final Tile next = next();
		traverse(next, run, force);
	}

	/**
	 * Walks one step in the path.
	 *
	 * @param next
	 *            The next <code>Tile</code>
	 */

	private void traverse(final Tile next, final boolean run) {
		traverse(next, run, false);
	}

	private void traverse(final Tile next, final boolean run, final boolean force) {
		if (Game.getGameState() == 30
				&& next != null && !isAtEnd(next, force)
				&& next.distanceTo(Walking.getDestination())> 3) {
			if (run) {
				//Keyboard.pressKey(KeyEvent.VK_CONTROL);
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				Methods.sleep(80, 150);
			}
			Walking.walkTo(next);
			if (run) {
				//Keyboard.releaseKey(KeyEvent.VK_CONTROL,Random.nextInt(80, 150));
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
				Methods.sleep(1000, 2000);
			}
		}
	}


 
	/**
	 * @param next
	 *            The next <code>Tile</code>
	 * @return Whether the given <code>Tile</code> is at the end of the path.
	 */
	private boolean isAtEnd(Tile next) {
		return isAtEnd(next, false);
	}

	private boolean isAtEnd(Tile next, int deviation) {
		return distanceTo(next) < deviation
				|| Calculations.distanceTo(Walking.getDestination()) < deviation;
	}
 
	/**
	 * @return The next walkable <code>Tile</code> on the minimap.
	 */
	private Tile next() {
		for (int i = tiles.length - 1; i >= 0; --i) {
			if (utilities.isOnMinimap(tiles[i])) { //TODO add isWalkable check
				return tiles[i];
			}
		}
		return null;
	}
 
	/**
	 * Lazily reverses this <code>TilePath</code>.
	 *
	 * @return The reversed <code>TilePath</code>
	 */
	public Path reverse() {
		if (reversed == null) {
			Tile[] reversedTiles = new Tile[tiles.length];
			for (int i = tiles.length - 1; i >= 0; i--) {
				reversedTiles[tiles.length - 1 - i] = tiles[i];
			}
			reversed = new Path(reversedTiles, this.context);
		}
		return reversed;
	}
 
	public void draw(Graphics g) {
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].draw(g);
		}
	}
 
	@Override
	public String toString() {
		return Arrays.toString(tiles);
	}
	
	private void log(String string) {
		this.context.getBot().log("Path", string);
	}
 
}