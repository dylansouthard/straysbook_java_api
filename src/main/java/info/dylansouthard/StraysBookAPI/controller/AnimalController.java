package info.dylansouthard.StraysBookAPI.controller;

import info.dylansouthard.StraysBookAPI.constants.ApiRoutes;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.CreateAnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.UpdateAnimalDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnimalController {
    @Autowired
    private AnimalService animalService;

    @PostMapping(ApiRoutes.ANIMALS.CREATE)
    @Operation(summary = "Register a new animal", description = "Creates and registers a new animal. Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Animal created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request. Error Code: INVALID_CREATE")
    @ApiResponse(responseCode = "401", description = "Unauthorized. Error Code: AUTH")
    @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    public ResponseEntity<AnimalDTO> createAnimal(
            @RequestBody CreateAnimalDTO dto,
            @AuthenticationPrincipal User user
    ) {

        if (user == null) throw ErrorFactory.auth();
        AnimalDTO created = animalService.createAnimal(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(ApiRoutes.ANIMALS.NEARBY)
    @Operation(summary = "Find animals near a location", description = "Retrieve a list of animals near the provided latitude and longitude.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animals found and returned"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates. Error Code: INVALID_COORDINATES"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<List<AnimalSummaryDTO>> getNearbyAnimals(
            @RequestParam(required = false)  // Not required in code
            @Parameter(description = "Latitude", example = "13.7563", required = true) Double lat,

            @RequestParam(required = false)  // Not required in code
            @Parameter(description = "Longitude", example = "100.5018", required = true) Double lon,

            @RequestParam(defaultValue = "1000")
            @Parameter(description = "Radius in meters", example = "1000") Double radius) {

        List<AnimalSummaryDTO> animals = animalService.getAnimalsInArea(lat, lon, radius);
        return ResponseEntity.ok(animals);
    }

    @GetMapping(ApiRoutes.ANIMALS.DETAIL)
    @Operation(summary = "Fetch detailed animal information", description = "Retrieve full animal details by ID.")
    @ApiResponse(responseCode = "200", description = "Animal found and returned")
    @ApiResponse(responseCode = "400", description = "Invalid parameters. Error Code: INVALID_PARAMS")
    @ApiResponse(responseCode = "404", description = "Animal not found. Error Code: ANIMAL_NOT_FOUND")
    @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable("id") Long id) {
        AnimalDTO animal = animalService.fetchAnimalDetails(id);
        return ResponseEntity.ok(animal);
    }

    @PatchMapping(ApiRoutes.ANIMALS.DETAIL)
    @Operation(summary = "Update an animal", description = "Updates allowed fields of an animal. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request. Error Code: INVALID_PARAMS or INVALID_UPDATE"),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Error Code: AUTH"),
            @ApiResponse(responseCode = "403", description = "Forbidden. Error Code: AUTH_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "Animal not found. Error Code: ANIMAL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<AnimalDTO> updateAnimal(
            @PathVariable("id") Long id,
            @RequestBody UpdateAnimalDTO updateDTO,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();
        AnimalDTO updated = animalService.updateAnimal(id, updateDTO, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(ApiRoutes.ANIMALS.DETAIL)
    @Operation(summary = "Delete an animal", description = "Deletes an animal. Only allowed for the primary caretaker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Animal deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden. Error Code: AUTH_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "Animal not found. Error Code: ANIMAL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<Void> deleteAnimal(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();

        animalService.deleteAnimal(id, user);

        return ResponseEntity.noContent().build();
    }
}
