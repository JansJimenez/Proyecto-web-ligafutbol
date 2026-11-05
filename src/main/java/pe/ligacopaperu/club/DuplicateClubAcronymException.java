package pe.ligacopaperu.club;

public class DuplicateClubAcronymException extends RuntimeException {
    public DuplicateClubAcronymException() {
        super("Ya existe un club con esas siglas.");
    }
}
