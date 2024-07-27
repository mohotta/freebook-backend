package org.freebook.backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    @Id
    private String name;
    private String email;
    private String bio;
    private String imgUrl;
    private String imgId;

}
