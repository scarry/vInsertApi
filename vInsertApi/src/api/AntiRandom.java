package api;

import org.vinsert.bot.script.ScriptContext;

import java.awt.*;

public abstract class AntiRandom extends Node {

    ScriptContext sc;

    public AntiRandom(ScriptContext context) {
        sc = context;
    }

    @Override
    public abstract boolean activate();

    @Override
    public abstract void execute();

    //START: Code generated using Enfilade's Easel
    protected final Color color1 = new Color(255, 0, 50, 127);
    protected final Color color2 = new Color(255, 0, 51);
    protected final Color color3 = new Color(0, 0, 0);

    protected final BasicStroke stroke1 = new BasicStroke(5);

    protected final Font font1 = new Font("Calibri", 0, 32);

    @Override
    public void render(Graphics2D g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(color1);
        g.fillRect(1, 1, 761, 502);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(1, 1, 761, 502);
        g.setFont(font1);
        g.setColor(color3);
        g.drawString(this.toString(), 15, 38);
    }

}
