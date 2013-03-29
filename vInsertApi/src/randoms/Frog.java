package randoms;

import api.Conditions;
import api.Utilities;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IWidget;

@ScriptManifest(name = "zFrog", authors = { "potofreak" })
public class Frog extends AntiRandom {

    Npc frog;
    int[] frog2 = {2469, 2470};
    Tile frogtile = new Tile(2464, 4776);
    public static final int CHAT1_WIDGET = 241;
    public static final int CHAT1_CONTINUE = 3;
    public static final int CHAT2_WIDGET = 64;
    public static final int CHAT2_CONTINUE = 3;
    public static final int CHAT3_WIDGET = 241;
    public static final int CHAT3_CONTINUE = 3;
    Widget chat1;
    Widget chat2;
    Widget chat3;
    Widget wid;



    @Override
    public boolean init() {
        System.out.println("in frog");
        return sc.npcs.getNearest(frog2) != null;
    }

    public class isChat1Open extends Conditions.Condition {

        @Override
        public boolean validate(ScriptContext ctx) {
               return utilities.getWidget(sc,CHAT1_WIDGET,CHAT1_CONTINUE) != null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public class isChat2Open extends Conditions.Condition {

        @Override
        public boolean validate(ScriptContext ctx) {
            return utilities.getWidget(sc,CHAT2_WIDGET,CHAT2_CONTINUE) != null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public class isChat3Open extends Conditions.Condition {

        @Override
        public boolean validate(ScriptContext ctx) {
            return utilities.getWidget(sc,CHAT3_WIDGET,CHAT3_CONTINUE) != null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public class isCloseToFrog extends Conditions.Condition{

        @Override
        public boolean validate(ScriptContext ctx) {
            if(frog != null)
                return frog.getLocation().distanceTo(localPlayer.getLocation()) < 4;
            else
                return false;
        }
    }

    @Override
    public int pulse() {
        frog = sc.npcs.getNearest(frog2);
        if (frog != null) {
            //log("starting frog");
            if (frog != null) {
                if(!new isCloseToFrog().validate(this.getContext())){
                    sc.navigation.navigate(frog.getLocation(), NavigationPolicy.MINIMAP);
                    if(!Conditions.waitFor(new isCloseToFrog(),1000, this.getContext()))
                        return 0;
                    Utils.sleep(300);
                }
                if(!new isChat1Open().validate(this.getContext())){
                    log("phase 1");
                    utilities.interact(frog,"Talk-to");
                    if(!Conditions.waitFor(new isChat1Open(),1000,this.getContext()))
                        return 0;

                    sleep(200);
                }
                if(!new isChat2Open().validate(this.getContext())) {
                    if(utilities.getWidget(sc,CHAT1_WIDGET,CHAT1_CONTINUE) != null){
                        log("phase 2");
                        wid = widgets.get(CHAT1_WIDGET,CHAT1_CONTINUE);
                        log(wid.getText());
                        utilities.clickWidget(wid);
                        if(!Conditions.waitFor(new isChat2Open(),4000,this.getContext()))
                            return 0;

                        sleep(200);
                    }
                }
                if(!new isChat3Open().validate(this.getContext())) {
                    if(utilities.getWidget(sc,CHAT2_WIDGET,CHAT2_CONTINUE) != null){
                        log("phase 3");
                        wid = widgets.get(CHAT2_WIDGET,CHAT2_CONTINUE);
                        utilities.clickWidget(wid);
                        if(!Conditions.waitFor(new isChat2Open(),2000,this.getContext()))
                            return 0;

                        sleep(200);
                    }
                }

                if(widgets.get(CHAT3_WIDGET,CHAT3_CONTINUE).isValid()){
                    chat3 = widgets.get(CHAT3_WIDGET,CHAT3_CONTINUE);
                    if(chat3.isValid()){
                        chat3.click();
                    }
                    if(Conditions.waitFor(new isChat3Open(),2000,this.getContext()))
                        return 0;

                    sleep(200);
                }
                //log("Frawgie random :D");
            } else {
                sc.navigation.navigate(frogtile, NavigationPolicy.MINIMAP);
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
