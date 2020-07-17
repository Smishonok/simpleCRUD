package com.valentinNikolaev.simpleCRUD.repository.fileRepository;

import com.valentinNikolaev.simpleCRUD.models.Post;
import com.valentinNikolaev.simpleCRUD.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilePostRepositoryImplTest {

    private UserRepository         mockitoUserRepository = Mockito.mock(UserRepository.class);
    private FilePostRepositoryImpl filePostRepository;

    private static Path   testsRootPath      = Path.of("src/test/resources/tests");
    private        String repositoryFileName = "postsRepository.txt";
    private        Path   testFolderPath;

    //Post`s fields names for parsing
    private final String POST_ID       = "Post`s id:";
    private final String USER_ID       = "User`s id:";
    private final String CREATION_DATE = "Post`s creation date:";
    private final String UPDATING_DATE = "Post`s updating date:";
    private final String POST_CONTENT  = "Post`s content:";

    private long epochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    private LocalDateTime dateTimeForPostConstructor = LocalDateTime.ofEpochSecond(epochSeconds, 0,
                                                                                   ZoneOffset.UTC);


    @Rule public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void createTestFolder() throws IOException {
        if (! Files.exists(testsRootPath)) {
            try {
                Files.createDirectory(testsRootPath);
            } catch (IOException e) {
                System.err.println("Folder was not created: " + e.getMessage());
                throw new IOException("Test folder can`t be created.", e);
            }
        }
    }

    @Before
    public void setUp() {
        try {
            testFolderPath     = Files.createTempDirectory(testsRootPath, "test");
            filePostRepository = new FilePostRepositoryImpl(testFolderPath, mockitoUserRepository);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenAddPostThenReturnTheSamePostFromRepository() {
        Post postForAdding = new Post(1L, 1L, "This post for tests", dateTimeForPostConstructor,
                                      dateTimeForPostConstructor);

        Post postFromRepository = filePostRepository.add(postForAdding);
        Assert.assertEquals(postForAdding, postFromRepository);
    }

    @Test
    public void whenAddPostThenPostExistsInTheRepository() {
        long postId        = 456;
        Post postForAdding = new Post(postId, 1L, "This post for tests");

        filePostRepository.add(postForAdding);
        Assert.assertTrue(filePostRepository.isContains(postId));
    }

    @Test
    public void whenAddPostThenPostWritsIntoTheRepository() {
        long postId = 458;
        Post postForAdding = new Post(postId, 1L, "This post for tests", dateTimeForPostConstructor,
                                      dateTimeForPostConstructor);
        String postForDB =
                POST_ID + postForAdding.getId() + ";" + USER_ID + postForAdding.getUserId() + ";" +
                CREATION_DATE + postForAdding.getDateOfCreation().toEpochSecond(ZoneOffset.UTC) +
                ";" + UPDATING_DATE +
                postForAdding.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC) + ";" +
                POST_CONTENT + postForAdding.getContent() + ";\n";

        filePostRepository.add(postForAdding);

        String dataFromRepository = "";
        Path   repositoryFilePath = testFolderPath.resolve(repositoryFileName);
        try (BufferedInputStream reader = new BufferedInputStream(
                Files.newInputStream(repositoryFilePath, StandardOpenOption.READ))) {
            dataFromRepository = new String(reader.readAllBytes());
        } catch (IOException e) {

        }

        Assert.assertEquals(postForDB, dataFromRepository);
    }

    @Test
    public void whenGetPostWhichIsNotExistsThenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        filePostRepository.get(1L);
    }

    @Test
    public void whenGetPostByPostIdWhichExistInRepositoryThenReturnPost() {
        long postId = 456;
        Post testPost = new Post(postId, 1L, "TestPost", dateTimeForPostConstructor,
                                 dateTimeForPostConstructor);
        String postForDB = POST_ID + testPost.getId() + ";" + USER_ID + testPost.getUserId() + ";" +
                           CREATION_DATE +
                           testPost.getDateOfCreation().toEpochSecond(ZoneOffset.UTC) + ";" +
                           UPDATING_DATE +
                           testPost.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC) + ";" +
                           POST_CONTENT + testPost.getContent() + ";\n";

        Path repositoryFilePath = testFolderPath.resolve(repositoryFileName);

        try (BufferedWriter writer = new BufferedWriter(
                Files.newBufferedWriter(repositoryFilePath, StandardOpenOption.WRITE))) {
            writer.write(postForDB);
            writer.flush();
        } catch (IOException e) {

        }

        Post postFromRepository = filePostRepository.get(postId);
        Assert.assertEquals(testPost, postFromRepository);
    }

    @Test
    public void whenChangePostContentThenChangingPostUpdateDate() throws InterruptedException {
        Post post                             = new Post(1L, 2L, "Content for changing");
        Post postFromRepositoryBeforeChanging = filePostRepository.add(post);

        TimeUnit.SECONDS.sleep(1);
        post.setContent("Changed content");
        Post postFromRepositoryAfterChanging = filePostRepository.change(post);

        Assert.assertNotEquals(postFromRepositoryBeforeChanging.getDateOfLastUpdate(),
                               postFromRepositoryAfterChanging.getDateOfLastUpdate());
    }

    @Test
    public void whenTryChangePostWhichIsNotExistsInRepositoryThenThrowIllegalArgumentEx() {
        Post existsPost    = new Post(1L, 2L, "Some content");
        Post notExistsPost = new Post(2L, 2L, "Some content");

        filePostRepository.add(existsPost);
        notExistsPost.setContent("New content");

        thrown.expect(IllegalArgumentException.class);
        filePostRepository.change(notExistsPost);
    }

    @Test
    public void whenChangePostContentThenChangePostContentInRepository() {
        Post post1 = new Post(345L, 1L, "New comment");
        Post post2 = new Post(346L, 2L, "Post for changing");
        Post post3 = new Post(358L, 2L, "Some another post");

        Stream.of(post1, post2, post3).forEach(filePostRepository::add);

        String changedContent = "Changed content";
        post2.setContent(changedContent);
        filePostRepository.change(post2);

        Assert.assertEquals(changedContent, filePostRepository.get(post2.getId()).getContent());
    }

    @Test
    public void whenRemovePostThenPostRemovedFromRepository() {
        Post post1 = new Post(345L, 1L, "New comment");
        Post post2 = new Post(346L, 2L, "Post for changing");
        Post post3 = new Post(358L, 2L, "Some another post");

        Stream.of(post1, post2, post3).forEach(filePostRepository::add);

        filePostRepository.remove(post2.getId());

        MatcherAssert.assertThat(filePostRepository.getAll().size(), Is.is(2));
    }

    @Test
    public void whenGetAllPostThenWillBeReturnedListSizeEqualsAddedPostsNumber() {
        Post post1 = new Post(345L, 1L, "New comment");
        Post post2 = new Post(346L, 2L, "Post for changing");
        Post post3 = new Post(358L, 2L, "Some another post");

        Stream.of(post1, post2, post3).forEach(filePostRepository::add);

        Assert.assertEquals(filePostRepository.getAll().size(), 3);
    }

    @Test
    public void whenRemoveAllPostsThenWillBeReturnedEmptyListWithPosts() {
        Post post1 = new Post(345L, 1L, "New comment");
        Post post2 = new Post(346L, 2L, "Post for changing");
        Post post3 = new Post(358L, 2L, "Some another post");

        Stream.of(post1, post2, post3).forEach(filePostRepository::add);

        filePostRepository.removeAll();

        MatcherAssert.assertThat(filePostRepository.getAll().size(), Is.is(0));
    }

    @Test
    public void whenGetPostsByUserIdThenWillBeReturnedListOfPostsOneUserId() {
        long firstUserId  = 1;
        long secondUserId = 2;

        Post post1 = new Post(345L, firstUserId, "New comment");
        Post post2 = new Post(346L, secondUserId, "Post for changing");
        Post post3 = new Post(358L, firstUserId, "Some another post");
        Post post4 = new Post(358L, secondUserId, "Some another post");

        Stream.of(post1, post2, post3, post4).forEach(filePostRepository::add);

        MatcherAssert.assertThat(filePostRepository.getPostsByUserId(secondUserId)
                                                   .stream()
                                                   .map(Post::getUserId)
                                                   .collect(Collectors.toList()),
                                 CoreMatchers.everyItem(CoreMatchers.is(secondUserId)));
    }

    @Test
    public void whenRemovePostsByUserIdThenWillBeReturnedListOfPostsWithOutPostsByUser() {
        long firstUserId  = 1;
        long secondUserId = 2;

        Post post1 = new Post(345L, firstUserId, "New comment");
        Post post2 = new Post(346L, secondUserId, "Post for changing");
        Post post3 = new Post(358L, firstUserId, "Some another post");
        Post post4 = new Post(359L, secondUserId, "sdfgsdfg!");
        Post post5 = new Post(360L, firstUserId, "Ssfdgbretrethdb sdegs serg d");
        Post post6 = new Post(361L, secondUserId, "Ssdfg ertg njuytmd!");

        Stream.of(post1, post2, post3, post4,post5,post6).forEach(filePostRepository::add);

        filePostRepository.getAll().forEach(System.out::println);
        filePostRepository.removePostsByUserId(secondUserId);
        //filePostRepository.getAll().forEach(System.out::println);

        MatcherAssert.assertThat(filePostRepository.getAll()
                                                   .stream()
                                                   .map(Post::getUserId)
                                                   .collect(Collectors.toList()),
                                 CoreMatchers.everyItem(CoreMatchers.is(firstUserId)));
    }

    @AfterClass
    public static void deleteTestDirectory() throws InterruptedException {
        removeFolder(testsRootPath);
    }

    private static void removeFolder(Path targetPath) {
        try {
            if (Files.isDirectory(targetPath)) {
                for (Path path : Files.newDirectoryStream(targetPath)) {
                    removeFolder(path);
                }
            }
            Files.delete(targetPath);
        } catch (IOException e) {
            System.err.println("Folder was not deleted: " + e.getMessage());
        }
    }

}


