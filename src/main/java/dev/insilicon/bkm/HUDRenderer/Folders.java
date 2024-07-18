package dev.insilicon.bkm.HUDRenderer;

public enum Folders {
    GENERAL("General"),
    STAFF("Staff");

    private final String display;

    Folders(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
