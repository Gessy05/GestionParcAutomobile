package sn.gessy.l2gl.app.model;

@FunctionalInterface
public interface Transformations<T,R> {
	R convertir(T entree);
}
