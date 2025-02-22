package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.Animal;
import info.dylansouthard.StraysBookAPI.model.CareEvent;
import info.dylansouthard.StraysBookAPI.model.enums.CareEventType;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CareEventRepositoryTests extends RepositoryTestContainer{

    //CREATE
    @Test
    @Transactional
    public void When_DeletingCareEvent_Expect_CareEventDeletedFromUserAndAnimals() {
        // ✅ Save user & animal first
        Animal animal = animalRepository.saveAndFlush(validAnimal);
        User user = userRepository.saveAndFlush(validUser);

        // ✅ Create and associate the care event
        CareEvent careEvent = new CareEvent(CareEventType.FED, LocalDateTime.now(), user);
        careEvent.addAnimal(animal);

        // ✅ save care event (after relationship is established)
        CareEvent savedCareEvent = careEventRepository.saveAndFlush(careEvent);
        animalRepository.saveAndFlush(animal);

        // ✅ Retrieve data to verify
        Animal savedAnimal = animalRepository.findById(animal.getId()).orElseThrow();
        User savedUser = userRepository.findById(user.getId()).orElseThrow();

        assertAll(
                "Care event save assertions",
                () -> assertNotNull(savedCareEvent, "Care Event should exist"),
                () -> assertEquals(1, savedCareEvent.getAnimals().size(), "Care event should have animal"),
                () -> assertNotNull(savedCareEvent.getRegisteredBy(), "Care event should have a user"),
                () -> assertEquals(1, savedAnimal.getCareEvents().size(), "Animal should have care event"),
                () -> assertEquals(1, savedUser.getCareEvents().size(), "User should have care event")
        );

        careEventRepository.delete(savedCareEvent);

        Animal foundAnimal = animalRepository.findById(animal.getId()).orElseThrow();
        Optional<CareEvent> deletedCareEvent = careEventRepository.findById(savedCareEvent.getId());
        assertAll(
                "Care event delete assertions",
                ()->assertTrue(deletedCareEvent.isEmpty(), "Care event should be empty"),
                ()->assertEquals(0, foundAnimal.getCareEvents().size(), "Animal Care Events should be empty")
                );
    }

}
