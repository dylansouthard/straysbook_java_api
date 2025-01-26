package info.dylansouthard.StraysBookAPI.model.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class GeoSchema {

    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point location;

    public GeoSchema(double latitude, double longitude) {
        this.location = createPoint(latitude, longitude);
    }

    private Point createPoint(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
