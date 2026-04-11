package sn.gessy.l2gl.app.service;

import java.util.*;
import java.util.stream.*;
import sn.gessy.l2gl.app.model.*;
import java.time.LocalDate;

public class ParcAutoService {

    public List<Vehicule> filtrerVehicules(List<Vehicule> src, Tests<Vehicule> regle) {
        List<Vehicule> resultat = new ArrayList<>();
        for (Vehicule v : src) {
            if (regle.verifier(v)) resultat.add(v);
        }
        return resultat;
    }

    public List<String> mapperVehicules(List<Vehicule> src, Transformations<Vehicule, String> f) {
        List<String> resultat = new ArrayList<>();
        for (Vehicule v : src) resultat.add(f.convertir(v));
        return resultat;
    }

    public void appliquerSurVehicules(List<Vehicule> src, Actions<Vehicule> action) {
        for (Vehicule v : src) action.executer(v);
    }

    public void trierVehicules(List<Vehicule> src, Comparaisons<Vehicule> cmp) {
        for (int i = 0; i < src.size() - 1; i++) {
            for (int j = i + 1; j < src.size(); j++) {
                if (cmp.comparer(src.get(i), src.get(j)) > 0) {
                    Vehicule temp = src.get(i);
                    src.set(i, src.get(j));
                    src.set(j, temp);
                }
            }
        }
    }

    private final List<Vehicule>         vehicules       = new ArrayList<>();
    private final Map<String, Vehicule>  indexParImmat   = new HashMap<>();

    public void ajouterVehicule(Vehicule v) {
        if (v == null) throw new IllegalArgumentException("Vehicule invalide");
        if (indexParImmat.containsKey(v.getImmatriculation())) {
            System.out.println("⚠ Refus : immatriculation déjà présente → " + v.getImmatriculation());
            return;
        }
        vehicules.add(v);
        indexParImmat.put(v.getImmatriculation(), v);
    }

    public void supprimerVehicule(String immat) {
        Vehicule v = indexParImmat.remove(immat);
        if (v == null) {
            System.out.println("⚠ Aucun véhicule trouvé avec : " + immat);
            return;
        }
        vehicules.remove(v);
        System.out.println("✓ Supprimé : " + immat);
    }

    public Vehicule rechercher(String immat) {
        return indexParImmat.get(immat);
    }

    public List<Vehicule> getVehicules() {
        return Collections.unmodifiableList(vehicules);
    }

    public Set<Vehicule> vehiculesUniques() {
        return new HashSet<>(vehicules);
    }

    private final Map<Long, List<Entretien>> entretiensParVehiculeId = new HashMap<>();

    public void ajouterEntretien(Entretien e) {
        if (e == null) throw new IllegalArgumentException("Entretien invalide");
        Long vid = e.getVehicule().getId();

        entretiensParVehiculeId.computeIfAbsent(vid, k -> new ArrayList<>()).add(e);
    }

    public List<Entretien> getEntretiens(Long vehiculeId) {
        return entretiensParVehiculeId.getOrDefault(vehiculeId, Collections.emptyList());
    }

    public List<Vehicule> vehiculesDisponibles() {
        return vehicules.stream()
                .filter(v -> v.getEtat() == EtatVehicule.DISPONIBLE)
                .collect(Collectors.toList());
    }

    public List<String> immatriculationsTriees() {
        return vehicules.stream()
                .map(Vehicule::getImmatriculation)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Vehicule> top3PlusGrandKilometrage() {
        return vehicules.stream()
                .sorted(Comparator.comparingInt(Vehicule::getKilometrage).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    public double kilometrageMoyen() {
        return vehicules.stream()
                .mapToInt(Vehicule::getKilometrage)
                .average()
                .orElse(0.0);
    }

    public Map<EtatVehicule, Long> nombreParEtat() {
        return vehicules.stream()
                .collect(Collectors.groupingBy(Vehicule::getEtat, Collectors.counting()));
    }

    public Map<Long, Integer> coutTotalEntretiensParVehicule() {
        return entretiensParVehiculeId.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                      .mapToInt(Entretien::getCout)
                                      .sum()
                ));
    }

    public List<LigneRapport> genererRapport() {
        return vehicules.stream()
                .map(v -> new LigneRapport(
                        v.getImmatriculation(),
                        v.getMarque(),
                        v.getEtat(),
                        v.getKilometrage()))
                .collect(Collectors.toList());
    }

    private final List<Location> locations = new ArrayList<>();

    public void demarrerLocation(Location loc) {
        if (loc == null)
            throw new IllegalArgumentException("Location invalide");
        if (loc.getVehicule().getEtat() != EtatVehicule.DISPONIBLE)
            throw new IllegalStateException(
                "Le véhicule " + loc.getVehicule().getImmatriculation() +
                " n'est pas disponible (état : " + loc.getVehicule().getEtat() + ")");
        loc.getVehicule().setEtat(EtatVehicule.EN_LOCATION);
        locations.add(loc);
    }

    public void terminerLocation(Location loc, java.time.LocalDate dateFin) {
        if (loc == null)
            throw new IllegalArgumentException("Location invalide");
        if (!locations.contains(loc))
            throw new IllegalStateException("Cette location n'est pas enregistrée dans le service");
        loc.terminer(dateFin);
        loc.getVehicule().setEtat(EtatVehicule.DISPONIBLE);
    }

    public List<Vehicule> vehiculesAReviser(Tests<Vehicule> regle) {
        return vehicules.stream()
                .filter(regle::verifier)
                .collect(Collectors.toList());
    }

    public List<Location> getLocations() {
        return Collections.unmodifiableList(locations);
    }
}
