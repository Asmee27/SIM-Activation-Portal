package com.infy.sim.customerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A customer who placed an activation request while buying the SIM.
 * The address is empty until the customer fills it in on the portal.
 */
@Entity
@Table(name = "customer")
@Getter @Setter @NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(unique = true, nullable = false)
    private String email;
    private LocalDate dob;
    private String firstName;
    private String lastName;

    @Embedded
    private Address address;
}
