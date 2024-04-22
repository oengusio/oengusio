package app.oengus.domain.volunteering;

import app.oengus.domain.OengusUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Team {
    private final int id;

    private String marathonId;
    private String name;
    private String description;
    private int size;
    private boolean applicationsOpen;
    private ZonedDateTime applicationOpenDate;
    private ZonedDateTime applicationCloseDate;

    private List<OengusUser> leaders = new ArrayList<>();
}
