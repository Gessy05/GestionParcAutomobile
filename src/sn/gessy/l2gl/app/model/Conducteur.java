package sn.gessy.l2gl.app.model;

public class Conducteur extends Entite{
	private final String nom;
	private final String permis;

	public Conducteur(Long id, String nom, String permis) {
		super(id);
		if (nom == null || nom.isBlank()) {
			throw new IllegalArgumentException("Le nom du conducteur ne peut pas être vide ou null");
		}
		if (permis == null || permis.isBlank()) {
			throw new IllegalArgumentException("Le numéro de permis ne peut pas être vide ou null");
		}
		this.nom = nom;
		this.permis = permis;
	}

	public String getNom() {
		return nom;
	}

	public String getPermis() {
		return permis;
	}
	@Override
	public String afficher() {
		return "ID: " + getId() +
				", Nom : " + nom +
				", Permis : " + permis;
	}
}
