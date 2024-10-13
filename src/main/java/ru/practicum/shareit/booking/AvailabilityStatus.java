package ru.practicum.shareit.booking;

/**
 * ENUM of booking item status
 * WAITING - new booking, waiting for approving
 * APPROVED - booking approved by owner
 * REJECTED - booking has rejected by owner
 * CANCELED - booking canceled by creator of response
 */
public enum AvailabilityStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
