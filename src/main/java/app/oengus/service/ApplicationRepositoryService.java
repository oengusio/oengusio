package app.oengus.service;

import app.oengus.dao.ApplicationRepository;
import app.oengus.dao.ApplicationUserInformationRepository;
import app.oengus.entity.dto.ApplicationUserInformationDto;
import app.oengus.entity.model.Application;
import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.helper.BeanHelper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationRepositoryService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationUserInformationRepository applicationUserInformationRepository;

    @Autowired
    public ApplicationRepositoryService(
        final ApplicationRepository applicationRepository,
        final ApplicationUserInformationRepository applicationUserInformationRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.applicationUserInformationRepository = applicationUserInformationRepository;
    }

    // TODO: throw on missing?
    public ApplicationUserInformation getInfoForUser(User user) {
        return this.applicationUserInformationRepository.findByUser(user);
    }

    public ApplicationUserInformation update(User user, ApplicationUserInformationDto dto) {
        ApplicationUserInformation infoForUser = this.applicationUserInformationRepository.findByUser(user);

        if (infoForUser == null) {
            infoForUser = new ApplicationUserInformation();

            infoForUser.setUser(user);
        }

        BeanHelper.copyProperties(dto, infoForUser);

        return this.applicationUserInformationRepository.save(infoForUser);
    }


    public List<Application> getApplications(Marathon marathon) {
        return new ArrayList<>();
    }
}
