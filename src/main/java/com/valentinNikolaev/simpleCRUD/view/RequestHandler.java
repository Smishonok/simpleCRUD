package com.valentinNikolaev.simpleCRUD.view;

import java.util.ArrayList;
import java.util.List;

public abstract class RequestHandler {

    protected final String ADD    = "add";
    protected final String GET    = "get";
    protected final String CHANGE = "change";
    protected final String REMOVE = "remove";
    protected final String                     HELP   = "help";

    private RequestHandler nextRequestHandler;

    public RequestHandler() {}

    public RequestHandler(RequestHandler nextRequestHandler) {
        this.nextRequestHandler = nextRequestHandler;
    }

    public abstract void handleRequest(String action, List<String> options)
            throws ClassNotFoundException;

    public RequestHandler setNextHandler(RequestHandler handler) {
        this.nextRequestHandler = handler;
        return handler;
    }

    public boolean hasNextHandler() {
        boolean hasNext = false;
        if (nextRequestHandler != null) {
            hasNext = true;
        }
        return hasNext;
    }

    public void getNextHandler(String action, List<String> options) throws ClassNotFoundException {
        if (hasNextHandler()) {
            nextRequestHandler.handleRequest(action, options);
        } else {
            getHelp();
        }
    }

    public abstract void getHelp();

    public List<String> getOptionsWithOutFirst(List<String> options) {
        RequestParametersProcessor stringProcessor = new RequestParametersProcessor();
        return stringProcessor.getOptionsWithOutFirst(options);
    }

    protected boolean isLong(String string) {
        RequestParametersProcessor stringProcessor = new RequestParametersProcessor();
        return stringProcessor.isLong(string);
    }

}
