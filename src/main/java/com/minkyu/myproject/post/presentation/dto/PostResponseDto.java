package com.minkyu.myproject.post.presentation.dto;

import com.minkyu.myproject.common.model.Id;
import com.minkyu.myproject.post.domain.Post;
import com.minkyu.myproject.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor()
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String body;


    private PostResponseDto(Long id, Id<User, Long> userId, String title, String body) {
        this.id = id;
        this.userId = userId.value();
        this.title = title;
        this.body = body;
    }

    public static PostResponseDto fromEntity(Post entity) {
        User user = entity.getUser();

        return new PostResponseDto(
                entity.getId(),
                user.getId(),
                entity.getTitle(),
                entity.getBody()
        );
    }
}
