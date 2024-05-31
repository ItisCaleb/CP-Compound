package com.itiscaleb.cpcompound.utils;

import javafx.scene.control.Tab;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TabManager {
    private Map<Tab, Boolean> tabSaveStateMap;
    public TabManager() {
        this.tabSaveStateMap = new HashMap<>();
    }


    public void addTab(Tab tab, String tabName) {
        tab.setText(tabName);
        tabSaveStateMap.put(tab, true);
    }
    public void setTabSaveState(Tab tab, boolean state) {
        tabSaveStateMap.put(tab, state);
    }
    public Boolean getTabSaveState(Tab tab) {
        return tabSaveStateMap.get(tab);
    }

    public void saveTab(Tab tab) {
        if(getTabSaveState(tab)){
            return;
        }
        setTabSaveState(tab, true);
        tab.setText(tab.getText().substring(0,tab.getText().length()-1));
    }

    public void changeTab(Tab tab) {
        if(!getTabSaveState(tab)){
            return;
        }
        setTabSaveState(tab, false);
        tab.setText(tab.getText() + "*");
    }
}
