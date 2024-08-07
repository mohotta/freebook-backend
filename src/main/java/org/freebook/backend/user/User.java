package org.freebook.backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.freebook.backend.post.Post;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private UUID id;
    private UUID accountId;
    private String username;
    private String name;
    private String email;
    private String bio;
    private String imgUrl;
    private String imgId;
    private List<UUID> likedPosts;
    private List<UUID> savedPosts;

}
