package com.valentinNikolaev.simpleCRUD.controller;

import com.valentinNikolaev.simpleCRUD.models.Post;
import com.valentinNikolaev.simpleCRUD.models.User;
import com.valentinNikolaev.simpleCRUD.repository.PostRepository;
import com.valentinNikolaev.simpleCRUD.repository.RepositoryManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;


public class PostController {

    static Logger log = Logger.getLogger(PostController.class);

    private PostRepository postRepository;
    private UserController userController;

    public PostController() throws ClassNotFoundException {
        initPostRepository();
        this.userController = new UserController();
    }

    private void initPostRepository() throws ClassNotFoundException {
        log.debug("Starting initialisation of posts repository");
        postRepository = RepositoryManager.getRepositoryFactory().getPostRepository();
        log.debug("Posts repository implementation is: " + postRepository.getClass().getName());
    }

    public void addPost(String userId, String content) {
        Optional<User> user = this.userController.getUserById(userId);
        if (user.isPresent()) {
            long id   = Long.parseLong(userId);
            Post post = new Post(id, content);
            this.postRepository.add(post);
        } else {
            throw new IllegalArgumentException("The user with id: " + userId + " is not exists.");
        }
    }

    public Optional<Post> getPost(String postId) {
        long           id   = Long.parseLong(postId);
        Optional<Post> post = this.postRepository.contains(id) ? Optional.of(
                this.postRepository.get(id)) : Optional.empty();

        return post;
    }

    public List<Post> getAllPostsList() {
        return this.postRepository.getAll();
    }

    public List<Post> getPostsByUserId(String userId) {
        long id = Long.parseLong(userId);
        return this.postRepository.getPostsByUserId(id);
    }

    public void changePost(String postId, String newContent) {
        long id = Long.parseLong(postId);
        if (this.postRepository.contains(id)) {
            Post post = this.postRepository.get(id);
            post.setContent(newContent);
            this.postRepository.change(post);
        }
    }

    public void removePost(String postId) {
        long id = Long.parseLong(postId);
        this.postRepository.remove(id);
    }

    public void removeAllPostByUser(String userId) {
        long id = Long.parseLong(userId);
        this.postRepository.removePostsByUserId(id);
    }

    public void removeAllPosts() {
        this.postRepository.removeAll();
    }
}
