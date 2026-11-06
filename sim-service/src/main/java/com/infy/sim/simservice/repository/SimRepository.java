package com.infy.sim.simservice.repository;

import com.infy.sim.simservice.entity.Sim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimRepository extends JpaRepository<Sim, String> { }