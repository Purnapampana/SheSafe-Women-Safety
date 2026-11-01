package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.LiveLocation;

public interface LiveLocationRepository
        extends JpaRepository<LiveLocation, Long> {

    List<LiveLocation> findByUserIdOrderByRecordedAtAsc(Long userId);

}