package com.valentinNikolaev.simpleCRUD.repository.fileRepository;

import com.valentinNikolaev.simpleCRUD.models.Post;
import com.valentinNikolaev.simpleCRUD.models.User;
import com.valentinNikolaev.simpleCRUD.repository.PostRepository;
import com.valentinNikolaev.simpleCRUD.repository.RepositoryManager;
import com.valentinNikolaev.simpleCRUD.repository.UserRepository;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FilePostRepositoryImpl implements PostRepository {

    static Logger log = Logger.getLogger(FilePostRepositoryImpl.class);

    private Path           postsRepositoryPath;
    private UserRepository userRepository;

    //Post`s fields names for parsing
    private final String POST_ID       = "Post`s id:";
    private final String USER_ID       = "User`s id:";
    private final String CREATION_DATE = "Post`s creation date:";
    private final String UPDATING_DATE = "Post`s updating date:";
    private final String POST_CONTENT  = "Post`s content:";

    public FilePostRepositoryImpl(Path repositoryRootPath) throws ClassNotFoundException {
        postsRepositoryPath = repositoryRootPath.resolve("postsRepository.txt");
        this.userRepository = RepositoryManager.getRepositoryFactory().getUserRepository(this);
        createPostsRepository();
    }

    public FilePostRepositoryImpl(Path repositoryRootPath, UserRepository userRepository)
    throws ClassNotFoundException {
        postsRepositoryPath = repositoryRootPath.resolve("postsRepository.txt");
        this.userRepository = userRepository;
        createPostsRepository();
    }

    private void createPostsRepository() {
        log.debug("The operation of checking existence of repository file is started.");
        if (! Files.exists(this.postsRepositoryPath)) {
            log.debug(
                    "Posts repository does not exist, started the creation of a repository file.");
            try {
                Files.createFile(this.postsRepositoryPath);
            } catch (IOException e) {
                log.debug("File \"postsRepository.txt\" can`t be created: " + e.getMessage());
            }
            log.debug("The repository file with posts data created successfully");
        } else {
            log.debug("The repository file with posts data exists.");
        }
    }

    @Override
    public Post add(Post post) {
        log.debug("The operation of adding the new post with id: " + post.getId() +
                  " in the repository is started.");
        try (Writer writer = Files.newBufferedWriter(postsRepositoryPath, Charset.forName("UTF-8"),
                                                     StandardOpenOption.WRITE,
                                                     StandardOpenOption.APPEND)) {
            writer.write(this.createStringWithPostData(post));
            writer.flush();
        } catch (IOException e) {
            log.error("Can`t write the post`s data into repository file: " + e.getMessage());
        }
        Post addedPost = this.get(post.getId());
        log.debug("The operation of adding the new post with id: " + addedPost.getId() +
                  " in the repository is started.");
        return addedPost;
    }

    @Override
    public Post get(Long postId) {
        log.debug("The operation of getting the post with id: " + postId +
                  " from the repository is " + "started.");
        Optional<Post> post = Optional.empty();
        try {
            post = Files.lines(postsRepositoryPath)
                        .filter(postData->parsePostId(postData) == postId)
                        .map(this::parsePost)
                        .findFirst();
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }

        if (post.isPresent()) {
            log.debug("The post wih id: " + post.get().getId() +
                      " was founded in repository and returned by the request.");
            return post.get();
        } else {
            throw new IllegalArgumentException(
                    "Post with id: " + postId + " does not contain in repository.");
        }
    }

    @Override
    public Post change(Post post) {
        log.debug("The operation of changing content of post with id: " + post.getId() +
                  "is started.");
        List<Post> postsList = getAll();

        int indexOfPostInPostList = - 1;
        for (int i = 0; i < postsList.size(); i++) {
            if (postsList.get(i).getId() == post.getId()) {
                indexOfPostInPostList = i;
            }
        }

        if (indexOfPostInPostList == - 1) {
            throw new IllegalArgumentException(
                    "Post with ID: " + post.getId() + " is not contains in repository.");
        }

        postsList.set(indexOfPostInPostList, post);
        rewriteInRepository(postsList.stream()
                                     .map(this::createStringWithPostData)
                                     .collect(Collectors.toList()));

        Post changedPost = this.get(post.getId());
        log.debug("Post with id: " + changedPost.getId() + "was changed.");
        return changedPost;
    }

    @Override
    public boolean remove(Long postId) {
        log.debug("The operation of removing post with id: " + postId +
                  " from repository is started.");
        List<String> postsList = getPostsListExcludePostWithId(postId);
        rewriteInRepository(postsList);

        boolean isRemoved = ! this.isContains(postId);
        log.debug("The operation was ended with status: " + isRemoved);
        return isRemoved;
    }

    @Override
    public List<Post> getAll() {
        log.debug("The operation of getting the list with all posts from repository is started.");
        List<Post> postsList = getPostsListExcludePostWithId(0).stream()
                                                               .map(this::parsePost)
                                                               .collect(Collectors.toList());
        log.debug(
                "The operation was ended. The size of returned posts list is: " + postsList.size());
        return postsList;
    }

    @Override
    public boolean removeAll() {
        log.debug("The operation of removing all posts from the repository is started.");
        if (Files.exists(postsRepositoryPath)) {
            try {
                Files.delete(postsRepositoryPath);
            } catch (IOException e) {
                log.error("The repository file can`t be deleted: " + e.getMessage());
            }
        }

        try {
            Files.createFile(postsRepositoryPath);
        } catch (IOException e) {
            log.error("The repository file can`t be created: " + e.getMessage());
        }

        boolean isEmpty = false;
        try (BufferedReader reader = Files.newBufferedReader(this.postsRepositoryPath)) {
            isEmpty = reader.read() == - 1;
        } catch (IOException e) {
            log.error("Repository file can`t be read: " + e.getMessage());
        }
        log.debug("The operation was ended with status: " + isEmpty);
        return isEmpty;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        log.debug("The operation of getting list of posts mad by the user with id: " + userId +
                  "is started.");
        List<Post> userPostsList = new ArrayList<>();
        try {
            userPostsList = Files.lines(postsRepositoryPath)
                                 .filter(postData->parseUserId(postData) == userId)
                                 .map(this::parsePost)
                                 .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        log.debug("The operation was ended. The size of list with users`s posts is: " +
                  userPostsList.size());
        return userPostsList;
    }

    @Override
    public boolean removePostsByUserId(Long userId) {
        log.debug("The operation of removing posts, which created by user with id: " + userId +
                  ", is started.");
        List<String> postsList = getPostsListWithOutCreatedByUser(userId);
        rewriteInRepository(postsList);

        boolean isUsersPostsRemoved = getPostsByUserId(userId).size() == 0;
        log.debug("The operation was ended with status: " + isUsersPostsRemoved);
        return isUsersPostsRemoved;
    }

    @Override
    public boolean isContains(Long postId) {
        log.debug("The operation of checking existence in repository of post with id: " + postId +
                  " " + "is started.");
        boolean isExists = false;
        try {
            isExists = Files.lines(postsRepositoryPath)
                            .anyMatch(postData->parsePostId(postData) == postId);
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        log.debug("The operation was ended. The result of checking is: " + isExists);
        return isExists;
    }

    private void rewriteInRepository(List<String> postsList) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(postsRepositoryPath,
                                                            Charset.forName("UTF-8"),
                                                            StandardOpenOption.WRITE,
                                                            StandardOpenOption.TRUNCATE_EXISTING);
            for (String post : postsList) {
                writer.write(post);
                writer.flush();
            }
        } catch (IOException e) {
            log.error("Can`t write the post`s data into repository file: " + e.getMessage());
        }
    }

    private List<String> getPostsListExcludePostWithId(long id) {
        List<String> postsList = new ArrayList<>();
        try {
            postsList = Files.lines(postsRepositoryPath)
                             .filter(postData->parsePostId(postData) != id)
                             .map(post->post + "\n")
                             .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        return postsList;
    }

    private List<String> getPostsListWithOutCreatedByUser(long userId) {
        List<String> postsList = new ArrayList<>();
        try {
            postsList = Files.lines(postsRepositoryPath)
                             .filter(postData->parseUserId(postData) != userId)
                             .map(post->post + "\n")
                             .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        return postsList;
    }

    private String createStringWithPostData(Post post) {
        long   postId  = post.getId();
        long   userId  = post.getUserId();
        String content = post.getContent();

        long creationTimeInEpochSeconds = post.getDateOfCreation().toEpochSecond(ZoneOffset.UTC);
        long updatingTimeInEpochSeconds = post.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC);


        return POST_ID + postId + ";" + USER_ID + userId + ";" + CREATION_DATE +
               creationTimeInEpochSeconds + ";" + UPDATING_DATE + updatingTimeInEpochSeconds + ";" +
               POST_CONTENT + content + ";\n";
    }

    private long parsePostId(String postData) {
        Scanner scanner = new Scanner(postData);
        scanner.useDelimiter(";");

        scanner.findInLine(POST_ID);
        if (scanner.hasNextLong()) {
            return scanner.nextLong();
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the post Id.");
        }
    }

    private long parseUserId(String postData) {
        Scanner scanner = new Scanner(postData);
        scanner.useDelimiter(";");

        scanner.findInLine(USER_ID);
        if (scanner.hasNextLong()) {
            return scanner.nextLong();
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the user Id.");
        }
    }

    private LocalDateTime parseDateTime(String dateType, String postData) {
        Scanner scanner = new Scanner(postData);
        scanner.useDelimiter(";");

        scanner.findInLine(dateType);
        if (scanner.hasNextLong()) {
            return LocalDateTime.ofEpochSecond(scanner.nextLong(), 0, ZoneOffset.UTC);
        } else {
            throw new IllegalArgumentException("Invalid data. The string does not contain the" +
                                               dateType.toLowerCase().replace(":", "") + ".");
        }
    }

    private String parseContent(String postData) {
        Scanner scanner = new Scanner(postData);
        scanner.useDelimiter(";");

        scanner.findInLine(POST_CONTENT);
        String content = "";
        content += scanner.next();
        return content;
    }

    private Post parsePost(String postData) {
        if (postData.isBlank() || postData.isEmpty()) {
            throw new IllegalArgumentException(
                    "String with post`s data for parsing can`t be " + "empty");
        }

        long          postId       = parsePostId(postData);
        long          userId       = parseUserId(postData);
        LocalDateTime creationDate = parseDateTime(CREATION_DATE, postData);
        LocalDateTime updatingDate = parseDateTime(UPDATING_DATE, postData);
        String        content      = parseContent(postData);

        return new Post(postId, userId, content, creationDate, updatingDate);
    }
}
