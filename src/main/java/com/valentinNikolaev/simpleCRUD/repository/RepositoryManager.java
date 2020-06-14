package com.valentinNikolaev.simpleCRUD.repository;

import com.valentinNikolaev.simpleCRUD.repository.FileRepository.FileRepositoryFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class RepositoryManager {

    static Logger log = Logger.getLogger(RepositoryManager.class.getName());

    private static final String DEFAULT_REPOSITORY_FACTORY = "JavaIORepository";

    public static RepositoryFactory getRepositoryFactory() throws ClassNotFoundException {
        log.debug("Start to choose the repository factory.");
        RepositoryFactory repositoryFactory;
        String            repositoryFactoryName = getRepositoryFactoryName();

        //Switch used in this case with a look into the future, if another repository
        //implementations will be added in this project.
        switch (repositoryFactoryName) {
            case "JavaIORepository":
                repositoryFactory = new FileRepositoryFactory();
                break;
            default:
                throw new ClassNotFoundException();
        }

        log.debug("Chosen repository factory is: "+ repositoryFactory.getClass().getName());
        return repositoryFactory;
    }

    private static String getRepositoryFactoryName() {
        log.debug("Choosing the repository factory, which will used.");
        String repositoryFactoryName;
        if (isConfigFileExists()) {
            log.debug("Getting the repository factory`s name from config file.");
            Properties config = getProperties();
            repositoryFactoryName = config.getProperty("RepositoryFactory", DEFAULT_REPOSITORY_FACTORY);
        } else {
            log.debug("Getting the default repository factory`s name.");
            repositoryFactoryName = DEFAULT_REPOSITORY_FACTORY;
        }
        log.debug("Chosen repository factory`s name is: "+ repositoryFactoryName);
        return repositoryFactoryName;
    }

    private static boolean isConfigFileExists() {
        log.debug("Checking is the properties file exists.");
        File config = new File("src/main/resources/config.properties");
        log.debug("Checking result is: "+ config.exists());
        return config.exists();
    }

    private static Properties getProperties() {
        Properties config = new Properties();

        try {
            log.debug("Start loading of property`s file");
            config.load(RepositoryManager.class.getClassLoader()
                                               .getResourceAsStream("config" + ".properties"));
            log.debug("Properties file loaded successfully. Config file size: " + config.size());
        } catch (FileNotFoundException e) {
            log.error("Properties`s file not found: " + e);
        } catch (IOException e) {
            log.error("IO file exception occurred: " + e);
        }

        return config;
    }


}
