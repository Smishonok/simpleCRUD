package com.valentinNikolaev.simpleCRUD.view.RegionRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.RegionController;
import com.valentinNikolaev.simpleCRUD.models.Region;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;
import java.util.Optional;

public class GetRegionRequestHandler extends RegionRequestHandler {

    private RegionController regionController;

    public GetRegionRequestHandler() {}

    public GetRegionRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (GET.equals(action)) {
            this.regionController = new RegionController();
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        String requestType = "";
        if (options.size() != 0) {
            requestType = options.get(0);
        }

        List<String> requestOptions = getOptionsWithOutFirst(options);

        switch (requestType) {
            case HELP:
                getHelpForGettingRegionDataRequest();
                break;
            case ID:
                getRegionByID(requestOptions);
                break;
            case NAME:
                getRegionByName(requestOptions);
                break;
            case ALL:
                getRegionsList();
                break;
            default:
                System.out.println(
                        "Invalid request type. Please, check request and try again, or call \"get help\".");
                break;
        }
    }

    private void getRegionByID(List<String> requestOptions) {
        if (isRequestEmpty(requestOptions)) {
            return;
        }

        String regionId = requestOptions.get(0);
        if (! isLong(regionId)) {
            System.out.println(
                    "The region`s id should consist only of numbers. Please, check the region`s " +
                            "id and try again.\n");
            return;
        }

        Optional<Region> region = this.regionController.getRegionById(regionId);
        if (region.isPresent()) {
            System.out.println(region.get().toString());
        } else {
            System.out.println(
                    "The repository does not contain the region with ID: " + regionId + "\n");
        }
    }

    private void getRegionByName(List<String> requestOptions) {
        if (isRequestEmpty(requestOptions)) {
            return;
        }

        String           regionName = requestOptions.get(0);
        Optional<Region> region     = this.regionController.getRegionByName(regionName);
        if (region.isPresent()) {
            System.out.println(region.get().toString());
        } else {
            System.out.println(
                    "The repository does not contain the region with name: " + regionName + "\n");
        }
    }

    private boolean isRequestEmpty(List<String> requestOptions) {
        boolean isEmpty = false;
        if (requestOptions.size() == 0) {
            System.out.println(
                    "The request does not contain parameter`s value. Please, check the " +
                            "request and try again, or take help information.\n");
            isEmpty = true;
        }
        return isEmpty;
    }

    private void getRegionsList() {
        List<Region> regions = this.regionController.getAllRegions();
        if (regions.size() != 0) {
            regions.forEach(System.out::println);
        } else {
            System.out.println("Regions list in the repository is empty.\n");
        }
    }

    private void getHelpForGettingRegionDataRequest() {
        String helpInfo =
                "For getting region`s data from the repository it can be used next formats of" +
                        "request:\n" + "\t1: " + GET + " " + ID +
                        " [id number] - return the region with " + "requested id\n" + "\t2: " +
                        GET + " " + NAME +
                        " [id number] - return the region with requested name\n" + "\t3: " + GET +
                        " " + ALL + " - return list of all regions in repository\n";

        System.out.println(helpInfo);
    }
}
