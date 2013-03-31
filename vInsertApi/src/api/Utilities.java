package api;

import java.awt.*;
import java.util.ArrayList;

import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.*;
import org.vinsert.bot.script.api.tools.Bank;
import org.vinsert.bot.script.api.tools.Camera;
import org.vinsert.bot.script.api.tools.Game;
import org.vinsert.bot.script.api.tools.Inventory;
import org.vinsert.bot.script.api.tools.Keyboard;
import org.vinsert.bot.script.api.tools.Menu;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.script.api.tools.Mouse;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.script.api.tools.Npcs;
import org.vinsert.bot.script.api.tools.Objects;
import org.vinsert.bot.script.api.tools.Players;
import org.vinsert.bot.script.api.tools.Settings;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.script.api.tools.Widgets;
import org.vinsert.component.debug.DebugTiles;
import org.vinsert.insertion.IWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Utilities {

    protected static Point GAMEWINDOW_LR = new Point(511, 334);

    ScriptContext ctx;
    protected Bank bank;
    protected Camera camera;
    protected Inventory inventory;
    protected Keyboard keyboard;
    protected Menu menu;
    protected Mouse mouse;
    protected Navigation navigation;
    protected Npcs npcs;
    protected Objects objects;
    protected Players players;
    protected Widgets widgets;
    protected Player localPlayer;
    protected Game game;
    protected Skills skills;
    protected Settings settings;
    protected Bot bot;

    public Utilities(ScriptContext ctx) {
        this.ctx = ctx;

        game = ctx.game;
        bank = ctx.bank;
        camera = ctx.camera;
        inventory = ctx.inventory;
        keyboard = ctx.keyboard;
        menu = ctx.menu;
        mouse = ctx.mouse;
        navigation = ctx.navigation;
        npcs = ctx.npcs;
        objects = ctx.objects;
        players = ctx.players;
        widgets = ctx.widgets;
        skills = ctx.skills;
        settings = ctx.settings;
        localPlayer = ctx.players.getLocalPlayer();
        bot = ctx.getBot();
    }

    public void log(String string) {
        bot.log("Util", string);
    }


    /**
     * Havles distance between two tiles.
     * @param a
     *      Source tile.
     * @param b
     *      Destination tile.
     * @return
     *      Midpoint tile.
     */
    public Tile halveDistance(Tile a, Tile b) {
        int x, y;
        x = a.getX() + b.getX();
        x /= 2;
        y = a.getY() + b.getY();
        y /= 2;
        return new Tile(x, y);
    }

    /**
     * Clicks item in inventory.
     * @param item
     *      Item to click.
     */
    public void clickItem(Item item) {
        int slot = inventory.indexOf(item);
        Point point = inventory.getClickPoint(slot);
        mouse.click(point.x, point.y);
    }

    /**
     * Creates a path between src and dest tiles excludes dest tile.
     *
     * @param src      The start tile of the path.
     * @param dest     The destination tile of the path.
     * @param distance The desired distance between path tiles.
     * @return The created path.
     */
    private Path createPath(Tile src, Tile dest, int distance) {
        if (!(distance > 0) || src == null || dest == null)
            return null;

        int totalDistance = src.distanceTo(dest);
        int numberOfTiles = totalDistance / distance;

        Tile[] pathTiles = new Tile[numberOfTiles];

        int srcX = src.getX();
        int srcY = src.getY();

        int deltaX = dest.getX() - srcX;
        int deltaY = dest.getY() - srcY;

        int adjustX = deltaX / numberOfTiles;
        int adjustY = deltaY / numberOfTiles;

        pathTiles[0] = src;
        for (int i = 1; i < pathTiles.length; i++)
            pathTiles[i] = new Tile(srcX + (adjustX * i), srcY + (adjustY * i));

        return new Path(pathTiles, this.ctx);
    }

    /**
     * Creates a path between any number of tiles.
     * @param distance
     *      Distance between tiles.
     * @param tiles
     *      Tiles along path.
     * @return
     *      Created path.
     */
    public Path createPath(int distance, Tile... tiles) {
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Tile> cumulativeTiles = new ArrayList<>();
        for (int i = 0; i < tiles.length - 1; i++) {
            paths.add(createPath(tiles[i], tiles[i + 1], distance));
        }
        for (Path p : paths) {
            Collections.addAll(cumulativeTiles, p.getTiles());
        }
        cumulativeTiles.add(tiles[tiles.length-1]);
        return new Path(cumulativeTiles.toArray(new Tile[cumulativeTiles.size()]), this.ctx);
    }

    /**
     * Interacts with Actor.
     * @param actor
     *      Actor to interact with.
     * @param action
     *      Action to perform.
     * @return
     *      Did interact.
     */
    public boolean interact(Actor actor, String action)
    {
        if (actor == null)
            return false;

        int speed = this.ctx.mouse.getSpeed();
        this.ctx.mouse.setSpeed(speed - 4);

        Point point = actor.hullPoint(actor.hull());
        this.ctx.mouse.move(point.x, point.y);
        Utils.sleep(Utils.random(15, 35));

        int index = this.ctx.menu.getIndex(action);

        if (index == 0) {
            this.ctx.mouse.click();
            this.ctx.mouse.setSpeed(speed);
            return true;
        }


        if (index != -1) {
            this.ctx.mouse.click(true);
            Point menuPoint = this.ctx.menu.getClickPoint(index);
            this.ctx.mouse.click(menuPoint.x, menuPoint.y);
            this.ctx.mouse.setSpeed(speed);
            return true;
        }

        this.ctx.mouse.setSpeed(speed);
        return false;
    }

    /**
     * Gives a walkable location between player and loc.
     * @param loc
     *      Destination.
     * @return
     *      Walkable tile.
     */
    public Tile walkableLocation(Tile loc) {
        if (isOnMinimap(loc))
            return loc;
        Tile halfway = halveDistance(localPlayer.getLocation(), loc);
        return walkableLocation(halfway);
    }

    /**
     * Is tile on minimap
     * @param t
     *      Tile.
     * @return
     *      Is on Minimap.
     */
    public boolean isOnMinimap(Tile t) {
        return localPlayer.getLocation().distanceTo(t) < 17;
    }

    /**
     * Call within a script's render in order to print Node debug information
     * @param script
     *      Current script
     * @param g
     *      Graphics in script's render
     * @param startX
     *      X position to start the list of Nodes
     * @param startY
     */
    public void renderNodes(ScriptBase script, Graphics2D g, int startX, int startY){
        int y_dist = 15;
        if(script.getNodes().size() > 0){
            for(int x = 0; x < script.getNodes().size(); x++){
                script.getNodes().get(x).render(g,startX,startY+(y_dist*x));
            }
        }
    }



    public void drawTile(ScriptBase script, Graphics2D g, Tile tile){
        Point point = Perspective.trans_tile_screen(script.getContext().getClient(), tile, 0, 0, 1);
        g.setColor(COLOR_PINK);
        if(point.x < GAMEWINDOW_LR.x && point.y < GAMEWINDOW_LR.y)
            g.fillOval(point.x, point.y, 15, 15);
    }

    public void drawPath(ScriptBase script, Graphics2D g, Path path){
        Tile[] tiles = path.getTiles();
        for(Tile t : tiles){
            drawTile(script,g,t);
        }
    }


    protected Color COLOR_PINK = new Color(255, 105, 224);
    protected Color COLOR_GREY = new Color(0,0,0,75);

    /**
     * Draws a progress bar
     * @param g
     *      Graphics2D of the script
     * @param sd
     *      SkillData of the script
     * @param skill
     *      Skill for the progress bar (Skills.THIEVING)
     * @param start
     *      Upper left point to draw progres bar
     * @param width
     *      Width of the progress bar
     * @param height
     *      Height of the progress bar
     * @param textColor
     *      Color of the Text
     * @param gradient1
     *      First color in gradient
     * @param gradient2
     *      Second color in gradient
     */
    public void drawProgressBar(Graphics2D g, SkillData sd, int skill, Point start, int width,
                                int height, Color textColor, Color gradient1, Color gradient2){
        drawProgressBar(g, sd, skill, start, width, height, textColor, gradient1, gradient2, Color.black, COLOR_GREY, new Point(10, 10), new Point(30, 30));
    }

    /**
     * Draws a progress bar
     * @param g
     *      Graphics2D of the script
     * @param sd
     *      SkillData of the script
     * @param skill
     *      Skill for the progress bar (Skills.THIEVING)
     * @param start
     *      Upper left point to draw progres bar
     * @param width
     *      Width of the progress bar
     * @param height
     *      Height of the progress bar
     * @param textColor
     *      Color of the Text
     * @param gradient1
     *      First color in gradient
     * @param gradient2
     *      Second color in gradient
     * @param p1
     *      First point of gradient.
     * @param p2
     *      Second point of gradient.
     */
    public void drawProgressBar(Graphics2D g, SkillData sd, int skill, Point start, int width,
                                int height, Color textColor, Color gradient1, Color gradient2,
                                Color border, Color fill, Point p1, Point p2){
        /*
            Draw progress bar using gradient
         */
        GradientPaint gradient = new GradientPaint(p1.x,p1.y,gradient1,p2.x,p2.y,gradient2,true);
        int expAtCurrLevel = Skills.EXPERIENCE_TABLE[sd.getLevel(skill)];
        int expAtNextLevel = Skills.EXPERIENCE_TABLE[sd.getLevel(skill)+1];
        double totalExpNeeded = expAtNextLevel - expAtCurrLevel;
        double progressWidth;
        progressWidth = skills.getExperienceToNextLevel(skill) / totalExpNeeded;
        progressWidth = 1 - progressWidth;
        /*
            Draw filler rectangle
         */
        g.setColor(fill);
        g.fillRect(start.x, start.y, width, height);

        g.setPaint(gradient);
        g.fillRect(start.x,start.y,(int)(width*progressWidth),height);
        /*
            Draw outer border rectangle
         */
        g.setColor(border);
        g.drawRect(start.x, start.y, width, height);
        int fontHeight = g.getFontMetrics().getHeight();
        /*
            Draw Progress text
         */
        g.setColor(textColor);
        g.drawString(sd.generateSkillString(skill),start.x+5,start.y+fontHeight);
    }

    public Point getClickPoint(int slot)
    {
        Point loc = getMidpoint(slot);
        loc.setLocation(loc.x + random(-20, 10) + random(0, 5), loc.y + random(-25, -10) + random(10, 20));
        return loc;
    }

    public Point getMidpoint(int slot)
    {
        Widget child = new Widget(this.ctx, this.ctx.getClient().getWidgets()[12][89]);

        Rectangle area = child.getBounds();

        int x = slot % 8 * 47 + area.x + 57;
        int y = slot / 8 * 37 + area.y + 75;
        return new Point(x, y);
    }

    public int random(int low, int high){
        Random rand = new Random();
        return rand.nextInt(high - low) + low;
    }

    public boolean withdraw(Filter<Item> withdrawals, int amount)
    {
        Item[] items = bank.getItems();

        for (int slot = 0; slot < items.length; slot++) {
            Item i = items[slot];
            if ((i != null) && (withdrawals.accept(i))) {
                Point point = getClickPoint(slot);
                this.ctx.mouse.move(point.x, point.y);
                Utils.sleep(random(125, 250));
                this.ctx.mouse.click(true);

                int index = -1;
                if (amount == 0)
                    index = this.ctx.menu.getIndex("Withdraw All");
                else {
                    this.ctx.menu.getIndex("Withdraw " + amount);
                }

                if (index == -1) {
                    if (!this.ctx.menu.getBounds().contains(this.ctx.mouse.getPosition())) {
                        Utils.sleep(random(20, 50));
                    }

                    if (!this.ctx.menu.getBounds().contains(this.ctx.mouse.getPosition())) {
                        return false;
                    }

                    Point p = this.ctx.menu.getClickPoint(4);
                    Utils.sleep(random(125, 250));
                    this.ctx.mouse.click(p.x, p.y);

                    Utils.sleep(random(900, 1350));

                    this.ctx.keyboard.type(String.valueOf(amount));
                    this.ctx.keyboard.hold(10, random(20, 50));
                    Utils.sleep(random(750, 1200));
                    return true;
                }

                if (!this.ctx.menu.getBounds().contains(this.ctx.mouse.getPosition())) {
                    Utils.sleep(random(20, 50));
                }

                Point p = this.ctx.menu.getClickPoint(index);
                this.ctx.mouse.click(p.x, p.y);
                Utils.sleep(random(500, 900));
                return true;
            }
        }
        return false;
    }

    public Widget getWidget(ScriptContext ctx,int widget, int child){
        if(ctx.getClient().getWidgets() != null){
            if(ctx.getClient().getWidgets()[widget] != null){
                IWidget[] iWids = ctx.getClient().getWidgets()[widget];
                Widget[] wids = new Widget[iWids.length];
                for (int j = 0; j < iWids.length; j++) {
                    if (iWids[j] != null)
                    {
                        Widget w = new Widget(ctx, ctx.getClient().getWidgets()[widget][j]);
                        wids[j] = w;
                    }
                }
                if(wids[child] != null){
                    log("w: " + widget + " c: " + child + " t: " + wids[child].getText());
                    return wids[child];
                }
                else{
                    log("wids child failed");
                    return null;
                }

            }
        }
        return null;

    }

    public void clickWidget(Widget wid){
        int x = random(wid.getX() + 4, wid.getX() + wid.getBounds().width -4);
        int y = random(wid.getX() + 4, wid.getY() + wid.getBounds().height -4);
        Point point = new Point(x,y);
        //this.ctx.mouse.click(point.x, point.y);
        ctx.mouse.move(point.x,point.y);
        Utils.sleep(random(45, 150));
    }

    public void clickToContinue(){
        int x = random(148,280);
        int y = random(448,453);
        ctx.mouse.click(x,y);
        Utils.sleep(random(30,50));
    }
}
