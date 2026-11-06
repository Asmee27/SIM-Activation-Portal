package com.infy.sim.simservice.service;

import com.infy.sim.simservice.dto.ActivationRequest;
import com.infy.sim.simservice.dto.SimValidationRequest;
import com.infy.sim.simservice.entity.Offer;
import com.infy.sim.simservice.entity.Sim;
import com.infy.sim.simservice.exception.ResourceNotFoundException;
import com.infy.sim.simservice.exception.SimAlreadyActiveException;
import com.infy.sim.simservice.repository.OfferRepository;
import com.infy.sim.simservice.repository.SimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimServiceTest {

    @Mock
    private SimRepository simRepository;

    @Mock
    private OfferRepository offerRepository;

    private SimService simService;

    @BeforeEach
    void setUp() {
        simService = new SimService(simRepository, offerRepository);
    }

    @Test
    void validatesInactiveSimWithMatchingServiceNumber() {
        Sim sim = sim("INACTIVE", 5000L);
        when(simRepository.findById("1234567891234")).thenReturn(Optional.of(sim));

        assertEquals("Valid details. Welcome!!.",
                simService.validate(new SimValidationRequest("1234567891234", "1234567891")).message());
    }

    @Test
    void rejectsWrongServiceNumber() {
        when(simRepository.findById("1234567891234")).thenReturn(Optional.of(sim("INACTIVE", 5000L)));

        assertEquals("Invalid details, please check again SIM number/Service number!!.",
                assertThrows(ResourceNotFoundException.class,
                        () -> simService.validate(new SimValidationRequest("1234567891234", "0000000000")))
                        .getMessage());
    }

    @Test
    void rejectsAlreadyActiveSim() {
        Sim sim = sim("ACTIVE", 5001L);
        sim.setSimNumber("1234567891235");
        sim.setServiceNumber("1234567892");
        when(simRepository.findById("1234567891235")).thenReturn(Optional.of(sim));

        assertEquals("SIM already active",
                assertThrows(SimAlreadyActiveException.class,
                        () -> simService.validate(new SimValidationRequest("1234567891235", "1234567892")))
                        .getMessage());
    }

    @Test
    void returnsOfferForSim() {
        when(simRepository.findById("1234567891234")).thenReturn(Optional.of(sim("INACTIVE", 5000L)));
        Offer offer = new Offer();
        offer.setOfferId(5000L);
        offer.setOfferName("Welcome Offer");
        offer.setCallQty(100);
        offer.setDataQty(120);
        offer.setDuration(10);
        offer.setCost(20);
        when(offerRepository.findById(5000L)).thenReturn(Optional.of(offer));

        assertEquals("Welcome Offer", simService.findOffer("1234567891234").offerName());
    }

    @Test
    void activatesInactiveSim() {
        Sim sim = sim("INACTIVE", 5000L);
        when(simRepository.findById("1234567891234")).thenReturn(Optional.of(sim));

        assertEquals("SIM activated successfully",
                simService.activate(new ActivationRequest("1234567891234")).message());
        assertEquals("ACTIVE", sim.getStatus());
    }

    @Test
    void refusesToActivateActiveSim() {
        when(simRepository.findById("1234567891234")).thenReturn(Optional.of(sim("ACTIVE", 5000L)));

        assertThrows(SimAlreadyActiveException.class,
                () -> simService.activate(new ActivationRequest("1234567891234")));
    }

    private Sim sim(String status, Long offerId) {
        Sim sim = new Sim();
        sim.setSimNumber("1234567891234");
        sim.setServiceNumber("1234567891");
        sim.setStatus(status);
        sim.setOfferId(offerId);
        return sim;
    }
}
