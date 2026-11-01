package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.IncidentTimeline;

public interface IncidentTimelineRepository
        extends JpaRepository<IncidentTimeline, Long> {


    // =====================================================
    // ALL INCIDENTS OF USER
    // =====================================================

    List<IncidentTimeline> findByUserId(
            Long userId);


    // =====================================================
    // USER INCIDENTS - NEWEST FIRST
    // =====================================================

    List<IncidentTimeline>
    findByUserIdOrderBySosActivationTimeDesc(
            Long userId);


    // =====================================================
    // GET LATEST INCIDENT NUMBER FOR USER
    // =====================================================

    IncidentTimeline
    findTopByUserIdOrderByIncidentNumberDesc(
            Long userId);
}