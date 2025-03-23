package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.CreateAnimalDTO;
import info.dylansouthard.StraysBookAPI.mapper.AnimalMapper;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.repository.AnimalRepository;
import info.dylansouthard.StraysBookAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimalMapper animalMapper;

    public AnimalDTO createAnimal(CreateAnimalDTO createDTO, User user){
        Animal animal = animalMapper.fromCreateAnimalDTO(createDTO);
        animal.setRegisteredBy(user);
       Animal savedAnimal = animalRepository.save(animal);
       user.addWatchedAnimal(animal);
       userRepository.save(user);
       return animalMapper.toAnimalDTO(savedAnimal);
    }
}
