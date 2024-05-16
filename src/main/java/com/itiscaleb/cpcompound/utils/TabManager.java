package com.itiscaleb.cpcompound.utils;

import javafx.scene.control.Tab;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TabManager {
    private Map<Tab, String> tabContentMap;
    private Map<Tab, Boolean> tabSaveStateMap;
    public TabManager() {
        this.tabContentMap = new HashMap<>();
        this.tabSaveStateMap = new HashMap<>();
    }


    public void addTab(Tab tab, String tabName, String content) {
        tab.setText(tabName+"*");
        tabContentMap.put(tab, content);
        tabSaveStateMap.put(tab, false);
    }
    public void setTabSaveState(Tab tab, boolean state) {
        tabSaveStateMap.put(tab, state);
    }
    public Boolean getTabSaveState(Tab tab) {
        return tabSaveStateMap.get(tab);
    }
    public String getTabContent(Tab tab) {
        return tabContentMap.get(tab);
    }
    public void saveTab(Tab tab, String updateContent) {
        if(getTabSaveState(tab)){
            return;
        }
        setTabSaveState(tab, true);
        updateTabContent(tab, updateContent);
        tab.setText(tab.getText().substring(0,tab.getText().length()-1));
    }

    public boolean containsTab(Tab tab) {
        return tabContentMap.containsKey(tab);
    }

    public void removeTab(Tab tab) {
        tabContentMap.remove(tab);
    }


    public void updateTabContent(Tab tab,String content) {
        if (!tabContentMap.containsKey(tab)) {
            addTab(tab, tab.getText(),"");
        }else{
            tabContentMap.put(tab, content);
        }
    }
    //get all tabs
    public Set<Tab> getAllTabs() {
        return tabContentMap.keySet();
    }
}
