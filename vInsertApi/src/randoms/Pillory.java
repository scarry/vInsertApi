package randoms;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Widgets;
import org.vinsert.bot.util.Utils;


@ScriptManifest(name = "zPillory", authors = { "potofreak" })
public class Pillory extends AntiRandom {

	/*
	 * Author: Potofreak
	 */	
	
	public static int LOCK_ID = 6836;
	public static int PRISONPETE_WIDGET = 189;
	public final static int LARGELOCK_COMPONENT = 2;
	public final static int LARGELOCK_DIAMOND = 9753;
	public final static int LARGELOCK_SQUARE = 9754;
	public final static int LARGELOCK_CIRCLE = 9755;
	public final static int LARGELOCK_TRIANGLE = 9756;
	public static int[] KEY_COMPONENTS = {3,4,5};
	public static int KEY_DIAMOND = 9749;
	public static int KEY_SQUARE = 9750;
	public static int KEY_CIRCLE = 9751;
	public static int KEY_TRIANGLE = 9752;	
	public static Tile ACTIVATE_TILE = new Tile(2606, 3104);
	public static int TRAMP_ID = 2792;
	/*
	 * NOTE:
	 * KEY_COMPONENT 3 = Left key head
	 * KEY_COMPONENT 3 + 3 = Left Key
	 * KEY_COMPONENT 4 = Middle key head
	 * KEY_COMPONENT 4 + 3 = Middle key
	 * KEY_COMPONENT 5 = Right key head
	 * KEY_COMPONENT 5 + 3 = Right key
	 */
	
	GameObject lock;
	Widget largeLock;
	
	@Override
	public boolean init() {
        //if(sc.widgets.get(PRISONPETE_WIDGET) != null)
            //return sc.widgets.get(PRISONPETE_WIDGET, LARGELOCK_COMPONENT) != null;
		return sc.npcs.getNearest(TRAMP_ID) != null;
	}

	@Override
	public int pulse() {
		log("Inside Pillory");
		lock = sc.objects.getNearest(Filters.objectId(LOCK_ID));

        if(lock != null){
			sc.camera.rotateToTile(lock.getLocation());
			lock.interact("Unlock");
			Utils.sleep(random(300, 600));
		}
        if(sc.widgets.get(PRISONPETE_WIDGET) != null){
            if(sc.widgets.get(PRISONPETE_WIDGET, LARGELOCK_COMPONENT) != null){
                switch(sc.widgets.get(PRISONPETE_WIDGET, LARGELOCK_COMPONENT).getModelId()){
                case(LARGELOCK_DIAMOND):
                    for(int component : KEY_COMPONENTS){
                        Widget key = sc.widgets.get(PRISONPETE_WIDGET, component);
                        if(key != null && key.getModelId() == KEY_DIAMOND){
                            Widget clickMe = sc.widgets.get(PRISONPETE_WIDGET, (component+3));
                            if (clickMe != null) {
                                clickMe.click();
                                Utils.sleep(random(300, 700));
                            }
                        }
                    }
                break;
                case(LARGELOCK_SQUARE):
                    for(int component : KEY_COMPONENTS){
                        Widget key = sc.widgets.get(PRISONPETE_WIDGET, component);
                        if(key != null && key.getModelId() == KEY_SQUARE){
                            Widget clickMe = sc.widgets.get(PRISONPETE_WIDGET, (component+3));
                            if (clickMe != null) {
                                clickMe.click();
                                Utils.sleep(random(300, 700));
                            }
                        }
                    }
                break;
                case(LARGELOCK_CIRCLE):
                    for(int component : KEY_COMPONENTS){
                        Widget key = sc.widgets.get(PRISONPETE_WIDGET, component);
                        if(key != null && key.getModelId() == KEY_CIRCLE){
                            Widget clickMe = sc.widgets.get(PRISONPETE_WIDGET, (component+3));
                            if (clickMe != null) {
                                clickMe.click();
                                Utils.sleep(random(300, 700));
                            }
                        }
                    }
                break;
                case(LARGELOCK_TRIANGLE):
                    for(int component : KEY_COMPONENTS){
                        Widget key = sc.widgets.get(PRISONPETE_WIDGET, component);
                        if(key != null && key.getModelId() == KEY_TRIANGLE){
                            Widget clickMe = sc.widgets.get(PRISONPETE_WIDGET, (component + 3));
                            if (clickMe != null) {
                                clickMe.click();
                                Utils.sleep(random(300, 700));
                            }
                        }
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return 0;
	}

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }
}
