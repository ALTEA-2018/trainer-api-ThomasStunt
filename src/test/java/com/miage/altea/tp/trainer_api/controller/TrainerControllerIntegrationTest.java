package com.miage.altea.tp.trainer_api.controller;

import com.miage.altea.tp.trainer_api.bo.Pokemon;
import com.miage.altea.tp.trainer_api.bo.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Test
    void trainerController_shouldBeInstanciated(){
        assertNotNull(controller);
    }

    @Test
    void getTrainers_shouldThrowAnUnauthorized(){
        var responseEntity = this.restTemplate
                .getForEntity("http://localhost:" + port + "/trainers/Ash", Trainer.class);
        assertNotNull(responseEntity);
        assertEquals(401, responseEntity.getStatusCodeValue());
    }

    @Test
    void getTrainer_withNameAsh_shouldReturnAsh() {
        var ash = this.restTemplate
                .withBasicAuth(username, password)
                .getForObject("http://localhost:" + port + "/trainers/Ash", Trainer.class);

        assertNotNull(ash);
        assertEquals("Ash", ash.getName());
        assertEquals(1, ash.getTeam().size());

        assertEquals(25, ash.getTeam().get(0).getPokemonType());
        assertEquals(18, ash.getTeam().get(0).getLevel());
    }

    @Test
    void getAllTrainers_shouldReturnAshAndMistyAndBugTrainer() {
        var trainers = this.restTemplate
                .withBasicAuth(username, password)
                .getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainers);
        assertEquals(2, trainers.length);

        assertEquals("Ash", trainers[0].getName());
        assertEquals("BugCatcher", trainers[1].getName());
        //assertEquals("Misty", trainers[2].getName());
    }

    @Test
    void createTrainer_shouldReturnBugCatcher() {
        var insert = new Trainer();
        insert.setName("BugCatcher");
        var pok1 = new Pokemon(13, 6);
        var pok2 = new Pokemon(10, 6);
        insert.setTeam(List.of(pok1, pok2));

        this.controller.createTrainer(insert);

        var BugCatcher = this.restTemplate
                .withBasicAuth(username, password)
                .getForObject("http://localhost:" + port + "/trainers/BugCatcher", Trainer.class);
        assertNotNull(BugCatcher);
        assertEquals("BugCatcher", BugCatcher.getName());
        assertEquals(2, BugCatcher.getTeam().size());

        assertEquals(13, BugCatcher.getTeam().get(0).getPokemonType());
        assertEquals(10, BugCatcher.getTeam().get(1).getPokemonType());
    }

    @Test
    void deleteTrainer_shouldReturnTrueWithMisty() {
        this.restTemplate
                .withBasicAuth(username, password)
                .delete("http://localhost:" + port + "/trainers/Misty");

        var misty = this.restTemplate
                .withBasicAuth(username, password)
                .getForObject("http://localhost:" + port + "/trainers/Misty", Trainer.class);
        assertNull(misty);
    }
}