package com.autocare.platform.service;

import com.autocare.platform.dto.CostReportDto;
import com.autocare.platform.model.ServiceRecord;
import com.autocare.platform.repository.ServiceRecordRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CostReportService {

    private final ServiceRecordRepository recordRepository;
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");

    public CostReportService(ServiceRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public CostReportDto generateUserReport(Long userId) {
        List<ServiceRecord> records = recordRepository.findByVehicleUserIdOrderByServiceDateDesc(userId);
        return aggregateReport(records);
    }

    public CostReportDto generateVehicleReport(Long vehicleId) {
        List<ServiceRecord> records = recordRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId);
        return aggregateReport(records);
    }

    public CostReportDto generateFleetReport() {
        List<ServiceRecord> records = recordRepository.findAllByOrderByServiceDateDesc();
        return aggregateReport(records);
    }

    private CostReportDto aggregateReport(List<ServiceRecord> records) {
        CostReportDto dto = new CostReportDto();

        if (records.isEmpty()) {
            dto.setTotalExpenditure(0.0);
            dto.setTotalServices(0);
            dto.setAverageCostPerService(0.0);
            dto.setRecentRecords(Collections.emptyList());
            return dto;
        }

        double total = 0.0;
        Map<String, Double> categoryMap = new LinkedHashMap<>();
        Map<String, Double> monthMap = new TreeMap<>();
        Map<String, Double> vehicleMap = new LinkedHashMap<>();

        for (ServiceRecord r : records) {
            double cost = r.getCost() != null ? r.getCost() : 0.0;
            total += cost;

            // By category / service type
            String cat = r.getServiceType() != null ? r.getServiceType() : "General";
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + cost);

            // By month
            if (r.getServiceDate() != null) {
                String monthKey = r.getServiceDate().format(MONTH_FORMAT);
                monthMap.put(monthKey, monthMap.getOrDefault(monthKey, 0.0) + cost);
            }

            // By vehicle
            if (r.getVehicle() != null) {
                String vName = r.getVehicle().getYear() + " " + r.getVehicle().getMake() + " " + r.getVehicle().getModel();
                vehicleMap.put(vName, vehicleMap.getOrDefault(vName, 0.0) + cost);
            }
        }

        // Round all values
        categoryMap.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);
        monthMap.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);
        vehicleMap.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);

        dto.setTotalExpenditure(total);
        dto.setTotalServices(records.size());
        dto.setAverageCostPerService(records.isEmpty() ? 0.0 : total / records.size());
        dto.setCostByCategory(categoryMap);
        dto.setCostByMonth(monthMap);
        dto.setCostByVehicle(vehicleMap);
        dto.setRecentRecords(records.stream().limit(10).toList());

        return dto;
    }
}
