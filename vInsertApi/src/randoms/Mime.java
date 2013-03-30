package randoms;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Utils;

import java.awt.*;

@ScriptManifest(name = "zMime", authors = { "potofreak" })
public class Mime extends AntiRandom{

	public int mime = 1056;
    public int mimeEmote = -1;
    boolean yourTurn = false;
    int turns = 0;
    public static Point ANCHORMOUSE;
    public static int DIFF_X = 15;
    public static int DIFF_Y = 5;
    public Point emotePoint;


   public boolean valid(Widget widget) {
      for (Widget i : sc.widgets.getValidated()) {
         if (((Widget) widget).getId() == i.getId())
            return true;
      }
      return false;
   }
	
	@Override
	public boolean init() {
		return sc.npcs.getNearest(mime) != null;
	}

    public void clickEmote(Point emotePoint){
        mouse.click(random(emotePoint.x,emotePoint.x+DIFF_X),random(emotePoint.y,emotePoint.y+DIFF_Y));
    }

	@Override
	public int pulse() {
        if(!init()){
            requestExit();
            return 0;
        }
		if(sc.npcs.getNearest(mime).getAnimation() != -1 &&sc.npcs.getNearest(mime).getAnimation() != 858){
			mimeEmote =  sc.npcs.getNearest(mime).getAnimation();
            log("mimeEmote: " + mimeEmote);
        }
        Utils.sleep(1000);
        if(mimeEmote != -1 && sc.widgets.get(188) != null) {
            yourTurn = true;
        }
        if (yourTurn == true) {
           /*cry - 860 - index 2
           think - 857 index 3
           laugh - 861 index 4
           dance - 866 index 5
           climb rope - 1130 index 6
           lean on air - 1129 index 7
           glass wall - 1128 index 8
           glass box - 1131 index 9*/

           /*
           think    laugh     orpe        box
           cry      dance      air         wall

            */
           int child = -1;
           emotePoint = new Point(0,0);
           switch(mimeEmote) {
                 case 860: //cry
                    child = 2;
                     emotePoint = new Point(114,425);
                    break;
                 case 857: //think
                    child = 3;
                     emotePoint = new Point(116,387);
                    break;
                 case 861: //laugh
                    child = 4;
                     emotePoint = new Point(191,389);
                    break;
                 case 866: //dance
                    child = 5;
                     191,425
                    break;
                 case 1130://climb rope
                    child = 6;
                     270,386
                    break;
                 case 1129://lean on air
                    child = 7;
                     264,424
                    break;
                 case 1128://glass wall
                    child = 8;
                     373,426
                    break;
                 case 1131://glass box
                    child = 9;
                     373,384
                    break;
                    
           }
               if(utilities.getWidget(getContext(),188,child) != null){
                   //utilities.getWidget(getContext(),188, child).click();

                   mimeEmote = -1;
                   yourTurn = false;
                   turns++;
                   Utils.sleep(1000);
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
        return RandomEventPriority.HIGH;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
