package com.valentinNikolaev.simpleCRUD.repository;

public interface RepositoryFactory {

    UserRepository getUserRepository() throws ClassNotFoundException;

    PostRepository getPostRepository();

    RegionRepository getRegionRepository();

}
