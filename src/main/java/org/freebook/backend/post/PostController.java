package org.freebook.backend.post;

import lombok.RequiredArgsConstructor;
import org.freebook.backend.configs.JwtService;
import org.freebook.backend.user.User;
import org.freebook.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private Logger log = Logger.getLogger(PostController.class.getName());

    @GetMapping("")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity getPostById(@PathVariable UUID postId) {
        log.info("Get post by id: " + postId);
        var postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.warning("Post not found: " + postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
        }
        log.info("Get post by id completed");
        return ResponseEntity.ok(postOptional.get());
    }

    @GetMapping("/search")
    public ResponseEntity searchPost(@RequestParam String query) {
        log.info("Search post: " + query);
        return ResponseEntity.ok(postRepository.findPostsBySearchQuery(query));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity searchPostByCreator(@PathVariable UUID creatorId) {
        log.info("Search post by creator id: " + creatorId);
        var postList = postRepository.getByCreatorId(creatorId);
        if (postList.isEmpty()) {
            log.warning("No posts for the user: " + creatorId + " & request completed");
            return ResponseEntity.ok(new ArrayList<>());
        }
        log.info("Request completed");
        return ResponseEntity.ok(postList.get());
    }

    @GetMapping("/recent")
    public ResponseEntity getRecentPosts(@RequestParam int page, @RequestParam int limit) {
        log.info("Get recent posts, page: " + page + " limit: " + limit);
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        log.info("Request completed");
        return ResponseEntity.ok(posts.getContent());
    }

    @PostMapping("/create")
    public ResponseEntity createPost(@RequestBody PostRequest request, @RequestHeader("Authorization") String token) {
        log.info("Creating new post: " + request);

        var user = userRepository.findByEmail(jwtService.extractUsername(token.substring(7)));
        if (user.isEmpty()) {
            log.warning("User not found!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found!");
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
                .createdAt(LocalDateTime.now())
                // this is sure available
                .creatorId(creator.getId())
                .likedList(new ArrayList<>())
                .build();
        post = postRepository.save(post);
        log.info("Post created: " + post);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post created");
    }

    @PutMapping("/{postId}")
    public ResponseEntity updatePost(@RequestBody PostRequest request, @PathVariable UUID postId) {
        log.info("Updating post: " + request);
        var post = postRepository.findById(postId);
        if (post.isEmpty()) {
            log.warning("Post not found: " + postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
        };
        Post oldPost = post.get();
        Post newPost = Post.builder()
                .id(oldPost.getId())
                .caption(request.getCaption())
                .tags(request.getTags())
                .imgUrl(request.getImgUrl())
                .imgId(request.getImgId())
                .location(request.getLocation())
                .createdAt(oldPost.getCreatedAt())
                .creatorId(oldPost.getCreatorId())
                .likedList(oldPost.getLikedList())
                .build();
        newPost = postRepository.save(newPost);
        log.info("Post updated: " + newPost);
        return ResponseEntity.ok("Post updated!");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@PathVariable UUID postId) {
        log.info("Deleting post: " + postId);
        postRepository.deleteById(postId);
        log.info("Post deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post deleted!");
    }

    @PatchMapping("/save")
    public ResponseEntity savePost(@RequestParam UUID postId, @RequestHeader("Authorization") String token) {
        log.info("Saving post: " + postId);

        var userOptional = userRepository.findByEmail(jwtService.extractUsername(token.substring(7)));
        if (userOptional.isEmpty()) {
            log.warning("User not found!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found!");
        }
        User user = userOptional.get();

        var postOptional = postRepository.findById(postId);

        if (postOptional.isEmpty()) {
            log.warning("Post not found: " + postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
        }

        List<UUID> savesList = user.getSavedPosts();

        if (savesList.contains(postOptional.get().getId())) {
            log.warning("post already saved, duplicate request");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request!");
        }

        savesList.add(postOptional.get().getId());

        user.setSavedPosts(savesList);
        userRepository.save(user);

        log.info("Post saved");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/unsave")
    public ResponseEntity unSavePost(@RequestParam UUID postId, @RequestHeader("Authorization") String token) {
        log.info("Unsaving post: " + postId);

        var userOptional = userRepository.findByEmail(jwtService.extractUsername(token.substring(7)));
        if (userOptional.isEmpty()) {
            log.warning("User not found!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found!");
        }
        User user = userOptional.get();

        var postOptional = postRepository.findById(postId);

        if (postOptional.isEmpty()) {
            log.warning("Post not found: " + postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
        }

        List<UUID> savesList = user.getSavedPosts();
        if (!savesList.contains(postOptional.get().getId())) {
            log.warning("post is not saved, conflicting request");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicting request!");
        }
        savesList.remove(postOptional.get().getId());

        user.setSavedPosts(savesList);
        userRepository.save(user);

        log.info("Post unsaved");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/like")
    public ResponseEntity likePost(@RequestParam UUID postId, @RequestHeader("Authorization") String token) {
        log.info("Liking post: " + postId);

        var userOptional = userRepository.findByEmail(jwtService.extractUsername(token.substring(7)));
        if (userOptional.isEmpty()) {
            log.warning("User not found!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found!");
        }
        User user = userOptional.get();

        var postOptional = postRepository.findById(postId);

        if (postOptional.isEmpty()) {
            log.warning("Post not found: " + postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
        }

        Post post = postOptional.get();

        // updating likedPosts list in user
        List<UUID> likesList = user.getLikedPosts();
        if (likesList.contains(post.getId())) {
            log.warning("post is already in user's liked posts list");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request!");
        }
        likesList.add(post.getId());
        user.setLikedPosts(likesList);

        // updating likedUsers list in post
        List<UUID> likedList = post.getLikedList();
        if (likedList.contains(user.getId())) {
            log.warning("post is already in post liked users list");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request!");
        }
        likedList.add(user.getId());
        post.setLikedList(likedList);

        userRepository.save(user);
        postRepository.save(post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/unlike")
    public ResponseEntity unLikePost(@RequestParam UUID postId, @RequestHeader("Authorization") String token) {
        log.info("Unliking post: " + postId);

        var userOptional = userRepository.findByEmail(jwtService.extractUsername(token.substring(7)));
        if (userOptional.isEmpty()) {
            log.warning("User not found!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found!");
        }
        User user = userOptional.get();

        var postOptional = postRepository.findById(postId);

        if (postOptional.isEmpty()) {
            log.warning("Post not found: " + postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
        }

        Post post = postOptional.get();

        // updating likedPosts list in user
        List<UUID> likesList = user.getLikedPosts();
        if (!likesList.contains(post.getId())) {
            log.warning("post is not in user's liked posts list");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicting request!");
        }
        likesList.remove(post.getId());
        user.setLikedPosts(likesList);

        // updating likedUsers list in post
        List<UUID> likedList = post.getLikedList();
        if (!likedList.contains(user.getId())) {
            log.warning("post is not in post liked users list");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicting request!");
        }
        likedList.remove(user.getId());
        post.setLikedList(likedList);

        userRepository.save(user);
        postRepository.save(post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
