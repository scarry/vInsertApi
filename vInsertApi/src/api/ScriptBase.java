package api;

import java.awt.Graphics2D;
import java.io.PipedReader;
import java.util.*;

import org.vinsert.bot.script.Script;
import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.script.randevent.RandomEventPool;
import randoms.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ScriptBase extends Script {

	private static String currnode;
	ArrayList<Node> nodes = new ArrayList<>();
	private static int ret = 50;
    protected Utilities utilities;
    protected SkillData skillData;
    protected RandomEventPool randomEventPool;
    protected Timer runTime;

    @Override
    public void create(ScriptContext context) throws RuntimeException {
        super.create(context);
        this.utilities = new Utilities(getContext());
        this.skillData = new SkillData(getContext());
        this.runTime = new Timer(0);

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

    public ArrayList<Node> getNodes(){
        return nodes;
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
		log("Script finished!!");
	}

	@Override
	public abstract boolean init();

	@Override
	public int pulse() {
        randomEventPool.check();
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