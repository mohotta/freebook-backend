package org.freebook.backend.post;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends MongoRepository<Post, UUID> {

    @Query("{ '$or': [ { 'caption': { '$regex': ?0, '$options': 'i' } }, { 'tags': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Post> findPostsBySearchQuery(String searchQuery);

    Optional<List<Post>> getByCreatorId(UUID creatorId);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

}

