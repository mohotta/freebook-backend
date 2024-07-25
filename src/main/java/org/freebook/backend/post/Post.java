package org.freebook.backend.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.freebook.backend.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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
    @DocumentReference
    private User creator;
    @DocumentReference
    private List<User> likedList;

}
