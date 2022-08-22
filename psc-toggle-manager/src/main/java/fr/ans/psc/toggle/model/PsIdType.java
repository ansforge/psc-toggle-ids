package fr.ans.psc.toggle.model;

public enum PsIdType {
    ADELI("0"),
    FINESS("3"),
    SIRET("5"),
    RPPS("8"),
    RAW("");

    /** The value. */
    public String value;

    /**
     * Instantiates a new id type.
     *
     * @param value the value
     */
    PsIdType(String value) {
        this.value = value;
    }
}
