package com.itiscaleb.cpcompound.component;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.ContextMenuSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.beans.EventHandler;

public class CompletionMenu extends ContextMenu {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CompletionMenuSkin(this);
    }

    private static class CompletionMenuSkin extends ContextMenuSkin {

        /**
         * Creates a new ContextMenuSkin instance.
         *
         * @param control The control that this skin should be installed onto.
         */
        EventTarget lastFocusTarget;

        public CompletionMenuSkin(ContextMenu control) {
            super(control);
            control.addEventHandler(Menu.ON_SHOWN, (e)->{
                this.getNode().lookup(".menu-item").requestFocus();
            });
            control.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, Event::consume);
            control.addEventFilter(MouseEvent.ANY,(e -> {
                if(e.getClickCount() == 1) {
                    if(e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                        ((Node)e.getTarget()).requestFocus();
                    }
                    e.consume();
                }

            }));
            this.getNode().addEventFilter(KeyEvent.KEY_PRESSED, (ke) -> {
                switch (ke.getCode()) {
                    case SPACE:{
                        ke.consume();
                        control.hide();
                    }
                    case ENTER:{
                        if(ke.isShiftDown()){
                            ke.consume();
                            control.hide();
                            control.getOwnerNode().fireEvent(ke);
                        }
                    }
                }
            });
            this.getNode().addEventHandler(KeyEvent.KEY_PRESSED ,(ke) -> {
                switch (ke.getCode()) {
                    case TAB:{
                        control.getOwnerNode().fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                                KeyCode.ENTER,false, false, false, false));
                        ke.consume();
                    }

                }
            });
        }
    }

}
