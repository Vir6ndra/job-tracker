package com.application_service.application_service.entity;

import com.application_service.application_service.enums.RoundOutcome;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "interview_rounds", uniqueConstraints = @UniqueConstraint(
        columnNames = {"application_id", "round_number"}
))
//this prevents duplicate round numbers per application
//application_id + round_number
//        ↓
//must be unique together

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRound {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Column(name = "round_numder", nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoundOutcome outcome;

    @Column(columnDefinition = "TEXT")
    private String feedback;        // notes after the round
}

//Many interview rounds belong to one application thats why @ManyToOne
//FetchType.LAZY means the parent application is not loaded unless explicitly accessed
//The DB-level unique constraint on (application_id, round_number) enforces that you can't add "Round 2" twice for the same application