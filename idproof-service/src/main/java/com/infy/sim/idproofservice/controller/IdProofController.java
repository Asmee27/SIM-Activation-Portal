package com.infy.sim.idproofservice.controller;

import com.infy.sim.idproofservice.dto.IdProofRequest;
import com.infy.sim.idproofservice.dto.MessageResponse;
import com.infy.sim.idproofservice.service.IdProofService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/idproof")
public class IdProofController {

    private final IdProofService idProofService;

    public IdProofController(IdProofService idProofService) {
        this.idProofService = idProofService;
    }

    @PostMapping("/validation")
    public MessageResponse validate(@Valid @RequestBody IdProofRequest request) {
        return idProofService.validate(request);
    }
}
