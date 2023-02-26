package app.oengus.entity.dto.v2.marathon;

public class GameDto {
    private int id;
    private String name;
    private String description;
    private String console;
    private String ratio;
    private boolean emulated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public boolean isEmulated() {
        return emulated;
    }

    public void setEmulated(boolean emulated) {
        this.emulated = emulated;
    }
}
