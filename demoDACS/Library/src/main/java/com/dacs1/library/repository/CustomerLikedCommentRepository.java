package com.dacs1.library.repository;

import com.dacs1.library.model.CustomerLikedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerLikedCommentRepository extends JpaRepository<CustomerLikedComment, Long> {
}
