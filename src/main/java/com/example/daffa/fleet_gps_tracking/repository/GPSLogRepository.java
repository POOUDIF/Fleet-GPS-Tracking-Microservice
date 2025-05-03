package com.example.daffa.fleet_gps_tracking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.daffa.fleet_gps_tracking.model.GPSLog;

@Repository
public interface GPSLogRepository extends JpaRepository<GPSLog, Long> {

    Optional<GPSLog> findTopByVehicleIdOrderByTimestampDesc(Long vehicleId);

    List<GPSLog> findByVehicleIdAndTimestampBetweenOrderByTimestampAsc(
            @Param("vehicleId") Long vehicleId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}