package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.Animal;
import info.dylansouthard.StraysBookAPI.model.SterilizationStatus;
import info.dylansouthard.StraysBookAPI.model.Vaccination;
import info.dylansouthard.StraysBookAPI.model.enums.VaccinationType;
import info.dylansouthard.StraysBookAPI.model.enums.VerificationStatusType;
import info.dylansouthard.StraysBookAPI.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalRepositoryTests extends RepositoryTestContainer{


    @Test
    void whenSavingAnimal_Expect_AnimalIsRetrievable() {
        // Save it
        Animal savedAnimal = animalRepository.save(validAnimal);

        // Fetch from DB
        Optional<Animal> retrievedAnimal = animalRepository.findById(savedAnimal.getId());

        // Assertions
        assertTrue(retrievedAnimal.isPresent(), "Animal should be found in DB");
        assertEquals("Amelie", retrievedAnimal.get().getName(), "Animal name should match");
    }

    @Test
    void whenSavingInvalidAnimal_Expect_ThrowException() {
        Animal invalidAnimal = new Animal();

        assertThrows(DataIntegrityViolationException.class, () -> animalRepository.save(invalidAnimal));
    }

    @Test
    public void When_Finding_InvalidAnimal_Expect_AnimalNotFoundSuccessfully() {
        Optional<Animal> animal = animalRepository.findById(999L);
        assertTrue(animal.isEmpty());
    }

    @Test
    public void When_Finding_InactiveAnimal_Expect_AnimalNotFoundSuccessfully() {

        validAnimal.setShouldAppear(false);
        Animal savedAnimal = animalRepository.save(validAnimal);
        Optional<Animal> animal = animalRepository.findByActiveId(savedAnimal.getId());
        assertTrue(animal.isEmpty());
    }

    //UPDATE
    @Test
    public void When_UpdatingAnimal_Expect_AnimalUpdatedSuccessfully() {
        Animal animal = animalRepository.save(validAnimal);
        LocalDate birthday = LocalDate.now();
        animal.setBorn(birthday);
        animal.setDangerous(true);
        Animal updatedAnimal = animalRepository.save(animal);
        assertAll("Animal properties",
                ()->assertTrue(updatedAnimal.getDangerous()),
                ()->assertEquals(animal.getBorn(), birthday)
                );
    }

    //DELETE
    @Test
    public void When_DeletingAnimal_Expect_AnimalDeletedSuccessfully() {
        Animal animal = animalRepository.save(validAnimal);
        Long id = animal.getId();
        animalRepository.delete(animal);
        animalRepository.flush();
        assertTrue(animalRepository.findById(id).isEmpty(), "Deleted animal should not be found in DB");
    }



    //VALIDATION
    @Test
    public void When_UpdatingEmptyName_Expect_ThrowException() {
        Animal animal = animalRepository.save(validAnimal);
        animal.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> animalRepository.save(animal));
    }

    //RELATIONS
    @Test
    public void When_UpdatingSterilizationStatus_Expect_SterilizationStatusUpdatedSuccessfully() {
       validAnimal.setSterilizationStatus(new SterilizationStatus(true));
       Animal animal = animalRepository.save(validAnimal);

       Animal retrievedAnimal = animalRepository.findById(animal.getId()).get();

       SterilizationStatus sterilizationStatus = retrievedAnimal.getSterilizationStatus();

       long sterilizationStatusId = sterilizationStatus.getId();
       assertAll(
               "Sterilization status properties",
               () -> assertTrue(sterilizationStatus.getSterilized()),
               ()->assertEquals(VerificationStatusType.UNVERIFIED, sterilizationStatus.getVerificationStatus()
               ));

       User user = userRepository.save(validUser);
       sterilizationStatus.verify(user);
       sterilizationStatusRepository.saveAndFlush(sterilizationStatus);
       assertEquals(VerificationStatusType.VERIFIED,retrievedAnimal.getSterilizationStatus().getVerificationStatus());

       animalRepository.delete(animal);
       animalRepository.flush();
       Optional<SterilizationStatus> deletedSS = sterilizationStatusRepository.findById(sterilizationStatusId);
       assertTrue(deletedSS.isEmpty(), "Sterilization status should not be found in DB");
    }

    public void When_UpdatingVaccinationStatus_Expect_VaccinationStatusUpdatedSuccessfully() {
        User savedUser = userRepository.save(validUser);
        Vaccination vaccination = new Vaccination(VaccinationType.CCV, savedUser);
        validAnimal.getVaccinations().add(vaccination);
        Animal animal = animalRepository.save(validAnimal);

        Animal retrievedAnimal = animalRepository.findById(animal.getId()).get();

        assertEquals(1, retrievedAnimal.getVaccinations().size(), "Vaccination count should be 1");
        Vaccination retrievedVaccination = retrievedAnimal.getVaccinations().stream().findFirst().get();

        assertAll(
                "Vaccination assertions",
                ()->assertEquals(VaccinationType.CCV, retrievedVaccination.getType(), "Vaccination type should be CCV"),
                ()-> assertEquals(savedUser.getId(), retrievedVaccination.getRegisteredBy().getId(), "Vaccine should be registered by user ID")
                );

        animalRepository.delete(animal);
        animalRepository.flush();
        Optional<Vaccination> deletedVS = vaccinationRepository.findById(retrievedVaccination.getId());
        assertTrue(deletedVS.isEmpty(), "Vaccination status of deleted animal should not be found in DB");
    }
}


