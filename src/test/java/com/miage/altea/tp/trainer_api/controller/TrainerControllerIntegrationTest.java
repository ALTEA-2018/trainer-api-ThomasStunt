package com.miage.altea.tp.trainer_api.controller;

import com.miage.altea.tp.trainer_api.bo.Pokemon;
import com.miage.altea.tp.trainer_api.bo.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrainerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TrainerController controller;

    @Test
    void trainerController_shouldBeInstanciated(){
        assertNotNull(controller);
    }

    @Test
    void getTrainer_withNameAsh_shouldReturnAsh() {
        var ash = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/Ash", Trainer.class);
        assertNotNull(ash);
        assertEquals("Ash", ash.getName());
        assertEquals(1, ash.getTeam().size());

        assertEquals(25, ash.getTeam().get(0).getPokemonType());
        assertEquals(18, ash.getTeam().get(0).getLevel());
    }

    @Test
    void getAllTrainers_shouldReturnAshAndMisty() {
        var trainers = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainers);
        assertEquals(3, trainers.length);

        assertEquals("Ash", trainers[0].getName());
        assertEquals("BugCatcher", trainers[1].getName());
        assertEquals("Misty", trainers[2].getName());
    }

    @Test
    void createTrainer_shouldReturnBugCatcher() {
        var insert = new Trainer();
        insert.setName("BugCatcher");
        var pok1 = new Pokemon(13, 6);
        var pok2 = new Pokemon(10, 6);
        insert.setTeam(List.of(pok1, pok2));

        this.controller.createTrainer(insert);

        var BugCatcher = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/BugCatcher", Trainer.class);
        assertNotNull(BugCatcher);
        assertEquals("BugCatcher", BugCatcher.getName());
        assertEquals(2, BugCatcher.getTeam().size());

        assertEquals(13, BugCatcher.getTeam().get(0).getPokemonType());
        assertEquals(10, BugCatcher.getTeam().get(1).getPokemonType());
    }

    @Test
    void deleteTrainer_shouldReturnTrueWithMisty() {
        this.restTemplate.delete("http://localhost:" + port + "/trainers/Misty");

        var misty = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/Misty", Trainer.class);
        assertNull(misty);
    }
}