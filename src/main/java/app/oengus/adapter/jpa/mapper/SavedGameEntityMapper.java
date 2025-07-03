package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.SavedGameEntity;
import app.oengus.domain.user.SavedGame;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        SavedCategoryEntityMapper.class,
    }
)
public interface SavedGameEntityMapper {
    @Mapping(target = "userId", source = "user.id")
    SavedGame toDomain(SavedGameEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "user.id", source = "userId")
    @InheritConfiguration(name = "toDomain")
    SavedGameEntity fromDomain(SavedGame savedGame);
}
