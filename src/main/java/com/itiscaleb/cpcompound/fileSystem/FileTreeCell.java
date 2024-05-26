package com.itiscaleb.cpcompound.fileSystem;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public class FileTreeCell extends TreeCell<File> {
    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            TreeItem<File> treeItem = getTreeItem();
            updateGraphic(treeItem);
            treeItem.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> updateGraphic(treeItem));
        }
    }

    private void updateGraphic(TreeItem<File> treeItem) {
        FontIcon arrowIcon = new FontIcon(treeItem.isExpanded() ?Dashicons.ARROW_DOWN_ALT2 : Dashicons.ARROW_RIGHT_ALT2);
        arrowIcon.setIconColor(Color.valueOf("#b6aeae"));
        arrowIcon.setIconSize(10);
        arrowIcon.setTranslateY(5);
        arrowIcon.setTranslateY(6);
        arrowIcon.setTranslateX(8);
        setDisclosureNode(arrowIcon);
        if (treeItem.isLeaf()) {
            FontIcon icon = new FontIcon(BoxiconsSolid.FILE);
            icon.setIconColor(Color.valueOf("#B3CED8FF"));
            setGraphic(icon);
        } else {
            FontIcon icon = treeItem.isExpanded() ?  new FontIcon(FontAwesomeSolid.FOLDER_OPEN) :  new FontIcon(FontAwesomeSolid.FOLDER);
            icon.setIconColor(Color.valueOf("#BF9659FF"));
            setGraphic(icon);
        }
    }
}
