package sn.gessy.l2gl.app.app;

import java.util.ArrayList;
import java.util.List;

import sn.gessy.l2gl.app.model.*;
import sn.gessy.l2gl.app.service.ParcAutoService;

public class Test {
	public static void main(String[] args) {

		Vehicule v1 = new Vehicule(1L, "DK-123-AA", "Toyota", 50000, EtatVehicule.DISPONIBLE, 2015);
		Vehicule v2 = new Vehicule(2L, "DK-456-BB", "BMW", 120000, EtatVehicule.EN_PANNE, 2021);
		Vehicule v3 = new Vehicule(3L, "DK-789-CC", "Mercedes", 80000, EtatVehicule.DISPONIBLE, 2018);
		Vehicule v4 = new Vehicule(4L, "DK-321-DD", "Peugeot", 10000, EtatVehicule.EN_LOCATION, 2023);
		Vehicule v5 = new Vehicule(5L, "DK-654-EE", "Renault", 30000, EtatVehicule.DISPONIBLE, 2020);
		List<Vehicule> flotte = new ArrayList<>(List.of(v1, v2, v3, v4, v5));

		Conducteur c1 = new Conducteur(10L, "Jean", "Permis-B-123");
		Conducteur c2 = new Conducteur(11L, "Adama", "A1-456");

		Entretien e1 = new Entretien(101L, v1, java.time.LocalDate.now(), "Vidange", 5000);
		Location loc1 = new Location(201L, v4, c1, java.time.LocalDate.now(), 15000);

		Tests<Vehicule> c1_dispo = v -> v.getEtat() == EtatVehicule.DISPONIBLE;
		Tests<Vehicule> c2_panne = v -> v.getEtat() == EtatVehicule.EN_PANNE;
		Tests<Vehicule> c3_seuilKm = v -> v.getKilometrage() > 100000;
		Tests<Vehicule> c4_aReviser = v -> v.getKilometrage() > 100000 || v.getAnnee() < 2018;
		Tests<Conducteur> c5_permisB = c -> c.getPermis().contains("B");

		Transformations<Vehicule, String> c6_resume = v -> v.getMarque() + " (" + v.getImmatriculation() + ")";
		Transformations<Vehicule, String> c7_immat = v -> v.getImmatriculation();
		Transformations<Vehicule, Integer> c8_age = v -> java.time.LocalDate.now().getYear() - v.getAnnee();
		Transformations<Entretien, Integer> c9_coutTotal = e -> (int)(e.getCout() * 1.18);

		Actions<Vehicule> c10_goRevision = v -> v.setEtat(EtatVehicule.EN_REVISION);
		Actions<Vehicule> c11_ajouter100Km = v -> v.setKilometrage(v.getKilometrage() + 100);
		Actions<Location> c12_finirLoc = l -> l.terminer(java.time.LocalDate.now());

		Comparaisons<Vehicule> c13_triKm = (va, vb) -> Integer.compare(va.getKilometrage(), vb.getKilometrage());
		Comparaisons<Vehicule> c14_triImmat = (va, vb) -> va.getImmatriculation().compareTo(vb.getImmatriculation());

		ParcAutoService service = new ParcAutoService();

		System.out.println("--- DÉMO DES 14 COMPORTEMENTS ---");
		System.out.println("Véhicules à réviser : " + service.filtrerVehicules(flotte, c4_aReviser).size());
		System.out.println("Immatriculations : " + service.mapperVehicules(flotte, c7_immat));

		service.trierVehicules(flotte, c13_triKm);
		System.out.println("Premier véhicule après tri KM : " + flotte.get(0).getMarque());

		service.appliquerSurVehicules(flotte, c11_ajouter100Km);
		System.out.println("KM de v1 après action : " + v1.getKilometrage());

		Tests<Vehicule> estEnPanne = vh -> vh.getEtat() == EtatVehicule.EN_PANNE;
		Tests<Vehicule> aReviser = vh -> vh.getKilometrage() > 100000 || vh.getAnnee() < 2018;

		Actions<Vehicule> reparer = vh -> vh.setEtat(EtatVehicule.DISPONIBLE);

		Transformations<Vehicule, String> GenererBadge = vh -> "VÉHICULE: " + vh.getMarque() + " ["
				+ vh.getImmatriculation() + "]";

		System.out.println("V1 est-il en panne ? " + estEnPanne.verifier(v1));
		System.out.println("V2 doit-il être révisé ? " + aReviser.verifier(v2));

		System.out.println("Badge V1 : " + GenererBadge.convertir(v1));

		System.out.println("État V2 avant : " + v2.getEtat());
		reparer.executer(v2);
		System.out.println("État V2 après réparation : " + v2.getEtat());
		System.out.println("\n\nArrivage de la partie 3");

		List<Vehicule> maListe = new ArrayList<>();
		maListe.add(v1);
		maListe.add(v2);

		ParcAutoService service1 = new ParcAutoService();

		System.out.println("\n--- Arrivage de la partie 3 ---");

		List<Vehicule> resultat = service1.filtrerVehicules(maListe, v -> v.getEtat() == EtatVehicule.EN_PANNE);

		System.out.println("Nombre de véhicules trouvés : " + resultat.size());

		System.out.println("\n--- Liste des Badges ---");
		List<String> badges = service1.mapperVehicules(maListe, GenererBadge);
		badges.forEach(System.out::println);

		System.out.println("\n--- Action : Mise en révision collective ---");
		service1.appliquerSurVehicules(maListe, vh -> vh.setEtat(EtatVehicule.EN_REVISION));
		maListe.forEach(v -> System.out.println(v.getMarque() + " est maintenant en : " + v.getEtat()));

		maListe.add(v3);
		maListe.add(v4);
		maListe.add(v5);

		System.out.println("\n--- Avant le tri (Kilométrage) ---");
		maListe.forEach(v -> System.out.println(v.getMarque() + " : " + v.getKilometrage() + " km"));

		service1.trierVehicules(maListe, (va, vb) -> Integer.compare(va.getKilometrage(), vb.getKilometrage()));

		System.out.println("\n--- Après le tri (Croissant) ---");
		maListe.forEach(v -> System.out.println(v.getMarque() + " : " + v.getKilometrage() + " km"));

		System.out.println("\n--- Véhicules nécessitant une révision ---");
		List<Vehicule> urgents = service1.filtrerVehicules(maListe, aReviser);
		urgents.forEach(v -> System.out
				.println(v.getMarque() + " (Année: " + v.getAnnee() + ", KM: " + v.getKilometrage() + ")"));

		System.out.println("\nFin des tests du Parc Auto.");

		System.out.println("\n\n══════ Étape 6 : List + Map ══════");
		ParcAutoService s6 = new ParcAutoService();
		s6.ajouterVehicule(v1);
		s6.ajouterVehicule(v2);
		s6.ajouterVehicule(v3);
		s6.ajouterVehicule(v4);
		s6.ajouterVehicule(v5);

		Vehicule trouve = s6.rechercher("DK-456-BB");
		System.out.println("Recherche DK-456-BB → " + (trouve != null ? trouve.getMarque() : "introuvable"));

		Vehicule absent = s6.rechercher("XX-999-ZZ");
		System.out.println("Recherche XX-999-ZZ → " + (absent != null ? absent.getMarque() : "introuvable"));

		s6.supprimerVehicule("DK-123-AA");
		System.out.println("Après suppression DK-123-AA, taille flotte : " + s6.getVehicules().size());

		System.out.println("\n\n══════ Étape 7 : Set + unicité ══════");

		Vehicule doublon = new Vehicule(99L, "DK-456-BB", "Copie", 5000, EtatVehicule.DISPONIBLE, 2022);
		s6.ajouterVehicule(doublon);

		java.util.Set<Vehicule> uniques = s6.vehiculesUniques();
		System.out.println("Nombre de véhicules uniques : " + uniques.size());

		System.out.println("\n\n══════ Étape 8 : Entretiens par véhicule ══════");
		Entretien e2 = new Entretien(102L, v2, java.time.LocalDate.now().minusDays(10), "Changement plaquettes", 25000);
		Entretien e3 = new Entretien(103L, v2, java.time.LocalDate.now(), "Vidange", 8000);

		s6.ajouterEntretien(e1);
		s6.ajouterEntretien(e2);
		s6.ajouterEntretien(e3);

		System.out.println("Entretiens de v1 (id=1) : " + s6.getEntretiens(1L).size());
		System.out.println("Entretiens de v2 (id=2) : " + s6.getEntretiens(2L).size());
		System.out.println("Entretiens de v3 (id=3) : " + s6.getEntretiens(3L).size() + " (liste vide, pas null)");

		System.out.println("\n\n══════ Étape 9 : Streams — filter / map / sort ══════");

		System.out.println("Véhicules disponibles :");
		s6.vehiculesDisponibles()
		  .forEach(v -> System.out.println("  " + v.getMarque() + " [" + v.getImmatriculation() + "]"));

		System.out.println("Immatriculations triées : " + s6.immatriculationsTriees());

		System.out.println("Top 3 kilométrage :");
		s6.top3PlusGrandKilometrage()
		  .forEach(v -> System.out.printf("  %-12s %d km%n", v.getMarque(), v.getKilometrage()));

		System.out.println("\n\n══════ Étape 10 : Streams — stats & groupingBy ══════");

		System.out.printf("Kilométrage moyen : %.0f km%n", s6.kilometrageMoyen());

		System.out.println("Véhicules par état :");
		s6.nombreParEtat()
		  .forEach((etat, nb) -> System.out.printf("  %-15s → %d%n", etat, nb));

		System.out.println("Coût total entretiens par véhicule :");
		s6.coutTotalEntretiensParVehicule()
		  .forEach((id, total) -> System.out.printf("  Véhicule id=%-3d → %d FCFA%n", id, total));
	}
}
