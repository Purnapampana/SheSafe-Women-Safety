package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findByCity(String city);

}