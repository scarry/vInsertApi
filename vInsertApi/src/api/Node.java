package api;

import java.awt.Graphics2D;

import org.vinsert.component.ProjectionListener;

public abstract class Node implements ProjectionListener{
	public abstract boolean activate();
	public abstract void execute();
	
	public void stop() {
		ScriptBase.setReturn(-1);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

    public void render(Graphics2D g, int x, int y){
        g.drawString("" + this.getClass().getSimpleName() + ": " + activate(),x,y);
    }

	@Override
	public void render(Graphics2D arg0) {}
}