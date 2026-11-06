package com.infy.sim.simservice.service;

import com.infy.sim.simservice.dto.ActivationRequest;
import com.infy.sim.simservice.dto.MessageResponse;
import com.infy.sim.simservice.dto.OfferDTO;
import com.infy.sim.simservice.dto.SimValidationRequest;
import com.infy.sim.simservice.entity.Offer;
import com.infy.sim.simservice.entity.Sim;
import com.infy.sim.simservice.exception.ResourceNotFoundException;
import com.infy.sim.simservice.exception.SimAlreadyActiveException;
import com.infy.sim.simservice.repository.OfferRepository;
import com.infy.sim.simservice.repository.SimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimService {

    private static final String INVALID_DETAILS =
            "Invalid details, please check again SIM number/Service number!!.";
    private static final String SIM_ALREADY_ACTIVE = "SIM already active";
    private static final String VALID_DETAILS = "Valid details. Welcome!!.";
    private static final String SIM_ACTIVATED = "SIM activated successfully";

    private final SimRepository simRepository;
    private final OfferRepository offerRepository;

    public SimService(SimRepository simRepository, OfferRepository offerRepository) {
        this.simRepository = simRepository;
        this.offerRepository = offerRepository;
    }

    public MessageResponse validate(SimValidationRequest request) {
        Sim sim = simRepository.findById(request.simNumber())
                .orElseThrow(() -> new ResourceNotFoundException(INVALID_DETAILS));

        if (!sim.getServiceNumber().equals(request.serviceNumber())) {
            throw new ResourceNotFoundException(INVALID_DETAILS);
        }
        if ("ACTIVE".equalsIgnoreCase(sim.getStatus())) {
            throw new SimAlreadyActiveException(SIM_ALREADY_ACTIVE);
        }

        return new MessageResponse(VALID_DETAILS);
    }

    @Transactional
    public MessageResponse activate(ActivationRequest request) {
        Sim sim = simRepository.findById(request.simNumber())
                .orElseThrow(() -> new ResourceNotFoundException(INVALID_DETAILS));
        if ("ACTIVE".equalsIgnoreCase(sim.getStatus())) {
            throw new SimAlreadyActiveException(SIM_ALREADY_ACTIVE);
        }
        sim.setStatus("ACTIVE");
        return new MessageResponse(SIM_ACTIVATED);
    }

    public OfferDTO findOffer(String simNumber) {
        Sim sim = simRepository.findById(simNumber)
                .orElseThrow(() -> new ResourceNotFoundException(INVALID_DETAILS));
        if (sim.getOfferId() == null) {
            throw new ResourceNotFoundException("No offer available for this SIM");
        }

        Offer offer = offerRepository.findById(sim.getOfferId())
                .orElseThrow(() -> new ResourceNotFoundException("No offer available for this SIM"));
        return new OfferDTO(
                offer.getOfferId(),
                offer.getOfferName(),
                offer.getCallQty(),
                offer.getDataQty(),
                offer.getDuration(),
                offer.getCost());
    }
}
