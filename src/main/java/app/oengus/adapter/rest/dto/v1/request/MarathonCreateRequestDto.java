package app.oengus.adapter.rest.dto.v1.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Getter
@Setter
public class MarathonCreateRequestDto {
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[\\w\\-]{4,10}$")
    private String id;

    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- \\p{L}]{4,40}$")
    private String name;

    @NotNull
    private ZonedDateTime startDate;

    @NotNull
    private ZonedDateTime endDate;

    private Boolean onsite = false;
    private Integer maxGamesPerRunner = 5;
    private Integer maxCategoriesPerGame = 3;
    private Integer maxNumberOfScreens = 4;
    private Boolean discordPrivacy = false;
    private Boolean submitsOpen = false;
    private Boolean selectionDone = false;
    private Boolean videoRequired = true;
    private Boolean unlimitedGames = false;
    private Boolean unlimitedCategories = false;
    private Boolean emulatorAuthorized = true;
    private Boolean isPrivate = true;
    private Boolean announceAcceptedSubmissions = false;
}
