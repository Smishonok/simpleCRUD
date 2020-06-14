package com.valentinNikolaev.simpleCRUD.repository.FileRepository;

import com.valentinNikolaev.simpleCRUD.models.Post;
import com.valentinNikolaev.simpleCRUD.repository.PostRepository;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
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

    private Path postsRepositoryPath;

    //Post`s fields names for parsing
    private final String POST_ID       = "Post`s id:";
    private final String USER_ID       = "User`s id:";
    private final String CREATION_DATE = "Post`s creation date:";
    private final String UPDATING_DATE = "Post`s updating date:";
    private final String POST_CONTENT  = "Post`s content:";

    public FilePostRepositoryImpl(Path repositoryRootPath) {
        postsRepositoryPath = repositoryRootPath.resolve("postsRepository.txt");
        createPostsRepository();
    }

    private void createPostsRepository() {
        log.debug("Checking is repository file with posts data exists.");
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
    public void add(Post post) {
        try (BufferedWriter writer = Files.newBufferedWriter(postsRepositoryPath,
                                                             Charset.forName("UTF-8"),
                                                             StandardOpenOption.WRITE,
                                                             StandardOpenOption.APPEND)) {
            writer.write(this.prepareDataForSerialisation(post));
        } catch (IOException e) {
            log.error("Can`t write the post`s data into repository file: " + e.getMessage());
        }
    }

    @Override
    public Post get(Long postId) {
        Optional<Post> post = Optional.empty();
        try {
            post = Files.lines(postsRepositoryPath).filter(postData->parsePostId(postData) == postId)
                        .map(this::parsePost).findFirst();
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }

        if (! post.isEmpty()) {
            return post.get();
        } else {
            throw new IllegalArgumentException(
                    "Post with id: " + postId + " does not contain in repository.");
        }
    }

    @Override
    public void change(Post post) {
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

        rewriteInRepository(postsList.stream().map(this::prepareDataForSerialisation).collect(
                Collectors.toList()));
    }

    @Override
    public void remove(Long postId) {
        List<String> postsList = getPostsListExcludePostWithId(postId);
        rewriteInRepository(postsList);
    }

    @Override
    public List<Post> getAll() {
        return getPostsListExcludePostWithId(0).stream().map(this::parsePost).collect(
                Collectors.toList());
    }

    @Override
    public void removeAll() {
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
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        List<Post> userPostsList = new ArrayList<>();
        try {
            userPostsList = Files.lines(postsRepositoryPath).filter(
                    postData->parseUserId(postData) == userId).map(this::parsePost).collect(
                    Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        return userPostsList;
    }

    @Override
    public void removePostsByUserId(Long userId) {
        List<String> postsList = getPostsListWithOutCreatedByUser(userId);
        rewriteInRepository(postsList);
    }

    @Override
    public boolean contains(Long postId) {
        boolean isExists = false;
        try {
            isExists = Files.lines(postsRepositoryPath).anyMatch(
                    postData->parsePostId(postData) == postId);
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        return isExists;
    }

    private void rewriteInRepository(List<String> postsList) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(postsRepositoryPath,
                                                            Charset.forName("UTF-8"),
                                                            StandardOpenOption.WRITE);
            for (String post : postsList) {
                writer.write(post);
            }
        } catch (IOException e) {
            log.error("Can`t write the post`s data into repository file: " + e.getMessage());
        }
    }

    private List<String> getPostsListExcludePostWithId(long id) {
        List<String> postsList = new ArrayList<>();
        try {
            postsList = Files.lines(postsRepositoryPath).filter(
                    postData->parsePostId(postData) != id).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        return postsList;
    }

    private List<String> getPostsListWithOutCreatedByUser(long userId) {
        List<String> postsList = new ArrayList<>();
        try {
            postsList = Files.lines(postsRepositoryPath).filter(
                    postData->parseUserId(postData) != userId).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with posts data: " + e.getMessage());
        }
        return postsList;
    }

    private String prepareDataForSerialisation(Post post) {
        long   postId  = post.getId();
        long   userId  = post.getUserId();
        String content = post.getContent();

        long creationTimeInEpochSeconds = post.getCreatingDateAndTime().toEpochSecond(
                ZoneOffset.UTC);
        long updatingTimeInEpochSeconds = post.getUpdatingDateAndTime().toEpochSecond(
                ZoneOffset.UTC);


        return POST_ID + postId + ";" + USER_ID + userId + ";" + CREATION_DATE +
                creationTimeInEpochSeconds + ";" + UPDATING_DATE + updatingTimeInEpochSeconds +
                ";" + POST_CONTENT + content + ";\n";
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
                                                       dateType.toLowerCase().replace(":", "") +
                                                       ".");
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
            throw new IllegalArgumentException("String with post`s data for parsing can`t be " +
                                                       "empty");
        }

        long          postId       = parsePostId(postData);
        long          userId       = parseUserId(postData);
        LocalDateTime creationDate = parseDateTime(CREATION_DATE, postData);
        LocalDateTime updatingDate = parseDateTime(UPDATING_DATE, postData);
        String        content      = parseContent(postData);

        return new Post(postId, userId, content, creationDate, updatingDate);
    }
}
