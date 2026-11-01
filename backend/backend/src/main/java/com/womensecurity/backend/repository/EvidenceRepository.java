package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.Evidence;

public interface EvidenceRepository
        extends JpaRepository<Evidence, Long> {

    List<Evidence> findByUserId(Long userId);
}