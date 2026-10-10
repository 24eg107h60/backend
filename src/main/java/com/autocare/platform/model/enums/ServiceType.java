package com.autocare.platform.model.enums;

public enum ServiceType {
    OIL_CHANGE(
            "Oil Change & Filter",
            8000,
            6,
            75.0,
            "Engine Lubrication",
            "Replace synthetic/conventional engine oil, replace oil filter gasket, and top off basic fluids.",
            false,
            "bi-droplet-fill"
    ),
    BRAKE_INSPECTION(
            "Brake Pads & Rotors",
            20000,
            12,
            160.0,
            "Braking System",
            "Inspect brake pad thickness, rotor runout, calipers, brake fluid boiling point, and lines.",
            true,
            "bi-shield-check"
    ),
    BATTERY_MAINTENANCE(
            "Battery & Electrical",
            30000,
            24,
            110.0,
            "Electrical",
            "Test 12V battery cold cranking amps (CCA), clean terminal corrosion, verify alternator charging output.",
            true,
            "bi-battery-charging"
    ),
    TIRE_ROTATION(
            "Tire Rotation & Balance",
            10000,
            6,
            45.0,
            "Tires & Suspension",
            "Rotate tires diagonally, check tread depth across grooves, calibrate tire pressure (TPMS), balance wheels.",
            true,
            "bi-disc"
    ),
    AIR_FILTER(
            "Air & Cabin Filters",
            15000,
            12,
            55.0,
            "Intake & Climate",
            "Inspect and replace combustion engine air intake filter and cabin pollen/dust filter.",
            true,
            "bi-wind"
    ),
    TRANSMISSION_FLUID(
            "Transmission Fluid",
            45000,
            36,
            210.0,
            "Drivetrain",
            "Flush and replace transmission gear oil/ATF fluid, inspect transmission pan, pan gasket, and magnet.",
            false,
            "bi-gear-wide-connected"
    ),
    GENERAL_SERVICING(
            "General Servicing",
            10000,
            6,
            175.0,
            "Multi-Point Inspection",
            "Comprehensive 50-point safety check: suspension bushings, steering rack, exhaust, lights, wipers, computer diagnostics.",
            true,
            "bi-tools"
    );

    private final String displayName;
    private final int intervalKm;
    private final int intervalMonths;
    private final double standardCost;
    private final String category;
    private final String description;
    private final boolean appliesToElectric;
    private final String icon;

    ServiceType(String displayName, int intervalKm, int intervalMonths, double standardCost,
                String category, String description, boolean appliesToElectric, String icon) {
        this.displayName = displayName;
        this.intervalKm = intervalKm;
        this.intervalMonths = intervalMonths;
        this.standardCost = standardCost;
        this.category = category;
        this.description = description;
        this.appliesToElectric = appliesToElectric;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getIntervalKm() {
        return intervalKm;
    }

    public int getIntervalMonths() {
        return intervalMonths;
    }

    public double getStandardCost() {
        return standardCost;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAppliesToElectric() {
        return appliesToElectric;
    }

    public String getIcon() {
        return icon;
    }
}
