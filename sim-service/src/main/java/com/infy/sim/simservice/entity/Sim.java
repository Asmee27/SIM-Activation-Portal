package com.infy.sim.simservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sim")
@Getter @Setter @NoArgsConstructor
public class Sim {
    @Id
    @Column(name = "sim_number", length = 13)
    private String simNumber;
    private String serviceNumber;
    private String status;      // ACTIVE / INACTIVE
    private Long offerId;
}