package app.oengus.service.repository;

import app.oengus.dao.ApplicationRepository;
import app.oengus.dao.ApplicationUserInformationRepository;
import app.oengus.entity.dto.ApplicationDto;
import app.oengus.entity.dto.ApplicationUserInformationDto;
import app.oengus.entity.model.Application;
import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.helper.BeanHelper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public ApplicationUserInformation getInfoForUser(User user) throws NotFoundException {
        return this.applicationUserInformationRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    public ApplicationUserInformation update(User user, ApplicationUserInformationDto dto) {
        ApplicationUserInformation infoForUser = this.applicationUserInformationRepository.findByUser(user).orElse(null);

        if (infoForUser == null) {
            infoForUser = new ApplicationUserInformation();
            infoForUser.setId(-1);
            infoForUser.setUser(user);
        }

        BeanHelper.copyProperties(dto, infoForUser);

        return this.applicationUserInformationRepository.save(infoForUser);
    }


    public List<Application> getApplications(Marathon marathon) {
        return this.applicationRepository.findByMarathon(marathon);
    }

    public List<Application> getApplications(String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);

        return this.getApplications(marathon);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Application updateApplication(User user, Marathon marathon, ApplicationDto data) {
        final Application application = this.applicationRepository.findByMarathonAndUser(marathon, user).orElseGet(() -> {
            final Application app = new Application();
            app.setId(-1);
            app.setUser(user);
            app.setMarathon(marathon);

            return app;
        });

        BeanHelper.copyProperties(data, application);

        application.setUpdatedAt(LocalDateTime.now());

        return this.applicationRepository.save(application);
    }
}
