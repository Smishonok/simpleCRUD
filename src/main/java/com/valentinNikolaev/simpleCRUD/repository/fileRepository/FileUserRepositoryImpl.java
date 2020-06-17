package com.valentinNikolaev.simpleCRUD.repository.fileRepository;

import com.valentinNikolaev.simpleCRUD.models.Post;
import com.valentinNikolaev.simpleCRUD.models.Region;
import com.valentinNikolaev.simpleCRUD.models.Role;
import com.valentinNikolaev.simpleCRUD.models.User;
import com.valentinNikolaev.simpleCRUD.repository.PostRepository;
import com.valentinNikolaev.simpleCRUD.repository.RegionRepository;
import com.valentinNikolaev.simpleCRUD.repository.RepositoryManager;
import com.valentinNikolaev.simpleCRUD.repository.UserRepository;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserRepositoryImpl implements UserRepository {

    public static Logger log = Logger.getLogger(FileUserRepositoryImpl.class);

    private Path             usersRepositoryPath;
    private PostRepository   postRepository;
    private RegionRepository regionRepository;

    //User`s fields names for parsing
    private final String USER_ID         = "User`s id:";
    private final String USER_FIRST_NAME = "User`s first name:";
    private final String USER_LAST_NAME  = "User`s last name:";
    private final String REGION_ID       = "User`s region Id:";
    private final String USERS_ROLE      = "User`s role:";

    public FileUserRepositoryImpl(Path repositoryRootPath) throws ClassNotFoundException {
        this.usersRepositoryPath = repositoryRootPath.resolve("userRepository.txt");
        createUserRepository();
        this.postRepository   = RepositoryManager.getRepositoryFactory().getPostRepository(this);
        this.regionRepository = RepositoryManager.getRepositoryFactory().getRegionRepository();
    }

    public FileUserRepositoryImpl(Path repositoryRootPath, PostRepository postRepository) throws ClassNotFoundException {
        this.usersRepositoryPath = repositoryRootPath.resolve("userRepository.txt");
        createUserRepository();
        this.postRepository   = postRepository;
        this.regionRepository = RepositoryManager.getRepositoryFactory().getRegionRepository();
    }

    private void createUserRepository() {
        log.debug("Checking is repository file with users data exists.");
        if (! Files.exists(this.usersRepositoryPath)) {
            log.debug(
                    "Users repository does not exist, started the creation of a repository file.");
            try {
                Files.createFile(this.usersRepositoryPath);
            } catch (IOException e) {
                log.debug("File \"userRepository.txt\" can`t be created: " + e.getMessage());
            }
            log.debug("The repository file with users data created successfully");
        } else {
            log.debug("The repository file with users data exists.");
        }
    }

    @Override
    public User add(User user) {
        try (Writer writer = Files.newBufferedWriter(usersRepositoryPath,
                                                     Charset.forName("UTF-8"),
                                                     StandardOpenOption.WRITE,
                                                     StandardOpenOption.APPEND)) {
            writer.write(this.createStringWithUserData(user));
            writer.flush();
        } catch (IOException e) {
            log.error("Can`t write the user`s data into repository file: " + e.getMessage());
        }

        return get(user.getId());
    }

    @Override
    public User get(Long id) throws IllegalArgumentException {
        Optional<User> user = Optional.empty();
        try {
            user = Files.lines(usersRepositoryPath, Charset.forName("UTF-8")).filter(
                    userData->this.parseUserId(userData) == id).map(this::parseUser).findFirst();
        } catch (IOException e) {
            log.error("Can`t read repository file with users data: " + e.getMessage());
        }

        if (! user.isEmpty()) {
            return user.get();
        } else {
            throw new IllegalArgumentException(
                    "User with id '" + id + "' does not contain in database.");
        }
    }

    @Override
    public User change(User user) {
        List<User> usersList = getAll();

        int indexOfUserInList = - 1;
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == user.getId()) {
                indexOfUserInList = i;
            }
        }

        if (indexOfUserInList == - 1) {
            throw new IllegalArgumentException(
                    "User with ID: " + user.getId() + " is not contains in repository.");
        }

        usersList.set(indexOfUserInList, user);

        rewriteInRepository(usersList.stream().map(this::createStringWithUserData)
                                     .collect(Collectors.toList()));
        return get(user.getId());
    }

    @Override
    public boolean remove(Long id) {
        List<String> usersList = getUsersListExcludeUserWith(id);
        rewriteInRepository(usersList);
        return ! isContains(id);
    }

    @Override
    public List<User> getAll() {
        return getUsersListExcludeUserWith(0).stream().map(this::parseUser).collect(
                Collectors.toList());
    }

    @Override
    public boolean removeAll() {
        if (Files.exists(usersRepositoryPath)) {
            try {
                Files.delete(usersRepositoryPath);
            } catch (IOException e) {
                log.error("The repository file can`t be deleted: " + e.getMessage());
            }
        }

        try {
            Files.createFile(usersRepositoryPath);
        } catch (IOException e) {
            log.error("The repository file can`t be created: " + e.getMessage());
        }

        boolean isEmpty = false;
        try (BufferedReader reader = Files.newBufferedReader(usersRepositoryPath)) {
            isEmpty = reader.read() == - 1;
        } catch (IOException e) {
            log.error("Repository file can`t be read: " + e.getMessage());
        }
        return isEmpty;
    }

    @Override
    public boolean isContains(Long id) {
        boolean isExist = false;
        try {
            isExist = Files.lines(usersRepositoryPath).anyMatch(
                    userData->this.parseUserId(userData) == id);
        } catch (IOException e) {
            log.error("Users`s repository file can`t be opened and read: " + e.getMessage());
        }
        return isExist;
    }

    private void rewriteInRepository(List<String> usersList) {
        try (BufferedWriter writer = Files.newBufferedWriter(usersRepositoryPath,
                                                             Charset.forName("UTF-8"),
                                                             StandardOpenOption.WRITE)) {
            for (String userData : usersList) {
                writer.write(userData);
            }
        } catch (IOException e) {
            log.error("Can`t write in repository file with users data: " + e.getMessage());
        }
    }

    private List<String> getUsersListExcludeUserWith(long id) {
        List<String> usersList = new ArrayList<>();
        try {
            usersList = Files.lines(usersRepositoryPath, Charset.forName("UTF-8")).filter(
                    usersData->this.parseUserId(usersData) != id).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with users data: " + e.getMessage());
        }
        return usersList;
    }

    private String createStringWithUserData(User user) {
        return USER_ID + user.getId() + ";" + USER_FIRST_NAME + user.getFirstName() + ";" +
                USER_LAST_NAME + user.getLastName() + ";" + REGION_ID + user.getRegion().getId() +
                ";" + USERS_ROLE + user.getRole().toString() + ";\n";
    }

    private long parseUserId(String userData) {
        Scanner scanner = getScanner(userData);

        scanner.findInLine(USER_ID);
        if (scanner.hasNextLong()) {
            return scanner.nextLong();
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the user Id.");
        }
    }

    private String parseUserFirstName(String userData) {
        Scanner scanner = getScanner(userData);

        scanner.findInLine(USER_FIRST_NAME);
        return scanner.next();
    }

    private String parseUserLastName(String userData) {
        Scanner scanner = getScanner(userData);

        scanner.findInLine(USER_LAST_NAME);
        return scanner.next();
    }

    private Region parseRegion(String userData) {
        Scanner scanner = getScanner(userData);

        scanner.findInLine(REGION_ID);
        if (scanner.hasNextLong()) {
            return regionRepository.get(scanner.nextLong());
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the region Id.");
        }
    }

    private Role parseRole(String userData) {
        Scanner scanner = getScanner(userData);

        scanner.findInLine(USERS_ROLE);
        if (scanner.hasNext()) {
            return Role.valueOf(scanner.next());
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the user role.");
        }
    }

    private User parseUser(String userData) {
        if (userData.isBlank() || userData.isEmpty()) {
            log.error("String with users`s data for parsing can`t be empty");
            throw new IllegalArgumentException(
                    "String with users`s data for parsing can`t be " + "empty");
        }

        long       id        = parseUserId(userData);
        String     firstName = parseUserFirstName(userData);
        String     lastName  = parseUserLastName(userData);
        Region     region    = parseRegion(userData);
        Role       role      = parseRole(userData);
        List<Post> posts     = this.postRepository.getPostsByUserId(id);

        return new User(id, firstName, lastName, region, role, posts);
    }

    private Scanner getScanner(String source) {
        Scanner scanner = new Scanner(source);
        scanner.useDelimiter(";");
        return scanner;
    }
}
