package com.valentinNikolaev.simpleCRUD.view.PostsRequestsHandlers;

import com.valentinNikolaev.simpleCRUD.controller.PostController;
import com.valentinNikolaev.simpleCRUD.models.Post;
import com.valentinNikolaev.simpleCRUD.view.RequestHandler;

import java.util.List;
import java.util.Optional;

public class ChangePostRequestHandler extends PostRequestHandler {

    private PostController postController;

    public ChangePostRequestHandler() {
    }

    public ChangePostRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
        if (CHANGE.equals(action)) {
            this.postController = new PostController();
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        int optionsSize = options.size();
        switch (optionsSize) {
            case 1:
                getHelpForChangingPostContentRequest(options);
                break;
            case 2:
                changePost(options);
                break;
            default:
                getErrorMessage();
                break;
        }
    }

    private void changePost(List<String> options) {
        Optional<Post> post = this.postController.getPost(options.get(0));
        if (post.isPresent()) {
            this.postController.changePost(options.get(0), options.get(1));
        } else {
            System.out.println(
                    "The post with id: " + options.get(0) + " is not exists in repository." +
                            " Check post id and try again.");
        }
    }

    private void getHelpForChangingPostContentRequest(List<String> options) {
        if (options.get(0).equals(HELP)) {
            String helpInfo =
                    "For changing post content it can be used next format of request:\n" + "\t" +
                            CHANGE + " [post id] [new post content]";

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
