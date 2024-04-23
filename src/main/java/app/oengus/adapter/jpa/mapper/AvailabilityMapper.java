package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.AvailabilityEntity;
import app.oengus.domain.submission.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {
    Availability toDomain(AvailabilityEntity entity);

    AvailabilityEntity fromDomain(Availability availability);
}
