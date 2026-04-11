package sn.gessy.l2gl.app.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Location extends Entite {

    private final Vehicule vehicule;
    private final Conducteur conducteur;
    private final LocalDate dateDebut;
    private Optional<LocalDate> dateFin;
    private final int prixJour;

    public Location(Long id, Vehicule vehicule, Conducteur conducteur, LocalDate dateDebut, int prixJour) {
        super(id);

        if (vehicule == null) throw new IllegalArgumentException("Vehicule invalide");
        if (conducteur == null) throw new IllegalArgumentException("Conducteur invalide");
        if (dateDebut == null) throw new IllegalArgumentException("Date de début invalide");
        if (prixJour < 0) throw new IllegalArgumentException("Prix du jour invalide");

        this.vehicule = vehicule;
        this.conducteur = conducteur;
        this.dateDebut = dateDebut;
        this.dateFin = Optional.empty();
        this.prixJour = prixJour;
    }

    public Vehicule getVehicule() { return vehicule; }
    public Conducteur getConducteur() { return conducteur; }
    public LocalDate getDateDebut() { return dateDebut; }
    public Optional<LocalDate> getDateFin() { return dateFin; }
    public int getPrixJour() { return prixJour; }

    public void terminer(LocalDate fin) {
        if (fin == null) {
            throw new IllegalArgumentException("La date de fin ne peut pas être null");
        }
        if (dateFin.isPresent()) {
            throw new IllegalStateException("La location est déjà terminée le " + dateFin.get());
        }
        if (fin.isBefore(dateDebut)) {
            throw new IllegalArgumentException(
                "La date de fin (" + fin + ") ne peut pas être avant la date de début (" + dateDebut + ")");
        }
        this.dateFin = Optional.of(fin);
    }

    public long dureeJours() {
        LocalDate fin = dateFin.orElse(LocalDate.now());
        long jours = ChronoUnit.DAYS.between(dateDebut, fin);
        return Math.max(1, jours);
    }

    public boolean estTerminee() {
        return dateFin.isPresent();
    }

    @Override
    public String afficher() {
        return "ID: " + getId() +
               ", Location → Véhicule : " + vehicule.getMarque() + " [" + vehicule.getImmatriculation() + "]" +
               ", Conducteur : " + conducteur.getNom() +
               ", Début : " + dateDebut +
               ", Fin : " + dateFin.map(LocalDate::toString).orElse("en cours") +
               ", Durée : " + dureeJours() + " j" +
               ", Prix/j : " + prixJour + " FCFA";
    }
}
