package sn.gessy.l2gl.app.model;

import java.time.LocalDate;

public class Entretien extends Entite {

    private final Vehicule vehicule;
    private final LocalDate date;
    private final String description;
    private final int cout;

    public Entretien(Long id, Vehicule vehicule, LocalDate date, String description, int cout) {
        super(id);

        if (vehicule == null) {
            throw new IllegalArgumentException("Vehicule invalide");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date invalide");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description invalide");
        }
        if (cout < 0) {
            throw new IllegalArgumentException("Cout invalide");
        }

        this.vehicule = vehicule;
        this.date = date;
        this.description = description;
        this.cout = cout;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getCout() {
        return cout;
    }

    @Override
    public String afficher() {
        return "Entretien du véhicule : [" + vehicule.afficher() + "]" +
               ", Date : " + date +
               ", Description : " + description +
               ", Cout : " + cout + " FCFA";
    }
}
