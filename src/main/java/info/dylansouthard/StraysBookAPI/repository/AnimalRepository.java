package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

//    @Query(value = """
//    SELECT a
//    FROM Animal a
//    WHERE ST_DWithin(
//        a.location.location,
//        ST_MakePoint(:longitude, :latitude),
//        :radius
//    ) = true
//    """)
//    public List<Animal> findAllInArea(@Param("latitude") double latitude,
//                                      @Param("longitude") double longitude,
//                                      @Param("radius") double radius);

    @Query(value = """
    SELECT a
    FROM Animal a
    WHERE a.location.location IS NOT NULL
    """)
    List<Animal> findWithLocation();

    @Query("SELECT a from Animal a WHERE a.id = :id AND a.shouldAppear = true")
    Optional<Animal> findByActiveId(@Param("id") long id);
}
