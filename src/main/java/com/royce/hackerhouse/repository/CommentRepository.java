package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.CommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, String> {

    List<CommentEntity> findAllByEventId(String eventId);

    List<CommentEntity> findAllByEventIdOrderByTimestampDesc(String eventId);
}
