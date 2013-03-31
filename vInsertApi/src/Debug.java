import api.FWidgets;
import api.Node;
import api.ScriptBase;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;

import java.awt.*;

/**
 * Author: Fortruce
 */
@ScriptManifest(name = "Debug", authors = {"Fortruce"})
public class Debug extends ScriptBase {

    private FWidgets fWidgets;

    public class Test extends Node {
        @Override
        public boolean activate() {
            return true;
        }

        @Override
        public void execute() {
            Widget wid = fWidgets.get(137, 0);
            fWidgets.getGroupIndex(wid);
        }
    }

    @Override
    public void render(Graphics2D arg0) {
    }
}
