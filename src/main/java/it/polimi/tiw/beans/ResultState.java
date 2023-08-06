package it.polimi.tiw.beans;

public enum ResultState {
    NULL(0, "Non inserito"),
    INSERITO(1, "Inserito"),
    PUBBLICATO(2, "Pubblicato"),
    VERBALIZZATO(3, "Verbalizzato");

    private final int value;
    private final String description;

    ResultState(int value, String description) {
        this.value = value;
        this.description = description;
    }
    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ResultState fromValue(int value) {
        for (ResultState status : ResultState.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status value: " + value);
    }

    // implement < and >

}
