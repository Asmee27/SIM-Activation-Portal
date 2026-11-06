package com.infy.sim.idproofservice.repository;

import com.infy.sim.idproofservice.entity.IdProof;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdProofRepository extends JpaRepository<IdProof, String> { }
