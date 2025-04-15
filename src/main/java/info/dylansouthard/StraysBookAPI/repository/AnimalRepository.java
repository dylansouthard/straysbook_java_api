package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {


    @Query(value = """
SELECT * FROM animals
WHERE ST_DWithin(location::geography, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radius)
""", nativeQuery = true)
    public List<Animal> findByLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius);



    @Query("SELECT a from Animal a WHERE a.id = :id AND a.shouldAppear = true")
    Optional<Animal> findByActiveId(@Param("id") long id);

    String Sex(SexType sex);
}
