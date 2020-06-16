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

    public Post addPost(String userId, String content) {
        log.debug("Start creating and adding new post into repository.");
        Optional<User> user = this.userController.getUserById(userId);
        if (user.isPresent()) {
            long postId    = this.getLastPostId() + 1;
            Post addedPost = this.postRepository.add(
                    new Post(postId, user.get(), content));
            log.debug("New post with id: "+addedPost.getId()+" created and added into repository.");
            return addedPost;
        } else {
            throw new IllegalArgumentException("The user with id: " + userId + " is not exists.");
        }
    }

    public Optional<Post> getPost(String postId) {
        long id = Long.parseLong(postId);
        Optional<Post> post = this.postRepository.isContains(id) ? Optional.of(
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

    public boolean changePost(String postId, String newContent) {
        long id = Long.parseLong(postId);
        if (this.postRepository.isContains(id)) {
            Post post = this.postRepository.get(id);
            post.setContent(newContent);
            this.postRepository.change(post);
        }
        return this.postRepository.get(id).getContent().equals(newContent);
    }

    public boolean removePost(String postId) {
        long id = Long.parseLong(postId);
        boolean isPostRemoved = this.postRepository.remove(id);
        return isPostRemoved;
    }

    public boolean removeAllPostByUser(String userId) {
        long id = Long.parseLong(userId);
        boolean isAllUserPostsRemoved = this.postRepository.removePostsByUserId(id);
        return isAllUserPostsRemoved;
    }

    public boolean removeAllPosts() {
        boolean isAllPostsRemoved = this.postRepository.removeAll();
        return isAllPostsRemoved;
    }

    private long getLastPostId() {
        Optional<Long> maxPostId = getAllPostsList().stream().map(Post::getId).max(Long::compareTo);
        return maxPostId.isPresent() ? maxPostId.get() : 0;
    }
}
