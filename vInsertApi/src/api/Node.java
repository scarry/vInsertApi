package api;

public abstract class Node {
	public abstract boolean activate();
	public abstract void execute();
	
	public void stop() {
		ScriptBase.setReturn(-1);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}