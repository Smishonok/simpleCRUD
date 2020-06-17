package com.valentinNikolaev.simpleCRUD.view.usersRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.UserController;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;

public class RemoveUserRequestsHandler extends UserRequestHandler {

    private UserController userController;

    public RemoveUserRequestsHandler(){}

    public RemoveUserRequestsHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (REMOVE.equals(action)) {
            userController = new UserController();
            String       optionType     = options.get(0);
            List<String> requestOptions = getOptionsWithOutFirst(options);
            removeUser(optionType, requestOptions);
        } else {
            getNextHandler(action, options);
        }
    }

    private void removeUser(String optionType, List<String> options) {
        switch (optionType) {
            case HELP:
                getHelpForRemoveRequest();
                break;
            case ID:
                if (options.size() == 1) {
                    removeUserById(options.get(0));
                } else {
                    System.out.println(
                            "Invalid the request`s format to remove user from the repository.\n");
                }
                break;
            case ALL:
                removeAllUsers();
                break;
            default:
                System.out.println(
                        "Invalid option`s type requested. Please, check request`s option" +
                                " type and try again.\n");
                break;
        }
    }

    private void removeUserById(String id) {
        if (isLong(id)) {
            this.userController.removeUser(id);
        } else {
            System.out.println(
                    "The user`s id should consist only of numbers. Please, check the user`s id " +
                            "and try again.\n");
        }
    }

    private void removeAllUsers() {
        this.userController.removeAllUsers();
        System.out.println("All users was removed from repository.\n");
    }

    private void getHelpForRemoveRequest() {
        String helpInfo =
                "For removing users from repository it can be used two formats of " + "request:\n" +
                        "\t1: id [user id] - remove user with requested id\n" +
                        "\t2: all - remove all users\n";
        System.out.println(helpInfo);
    }

}
