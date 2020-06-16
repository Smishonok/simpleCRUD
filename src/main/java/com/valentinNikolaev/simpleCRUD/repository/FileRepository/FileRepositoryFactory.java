package com.valentinNikolaev.simpleCRUD.repository.FileRepository;

import com.valentinNikolaev.simpleCRUD.repository.PostRepository;
import com.valentinNikolaev.simpleCRUD.repository.RegionRepository;
import com.valentinNikolaev.simpleCRUD.repository.RepositoryFactory;
import com.valentinNikolaev.simpleCRUD.repository.UserRepository;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class FileRepositoryFactory implements RepositoryFactory {

    public static Logger log = Logger.getLogger(FileRepositoryFactory.class);

    private static FileRepositoryFactory fileRepositoryFactory;

    private Path                     repositoryRootPath;
    private FileUserRepositoryImpl   fileUserRepository;
    private FilePostRepositoryImpl   filePostRepository;
    private FileRegionRepositoryImpl fileRegionRepository;

    private FileRepositoryFactory() {
        getFilesRepositoryPath();
        createRepositoryDirectory();
    }

    public static FileRepositoryFactory getFactory() throws ClassNotFoundException {
        if (fileRepositoryFactory == null) {
            fileRepositoryFactory = new FileRepositoryFactory();
        }
        return fileRepositoryFactory;
    }

    @Override
    public UserRepository getUserRepository() throws ClassNotFoundException {
        if (this.fileUserRepository == null) {
            this.fileUserRepository = new FileUserRepositoryImpl(repositoryRootPath);
        }
        return this.fileUserRepository;
    }

    @Override
    public UserRepository getUserRepository(PostRepository postRepository) throws ClassNotFoundException {
        if (this.fileUserRepository == null) {
            this.fileUserRepository = new FileUserRepositoryImpl(repositoryRootPath,postRepository);
        }
        return this.fileUserRepository;
    }

    @Override
    public PostRepository getPostRepository() throws ClassNotFoundException {
        if (this.filePostRepository == null) {
            this.filePostRepository = new FilePostRepositoryImpl(repositoryRootPath);
        }
        return this.filePostRepository;
    }

    @Override
    public PostRepository getPostRepository(UserRepository userRepository) throws ClassNotFoundException {
        if (this.filePostRepository == null) {
            this.filePostRepository = new FilePostRepositoryImpl(repositoryRootPath,userRepository);
        }
        return this.filePostRepository;
    }

    @Override
    public RegionRepository getRegionRepository() {
        if (this.fileRegionRepository == null) {
            this.fileRegionRepository = new FileRegionRepositoryImpl(repositoryRootPath);
        }
        return this.fileRegionRepository;
    }

    private void getFilesRepositoryPath() {
        try {
            Path appRootPath = Path.of(ClassLoader.getSystemResource("").getPath()
                                                  .replaceFirst("/", ""));
            this.repositoryRootPath = appRootPath.resolve("FileRepository");
        } catch (InvalidPathException e) {
            log.error("Illegal path: " + e.getMessage());
        }
    }

    private void createRepositoryDirectory() {
        if (! Files.exists(repositoryRootPath)) {
            try {
                Files.createDirectory(repositoryRootPath);
            } catch (IOException e) {
                log.error("Directory can`t be creates: " + e.getMessage());
            }
        }
    }
}
