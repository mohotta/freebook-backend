package org.freebook.backend.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.freebook.backend.user.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "post")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    private UUID id;
    private String caption;
    private List<String> tags;
    private String imgUrl;
    private String imgId;
    private String location;
    @CreatedDate
    private LocalDateTime createdAt;
    private UUID creatorId;
    private List<UUID> likedList;

}
