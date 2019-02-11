package com.miage.altea.tp.trainer_api.repository;

import com.miage.altea.tp.trainer_api.bo.Trainer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends CrudRepository<Trainer, String> {
    Optional<Trainer> findById(String name);
    Trainer save(Trainer t);
    List<Trainer> findAll();
    void deleteById(String name);
}