package com.itiscaleb.cpcompound.component;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.ContextMenuSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.lang.reflect.Method;

public class CompletionMenu extends ContextMenu {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CompletionMenuSkin(this);
    }

    private static class CompletionMenuSkin extends ContextMenuSkin {

        public CompletionMenuSkin(ContextMenu control) {
            super(control);

            // limit height
            ((Region)this.getNode()).setMaxHeight(300);
            control.addEventHandler(Menu.ON_SHOWN, (e)->{
                // use reflection to focus first item
                // super evil hack
                try {
                    Method method = this.getNode().getClass().getMethod("requestFocusOnIndex", int.class);
                    method.invoke(this.getNode(), 0);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });

            // make it won't focus on mouse move
            control.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, Event::consume);

            // only focus on mouse click
            // if double click, then select
            control.addEventFilter(MouseEvent.ANY,(e -> {
                if(e.getClickCount() == 1) {
                    if(e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                        ((Node)e.getTarget()).requestFocus();
                    }
                    e.consume();
                }

            }));

            // consume space and enter
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

            // tab to auto complete
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
