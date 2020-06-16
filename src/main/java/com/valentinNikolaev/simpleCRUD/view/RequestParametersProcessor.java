package com.valentinNikolaev.simpleCRUD.view;

import java.util.ArrayList;
import java.util.List;

public class RequestParametersProcessor {

    public List<String> getOptionsWithOutFirst(List<String> options) {
        List<String> optionsListWithOutFirst = new ArrayList<>();
        if (options.size() > 1) {
            for (int i = 1; i < options.size(); i++) {
                optionsListWithOutFirst.add(options.get(i));
            }
        }
        return optionsListWithOutFirst;
    }

    public boolean isLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
