package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.PoliceStation;

public interface PoliceStationRepository extends JpaRepository<PoliceStation, Long> {

    List<PoliceStation> findByCity(String city);

}