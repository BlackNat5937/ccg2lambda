package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import visualization.tree.Tree;
import visualization.utils.formula.Formula;
import visualization.utils.formula.node.Actor;
import visualization.utils.formula.node.Conjunction;
import visualization.utils.formula.node.Event;

/**
 * @author Nathan Joubert
 */
public class TreeController implements Parametrable<Formula> {

    private Formula formula;

    @FXML
    private TitledPane box;

    @FXML
    private TreeView<String> treeViewCont;

    @Override
    public void initData(Formula data) {
        this.formula = data;

        System.out.println("this.formula.getActors().values() : " + this.formula.getActors().values());

        Tree tree = null;
        Tree child1 = null;

        for (Actor actor : this.formula.getActors().values()) {

            if (actor.getId().equals("x")) {
                System.out.println("l'actor : " + actor.getName() + " " + actor.getId());
                tree = new Tree(actor.getId());
                child1 = new Tree(actor.getId() + " is " + actor.getName());
                tree.getChildren().add(child1);
            }
        }

        for (Actor actor : this.formula.getActors().values()) {
            if (!actor.getId().equals("x")) {
                Tree child = new Tree(actor.getId());
                tree.getChildren().add(child);
                child.getChildren().add(new Tree(actor.getId() + " is " + actor.getName()));
            }
        }

        for (Event event : this.formula.getEvents().values()) {
            Tree child = new Tree(event.getId());
            tree.getChildren().add(child);
            child.getChildren().add(new Tree(event.getId() + " is " + event.getName()));
        }

        for (Conjunction conjunction : this.formula.getConjunctions().values()) {
            Tree child = new Tree(conjunction.getId());
            tree.getChildren().add(child);
            child.getChildren().add(new Tree(conjunction.getId() + " is " + conjunction.getName()));
        }

        //Tree tree = new Tree("Test");

        //  Tree child = new Tree("Child 1");

        //  tree.getChildren().add(child1);
        // tree.getChildren().add(new Tree("Child 2"));

        //  child.getChildren().add(new Tree("Child 1-1"));

        //System.out.println(tree.toString());

        TreeItem<String> ti = new TreeItem<>(tree.getLabel());
        displayTree(tree, ti);

        treeViewCont.setRoot(ti);


        box.setText(formula.getLambda());
    }

    /**
     * display the tree
     *
     * @param tree
     * @param ti
     */
    private void displayTree(Tree tree, TreeItem<String> ti) {
        for (Tree child : tree.getChildren()) {
            TreeItem<String> tiChild = new TreeItem<>(child.getLabel());
            ti.getChildren().add(tiChild);
            displayTree(child, tiChild);
        }
    }
}
