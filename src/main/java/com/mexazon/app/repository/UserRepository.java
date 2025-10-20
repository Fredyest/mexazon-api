package com.mexazon.app.repository;

import com.mexazon.app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // --- Basic lookups ---
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailOrPhone(String email, String phone);

    // --- Search/pagination ---
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Slice<User> findByUserTypeIgnoreCase(String userType, Pageable pageable);

    // --- Eager fetch posts only when you really need them ---
    @EntityGraph(attributePaths = "postsAuthored")
    @Query("select u from User u where u.userId = :id")
    Optional<User> findWithPosts(@Param("id") Long id);

    // --- Lightweight projection for public listing (no password) ---
    interface PublicUserView {
        Long getUserId();
        String getName();
        String getAvatar();
        String getUserType();
        LocalDateTime getCreatedAt();
    }

    Page<PublicUserView> findByNameStartsWithIgnoreCase(String name, Pageable pageable);

    // --- Targeted update (avoids fetching the entity first) ---
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.avatar = :avatar where u.userId = :id")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    // (Optional) Pessimistic lock if you have concurrent edits
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.userId = :id")
    Optional<User> lockById(@Param("id") Long id);
    	
}
