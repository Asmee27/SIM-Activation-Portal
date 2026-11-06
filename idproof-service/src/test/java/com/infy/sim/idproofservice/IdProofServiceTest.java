package com.infy.sim.idproofservice;

import com.infy.sim.idproofservice.dto.IdProofRequest;
import com.infy.sim.idproofservice.entity.IdProof;
import com.infy.sim.idproofservice.exception.InvalidIdProofException;
import com.infy.sim.idproofservice.repository.IdProofRepository;
import com.infy.sim.idproofservice.service.IdProofService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdProofServiceTest {

    private static final LocalDate DOB = LocalDate.of(1990, 12, 12);

    @Mock
    private IdProofRepository repository;

    private IdProofService service;

    @BeforeEach
    void setUp() {
        service = new IdProofService(repository);
        IdProof proof = new IdProof();
        proof.setIdNumber("12345");
        proof.setIdType("Aadhaar");
        proof.setFirstName("Mihir");
        proof.setLastName("Khandekar");
        proof.setDob(DOB);
        proof.setState("Maharashtra");
        when(repository.findById("12345")).thenReturn(Optional.of(proof));
    }

    @Test
    void acceptsMatchingProof() {
        assertEquals("ID proof validated successfully", service.validate(
                new IdProofRequest("aadhaar", "12345", "Mihir", "Khandekar", "Maharashtra", DOB)).message());
    }

    @Test
    void rejectsMismatchedDob() {
        assertThrows(InvalidIdProofException.class, () -> service.validate(
                new IdProofRequest("Aadhaar", "12345", "Mihir", "Khandekar", "Maharashtra", DOB.plusDays(1))));
    }

    @Test
    void rejectsMismatchedState() {
        assertThrows(InvalidIdProofException.class, () -> service.validate(
                new IdProofRequest("Aadhaar", "12345", "Mihir", "Khandekar", "Goa", DOB)));
    }
}
