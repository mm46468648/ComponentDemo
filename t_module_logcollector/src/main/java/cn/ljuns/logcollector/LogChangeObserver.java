package cn.ljuns.logcollector;

import java.util.ArrayList;
import java.util.Observable;

public class LogChangeObserver extends Observable {
    private ArrayList<String> arrayList = new ArrayList();


    public ArrayList getArrayList() {
        return arrayList;
    }

    public void addStr(String str){
        arrayList.add(str);
        setChanged();
        notifyObservers();
    }

    public void clear(){
        arrayList.clear();
        setChanged();
        notifyObservers();
    }

}


