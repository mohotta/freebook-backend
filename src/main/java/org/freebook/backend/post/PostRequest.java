package org.freebook.backend.post;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.freebook.backend.user.User;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private String caption;
    private List<String> tags;
    private String imgUrl;
    private String imgId;
    private String location;

}
