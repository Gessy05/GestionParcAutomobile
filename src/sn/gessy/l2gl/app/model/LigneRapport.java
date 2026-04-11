package sn.gessy.l2gl.app.model;

public record LigneRapport(
        String       immat,
        String       marque,
        EtatVehicule etat,
        int          km
) {

    public LigneRapport {
        if (immat == null || immat.isBlank())
            throw new IllegalArgumentException("Immatriculation obligatoire dans LigneRapport");
        if (marque == null || marque.isBlank())
            throw new IllegalArgumentException("Marque obligatoire dans LigneRapport");
        if (km < 0)
            throw new IllegalArgumentException("Kilométrage négatif dans LigneRapport");
    }

    public String afficher() {
        return String.format("%-14s | %-12s | %-15s | %7d km",
                immat, marque, etat, km);
    }
}
