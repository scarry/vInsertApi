package api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.vinsert.bot.script.ScriptContext;

public class AntiRandom extends Node{

	ScriptContext sc;
	
	public AntiRandom(ScriptContext context){
		sc = context;
	}
	
	@Override
	public boolean activate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

    //START: Code generated using Enfilade's Easel
    private final Color color1 = new Color(255, 0, 50, 127);
    private final Color color2 = new Color(255, 0, 51);
    private final Color color3 = new Color(0, 0, 0);

    private final BasicStroke stroke1 = new BasicStroke(5);

    private final Font font1 = new Font("Calibri", 0, 32);

    @Override
    public void render(Graphics2D g1) {
        Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(1, 1, 761, 502);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(1, 1, 761, 502);
        g.setFont(font1);
        g.setColor(color3);
        g.drawString(this.toString(), 15, 38);
    }
    //END: Code generated using Enfilade's Easel

}
