package cmanager.geo;

import cmanager.gui.ExceptionPanel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeMap {

    private final List<List<String>> map = new ArrayList<>();

    public void add(String... key) {
        final List<String> list = new ArrayList<>(key.length);
        Collections.addAll(list, key);
        map.add(list);
    }

    public Integer getLowercase(String key) {
        key = key.toLowerCase();

        for (final List<String> list : map) {
            for (final String string : list) {
                if (string != null && string.toLowerCase().equals(key)) {
                    return map.indexOf(list);
                }
            }
        }

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    public Integer get(String key) {
        for (final List<String> list : map) {
            for (final String string : list) {
                if (string != null && string.equals(key)) {
                    return map.indexOf(list);
                }
            }
        }

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    public String get(int i, int j) {
        final List<String> list = map.get(i);
        return list.get(j);
    }
}
