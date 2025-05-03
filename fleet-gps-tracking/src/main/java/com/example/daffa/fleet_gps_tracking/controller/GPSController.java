package com.example.daffa.fleet_gps_tracking.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.daffa.fleet_gps_tracking.model.GPSLog;
import com.example.daffa.fleet_gps_tracking.service.GPSLogService;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/gps")
@Validated
public class GPSController {

    private final GPSLogService gpsLogService;

    @Autowired
    public GPSController(GPSLogService gpsLogService) {
        this.gpsLogService = gpsLogService;
    }

    @PostMapping
    public ResponseEntity<String> receiveGPSLog(
            @RequestParam("vehicleId") @NotNull Long vehicleId,
            @RequestParam("latitude") @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam("longitude") @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam("speed") @NotNull @Min(0) Double speed
    ) {
        gpsLogService.saveGPSLog(vehicleId, latitude, longitude, speed);
        return new ResponseEntity<>("GPS log received and processed", HttpStatus.CREATED);
    }

    @GetMapping("/vehicles/{id}/last-location")
    public ResponseEntity<GPSLog> getLastLocation(@PathVariable Long id) {
        Optional<GPSLog> lastLocation = gpsLogService.getLastLocation(id);
        return lastLocation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicles/{id}/history")
    public ResponseEntity<List<GPSLog>> getHistory(
            @PathVariable Long id,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<GPSLog> history = gpsLogService.getHistory(id, from, to);
        return ResponseEntity.ok(history);
    }
}