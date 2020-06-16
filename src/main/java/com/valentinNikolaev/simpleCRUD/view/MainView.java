package com.valentinNikolaev.simpleCRUD.view;


import java.util.List;
import java.util.Scanner;

public class MainView {

    private final String POST   = "post";
    private final String USER   = "user";
    private final String REGION = "region";
    private final String HELP   = "help";
    private final String EXIT   = "exit";

    private PostView                   postView;
    private RegionView                 regionView;
    private UserView                   userView;
    private RequestParametersProcessor requestParametersProcessor;
    private String                     requestType;

    public MainView() throws ClassNotFoundException {
        this.postView              = new PostView();
        this.regionView            = new RegionView();
        this.userView              = new UserView();
        requestParametersProcessor = new RequestParametersProcessor();
    }

    public void initiateMainView() {
        System.out.println(
                "Hello, this is the simpleCRUDE app. This app created  in order to learn " +
                        "use to NIO package, streams API, lambda expressions, file`s input and " +
                        "output streams.\nThis app can:\n" +
                        "\tcreate users, regions, user`s posts and save its in file repository;\n" +
                        "\tchange user`s attributes (first name, last name, role, region), and  " +
                        "post`s content;\n" + "\tremove data from the repository.\n" +
                        "\nFor using this app you should enter the commands in command`s line. " +
                        "The commands " + "should contain the words which delimited by white " +
                        "spaces.\n" + "\nFor getting help information to enter \"help\"");
        requestType = "start";
        while (requestType != EXIT) {
            Scanner commandScanner = new Scanner(System.in);
            String  request        = commandScanner.nextLine();
            if (! request.isEmpty()) {
                processRequest(request);
            }
        }
    }

    private String processRequest(String request) {
        String requestType = getRequestType(request);
        String requestOrder = getRequestOrder(request);
        List<String> requestOptions = gerRequestOptions(request);

        switch (requestType) {

        }

    }

    private List<String> gerRequestOptions(String request) {

    }

    private String getRequestOrder(String request) {

    }

    private String getRequestType(String request) {

    }


    private void getErrorMessage() {
        System.out.println(
                "Invalid request type. Please, check request and try again. You can " + "use " +
                        HELP + " for getting help or use " + EXIT + " for closing app" + ".\n");
    }
}
