import api.*;
import api.Timer;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.api.tools.Skills;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


@ScriptManifest(name = "FightCave", authors = {"Fortruce"}, description = "Fight Cave Bot", version = 1.0)
public class FightCave extends ScriptBase {

    /**
     * Paint variables
     */
    private static final Timer TIMER = new Timer(0);
    private static SkillData SD;

    /**
     * Ids
     */
    private static final int ENTRANCE_ID = 9356;
    private static final int EXIT_ID = 9357;
    private static final int BANK_NPC_ID = 2619;
    private static final int TOKKUL_ID = 6530;
    private static final int ENTRANCE_NPC_ID = 2617;
    private static final int[] ENEMY_IDS = {2734, 2735, 2736, 2737, 2738, 2739};

    private static final Tile BANK_LOC = new Tile(2445, 5178);
    private static final Tile ENTRANCE_LOC = new Tile(2438, 5169);

    private static final int CENTER_X_OFFSET = -12;
    private static final int CENTER_Y_OFFSET = -30;


    /**
     * Center of the fight cave
     */
    private static Tile fightCaveCenter = null;
    private static Tile fightCaveTopLeft = null;
    private static Tile fightCaveTopRight = null;
    private static Tile fightCaveBotLeft = null;
    private static Tile fightCaveBotRight = null;
    private static Path fightCaveIdlePath;
    private static int bankTokkulEvery;
    public static int skillToTrain;


    private Utilities utilities;

    /**
     * Boolean helper methods
     */
    private boolean isCaveCenterSet() {
        if (fightCaveCenter != null)
            return true;
        return false;
    }

    private boolean isInCave() {
        Npc entranceNpc = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(ENTRANCE_NPC_ID));
        if (entranceNpc == null)
            return true;
        return false;
    }

    private boolean isEnemyLoaded() {
        Npc enemy = npcs.getNearest(ENEMY_IDS);
        if (enemy != null) //TODO add enemy.getLocation().isWalkable()
            return true;
        return false;
    }

    private boolean isEnemyOnscreen() {
        Npc enemy = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(ENEMY_IDS));
        if (enemy != null && camera.isVisible(enemy)) //TODO add !isDead && enemy.getLocation().isWalkable()
            return true;
        return false;
    }

    private boolean needToBank() {
        if (inventory.getCount(true, TOKKUL_ID) > bankTokkulEvery)
            return true;
        return false;
    }

    private boolean isBankerOnscreen() {
        Npc banker = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(BANK_NPC_ID));
        if (banker != null && camera.isVisible(banker))
            return true;
        return false;
    }

    private boolean isBankerLoaded() {
        Npc banker = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(BANK_NPC_ID));
        if (banker != null)
            return true;
        return false;
    }

    private boolean isEntranceOnscreen() {
        GameObject entrance = objects.getNearest(Filters.objectId(ENTRANCE_ID));
        if (entrance != null && camera.isVisible(entrance))
            return true;
        return false;

    }

    private boolean isEntranceLoaded() {
        GameObject entrance = objects.getNearest(Filters.objectId(ENTRANCE_ID));
        if (entrance != null)
            return true;
        return false;
    }

    private boolean isInCombat() {
        return localPlayer.isInCombat();
    }


    public class WalkToBank extends Node {

        @Override
        public boolean activate() {
            //activate if not in the cave, we have enough tokkul,  and banker is not on screen
            if (!isInCave() && needToBank() && isBankerLoaded() && !isBankerOnscreen())
                return true;
            return false;
        }

        @Override
        public void execute() {
            Npc banker = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(BANK_NPC_ID));
            Tile walkTo;
            walkTo = banker != null ? banker.getLocation() : BANK_LOC;
            navigation.navigate(walkTo, NavigationPolicy.MINIMAP);
            Conditions.waitFor(new Conditions.isNpcOnScreen(BANK_NPC_ID), random(800, 1500), getContext());
        }
    }

    public class OpenBank extends Node {

        @Override
        public boolean activate() {
            if (!isInCave() && needToBank() && !bank.isOpen() && isBankerOnscreen())
                return true;
            return false;
        }

        @Override
        public void execute() {
            Npc banker = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(BANK_NPC_ID));
            if (banker != null) {
                if (camera.isVisible(banker)) {
                    banker.interact("Bank");
                    Conditions.waitFor(new Conditions.isBankOpen(), random(600, 1000), getContext());
                } else {
                    camera.rotateToActor(banker);
                    sleep(100, 300);
                }
            }
        }
    }

    public class DepositBank extends Node {

        @Override
        public boolean activate() {
            return bank.isOpen() && needToBank();
        }

        @Override
        public void execute() {
            int amount = inventory.getCount(true, TOKKUL_ID);
            bank.deposit(inventory.getItem(TOKKUL_ID), amount);
            Conditions.waitFor(new Conditions.inventoryNotContains(TOKKUL_ID), random(300, 800), random(50, 100), getContext());
        }

    }

    public class WalkToEntrance extends Node {

        @Override
        public boolean activate() {
            if (!isInCave() && !needToBank() && !isEntranceOnscreen() && isEntranceLoaded())
                return true;
            return false;
        }

        @Override
        public void execute() {
            GameObject entrance = objects.getNearest(Filters.objectId(ENTRANCE_ID));
            if (entrance != null) {
                camera.rotateToTile(ENTRANCE_LOC);
                navigation.navigate(ENTRANCE_LOC, NavigationPolicy.MINIMAP);
                sleep(1000, 1200);
            }
        }
    }

    public class EnterCave extends Node {

        @Override
        public boolean activate() {
            if (!isInCave() && !needToBank() && isEntranceOnscreen())
                return true;
            return false;
        }

        @Override
        public void execute() {
            log("Entering cave");
            fightCaveCenter = null;
            GameObject entrance = objects.getNearest(Filters.objectId(ENTRANCE_ID));
            if (entrance != null) {
                if (camera.isVisible(entrance)) {
                    entrance.interact("Enter");
                    sleep(1000, 1500);
                } else {
                    camera.rotateToObject(entrance);
                }
            }
        }
    }

    public class SetCaveCenter extends Node {

        @Override
        public boolean activate() {
            return isInCave() && !isCaveCenterSet();
        }

        @Override
        public void execute() {
            log("setting cave center");
            GameObject exit = objects.getNearest(Filters.objectId(EXIT_ID));
            if (exit != null) {
                int centerX = exit.getLocation().getX() + CENTER_X_OFFSET;
                int centerY = exit.getLocation().getY() + CENTER_Y_OFFSET;
                fightCaveCenter = new Tile(centerX, centerY);
                fightCaveBotLeft = new Tile(centerX - 11, centerY - 14);
                fightCaveBotRight = new Tile(centerX + 10, centerY - 6);
                fightCaveTopLeft = new Tile(centerX - 10, centerY + 10);
                fightCaveTopRight = new Tile(centerX + 6, centerY + 12);

                //create idle path (for when enemy is attacking but not loaded
                fightCaveIdlePath = utilities.createPath(5, fightCaveCenter, fightCaveTopLeft, fightCaveTopRight, fightCaveBotRight, fightCaveBotLeft);
            }
        }
    }

    public class FindEnemy extends Node {

        @Override
        public boolean activate() {
            if (isInCave() && !isEnemyLoaded() && fightCaveCenter != null && isInCombat())
                return true;
            return false;
        }

        @Override
        public void execute() {
            if (utilities.isOnMinimap(fightCaveIdlePath.getStart(true)))
                fightCaveIdlePath.traverse(true);
            else
                navigation.navigate(utilities.walkableLocation(fightCaveIdlePath.getStart(true)), NavigationPolicy.MINIMAP);
            Conditions.waitFor(new Conditions.isNpcLoaded(ENEMY_IDS), random(800, 1200), getContext());
        }
    }

    public class WalkToEnemy extends Node {

        @Override
        public boolean activate() {

            if (isInCave() && !isEnemyOnscreen() && isEnemyLoaded())
                return true;
            return false;
        }

        @Override
        public void execute() {
            Npc enemy = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(ENEMY_IDS));
            if (enemy != null) {
                navigation.navigate(enemy.getLocation(), NavigationPolicy.MINIMAP);
                Conditions.waitFor(new Conditions.isNpcOnScreen(ENEMY_IDS), random(800, 1200), getContext());
            }
        }

    }

    public class AttackEnemy extends Node {

        @Override
        public boolean activate() {
            if (isInCave() && isEnemyOnscreen() && (!isInCombat() || localPlayer.isMoving() ||
                    localPlayer.getInteracting() == null))
                return true;
            return false;
        }

        @Override
        public void execute() {
            Npc enemy = npcs.getNearest(ENEMY_IDS);
            if (enemy != null) {
                utilities.interact(enemy, "Attack");
                Conditions.waitFor(new Conditions.isNotMoving(), random(300, 600), getContext());
            }
        }
    }

    public class WalkToCenter extends Node {

        @Override
        public boolean activate() {
            if (isInCave() && !isEnemyLoaded() && fightCaveCenter != null && !isInCombat()
                    && localPlayer.getLocation().distanceTo(fightCaveCenter) > 3)
                return true;
            return false;
        }

        @Override
        public void execute() {
            navigation.navigate(utilities.walkableLocation(fightCaveCenter), NavigationPolicy.MINIMAP);
            Conditions.waitFor(new Conditions.isNearTile(fightCaveCenter, 4), random(800, 1500), getContext());
        }

    }

    @Override
    public boolean init() {
        camera.adjustPitch(100);
        camera.rotateAngleTo(0);

//		fbg.setVisible(true);
//		while(guiWait) sleep(500);

        /**
         * TODO
         * Auto Login
         * Set Attack Mode
         */

        bankTokkulEvery = 99999;

        utilities = new Utilities(this.getContext());
        SD = new SkillData(this.getContext());

        submit(new SetCaveCenter());
        submit(new WalkToBank());
        submit(new OpenBank());
        submit(new DepositBank());
        submit(new WalkToCenter());
        submit(new WalkToEnemy());
        submit(new AttackEnemy());
        submit(new EnterCave());
        submit(new WalkToEntrance());
        submit(new FindEnemy());
        log("starting bot");

        return true;
    }

    @Override
    public void render(Graphics2D g) {

        int[] point = {385, 2};

        //box
        g.setColor(new Color(63, 63, 43, 200));
        g.draw3DRect(375, 5, 139, 225, true);
        g.fill3DRect(375, 5, 139, 325, true);

        int height = g.getFontMetrics().getHeight();

        g.setColor(Color.WHITE);
        g.drawString("Fortruce - FightCave", point[0], point[1] += height);
        g.drawLine(383, 21, 495, 21);

        g.drawString("Run Time:  " + TIMER.toElapsedString(), point[0], point[1] += height);

        if (localPlayer != null) {
            g.drawString(SD.generateSkillString(Skills.STRENGTH, SD), point[0], point[1] += height);

            Npc enemy = npcs.getNearest(localPlayer.getLocation(), Filters.npcId(ENEMY_IDS));
            if (enemy != null)
                g.drawString("enemy: " + enemy.getName(), point[0], point[1] += height);
            else
                g.drawString("null", point[0], point[1] += height);

            if (ScriptBase.getActiveNode() != null)
                g.drawString((String.format("%s", "Node: " + ScriptBase.getActiveNode().toString())), point[0], point[1] += height);
            g.drawString("caveCenterSet: " + String.valueOf(isCaveCenterSet()), point[0], point[1] += height);
            g.drawString("isInCave: " + String.valueOf(isInCave()), point[0], point[1] += height);
            g.drawString("enemyLoaded: " + String.valueOf(isEnemyLoaded()), point[0], point[1] += height);
            g.drawString("enemyOnscreen: " + String.valueOf(isEnemyOnscreen()), point[0], point[1] += height);
            g.drawString("needToBank: " + String.valueOf(needToBank()), point[0], point[1] += height);
            g.drawString("bankerOnscreen: " + String.valueOf(isBankerOnscreen()), point[0], point[1] += height);
            g.drawString("bankerLoaded: " + String.valueOf(isBankerLoaded()), point[0], point[1] += height);
            g.drawString("entranceOnscreen: " + String.valueOf(isEntranceOnscreen()), point[0], point[1] += height);
            g.drawString("entranceLoaded: " + String.valueOf(isEntranceLoaded()), point[0], point[1] += height);
            g.drawString("inCombat: " + String.valueOf(isInCombat()), point[0], point[1] += height);
            if (fightCaveCenter != null) {
                g.drawString("center: " + fightCaveCenter.toString(), point[0], point[1] += height);
            }
        }
    }


    /* GUI STUFF */
    private FightCaveGui fbg = new FightCaveGui();
    private static boolean guiWait = true;
    /* END GUI STUFF */

	/* GUI CODE BELOW */


    /* GUI CODE COPIED FROM GUI.JAVA */
    @SuppressWarnings("serial")
    class FightCaveGui extends JFrame {
        public FightCaveGui() {
            initComponents();
        }

        private void startButtonClicked(ActionEvent e) {
            String skillString = cbAttackSkill.getSelectedItem().toString();
            switch (skillString) {
                case "Attack":
                    skillToTrain = Skills.ATTACK;
                    break;
                case "Strength":
                    skillToTrain = Skills.STRENGTH;
                    break;
                case "Defense":
                    skillToTrain = Skills.DEFENSE;
                    break;
            }
            bankTokkulEvery = Integer.parseInt(tfBankEvery.getText());

            guiWait = false;
            this.dispose();

        }

        private void initComponents() {
            // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner Evaluation license - Joseph Rollins
            tabbedPane1 = new JTabbedPane();
            panel1 = new JPanel();
            label3 = new JLabel();
            label4 = new JLabel();
            tfBankEvery = new JTextField();
            label5 = new JLabel();
            cbAttackSkill = new JComboBox<>();
            cbAutoLoginEnabled = new JCheckBox();
            button1 = new JButton();
            panel2 = new JPanel();
            label1 = new JLabel();
            label2 = new JLabel();
            tfUsername = new JTextField();
            tfPassword = new JTextField();
            label6 = new JLabel();
            tfLoginEvery = new JTextField();
            label7 = new JLabel();

            //======== this ========
            setTitle("FightCave -- Fortruce");
            setIconImage(new ImageIcon("C:\\Users\\joseph\\workspace\\FortruceBots\\src\\icon.gif").getImage());
            Container contentPane = getContentPane();

            //======== tabbedPane1 ========
            {

                //======== panel1 ========
                {

                    // JFormDesigner evaluation mark
                    panel1.setBorder(new javax.swing.border.CompoundBorder(
                            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                                    java.awt.Color.red), panel1.getBorder()));
                    panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                        public void propertyChange(java.beans.PropertyChangeEvent e) {
                            if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                        }
                    });


                    //---- label3 ----
                    label3.setText("Skill:");
                    label3.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- label4 ----
                    label4.setText("Bank every:");
                    label4.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- tfBankEvery ----
                    tfBankEvery.setText("999999");
                    tfBankEvery.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
                    tfBankEvery.setHorizontalAlignment(SwingConstants.RIGHT);

                    //---- label5 ----
                    label5.setText("tokkul");
                    label5.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- cbAttackSkill ----
                    cbAttackSkill.setModel(new DefaultComboBoxModel<>(new String[]{
                            "Attack",
                            "Defense",
                            "Strength"
                    }));

                    //---- cbAutoLoginEnabled ----
                    cbAutoLoginEnabled.setText("Auto Login Enabled");
                    cbAutoLoginEnabled.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- button1 ----
                    button1.setText("start");
                    button1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            startButtonClicked(e);
                            startButtonClicked(e);
                        }
                    });

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                            panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel1Layout.createParallelGroup()
                                                    .addGroup(panel1Layout.createSequentialGroup()
                                                            .addGroup(panel1Layout.createParallelGroup()
                                                                    .addComponent(label4)
                                                                    .addComponent(label3))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                    .addComponent(tfBankEvery, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                                                                    .addComponent(cbAttackSkill, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(label5)
                                                            .addGap(0, 0, Short.MAX_VALUE))
                                                    .addGroup(panel1Layout.createSequentialGroup()
                                                            .addComponent(cbAutoLoginEnabled, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                                                            .addComponent(button1)))
                                            .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                            panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label3)
                                                    .addComponent(cbAttackSkill, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label4)
                                                    .addComponent(tfBankEvery, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(label5))
                                            .addGap(18, 18, 18)
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(cbAutoLoginEnabled)
                                                    .addComponent(button1))
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                }
                tabbedPane1.addTab("Main", panel1);


                //======== panel2 ========
                {

                    //---- label1 ----
                    label1.setText("Username:");
                    label1.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- label2 ----
                    label2.setText("Password:");
                    label2.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- tfUsername ----
                    tfUsername.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- tfPassword ----
                    tfPassword.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- label6 ----
                    label6.setText("Every:");
                    label6.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- tfLoginEvery ----
                    tfLoginEvery.setText("120");
                    tfLoginEvery.setHorizontalAlignment(SwingConstants.RIGHT);
                    tfLoginEvery.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    //---- label7 ----
                    label7.setText("minutes");
                    label7.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                            panel2Layout.createParallelGroup()
                                    .addGroup(panel2Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel2Layout.createParallelGroup()
                                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(label1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(label2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                    .addComponent(label6))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(panel2Layout.createParallelGroup()
                                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(tfUsername, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                                            .addComponent(tfPassword, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                                                    .addGroup(panel2Layout.createSequentialGroup()
                                                            .addComponent(tfLoginEvery, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(label7)))
                                            .addContainerGap(25, Short.MAX_VALUE))
                    );
                    panel2Layout.setVerticalGroup(
                            panel2Layout.createParallelGroup()
                                    .addGroup(panel2Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label1)
                                                    .addComponent(tfUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label2)
                                                    .addComponent(tfPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(tfLoginEvery, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(label7))
                                            .addContainerGap(20, Short.MAX_VALUE))
                    );
                }
                tabbedPane1.addTab("Login", panel2);

            }

            GroupLayout contentPaneLayout = new GroupLayout(contentPane);
            contentPane.setLayout(contentPaneLayout);
            contentPaneLayout.setHorizontalGroup(
                    contentPaneLayout.createParallelGroup()
                            .addComponent(tabbedPane1)
            );
            contentPaneLayout.setVerticalGroup(
                    contentPaneLayout.createParallelGroup()
                            .addComponent(tabbedPane1)
            );
            pack();
            setLocationRelativeTo(getOwner());
            // JFormDesigner - End of component initialization  //GEN-END:initComponents
        }

        // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
        // Generated using JFormDesigner Evaluation license - Joseph Rollins
        private JTabbedPane tabbedPane1;
        private JPanel panel1;
        private JLabel label3;
        private JLabel label4;
        private JTextField tfBankEvery;
        private JLabel label5;
        private JComboBox<String> cbAttackSkill;
        private JCheckBox cbAutoLoginEnabled;
        private JButton button1;
        private JPanel panel2;
        private JLabel label1;
        private JLabel label2;
        private JTextField tfUsername;
        private JTextField tfPassword;
        private JLabel label6;
        private JTextField tfLoginEvery;
        private JLabel label7;
        // JFormDesigner - End of variables declaration  //GEN-END:variables
    }


	/*END GUI CODE*/


}
