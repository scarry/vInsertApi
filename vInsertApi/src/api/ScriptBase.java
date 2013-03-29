package api;

import java.awt.Graphics2D;
import java.io.PipedReader;
import java.util.ArrayList;

import org.vinsert.bot.script.Script;
import org.vinsert.bot.Bot;
import org.vinsert.bot.script.randevent.RandomEventPool;
import randoms.*;

public abstract class ScriptBase extends Script {

	private static String currnode;
	ArrayList<Node> nodes = new ArrayList<>();
	private static int ret = 50;
    protected Utilities utilities;
    RandomEventPool randomEventPool;

    public ScriptBase(){
        utilities = new Utilities(getContext());
        randomEventPool = new RandomEventPool(getContext().getBot());
        randomEventPool.register(new DrunkenDwarf());
        randomEventPool.register(new Frog());
        randomEventPool.register(new Genie());
        randomEventPool.register(new Guard());
        randomEventPool.register(new Hyde());
        randomEventPool.register(new OldMan());
        randomEventPool.register(new Pirate());
        randomEventPool.register(new Plant());
        randomEventPool.register(new Rick());
    }

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
						return 0; // getReturn();
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