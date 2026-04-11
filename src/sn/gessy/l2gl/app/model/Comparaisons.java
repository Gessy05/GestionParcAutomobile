package sn.gessy.l2gl.app.model;

@FunctionalInterface
public interface Comparaisons<T> {
	int comparer(T a, T b);
}
