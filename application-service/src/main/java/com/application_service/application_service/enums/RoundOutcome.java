package com.application_service.application_service.enums;

public enum RoundOutcome {
    PENDING,   // round scheduled but not happened yet
    PASSED,    // moved to next round
    FAILED     // did not clear
}
