package sn.gessy.l2gl.app.app;

import java.time.LocalDate;
import java.util.*;

import sn.gessy.l2gl.app.model.*;
import sn.gessy.l2gl.app.repo.InMemoryCrud;
import sn.gessy.l2gl.app.service.ParcAutoService;

public class Main {

    public static void main(String[] args) {

        Vehicule v1 = new Vehicule(1L, "DK-123-AA", "Toyota",   50000, EtatVehicule.DISPONIBLE,  2015);
        Vehicule v2 = new Vehicule(2L, "DK-456-BB", "BMW",     120000, EtatVehicule.DISPONIBLE,  2021);
        Vehicule v3 = new Vehicule(3L, "DK-789-CC", "Mercedes", 80000, EtatVehicule.DISPONIBLE,  2018);
        Vehicule v4 = new Vehicule(4L, "DK-321-DD", "Peugeot",  10000, EtatVehicule.DISPONIBLE,  2023);
        Vehicule v5 = new Vehicule(5L, "DK-654-EE", "Renault",  30000, EtatVehicule.EN_PANNE,    2020);

        Conducteur c1 = new Conducteur(10L, "Jean Diallo",  "Permis-B-123");
        Conducteur c2 = new Conducteur(11L, "Adama Sow",    "A1-456");

        Entretien e1 = new Entretien(101L, v1, LocalDate.now().minusDays(30), "Vidange complète",        5000);
        Entretien e2 = new Entretien(102L, v2, LocalDate.now().minusDays(10), "Changement plaquettes",  25000);
        Entretien e3 = new Entretien(103L, v2, LocalDate.now().minusDays(2),  "Contrôle freins",         8000);

        System.out.println("\n=== Étape 11 — Polymorphisme afficher() ===");

        List<Entite> toutesEntites = new ArrayList<>(
                List.of(v1, v2, v3, v4, v5, c1, c2, e1, e2));

        toutesEntites.forEach(e -> System.out.println("  • " + e.afficher()));

        System.out.println("\n--- Validation — messages d'erreur explicites ---");
        try {
            new Vehicule(99L, "", "Tesla", 0, EtatVehicule.DISPONIBLE, 2022);
        } catch (IllegalArgumentException ex) {
            System.out.println("  ✓ Vehicule immat vide → " + ex.getMessage());
        }
        try {
            new Conducteur(99L, null, "B-999");
        } catch (IllegalArgumentException ex) {
            System.out.println("  ✓ Conducteur nom null → " + ex.getMessage());
        }
        try {
            new Entretien(99L, v1, null, "Test", 0);
        } catch (IllegalArgumentException ex) {
            System.out.println("  ✓ Entretien date null → " + ex.getMessage());
        }

        System.out.println("\n=== Étape 12 — CRUD générique ===");

        InMemoryCrud<Vehicule>   repoVehicules   = new InMemoryCrud<>();
        InMemoryCrud<Conducteur> repoConducteurs = new InMemoryCrud<>();
        InMemoryCrud<Entretien>  repoEntretiens  = new InMemoryCrud<>();

        for (Vehicule v : List.of(v1, v2, v3, v4, v5)) repoVehicules.create(v);
        repoConducteurs.create(c1);
        repoConducteurs.create(c2);
        for (Entretien e : List.of(e1, e2, e3)) repoEntretiens.create(e);
        System.out.println("  Véhicules créés : " + repoVehicules.size());
        System.out.println("  Conducteurs créés : " + repoConducteurs.size());

        v3.setKilometrage(85000);
        repoVehicules.update(v3);
        System.out.println("  Après update v3 → km = " + repoVehicules.readOpt(3L).get().getKilometrage());

        repoVehicules.delete(5L);
        System.out.println("  Après delete v5 → taille = " + repoVehicules.size());

        System.out.println("\n--- Violations de contrat CRUD ---");
        try {
            repoVehicules.create(v1);
        } catch (IllegalStateException ex) {
            System.out.println("  ✓ Double create → " + ex.getMessage());
        }
        try {
            repoVehicules.delete(99L);
        } catch (NoSuchElementException ex) {
            System.out.println("  ✓ Delete id inconnu → " + ex.getMessage());
        }

        System.out.println("\n=== Étape 13 — Optional ===");

        Vehicule inconnu = repoVehicules.readOpt(99L)
                .orElse(new Vehicule(0L, "XX-000-ZZ", "Inconnu", 0, EtatVehicule.EN_PANNE, 2000));
        System.out.println("  orElse id=99 → " + inconnu.getMarque());

        try {
            repoVehicules.readOpt(88L)
                    .orElseThrow(() -> new NoSuchElementException("Véhicule id=88 introuvable dans le parc"));
        } catch (NoSuchElementException ex) {
            System.out.println("  orElseThrow id=88 → " + ex.getMessage());
        }

        repoVehicules.readOpt(1L)
                .ifPresent(v -> System.out.println("  ifPresent id=1 → trouvé : " + v.getMarque()
                        + " [" + v.getImmatriculation() + "]"));

        String marque = repoVehicules.readOpt(2L)
                .map(Vehicule::getMarque)
                .orElse("(non trouvé)");
        System.out.println("  map+orElse id=2 → " + marque);

        System.out.println("\n=== Étape 14 — record LigneRapport ===");

        ParcAutoService service = new ParcAutoService();
        repoVehicules.findAll().forEach(service::ajouterVehicule);
        service.ajouterEntretien(e1);
        service.ajouterEntretien(e2);
        service.ajouterEntretien(e3);

        List<LigneRapport> rapport = service.genererRapport();

        System.out.println("  " + String.format("%-14s | %-12s | %-15s | %s",
                "IMMAT", "MARQUE", "ÉTAT", "KILOMÉTRAGE"));
        System.out.println("  " + "-".repeat(58));
        rapport.forEach(lr -> System.out.println("  " + lr.afficher()));

        LigneRapport r1 = new LigneRapport("DK-123-AA", "Toyota", EtatVehicule.DISPONIBLE, 50000);
        LigneRapport r2 = new LigneRapport("DK-123-AA", "Toyota", EtatVehicule.DISPONIBLE, 50000);
        System.out.println("\n  r1.equals(r2) → " + r1.equals(r2) + "  (champs identiques)");
        System.out.println("  toString → " + r1);

        System.out.println("\n=== Étape 15 — Scénario final intégral ===");

        System.out.println("\n--- Locations ---");

        LocalDate debutLoc1  = LocalDate.now().minusDays(7);
        LocalDate debutLoc2  = LocalDate.now().minusDays(3);

        Location loc1 = new Location(201L, v1, c1, debutLoc1, 15000);
        Location loc2 = new Location(202L, v2, c2, debutLoc2, 20000);

        service.demarrerLocation(loc1);
        service.demarrerLocation(loc2);
        System.out.println("  Après démarrage :");
        System.out.println("  v1 état → " + v1.getEtat());
        System.out.println("  v2 état → " + v2.getEtat());

        try {
            Location locInvalide = new Location(299L, v1, c1, LocalDate.now(), 10000);
            service.demarrerLocation(locInvalide);
        } catch (IllegalStateException ex) {
            System.out.println("  ✓ Démarrage impossible → " + ex.getMessage());
        }

        service.terminerLocation(loc1, LocalDate.now().minusDays(1));
        System.out.printf("  loc1 terminée — durée : %d j, coût total : %d FCFA%n",
                loc1.dureeJours(), (int)(loc1.dureeJours() * loc1.getPrixJour()));
        System.out.println("  v1 état après fin location → " + v1.getEtat());

        try {
            loc1.terminer(LocalDate.now());
        } catch (IllegalStateException ex) {
            System.out.println("  ✓ Double fin refusée → " + ex.getMessage());
        }

        System.out.println("\n--- Entretiens ---");
        System.out.println("  Entretiens v1 (id=1) : " + service.getEntretiens(1L).size());
        System.out.println("  Entretiens v2 (id=2) : " + service.getEntretiens(2L).size());

        System.out.println("\n--- Statistiques (Étape 10 rappel) ---");
        System.out.printf("  Kilométrage moyen : %.0f km%n", service.kilometrageMoyen());
        System.out.println("  Par état :");
        service.nombreParEtat()
               .forEach((etat, nb) -> System.out.printf("    %-15s → %d%n", etat, nb));
        System.out.println("  Coût entretiens par véhicule :");
        service.coutTotalEntretiensParVehicule()
               .forEach((id, total) -> System.out.printf("    id=%-3d → %d FCFA%n", id, total));

        System.out.println("\n--- Rapport final (records) ---");
        List<LigneRapport> rapportFinal = service.genererRapport();
        System.out.println("  " + String.format("%-14s | %-12s | %-15s | %s",
                "IMMAT", "MARQUE", "ÉTAT", "KILOMÉTRAGE"));
        System.out.println("  " + "-".repeat(58));
        rapportFinal.forEach(lr -> System.out.println("  " + lr.afficher()));

        System.out.println("\n--- Véhicules à réviser (règle lambda) ---");
        int anneeActuelle = LocalDate.now().getYear();
        List<Vehicule> aReviser = service.vehiculesAReviser(
                v -> v.getKilometrage() > 70000 || (anneeActuelle - v.getAnnee()) > 7
        );
        if (aReviser.isEmpty()) {
            System.out.println("  Aucun véhicule à réviser.");
        } else {
            aReviser.forEach(v -> System.out.printf(
                    "  %-12s | %d km | %d ans%n",
                    v.getMarque(), v.getKilometrage(), anneeActuelle - v.getAnnee()));
        }
        System.out.println("\n=== Fin du scénario intégral ===");
    }
}
