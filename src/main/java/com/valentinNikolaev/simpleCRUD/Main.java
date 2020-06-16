package com.valentinNikolaev.simpleCRUD;

import com.valentinNikolaev.simpleCRUD.view.MainView;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class Main {
    public static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            MainView mainView = new MainView();
            mainView.initiateMainView();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(log::error);
        }
    }
}
