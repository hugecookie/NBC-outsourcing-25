package org.example.outsourcing.domain.store;

public enum StoreStatus {
    OPEN("영업 중"),
    CLOSED("일시 휴업"),
    TERMINATED("폐업함");

    private final String description;

    StoreStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



