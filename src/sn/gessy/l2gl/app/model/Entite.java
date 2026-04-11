package sn.gessy.l2gl.app.model;

public abstract class Entite implements Identifiable {
	private final Long id;

	protected Entite(Long id) {
		if(id == null) {
			throw new IllegalArgumentException("Le matricule ne peut pas etre vide");
		}
		this.id = id;
	}

	public final Long getId() {
		return id;
	}

	public abstract String afficher();
}
