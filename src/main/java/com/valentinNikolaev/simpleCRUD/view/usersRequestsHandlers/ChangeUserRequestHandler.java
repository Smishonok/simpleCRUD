package com.valentinNikolaev.simpleCRUD.view.usersRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.UserController;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;

public class ChangeUserRequestHandler extends UserRequestHandler {

    private UserController userController;

    public ChangeUserRequestHandler(){}

    public ChangeUserRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (CHANGE.equals(action)) {
            userController = new UserController();
            String       requestType    = options.get(0);
            List<String> requestOptions = getOptionsWithOutFirst(options);
            processRequest(requestType, requestOptions);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(String requestType, List<String> requestOptions)
            throws ClassNotFoundException {
        int requestOptionsLength = requestOptions.size();
        switch (requestType) {
            case HELP:
                getHelpForChangingUserDataRequest();
                break;
            case ID:
                if (requestOptionsLength == 3 && isLong(requestOptions.get(0))) {
                    changeUserParameters(requestOptions);
                } else {
                    System.out.println("Invalid request format. Check the request format and try " +
                                               "again or get help information.");
                }
            default:
                System.out.println("Invalid request type. Check the request type and try again or" +
                                           " get help information");
                break;
        }
    }

    private void changeUserParameters(List<String> requestOptions) throws ClassNotFoundException {
        String userId            = requestOptions.get(0);
        String changingParameter = requestOptions.get(1);
        String parameterValue    = requestOptions.get(2);
        switch (changingParameter) {
            case FIRST_NAME:
                this.userController.changeUserFirstName(userId, parameterValue);
                break;
            case LAST_NAME:
                this.userController.changeUserLastName(userId, parameterValue);
                break;
            case REGION:
                changeUserRegion(userId, parameterValue);
                break;
            case ROLE:
                changeUserRole(userId, parameterValue);
                break;
            default:
                System.out.println("Invalid parameter name. Check parameter name and try" +
                                           " again, or get help information.");
                break;
        }

    }

    private void changeUserRegion(String userId, String regionName) throws ClassNotFoundException {
        if (isRegionNameValid(regionName)) {
            this.userController.changeUserRegion(userId, regionName);
        } else {
            System.out.println(
                    "Illegal region name. Please, check the list of valid region`s name " +
                            "and try again.");
        }
    }

    private void changeUserRole(String userId, String roleName) {
        if (isRoleNameValid(roleName)) {
            this.userController.changeUserRole(userId, roleName);
        } else {
            System.out.println("Illegal role name. Please, check the list of valid role`s names " +
                                       "and try again.");
        }
    }

    private void getHelpForChangingUserDataRequest() {
        String helpInfo =
                "For changing user`s data in the repository it can be used next formats of" +
                        "request:\n" + "\t1: " + ID + " [id number]" + FIRST_NAME +
                        " [new user first name] - change " + "user first name\n" + "\t2: " + ID +
                        " [id number]" + LAST_NAME + " [new user last name] - change " +
                        "user last name\n" + "\t1: " + ID + " [id number]" + ROLE +
                        " [new user role] - change " + "user role\n" + "\t1: " + ID +
                        " [id number]" + REGION + " [new user region] - change " + "user region\n";

        System.out.println(helpInfo);
    }
}
