package api;

import org.vinsert.component.ProjectionListener;

import java.awt.*;

public abstract class Node implements ProjectionListener {
    public abstract boolean activate();

    public abstract void execute();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void render(Graphics2D arg0) {
    }
}