package randoms;


import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Game;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Utils;

@ScriptManifest(name = "zLampSolver", authors = { "potofreak" })

public class LampSolver extends AntiRandom{
        /*
         * Author: Potofreak
         */
       
        public static int GENIELAMP_ID = 2529;
        public static int LAMP_WIDGET = 134;
        public Widget lampMenu;
        public int chosenSkill;
        public int SKILL_COMPONENT;
       
        private void setSkillComponent(){
                switch(sc.getAccount().getReward()){
                case("Attack"):            SKILL_COMPONENT = 3;
                                                	break;
                case("Strength"):          SKILL_COMPONENT = 4;
                                                	break;
                case("Range"):             SKILL_COMPONENT = 5;
                                                	break;
                case("Magic"):             SKILL_COMPONENT = 6;
                                                	break;
                case("Defense"):           SKILL_COMPONENT = 7;
                                                	break;
                case("Constitution"):      SKILL_COMPONENT = 8;
                                                	break;
                case("Prayer"):            SKILL_COMPONENT = 9;
                                                	break;
                case("Agility"):           SKILL_COMPONENT = 10;
                                                	break;
                case("Herblore"):          SKILL_COMPONENT = 11;
                                                	break;
                case("Thieving"):          SKILL_COMPONENT = 12;
                                                	break;
                case("Crafting"):          SKILL_COMPONENT = 13;
                									break;
                case("Runecrafting"):      SKILL_COMPONENT = 14;
                                                	break;
                case("Mining"):            SKILL_COMPONENT = 15;
                                                	break;
                case("Smithing"):          SKILL_COMPONENT = 16;
                                                	break;
                case("Fishing"):           SKILL_COMPONENT = 17;
                                                	break;
                case("Cooking"):          	SKILL_COMPONENT = 18;
                                                	break;
                case("Firemaking"):       	SKILL_COMPONENT = 19;
                                                	break;
                case("Woodcuting"):       SKILL_COMPONENT = 20;
                                                	break;
                case("Fletching"):			SKILL_COMPONENT = 21;
                                                	break;
                case("Slayer"):       		SKILL_COMPONENT = 22;
                                                	break;
                case("Farming"):        	SKILL_COMPONENT = 23;
                                                	break;
                case("Construction"):      SKILL_COMPONENT = 24;
                                                	break;
                case("Hunter"):            SKILL_COMPONENT = 25;
                                                	break;
                default:                        SKILL_COMPONENT = 0;
                                                	return;
                }
        }
       
        @Override
        public boolean init() {
                return sc.inventory.contains(Filters.itemId(GENIELAMP_ID));
        }
 
        @Override
        public int pulse() {
                        if(game.getCurrentTab() != Game.Tabs.INVENTORY){
                            game.openTab(Game.Tabs.INVENTORY);
                            return 0;
                        }

                        if(sc.widgets.get(LAMP_WIDGET) != null){
                            lampMenu = sc.widgets.get(LAMP_WIDGET, SKILL_COMPONENT);
                            if(lampMenu == null){
                                    Item lamp = sc.inventory.getItem(GENIELAMP_ID);
                                    if(lamp != null){

                                            utilities.clickItem(lamp);
                                            Utils.sleep(random(800, 1400));
                                    }
                            }
                            else if(lampMenu != null){
                                    Widget skillComponent = sc.widgets.get(LAMP_WIDGET, SKILL_COMPONENT);
                                    if (skillComponent != null)
                                        skillComponent.click();
                                    else
                                        return 0;
                                    Utils.sleep(800, 1400);
                                    Widget clickMe = sc.widgets.get(LAMP_WIDGET, 26);
                                    if (clickMe != null)
                                        clickMe.click();
                            }
                        }
                        return 0;
                }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }


}