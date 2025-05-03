package com.example.daffa.fleet_gps_tracking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.daffa.fleet_gps_tracking.model.GPSLog;
import com.example.daffa.fleet_gps_tracking.model.Vehicle;
import com.example.daffa.fleet_gps_tracking.repository.GPSLogRepository;

@Service
public class GPSLogService {

    private final GPSLogRepository gpsLogRepository;
    private final VehicleService vehicleService;

    @Autowired
    public GPSLogService(GPSLogRepository gpsLogRepository, VehicleService vehicleService) {
        this.gpsLogRepository = gpsLogRepository;
        this.vehicleService = vehicleService;
    }

    @Transactional
    public GPSLog saveGPSLog(Long vehicleId, Double latitude, Double longitude, Double speed) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        GPSLog gpsLog = new GPSLog(vehicleId, vehicle, latitude, longitude, speed, LocalDateTime.now());
        return gpsLogRepository.save(gpsLog);
    }

    public Optional<GPSLog> getLastLocation(Long vehicleId) {
        vehicleService.getVehicleById(vehicleId); // Ensure vehicle exists
        return gpsLogRepository.findTopByVehicleIdOrderByTimestampDesc(vehicleId);
    }

    public List<GPSLog> getHistory(Long vehicleId, LocalDateTime from, LocalDateTime to) {
        vehicleService.getVehicleById(vehicleId); // Ensure vehicle exists
        return gpsLogRepository.findByVehicleIdAndTimestampBetweenOrderByTimestampAsc(vehicleId, from, to);
    }
}