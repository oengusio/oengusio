package app.oengus.entity.constants;

import javax.validation.constraints.NotNull;

public enum TShirtSize {
    XS("XS"),
    S("X"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("2XL"),
    XXXL("3XL"),
    XXXXL("4XL"),
    XXXXXL("5XL"),
    XXXXXXL("6XL");

    private final String display;

    TShirtSize(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public static TShirtSize fromString(@NotNull String string) {
        for (final TShirtSize value : values()) {
            if (value.display.equals(string) || value.name().equals(string)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant " + TShirtSize.class.getCanonicalName() + "." + string);
    }

    @Override
    public String toString() {
        return display;
    }
}
