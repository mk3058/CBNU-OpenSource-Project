package com.minkyu.myproject.post.domain.repository;

import com.minkyu.myproject.post.domain.Post;

import java.util.Optional;
import java.util.stream.Stream;

public interface PostRepository {
    void save(Post post);

    Stream<Post> findAll();

    Optional<Post> findById(Long id);

    void deleteById(Long id);

    Stream<Post> findByTitleContaining(String keyword);

    Stream<Post> findByBodyContaining(String keyword);

    Stream<Post> findByUserId(String keyword);
}
