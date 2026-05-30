package com.application_service.application_service.enums;

public enum ApplicationStatus {
    BOOKMARKED,   // saved but not applied yet
    APPLIED,      // application submitted
    OA,           // online assessment stage
    INTERVIEW,    // interview rounds ongoing
    OFFER,        // received an offer
    REJECTED,     // rejected at any stage
    WITHDRAWN     // candidate withdrew
}

//we careate this enum coz in is stored as a String in DB (via @Enumerated(EnumType.STRING)).
// Using an enum instead of a plain String prevents typos and makes status transitions type-safe.
