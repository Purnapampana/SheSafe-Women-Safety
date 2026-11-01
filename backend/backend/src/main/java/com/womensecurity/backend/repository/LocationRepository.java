package com.womensecurity.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUserId(Long userId);

    Optional<Location> findFirstByUserIdOrderByUpdatedAtDesc(Long userId);
    List<Location> findByIncidentId(Long incidentId);

}