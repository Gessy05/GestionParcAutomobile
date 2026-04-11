package sn.gessy.l2gl.app.model;

import java.util.Objects;

public class Vehicule extends Entite {

    private final String immatriculation;
    private final String marque;
    private int kilometrage;
    private EtatVehicule etat;
    private final int annee;

    public Vehicule(Long id,String immatriculation, String marque, int kilometrage, EtatVehicule etat, int annee) {
    	super(id);
        if (immatriculation == null || immatriculation.isEmpty()) {
            throw new IllegalArgumentException("Immatriculation invalide");
        }
        if (marque == null || marque.isEmpty()) {
            throw new IllegalArgumentException("Marque invalide");
        }
        if (kilometrage < 0) {
            throw new IllegalArgumentException("Kilometrage invalide");
        }
        if (annee < 1990) {
            throw new IllegalArgumentException("Année invalide");
        }

        this.immatriculation = immatriculation;
        this.marque = marque;
        this.kilometrage = kilometrage;
        this.etat = etat;
        this.annee = annee;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public int getKilometrage() {
        return kilometrage;
    }

    public EtatVehicule getEtat() {
        return etat;
    }

    public int getAnnee() {
        return annee;
    }

    public void setKilometrage(int km) {
        if (km < 0) {
            throw new IllegalArgumentException("Kilometrage invalide");
        }
        this.kilometrage = km;
    }

    public void setEtat(EtatVehicule e) {
        if (e == null) {
            throw new IllegalArgumentException("Etat invalide");
        }
        this.etat = e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicule)) return false;
        Vehicule autre = (Vehicule) o;
        return Objects.equals(immatriculation, autre.immatriculation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(immatriculation);
    }

    @Override
    public String afficher() {
        return "ID: " + getId() +
               ", Immatriculation : " + immatriculation +
               ", Marque : " + marque +
               ", Kilométrage : " + kilometrage +
               ", Etat : " + etat +
               ", Mise En Service (Année) : " + annee;
    }
}
