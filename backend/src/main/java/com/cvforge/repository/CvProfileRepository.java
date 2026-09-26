package com.cvforge.repository;

import com.cvforge.domain.CvProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CvProfileRepository extends JpaRepository<CvProfile, UUID> {

    List<CvProfile> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    Optional<CvProfile> findByIdAndSessionId(UUID id, String sessionId);
}
