package app.oengus.util;

import app.oengus.domain.marathon.Marathon;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

// Yup, we're abusing mapstruct to clone objects :)
@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        //
    }
)
public interface ObjectCloner {
    Marathon clone(Marathon source);
}
