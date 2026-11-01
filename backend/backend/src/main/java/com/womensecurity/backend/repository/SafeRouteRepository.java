package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.SafeRoute;

public interface SafeRouteRepository extends JpaRepository<SafeRoute, Long> {

    List<SafeRoute> findByUserId(Long userId);

}