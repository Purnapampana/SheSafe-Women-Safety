package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.HospitalRequest;
import com.womensecurity.backend.entity.Hospital;
import com.womensecurity.backend.service.HospitalService;

@RestController
@RequestMapping("/api/hospital")
@CrossOrigin("*")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    // CREATE
    @PostMapping("/add")
    public String addHospital(@RequestBody HospitalRequest request) {

        return hospitalService.addHospital(request);

    }

    // READ ALL
    @GetMapping("/all")
    public List<Hospital> getAllHospitals() {

        return hospitalService.getAllHospitals();

    }

    // READ BY CITY
    @GetMapping("/city/{city}")
    public List<Hospital> getHospitalsByCity(@PathVariable String city) {

        return hospitalService.getHospitalsByCity(city);

    }

    // UPDATE
    @PutMapping("/update/{id}")
    public Hospital updateHospital(
            @PathVariable Long id,
            @RequestBody Hospital hospital) {

        return hospitalService.updateHospital(id, hospital);

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteHospital(@PathVariable Long id) {

        return hospitalService.deleteHospital(id);

    }
}