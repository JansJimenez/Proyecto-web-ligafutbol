package pe.ligacopaperu.club;

public class ClubNotFoundException extends RuntimeException {
    public ClubNotFoundException(long id) {
        super("No se encontró el club solicitado.");
    }
}
