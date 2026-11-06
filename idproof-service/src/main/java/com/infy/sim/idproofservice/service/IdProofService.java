package com.infy.sim.idproofservice.service;

import com.infy.sim.idproofservice.dto.IdProofRequest;
import com.infy.sim.idproofservice.dto.MessageResponse;
import com.infy.sim.idproofservice.entity.IdProof;
import com.infy.sim.idproofservice.exception.InvalidIdProofException;
import com.infy.sim.idproofservice.repository.IdProofRepository;
import org.springframework.stereotype.Service;

@Service
public class IdProofService {

    private static final String INVALID_DETAILS = "Invalid details";
    private static final String VALID_ID_PROOF = "ID proof validated successfully";

    private final IdProofRepository idProofRepository;

    public IdProofService(IdProofRepository idProofRepository) {
        this.idProofRepository = idProofRepository;
    }

    /** Every submitted field must match the government record held for that ID number. */
    public MessageResponse validate(IdProofRequest request) {
        IdProof proof = idProofRepository.findById(request.idNumber().trim())
                .orElseThrow(() -> new InvalidIdProofException(INVALID_DETAILS));

        boolean matches = proof.getIdType().equalsIgnoreCase(request.idType().trim())
                && proof.getFirstName().equalsIgnoreCase(request.firstName())
                && proof.getLastName().equalsIgnoreCase(request.lastName())
                && proof.getState().equalsIgnoreCase(request.state().trim())
                && proof.getDob().equals(request.dob());
        if (!matches) {
            throw new InvalidIdProofException(INVALID_DETAILS);
        }
        return new MessageResponse(VALID_ID_PROOF);
    }
}
