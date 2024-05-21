package com.itiscaleb.cpcompound.utils;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.evaicons.Evaicons;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
public class FileTreeCell extends TreeCell<String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item);
            TreeItem<String> treeItem = getTreeItem();
            updateGraphic(treeItem);
            treeItem.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> updateGraphic(treeItem));
        }
    }

    private void updateGraphic(TreeItem<String> treeItem) {
        FontIcon arrowIcon = new FontIcon(treeItem.isExpanded() ?Dashicons.ARROW_DOWN_ALT2 : Dashicons.ARROW_RIGHT_ALT2);
        arrowIcon.setIconColor(Color.valueOf("#b6aeae"));
        arrowIcon.setIconSize(10);
        arrowIcon.setTranslateY(4);
        arrowIcon.setTranslateX(-3);
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
