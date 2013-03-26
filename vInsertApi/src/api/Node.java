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
	
	@Override
	public void render(Graphics2D arg0) {}
}