package com.vti.springdatajpa.entity.enums;

public enum LoanStatus {
    PENDING_AI,      // Waiting for AI decision
    PENDING_ADMIN,   // Waiting for admin review
    APPROVED,
    REJECTED
}
