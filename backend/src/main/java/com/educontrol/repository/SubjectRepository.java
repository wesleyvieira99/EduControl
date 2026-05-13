package com.educontrol.repository;

import com.educontrol.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByOrderByCreatedAtAsc();
    boolean existsByNameIgnoreCase(String name);
}
