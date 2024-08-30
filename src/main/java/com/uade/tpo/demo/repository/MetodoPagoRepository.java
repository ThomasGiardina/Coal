package com.uade.tpo.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.demo.entity.MetodoPago;;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    
}