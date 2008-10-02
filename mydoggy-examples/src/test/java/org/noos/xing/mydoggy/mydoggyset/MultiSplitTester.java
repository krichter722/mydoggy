package org.noos.xing.mydoggy.mydoggyset;

import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.AggregationPosition.*;

import javax.swing.*;
import java.util.Random;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitTester {

    public static void main(String[] args) {
        MyDoggySet test = new MyDoggySet();
        try {
            test.setUp();
            test.start(new RandomConstraints(test));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class RandomConstraints implements Runnable {
        MyDoggySet myDoggySet;

        public RandomConstraints(MyDoggySet myDoggySet) {
            this.myDoggySet = myDoggySet;
        }

        public void run() {
            try {
                Random random = new Random();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindowManager.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindow.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(Content.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(MyDoggySet.class, null);
                    }
                });

                Thread.sleep(2000);
                for (int i = 0; i < 200; i++) {
                    int index = random.nextInt(4);
                    Content content = null;
                    switch (index) {
                        case 0:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Welcome");
                            break;
                        case 1:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Manager");
                            break;
                        case 2:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Tools");
                            break;
                        case 3:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Contents");
                            break;
                    }


                    index = random.nextInt(2);
                    Content contentOn = null;
                    switch (index) {
                        case 0:
                            index = random.nextInt(4);
                            switch (index) {
                                case 0:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Welcome");
                                    break;
                                case 1:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Manager");
                                    break;
                                case 2:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Tools");
                                    break;
                                case 3:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Contents");
                                    break;
                            }
                            if (contentOn == content)
                                contentOn = null;
                            break;

                    }

                    index = random.nextInt(2);
                    AggregationPosition aggregationPosition = null;
                    switch (index) {
                        case 0:
                            index = random.nextInt(5);
                            switch (index) {
                                case 0:
                                    aggregationPosition = AggregationPosition.BOTTOM;
                                    break;
                                case 1:
                                    aggregationPosition = AggregationPosition.TOP;
                                    break;
                                case 2:
                                    aggregationPosition = AggregationPosition.LEFT;
                                    break;
                                case 3:
                                    aggregationPosition = AggregationPosition.RIGHT;
                                    break;
                                case 4:
                                    aggregationPosition = AggregationPosition.DEFAULT;
                                    break;
                            }
                            if (contentOn == content)
                                contentOn = null;
                            break;

                    }

                    MultiSplitConstraint constraint = new MultiSplitConstraint(
                            contentOn, aggregationPosition
                    );

                    StringBuffer sb = new StringBuffer();
                    sb.append("apply(\"").append(content.getId()).append("\",");
                    if (contentOn != null)
                        sb.append("\"").append(contentOn.getId()).append("\",");
                    if (aggregationPosition != null)
                        sb.append(aggregationPosition);
                    sb.append(");");

                    System.out.println(sb);

                    content.getContentUI().setConstraints(constraint);

                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class DeterminatedConstraints implements Runnable {
        MyDoggySet myDoggySet;
        ContentManager contentManager;

        public DeterminatedConstraints(MyDoggySet myDoggySet) {
            this.myDoggySet = myDoggySet;
            this.contentManager = myDoggySet.getToolWindowManager().getContentManager();
        }

        public void run() {
            try {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindowManager.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindow.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(Content.class, null);
                    }
                });

                Thread.sleep(2000);

                apply("Contents", TOP);
                apply("Welcome");
                apply("Welcome");
                apply("Tools", "Contents");
                apply("Welcome");
                apply("Manager", "Welcome", TOP);
                apply("Manager", "Welcome", BOTTOM);
                apply("Contents");
                apply("Tools", "Manager", DEFAULT);
                apply("Manager", "Contents");
                apply("Manager", BOTTOM);
                apply("Tools", TOP);
                apply("Contents", "Manager");
                apply("Tools");
                apply("Contents", "Tools", BOTTOM);
                apply("Manager");
                apply("Manager", "Tools", LEFT);
                apply("Tools", DEFAULT);
                apply("Tools");
                apply("Contents");
                apply("Tools");
                apply("Tools");
                apply("Manager", "Contents");
                apply("Contents", "Manager", LEFT);
                apply("Tools", "Welcome", LEFT);
                apply("Welcome", "Tools", BOTTOM);
                apply("Manager", LEFT);
                apply("Welcome", "Manager", TOP);
                apply("Manager", DEFAULT);
                apply("Tools");
                apply("Contents", "Welcome");
                apply("Manager", "Welcome");
                apply("Welcome", "Manager", DEFAULT);
                apply("Tools");
                apply("Manager", TOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        protected void apply(String dockableId, String aggDockId, AggregationPosition aggregationPosition) throws InterruptedException {
            contentManager.getContent(dockableId).getContentUI().setConstraints(
                    new MultiSplitConstraint(contentManager.getContent(aggDockId),
                                             aggregationPosition)
            );
            Thread.sleep(500);
        }

        protected void apply(String dockableId, AggregationPosition aggregationPosition) throws InterruptedException {
            contentManager.getContent(dockableId).getContentUI().setConstraints(
                    new MultiSplitConstraint(aggregationPosition)
            );
            Thread.sleep(500);
        }

        protected void apply(String dockableId) throws InterruptedException {
            apply(dockableId, AggregationPosition.BOTTOM);
        }

        protected void apply(String dockableId, String aggDockId) throws InterruptedException {
            contentManager.getContent(dockableId).getContentUI().setConstraints(
                    new MultiSplitConstraint(contentManager.getContent(aggDockId))
            );
            Thread.sleep(500);
        }


    }

}
