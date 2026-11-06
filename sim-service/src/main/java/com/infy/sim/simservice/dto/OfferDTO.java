package com.infy.sim.simservice.dto;
public record OfferDTO(Long offerId, String offerName, Integer callQty,
                       Integer dataQty, Integer duration, Integer cost) { }