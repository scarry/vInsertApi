package randoms;

import api.Conditions;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.util.Timer;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.randevent.RandomEvent;

        @ScriptManifest(name = "Mystery Box", description = "Solves the Mystery Box random", authors = { "listerkeler" }, version = 1.0D)
public class MysteryBox extends AntiRandom {
        private final int[] MYSTERY_BOX = { 3063 };
        private final int[] OLD_MAN = { 410 };
    
                public boolean init()
        {
                if (inventory.contains(Filters.itemId(MYSTERY_BOX))) {
                        return true;
                    }
                if (npcs.getNearest(OLD_MAN) != null) {
                        if (localPlayer.getLocation().distanceTo(npcs.getNearest(OLD_MAN).getLocation()) <= 2) {
                                return true;
                            }
                    }
                return false;
            }
    
                public boolean waitFor(final Conditions.Condition condition, final long timeOut) {
                Timer timer = new Timer(timeOut);
                ScriptContext ctx = this.getContext();
                while (timer.isRunning()) {
                        if (condition.validate(ctx)) {
                                return true;
                            }
                    }
                return false;
            }
    
                public int pulse()
        {
                Item box = inventory.getItem(MYSTERY_BOX);
                Npc oldMan = npcs.getNearest(OLD_MAN);
        
                        if (oldMan != null) {
                        talkWith(oldMan);
                    } else if (box != null) {
                        solveBox(box);
                    }
        
                        return 500;
            }
    
                public void close()
        {
                log("Mystery Box successfully solved!");
            }


                private void talkWith(Npc oldMan) {
                if (oldMan != null) {

                        oldMan.interact("Talk-to");
                        Conditions.waitFor(new isValid(242),2000,this.getContext());
                    }
                sleep(500, 900);
                widgets.clickContinue();
                sleep(500, 900);
                requestExit();
            }
    
                private void solveBox(Item box) {
                if (box != null) {
                        if (isValid(190)) {
                                String answer = getAnswer();
                                for (int i = 10; i < 13; i++) {
                                        try {
                                                Widget found = widgets.get(190, i);
                                                if (found.getText().toLowerCase().contains(answer)) {
                                                        found.click();
                                                        requestExit();
                                                    }
                                            } catch (ArrayIndexOutOfBoundsException ex) {
                                                //System.out.println("Apparently "  i  " is not in the widget children");
                                            }
                                    }
                            } else {
                                int itemSlot = inventory.indexOf(box);
                
                                        //System.out.println("Clicking box ("  itemSlot  ") in inventory");
                                inventory.interact(itemSlot, "Open");
                                for (Timer t = new Timer(2000L); (t.getElapsed() < 2000L) && (!isValid(190)); sleep(100));
                            }
                    }
            }
    
                private String getAnswer() {
                String answer = "";
                String question = getQuestion();
                String sides = getSides();
                String[] temp = sides.split("-");
                for (String i : temp) {
                        if (i.contains(question)) {
                                answer = i.replace(question, "");
                            }
                    }
                //System.out.println("The box answer is "  answer);
                return answer;
            }
    
                private String getQuestion() {
                String question = "";
                if (isValid(190)) {
                        question = widgets.get(190, 6).getText();
                        if (question.contains("shape has")) {
                                question = question.substring(question.indexOf("number ") + "number ".length(), question.indexOf("?"));
                            }
                        if (question.contains("number is")) {
                                question = question.substring(question.indexOf("the ") + "the ".length(), question.indexOf("?")).toLowerCase();
                            }
                    }
                //System.out.println("The box question is "  question);
                return question;
            }
    
                private String getSides() {
                String result = "";
                String[] shape = { "", "", "" };
                String[] number = { "", "", "" };
        
                        int[] circle = { 7005, 7020, 7035 };
                int[] pentagon = { 7006, 7021, 7036 };
                int[] square = { 7007, 7022, 7037 };
                int[] star = { 7008, 7023, 7038 };
                int[] triangle = { 7009, 7024, 7039 };
        
                        int[] n0 = { 7010, 7025, 7040 };
                int[] n1 = { 7011, 7026, 7041 };
                int[] n2 = { 7012, 7027, 7042 };
                int[] n3 = { 7013, 7028, 7043 };
                int[] n4 = { 7014, 7029, 7044 };
                int[] n5 = { 7015, 7030, 7045 };
                int[] n6 = { 7016, 7031, 7046 };
                int[] n7 = { 7017, 7032, 7047 };
                int[] n8 = { 7018, 7033, 7048 };
                int[] n9 = { 7019, 7034, 7049 };
        
                        if (isValid(190)) {
                        for (int i = 0; i < 3; i++) {
                                final int widgetShape = widgets.get(190, i).getModelId();
                                if (contains(circle, widgetShape)) shape[i] = "circle";
                                if (contains(pentagon, widgetShape)) shape[i] = "pentagon";
                                if (contains(star, widgetShape)) shape[i] = "star";
                                if (contains(square, widgetShape)) shape[i] = "square";
                                if (contains(triangle, widgetShape)) shape[i] = "triangle";
                            }
            
                                for (int i = 3; i < 6; i++) {
                                final int widgetNumber = widgets.get(190, i).getModelId();
                                if (contains(n0, widgetNumber)) number[(i - 3)] = "0";
                                if (contains(n1, widgetNumber)) number[(i - 3)] = "1";
                                if (contains(n2, widgetNumber)) number[(i - 3)] = "2";
                                if (contains(n3, widgetNumber)) number[(i - 3)] = "3";
                                if (contains(n4, widgetNumber)) number[(i - 3)] = "4";
                                if (contains(n5, widgetNumber)) number[(i - 3)] = "5";
                                if (contains(n6, widgetNumber)) number[(i - 3)] = "6";
                                if (contains(n7, widgetNumber)) number[(i - 3)] = "7";
                                if (contains(n8, widgetNumber)) number[(i - 3)] = "8";
                                if (contains(n9, widgetNumber)) number[(i - 3)] = "9";
                            }
            
                                for (int i = 0; i < 3; i++) {
                                result = result + shape[i] + number[i] + "-";
                            }
                    }
                //System.out.println("The box sides are "  result);
                return result;
            }
    
                private boolean isValid(int id) {
                return widgets.get(id).length != 0;
            }

                public class isValid extends Conditions.Condition {
                    private int id;

                    public isValid(int newId){
                        id = newId;
                    }
                    @Override
                    public boolean validate(ScriptContext ctx) {
                        return isValid(id);
                    }
                }

                private boolean contains(int[] y, int i) {
                for (int x : y) {
                        if (x == i) {
                                return true;
                            }
                    }
                return false;
            }
    
                public RandomEvent.RandomEventPriority priority()
        {
                return RandomEvent.RandomEventPriority.HIGH;
            }
    }