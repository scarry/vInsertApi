package api;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Keyboard;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Utils;
 
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
		
		@Override
		public String toString() {
			return String.format("%d", this.objectId) + " " + this.location.toString();
		}
	}
 
	public Path(final Tile[] tiles, TraversableObject[] objects, ScriptContext context) {
		this.tiles = tiles;
		if (objects.length == 0)
			this.objects = null;
		else
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
	 * Traverses the path. This method must be looped.
	 */
	public void traverse(Direction direction) {
		traverse(direction, true);
	}
 
	public void traverse(Direction direction, final boolean run) {
		final Tile next = next();
		traverse(next, direction, run);
	}
	
	public void traverse(Direction direction, final boolean run, int deviation) {
		final Tile next = next();
		traverse(next, direction, run, deviation);
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
//	public void traverseObjects(TraversableObject[] objects, final Direction direction, final boolean run, final boolean force) {
//		final Tile next = next();
//
//		for (int i = 0; i < objects.length; i++) {
//			GameObject object = Objects.getNearest(objects[i].objectId);
//			if(object != null && object.getLocation().equals(objects[i].location) 
//					&& Game.getPlane() == objects[i].plane &&
//					(objects[i].direction == direction || objects[i].direction == Direction.BOTH)) {
//				if (object.isVisible()) {
//				//found matching object
//				Methods.log("Found matching object -- interacting");
//				//traverse thru object
//				object.interact(objects[i].interaction);
//				}
//				else {
//					log("walking to object");
//					Walking.walkTo(objects[i].location);
//					ExConditions.waitFor(new ExConditions.isObjectVisible(objects[i].objectId), 2000);
//				}
//			}
//			else
//				traverse(next, run, force);
//		}
//	}
	
	private void traverse(final Tile next, Direction direction, final boolean run, int deviation) {
//		log("in traverse");
		boolean traversingObject = false;
		if (objects != null) {
			log("objects not null");
			for (TraversableObject obj : objects) {
				GameObject gameObj = context.objects.getNearest(Filters.objectId(obj.objectId));
				if (gameObj != null && gameObj.getLocation().equals(obj.location) &&
						context.getClient().getPlane() == obj.plane &&
						(obj.direction == direction || obj.direction == Direction.BOTH)) {
					//found obj - traverse
					log("traversing object");
					traversingObject = true;
					if (context.camera.isVisible(gameObj)) {
						log("found object " + obj.toString() + " traversing...");
						gameObj.interact(obj.interaction);
						Utils.sleep(Utils.random(1000, 1500));
					} else {
						log("walking to " + obj.toString() + "...");
						context.navigation.navigate(gameObj.getLocation(), NavigationPolicy.MINIMAP);
						Utils.sleep(Utils.random(1000, 1500));
					}
				}
			}
		}
//		log(String.format("%d", context.game.getGameState().id()));
//		if (next == null) log("ERROR: next == null");
//		if (next.distanceTo(this.getEnd()) > 3) log("distance to end true");
//		else log("distance to end false");
//		if (!traversingObject) log("not traversing object");
//		else log("traversing object");
		if (context.game.getGameState().id() == 30
				&& next != null && !isAtEnd(next, deviation)
//				&& next.distanceTo(this.getEnd())> 3
				&& !traversingObject) { //TODO check the this.getEnd()
//			log("walking");
			if (run) {
				//Keyboard.pressKey(KeyEvent.VK_CONTROL);
				context.keyboard.press(KeyEvent.VK_CONTROL);
				Utils.sleep(Utils.random(80, 150));
			}
			context.navigation.navigate(next, NavigationPolicy.MINIMAP);
			if (run) {
				//Keyboard.releaseKey(KeyEvent.VK_CONTROL,Random.nextInt(80, 150));
				context.keyboard.press(KeyEvent.VK_CONTROL);
				Utils.sleep(Utils.random(1000, 2000));
			}
		}
	}
 
	/**
	 * Walks one step in path.
	 * @param run
	 * 			Toggle run on off.
	 * @param force
	 * 			Toggle close distance check for end checking.
	 */

	/**
	 * Walks one step in the path.
	 *
	 * @param next
	 *            The next <code>Tile</code>
	 */

	private void traverse(final Tile next, Direction direction, final boolean run) {
		traverse(next, direction, run, 3);
	}

 
	/**
	 * @param next
	 *            The next <code>Tile</code>
	 * @return Whether the given <code>Tile</code> is at the end of the path.
	 */
	private boolean isAtEnd(Tile next) {
		return isAtEnd(next, 3);
	}

	private boolean isAtEnd(Tile next, int deviation) {
		return localPlayer.getLocation().distanceTo(next) < deviation
				|| localPlayer.getLocation().distanceTo(this.getEnd()) < deviation; //TODO check getEnd()
	}
 
	/**
	 * @return The next walkable <code>Tile</code> on the minimap.
	 */
	private Tile next() {
		log("getting next");
		for (int i = tiles.length - 1; i >= 0; --i) {
			if (utilities.isOnMinimap(tiles[i])) { //TODO add isWalkable check
				log("found next");
				return tiles[i];
			}
		}
		log("didn't find next");
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
 
	@Override
	public String toString() {
		return Arrays.toString(tiles);
	}
	
	private void log(String string) {
		this.context.getBot().log("Path", string);
	}
 
}