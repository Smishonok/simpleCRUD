package com.valentinNikolaev.simpleCRUD.view.regionRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.RegionController;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;

public class AddRegionRequestHandler extends RegionRequestHandler {

    private RegionController regionController;

    public AddRegionRequestHandler (){}

    public AddRegionRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (ADD.equals(action)) {
            this.regionController = new RegionController();
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        if (options.size() != 1) {
            System.out.println(
                    "Invalid request format. Please, check request format and try again, or get " +
                            "help information.");
            return;
        }

        if (options.get(0).equals(HELP)) {
            getHelpForAddingRegionDataRequest();
        } else {
            this.regionController.addRegion(options.get(0));
        }
    }

    private void getHelpForAddingRegionDataRequest() {
        String helpInfo = "For adding regions into the repository it can be used next formats of" +
                "request:\n" + "\t1: " + ADD + " [region name]";
        System.out.println(helpInfo);
    }
}
