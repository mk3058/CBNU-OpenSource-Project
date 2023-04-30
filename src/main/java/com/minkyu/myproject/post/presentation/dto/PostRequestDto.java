package com.minkyu.myproject.post.presentation.dto;

import com.minkyu.myproject.post.domain.Post;
import com.minkyu.myproject.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String body;
    private User user;

    @Builder
    public PostRequestDto(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Post toEntity() {
        Post post = Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();

        return post;
    }
}
