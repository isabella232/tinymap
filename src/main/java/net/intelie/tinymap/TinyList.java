package net.intelie.tinymap;

import net.intelie.tinymap.util.Preconditions;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Objects;
import java.util.function.Consumer;

public class TinyList<T> extends AbstractList<T> implements Serializable {
    private final Object[] values;

    public TinyList(Object[] values) {
        this.values = values;
    }

    public static <T> TinyListBuilder<T> builder() {
        return new TinyListBuilder<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int index) {
        Preconditions.checkElementIndex(index, values.length);
        return (T) values[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEach(Consumer<? super T> action) {
        Object[] values = this.values;
        int length = values.length;
        for (int i = 0; i < length; i++) {
            action.accept((T) values[i]);
        }
    }

    @Override
    public int size() {
        return values.length;
    }
}
