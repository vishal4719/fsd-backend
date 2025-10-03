package com.fsd1.group_project.Service;

import com.fsd1.group_project.Entity.Participation;
import com.fsd1.group_project.Entity.User;
import com.fsd1.group_project.Repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;

    public boolean hasUserSubmittedForm(String username) {
        return participationRepository.existsByUsername(username);
    }

    public Participation saveParticipation(Participation participation, User user) {
        participation.setUsername(user.getEmail()); // Using email as username
        participation.setUser(user);
        participation.setSubmittedAt(LocalDateTime.now());
        return participationRepository.save(participation);
    }

    public Optional<Participation> getParticipationByUser(String username) {
        return Optional.ofNullable(participationRepository.findByUsername(username));
    }
}