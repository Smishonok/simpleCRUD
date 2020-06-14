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

    private Path repositoryRootPath;

    public FileRepositoryFactory() {
        getFilesRepositoryPath();
        createRepositoryDirectory();
    }

    @Override
    public UserRepository getUserRepository() throws ClassNotFoundException {
        return new FileUserRepositoryImpl(repositoryRootPath);
    }

    @Override
    public PostRepository getPostRepository() {
        return new FilePostRepositoryImpl(repositoryRootPath);
    }

    @Override
    public RegionRepository getRegionRepository() {
        return new FileRegionRepositoryImpl(repositoryRootPath);
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
