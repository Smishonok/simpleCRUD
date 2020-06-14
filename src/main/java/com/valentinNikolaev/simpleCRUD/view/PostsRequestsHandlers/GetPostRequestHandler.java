package com.valentinNikolaev.simpleCRUD.view.PostsRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.PostController;
import com.valentinNikolaev.simpleCRUD.controller.UserController;
import com.valentinNikolaev.simpleCRUD.models.User;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;
import java.util.Optional;

public class GetPostRequestHandler extends PostRequestHandler {

    private PostController postController;
    private UserController userController;

    public GetPostRequestHandler(){}

    public GetPostRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (GET.equals(action)) {
            this.postController = new PostController();
            this.userController = new UserController();
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
                getHelpForAddingPostRequest();
                break;
            case USER_ID:
                addPost(requestOptions);
                break;
            default:
                System.out.println("Invalid request type. Please, check request type and try " +
                                           "again, or take help information using \"" + ADD + " " +
                                           HELP + "\".\n");
                break;
        }
    }

    private void addPost(List<String> requestOptions) {
        if (checkRequestOptions(requestOptions)) {
            String userIdValue = requestOptions.get(0);
            String content = requestOptions.get(1);
            this.postController.addPost(userIdValue,content);
        }
    }

    private boolean checkRequestOptions(List<String> requestOptions) {
        boolean isOptionsCorrect = true;
        if (requestOptions.size() != 2) {
            System.out.println(
                    "Invalid request format. Please, check request format and try again, " +
                            "or get help information.");
            isOptionsCorrect = false;
        }

        String userIdValue = requestOptions.get(0);
        if (! isLong(userIdValue)) {
            System.out.println("The user`s id should consist only of numbers. Please, check the " +
                                       "user`s id and try again.");
            isOptionsCorrect = false;
        }

        if (! isUserExists(userIdValue)) {
            System.out.println("User with id: "+userIdValue+" is not exists. Please, check the " +
                                       "user`s id number and try again.\n");
            isOptionsCorrect = false;
        }
        return isOptionsCorrect;
    }

    private boolean isUserExists(String userId) {
        Optional<User> user = this.userController.getUserById(userId);
        return user.isPresent();
    }

    private void getHelpForAddingPostRequest() {
        String helpInfo = "For adding posts into the repository it can be used next formats of" +
                "request:\n" + "\t" + ADD + " " + USER_ID + "[user id]" + " [content]";
        System.out.println(helpInfo);
    }
}
