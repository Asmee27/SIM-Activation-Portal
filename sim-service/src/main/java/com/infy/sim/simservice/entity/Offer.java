package com.infy.sim.simservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offer")
@Getter @Setter @NoArgsConstructor
public class Offer {
    @Id
    private Long offerId;
    private String offerName;
    private Integer callQty;
    private Integer dataQty;
    private Integer duration;
    private Integer cost;
}