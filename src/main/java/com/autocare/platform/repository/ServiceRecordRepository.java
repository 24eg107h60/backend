package com.autocare.platform.repository;

import com.autocare.platform.model.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByVehicleIdOrderByServiceDateDesc(Long vehicleId);
    List<ServiceRecord> findByVehicleUserIdOrderByServiceDateDesc(Long userId);
    List<ServiceRecord> findAllByOrderByServiceDateDesc();

    @Query("SELECT r FROM ServiceRecord r WHERE r.vehicle.id = :vehicleId AND r.serviceType = :serviceType ORDER BY r.serviceDate DESC, r.mileageAtService DESC LIMIT 1")
    Optional<ServiceRecord> findLatestByVehicleAndType(@Param("vehicleId") Long vehicleId, @Param("serviceType") String serviceType);

    @Query("SELECT COALESCE(SUM(r.cost), 0.0) FROM ServiceRecord r")
    Double sumTotalMaintenanceCost();

    @Query("SELECT COALESCE(SUM(r.cost), 0.0) FROM ServiceRecord r WHERE r.vehicle.id = :vehicleId")
    Double sumCostByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT COALESCE(SUM(r.cost), 0.0) FROM ServiceRecord r WHERE r.vehicle.user.id = :userId")
    Double sumCostByUserId(@Param("userId") Long userId);
}
