package com.valentinNikolaev.simpleCRUD.view.PostsRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;

public class RemovePostRequestHandler extends PostRequestHandler {

    public RemovePostRequestHandler(){}

    public RemovePostRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {

    }
}
