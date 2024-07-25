package org.freebook.backend.post;


import lombok.RequiredArgsConstructor;
import org.freebook.backend.user.User;
import org.freebook.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private Logger log = Logger.getLogger(PostController.class.getName());

    @GetMapping("/list")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable UUID postId) {
        var postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(postOptional.get());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPost(@RequestParam String query) {
        return ResponseEntity.ok(postRepository.findPostsBySearchQuery(query));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody PostRequest request) {
        log.info("Creating new post: " + request);

        var user = userRepository.findById(request.getCreatorId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        User creator = user.get();
        log.info("User: " + user);

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .caption(request.getCaption())
                .tags(request.getTags())
                .imgUrl(request.getImgUrl())
                .imgId(request.getImgId())
                .location(request.getLocation())
                // this is sure available
                .creator(creator)
                .likedList(new ArrayList<User>())
                .build();
        post = postRepository.save(post);
        log.info("Post created: " + post);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<String> updatePost(@RequestBody PostRequest request, @PathVariable UUID postId) {
        log.info("Updating post: " + request);
        var post = postRepository.findById(postId);
        if (post.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Post oldPost = post.get();
        Post newPost = Post.builder()
                .id(oldPost.getId())
                .caption(request.getCaption())
                .tags(request.getTags())
                .imgUrl(request.getImgUrl())
                .imgId(request.getImgId())
                .location(request.getLocation())
                .creator(oldPost.getCreator())
                .likedList(oldPost.getLikedList())
                .build();
        newPost = postRepository.save(newPost);
        log.info("Post updated: " + newPost);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable UUID postId) {
        postRepository.deleteById(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
