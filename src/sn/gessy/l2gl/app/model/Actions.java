package sn.gessy.l2gl.app.model;

@FunctionalInterface
public interface Actions<T> {
	void executer(T cible);
}
