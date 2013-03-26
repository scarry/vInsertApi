package api;

import java.awt.Graphics2D;
import java.util.ArrayList;

import org.vinsert.bot.script.Script;

public abstract class ScriptBase extends Script {
	
	private static String currnode;
	ArrayList<Node> nodes = new ArrayList<Node>();
	private static int ret = 50;
	
	public static int getReturn() {
		return ret;
	}
	
	public static void setReturn(int millis) {
		ret = millis;
	}
	
	public static String getActiveNode() {
		return currnode;
	}

	public void submit(Node node) {
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
	}

	@Override
	public void close() {
		log("Script finished.");
	}

	@Override
	public abstract boolean init();

	@Override
	public int pulse() {
		try {
			if(nodes.size() > 0) {
				for (Node n : nodes) {
					if (n.activate()) {
						currnode = n.getClass().getSimpleName();
						n.execute();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return getReturn();
	}

	@Override
	public abstract void render(Graphics2D arg0);

}
