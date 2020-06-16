package com.valentinNikolaev.simpleCRUD.repository;

public interface RepositoryFactory {

    UserRepository getUserRepository() throws ClassNotFoundException;

    UserRepository getUserRepository(PostRepository postRepository) throws ClassNotFoundException;

    PostRepository getPostRepository() throws ClassNotFoundException;

    PostRepository getPostRepository(UserRepository userRepository) throws ClassNotFoundException;

    RegionRepository getRegionRepository();

}
