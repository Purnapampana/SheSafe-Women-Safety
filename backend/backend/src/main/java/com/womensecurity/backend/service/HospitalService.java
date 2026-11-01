package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.HospitalRequest;
import com.womensecurity.backend.entity.Hospital;
import com.womensecurity.backend.repository.HospitalRepository;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    // CREATE
    public String addHospital(HospitalRequest request) {

        Hospital hospital = new Hospital();

        hospital.setHospitalName(request.getHospitalName());
        hospital.setAddress(request.getAddress());
        hospital.setPhoneNumber(request.getPhoneNumber());
        hospital.setLatitude(request.getLatitude());
        hospital.setLongitude(request.getLongitude());
        hospital.setCity(request.getCity());
        hospital.setCreatedAt(LocalDateTime.now());

        hospitalRepository.save(hospital);

        return "Hospital Added Successfully";
    }

    // READ ALL
    public List<Hospital> getAllHospitals() {

        return hospitalRepository.findAll();

    }

    // READ BY CITY
    public List<Hospital> getHospitalsByCity(String city) {

        return hospitalRepository.findByCity(city);

    }

    // UPDATE
    public Hospital updateHospital(Long id, Hospital updatedHospital) {

        Optional<Hospital> hospital = hospitalRepository.findById(id);

        if (hospital.isPresent()) {

            Hospital existing = hospital.get();

            existing.setHospitalName(updatedHospital.getHospitalName());
            existing.setAddress(updatedHospital.getAddress());
            existing.setPhoneNumber(updatedHospital.getPhoneNumber());
            existing.setLatitude(updatedHospital.getLatitude());
            existing.setLongitude(updatedHospital.getLongitude());
            existing.setCity(updatedHospital.getCity());

            return hospitalRepository.save(existing);
        }

        return null;
    }

    // DELETE
    public String deleteHospital(Long id) {

        if (hospitalRepository.existsById(id)) {

            hospitalRepository.deleteById(id);

            return "Hospital Deleted Successfully";
        }

        return "Hospital Not Found";
    }
}