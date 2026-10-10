package com.autocare.platform.config;

import com.autocare.platform.model.*;
import com.autocare.platform.model.enums.*;
import com.autocare.platform.repository.*;
import com.autocare.platform.service.PredictiveMaintenanceEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceBookingRepository bookingRepository;
    private final ServiceRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;
    private final PredictiveMaintenanceEngine predictiveEngine;

    public DataInitializer(UserRepository userRepository,
                           VehicleRepository vehicleRepository,
                           ServiceBookingRepository bookingRepository,
                           ServiceRecordRepository recordRepository,
                           PasswordEncoder passwordEncoder,
                           PredictiveMaintenanceEngine predictiveEngine) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.recordRepository = recordRepository;
        this.passwordEncoder = passwordEncoder;
        this.predictiveEngine = predictiveEngine;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized with seed data.");
            return;
        }

        log.info("Initializing Vehicle Service Platform with default seed data...");

        // 1. Seed Users
        User admin = new User(
                "Fleet Operations Admin",
                "admin@autocare.com",
                passwordEncoder.encode("admin123"),
                "+1 555-0100",
                Role.ROLE_ADMIN
        );
        userRepository.save(admin);

        User alex = new User(
                "Alex Morgan",
                "user@autocare.com",
                passwordEncoder.encode("user123"),
                "+1 555-0199",
                Role.ROLE_USER
        );
        userRepository.save(alex);

        User sarah = new User(
                "Sarah Connor",
                "sarah@autocare.com",
                passwordEncoder.encode("user123"),
                "+1 555-0255",
                Role.ROLE_USER
        );
        userRepository.save(sarah);

        // 2. Seed Vehicles
        // Vehicle 1: Toyota Camry (Overdue on oil, due soon on brakes)
        Vehicle camry = new Vehicle(
                alex,
                "Toyota",
                "Camry SE",
                2021,
                "ABC-7890",
                "4T1B11HK8MU102938",
                FuelType.PETROL,
                48500,
                1200,
                LocalDate.now().minusMonths(7),
                40000
        );
        vehicleRepository.save(camry);

        // Vehicle 2: Honda CR-V (Moderate wear, oil due soon)
        Vehicle crv = new Vehicle(
                alex,
                "Honda",
                "CR-V AWD",
                2020,
                "CRV-4421",
                "2HKRW2H87LH594832",
                FuelType.PETROL,
                62000,
                1400,
                LocalDate.now().minusMonths(4),
                56000
        );
        vehicleRepository.save(crv);

        // Vehicle 3: Tesla Model 3 (Electric, pristine condition)
        Vehicle tesla = new Vehicle(
                alex,
                "Tesla",
                "Model 3 LR",
                2023,
                "EV-9021",
                "5YJ3E1EB8NF192834",
                FuelType.ELECTRIC,
                28500,
                1100,
                LocalDate.now().minusMonths(2),
                26000
        );
        vehicleRepository.save(tesla);

        // Vehicle 4: Ford F-150 (Sarah's vehicle - heavily overdue)
        Vehicle f150 = new Vehicle(
                sarah,
                "Ford",
                "F-150 XLT",
                2019,
                "TRK-5510",
                "1FTFW1E84KFA38472",
                FuelType.DIESEL,
                92400,
                1800,
                LocalDate.now().minusMonths(9),
                78000
        );
        vehicleRepository.save(f150);

        // 3. Historical Service Records for Cost Reporting & Charts
        // Camry history
        recordRepository.save(new ServiceRecord(
                camry, null, ServiceType.OIL_CHANGE.name(),
                "Synthetic Oil 5W-30 + OEM Filter Replacement",
                LocalDate.now().minusMonths(7),
                40000, 79.50, "INV-10023", "Downtown AutoCare Hub", "No leaks detected."
        ));
        recordRepository.save(new ServiceRecord(
                camry, null, ServiceType.TIRE_ROTATION.name(),
                "Tire rotation and dynamic road-force balancing",
                LocalDate.now().minusMonths(7),
                40000, 45.00, "INV-10024", "Downtown AutoCare Hub", "Tread depths at 6mm."
        ));
        recordRepository.save(new ServiceRecord(
                camry, null, ServiceType.GENERAL_SERVICING.name(),
                "35,000 km Scheduled Maintenance & Inspection",
                LocalDate.now().minusMonths(13),
                32000, 185.00, "INV-09412", "Downtown AutoCare Hub", "Fluid levels nominal."
        ));

        // CR-V history
        recordRepository.save(new ServiceRecord(
                crv, null, ServiceType.BRAKE_INSPECTION.name(),
                "Front ceramic brake pads replacement and rotor resurfacing",
                LocalDate.now().minusMonths(4),
                56000, 240.00, "INV-10492", "Metro Express Pitstop", "Rear pads at 50% wear."
        ));
        recordRepository.save(new ServiceRecord(
                crv, null, ServiceType.OIL_CHANGE.name(),
                "Engine Oil Change 0W-20 & Filter",
                LocalDate.now().minusMonths(4),
                56000, 82.00, "INV-10493", "Metro Express Pitstop", "Clean engine bay."
        ));
        recordRepository.save(new ServiceRecord(
                crv, null, ServiceType.AIR_FILTER.name(),
                "Engine intake and HEPA cabin filter swap",
                LocalDate.now().minusMonths(8),
                48000, 55.00, "INV-09811", "Metro Express Pitstop", "Cabin filter was clogged."
        ));

        // Tesla history
        recordRepository.save(new ServiceRecord(
                tesla, null, ServiceType.TIRE_ROTATION.name(),
                "EV tire rotation and TPMS calibration",
                LocalDate.now().minusMonths(2),
                26000, 50.00, "INV-10889", "Tesla Service & AutoCare", "Even tread wear."
        ));
        recordRepository.save(new ServiceRecord(
                tesla, null, ServiceType.AIR_FILTER.name(),
                "Bioweapon defense HEPA air filter replacement",
                LocalDate.now().minusMonths(6),
                20000, 85.00, "INV-10112", "Tesla Service & AutoCare", "Complete system sanitized."
        ));

        // F-150 history
        recordRepository.save(new ServiceRecord(
                f150, null, ServiceType.TRANSMISSION_FLUID.name(),
                "Heavy-duty ATF transmission fluid flush & filter",
                LocalDate.now().minusMonths(9),
                78000, 260.00, "INV-09650", "Truck Pro Servicing", "Magnet cleaned of shavings."
        ));
        recordRepository.save(new ServiceRecord(
                f150, null, ServiceType.OIL_CHANGE.name(),
                "Diesel engine 15W-40 heavy-duty oil service",
                LocalDate.now().minusMonths(9),
                78000, 110.00, "INV-09651", "Truck Pro Servicing", "Fuel filter inspected."
        ));

        // 4. Seed Service Bookings
        ServiceBooking booking1 = new ServiceBooking(
                alex,
                camry,
                "Oil Change & Filter, Brake Pads & Rotors",
                LocalDate.now().plusDays(2),
                "10:00 AM",
                "Downtown AutoCare Hub",
                235.0,
                "Engine oil is overdue and brake pedal feels slightly spongy."
        );
        booking1.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking1);

        ServiceBooking booking2 = new ServiceBooking(
                alex,
                crv,
                "Tire Rotation & Balance",
                LocalDate.now().plusDays(4),
                "02:30 PM",
                "Metro Express Pitstop",
                45.0,
                "Routine 10,000 km tire rotation."
        );
        booking2.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking2);

        ServiceBooking booking3 = new ServiceBooking(
                alex,
                tesla,
                "General Servicing",
                LocalDate.now().minusDays(15),
                "09:00 AM",
                "Downtown AutoCare Hub",
                120.0,
                "Annual EV safety check."
        );
        booking3.setStatus(BookingStatus.COMPLETED);
        booking3.setFinalCost(120.0);
        booking3.setTechnicianNotes("Passed 50-point safety check with flying colors.");
        bookingRepository.save(booking3);

        ServiceBooking booking4 = new ServiceBooking(
                sarah,
                f150,
                "Oil Change & Filter, Battery & Electrical, General Servicing",
                LocalDate.now().plusDays(1),
                "11:30 AM",
                "Truck Pro Servicing",
                395.0,
                "Urgent multi-system overhaul before cross-country haul."
        );
        booking4.setStatus(BookingStatus.IN_PROGRESS);
        bookingRepository.save(booking4);

        // 5. Trigger automated initial predictive reminders for vehicles
        predictiveEngine.checkAndTriggerReminders(camry);
        predictiveEngine.checkAndTriggerReminders(crv);
        predictiveEngine.checkAndTriggerReminders(tesla);
        predictiveEngine.checkAndTriggerReminders(f150);

        log.info("Vehicle Service Platform seed data successfully initialized!");
    }
}
