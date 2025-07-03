package app.oengus.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SavedGame {
    private final int id;
    private final int userId;

    private String name;
    private String description;
    private String console;
    private String ratio;
    private boolean emulated;

    private List<SavedCategory> categories = new ArrayList<>();
}
