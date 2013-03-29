package api;

import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.randevent.RandomEventPool;

import java.awt.*;
import java.util.ArrayList;

public abstract class ScriptBase extends Script {

	private static String currnode;
	ArrayList<Node> nodes = new ArrayList<>();
    protected Utilities utilities;
    RandomEventPool randomEventPool;

    @Override
    public void create(ScriptContext context) throws RuntimeException {
        super.create(context);
        this.utilities = new Utilities(getContext());
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
        return 50;
	}

	@Override
	public abstract void render(Graphics2D arg0);

}