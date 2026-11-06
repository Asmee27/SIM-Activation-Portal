package com.infy.sim.idproofservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "id_proof")
@Getter @Setter @NoArgsConstructor
public class IdProof {
    @Id
    @Column(name = "id_number")
    private String idNumber;
    private String idType;      // e.g. Aadhaar
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String state;
}
