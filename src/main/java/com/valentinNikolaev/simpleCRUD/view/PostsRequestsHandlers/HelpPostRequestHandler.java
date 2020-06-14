package com.valentinNikolaev.simpleCRUD.view.PostsRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;

public class HelpPostRequestHandler extends PostRequestHandler {

    public HelpPostRequestHandler(){}

    public HelpPostRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (HELP.equals(action)) {
            String helpInfo =
                    "This is the part of the console app in which you can add, change and " +
                            "remove posts from repository. The main commands are:\n" +
                            "\tadd - adding new post;\n" +
                            "\tget - getting posts from repository;\n" +
                            "\tchange - changing posts in repository;\n" +
                            "\tremove - removing posts from repository;\n" + "\n\tCalling \"" +
                            HELP + "\" after each of commands calls the help`s " +
                            "information for the corresponding command.";
            System.out.println(helpInfo);
        } else {
            getNextHandler(action, options);
        }
    }
}
