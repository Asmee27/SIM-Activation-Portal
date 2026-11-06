package com.infy.sim.simservice.controller;

import com.infy.sim.simservice.dto.ActivationRequest;
import com.infy.sim.simservice.dto.MessageResponse;
import com.infy.sim.simservice.dto.OfferDTO;
import com.infy.sim.simservice.dto.SimValidationRequest;
import com.infy.sim.simservice.service.SimService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/sim")
public class SimController {

    private final SimService simService;

    public SimController(SimService simService) {
        this.simService = simService;
    }

    @PostMapping("/validation")
    public MessageResponse validate(@Valid @RequestBody SimValidationRequest request) {
        return simService.validate(request);
    }

    @PostMapping("/activation")
    public MessageResponse activate(@Valid @RequestBody ActivationRequest request) {
        return simService.activate(request);
    }

    @GetMapping("/offer/{simNumber}")
    public OfferDTO offer(@PathVariable
                          @Pattern(regexp = "\\d{13}", message = "SIM number should be a 13 digit number")
                          String simNumber) {
        return simService.findOffer(simNumber);
    }
}
