package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import visualization.tree.Tree;
import visualization.utils.formula.Formula;

public class TreeController implements Parametrable<Formula> {

    private Formula formula;

    @FXML
    private TitledPane box;

    @FXML
    private TreeView<String> treeViewCont;

    @Override
    public void initData(Formula data) {
        this.formula = data;

        Tree tree = new Tree("Test");

        Tree child = new Tree("Child 1");

        tree.getChildren().add(child);
        tree.getChildren().add(new Tree("Child 2"));

        child.getChildren().add(new Tree("Child 1-1"));

        //System.out.println(tree.toString());

        TreeItem<String> ti = new TreeItem<>(tree.getLabel());
        displayTree(tree, ti);

        treeViewCont.setRoot(ti);


        box.setText(formula.getLambda());
    }

    private void displayTree(Tree tree, TreeItem<String> ti) {
        for (Tree child : tree.getChildren()) {
            TreeItem<String> tiChild = new TreeItem<>(child.getLabel());
            ti.getChildren().add(tiChild);
            displayTree(child, tiChild);
        }
    }
}
