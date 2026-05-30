package com.application_service.application_service.entity;

import com.application_service.application_service.enums.ApplicationStatus;
import com.application_service.application_service.enums.JobPriority;
import com.application_service.application_service.enums.WorkMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job-applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String companyName; //// denormalized — avoids joining company-service on every read
    /// Denormalized means storing some data twice to avoid expensive lookups or service calls.

    private String jobUrl;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    private WorkMode mode;

    @Enumerated(EnumType.STRING)
    private JobPriority priority;

    private LocalDate appliedDate;

    private String salaryRange;    // stored as string e.g. "12-15 LPA"

    private String location;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //They allow you to execute a method automatically before an entity is saved or updated in the database.
    //@PrePersist Runs before INSERT.
    //@PreUpdate Runs before UPDATE.

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.BOOKMARKED; // default status
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
