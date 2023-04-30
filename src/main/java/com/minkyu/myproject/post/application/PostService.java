package com.minkyu.myproject.post.application;

import com.minkyu.myproject.common.exception.NotFoundException;
import com.minkyu.myproject.post.application.searchType.SearchType;
import com.minkyu.myproject.post.domain.Post;
import com.minkyu.myproject.post.domain.repository.PostRepository;
import com.minkyu.myproject.post.presentation.dto.PostRequestDto;
import com.minkyu.myproject.post.presentation.dto.PostResponseDto;
import com.minkyu.myproject.post.presentation.dto.PostUpdateDto;
import com.minkyu.myproject.user.domain.User;
import com.minkyu.myproject.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long write(PostRequestDto postRequestDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        postRequestDto.setUser(user);

        Post post = postRequestDto.toEntity();

        postRepository.save(post);

        return (post.getId());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAll() {

        List<PostResponseDto> postResponseDtoList;

        try (Stream<Post> postStream = postRepository.findAll()) {
            postResponseDtoList = postStream
                    .map(PostResponseDto::fromEntity)
                    .collect(Collectors.toList());
        }

        return postResponseDtoList;
    }

    @Transactional(readOnly = true)
    public PostResponseDto findById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElseThrow(NotFoundException::new);

        return PostResponseDto.fromEntity(post);
    }

    @Transactional
    public void edit(Long id, PostUpdateDto postUpdateDto) {
        Optional<Post> optionalPost = postRepository.findById(id);

        optionalPost.ifPresentOrElse(
                post -> {
                    post.updatePost(postUpdateDto);
                },
                () -> {
                    throw new NotFoundException();
                }
        );
    }

    @Transactional
    public void delete(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);

        optionalPost.ifPresentOrElse(
                post -> {
                    postRepository.deleteById(id);
                },
                () -> {
                    throw new NotFoundException();
                }
        );
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> search(SearchType type, String keyword) {

        Stream<Post> postStream;

        switch (type) {
            case TITLE:
                postStream = postRepository.findByTitleContaining(keyword);
                break;
            case BODY:
                postStream = postRepository.findByBodyContaining(keyword);
                break;
            case AUTHOR:
                postStream = postRepository.findByUserId(keyword);
                break;
            default:
                throw new IllegalArgumentException();
        }

        try (postStream) {
            return postStream
                    .map(PostResponseDto::fromEntity)
                    .collect(Collectors.toList());
        }
    }
}
