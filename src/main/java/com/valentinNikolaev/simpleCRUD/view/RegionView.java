package com.valentinNikolaev.simpleCRUD.view;

import com.valentinNikolaev.simpleCRUD.view.regionRequestsHandlers.*;

import java.util.List;

public class RegionView {

    private RequestHandler requestHandler;

    public RegionView() throws ClassNotFoundException {
        this.requestHandler = new HelpRegionRequestHandler(new AddRegionRequestHandler(
                new ChangeRegionRequestHandler(
                        new GetRegionRequestHandler(new RemoveRegionRequestHandler()))));
    }

    public void action(String action, List<String> options) throws ClassNotFoundException {
        requestHandler.handleRequest(action, options);
    }
}
