package api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.insertion.IWidget;

/**
 * Author: Fortruce
 */
public class FWidgets {

    private ScriptContext ctx;

    public FWidgets(ScriptContext ctx) {
        this.ctx = ctx;
    }

    public Widget[] get(int group) {
        if ((group > this.ctx.getClient().getWidgets().length - 1) ||
                (this.ctx.getClient().getWidgets()[group] == null))
            return null;

        IWidget[] widgets = this.ctx.getClient().getWidgets()[group];
        Widget[] valid = new Widget[widgets.length];

        for (int i = 0; i < widgets.length; i++) {
            if (widgets[i] != null) {
                Widget w = new Widget(this.ctx, widgets[i]);
                if (w != null)
                    valid[i] = w;
            }
        }
        return valid;
    }

    public Widget get(int group, int child) {
        Widget[] widgets = get(group);
        if ((widgets != null) && (widgets.length > child) && widgets[child] != null)
            return widgets[child];
        return null;
    }

    public int getGroupIndex(Widget w) {
        if (w == null || w.getIndex() < 0)
            return -1;

        final IWidget[][] widgetCollection = this.ctx.getClient().getWidgets();
        for (int groupIndex = 0; groupIndex < widgetCollection.length; groupIndex++) {
            if (widgetCollection[groupIndex] != null) {
                if (w.getIndex() < widgetCollection[groupIndex].length) {
                    Widget temp = new Widget(this.ctx, widgetCollection[groupIndex][w.getIndex()]);
                    if (widgetEquals(temp, w))
                        return groupIndex;
                }
            }
        }
        return -1;
    }

    public boolean widgetEquals(Widget x, Widget y) {
        if (x == null && y == null)
            return true;
        if (x == null || y == null)
            return false;
        if (x.getName().equals(y.getName()) &&
                x.getX() == y.getX() &&
                x.getY() == y.getY() &&
                x.getModelId() == y.getModelId() &&
                x.getText().equals(y.getText()))
            return true;
        return false;
    }
}
