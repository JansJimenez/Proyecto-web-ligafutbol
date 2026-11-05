package pe.ligacopaperu.cuerpotecnico;

public enum StaffRole {
    HEAD_COACH("Director técnico"),
    ASSISTANT_COACH("Asistente técnico"),
    GOALKEEPER_COACH("Entrenador de arqueros"),
    FITNESS_COACH("Preparador físico"),
    PHYSIOTHERAPIST("Fisioterapeuta"),
    DELEGATE("Delegado"),
    OTHER("Otro");

    private final String label;

    StaffRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
