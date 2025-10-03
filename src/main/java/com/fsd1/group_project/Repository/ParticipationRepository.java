package com.fsd1.group_project.Repository;

import com.fsd1.group_project.Entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Participation findByUsername(String username);
    boolean existsByUsername(String username);
}
