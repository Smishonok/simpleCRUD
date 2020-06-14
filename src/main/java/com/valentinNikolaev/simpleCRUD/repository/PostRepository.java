package com.valentinNikolaev.simpleCRUD.repository;

import com.valentinNikolaev.simpleCRUD.models.Post;

import java.util.List;

public interface PostRepository extends GenericRepository<Post,Long> {

    List<Post> getPostsByUserId(Long userId);

    void removePostsByUserId(Long userId);


}
