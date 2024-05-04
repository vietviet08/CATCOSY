package com.dacs1.library.repository;

import com.dacs1.library.model.Comment;
import com.dacs1.library.model.CommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
