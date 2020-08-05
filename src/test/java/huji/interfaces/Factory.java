package huji.interfaces;

public interface Factory<T,R> {
    T get(R value);
}
