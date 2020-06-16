package com.valentinNikolaev.simpleCRUD.view.PostsRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.PostController;
import com.valentinNikolaev.simpleCRUD.controller.UserController;
import com.valentinNikolaev.simpleCRUD.models.User;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;
import java.util.Optional;

public class AddPostRequestHandler extends PostRequestHandler {

    private PostController postController;
    private UserController userController;

    public AddPostRequestHandler() {
    }

    public AddPostRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (ADD.equals(action)) {
            this.postController = new PostController();
            this.userController = new UserController();
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        int optionsSize = options.size();
        switch (optionsSize) {
            case 1:
                getHelpForAddingPostRequest(options);
                break;
            case 2:
                addPost(options);
                break;
            default:
                this.getErrorMessage();
        }
    }

    private void addPost(List<String> options) {
        Optional<User> user = this.userController.getUserById(options.get(0));

        if (user.isPresent()) {
            this.postController.addPost(options.get(0), options.get(1));
        } else {
            System.out.println("User with id: "+options.get(0)+" is not exists in repository. " +
                                       "Check user id and try again.");
        }
    }

    private void getHelpForAddingPostRequest(List<String> options) {
        if (options.get(0).equals(HELP)) {
            String helpInfo = "For adding post into the repository it can be used next formats of" +
                    "request:\n" + "\t1: " + ADD + "[user id]  [post content]";
            System.out.println(helpInfo);
        } else {
            getErrorMessage();
        }
    }

    private void getErrorMessage() {
        System.out.println(
                "Invalid request format. Please, check request format and try again, or get " +
                        "help information.");
    }
}
