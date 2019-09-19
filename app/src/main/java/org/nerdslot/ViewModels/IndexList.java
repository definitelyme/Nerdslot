package org.nerdslot.ViewModels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class IndexList<E> extends AbstractList<E> {
    private final Map<Integer, E> indexObj;
    private final Map<String, Integer> keyIndex;
    private final Function<E, String> getKey;

    public IndexList(Function<E, String> getKey) {
        this.getKey = getKey;
        this.indexObj = new HashMap<>();
        this.keyIndex = new HashMap<>();
    }

    @Override
    public E get(int i) {
        return indexObj.get(i);
    }

    @Override
    public E set(int index, E element) {
        E oldValue = get(index);
        indexObj.remove(index);
        indexObj.put(index, element);
        return oldValue;
    }

    @Override
    public int size() {
        return keyIndex.size();
    }

    public int indexOf(@Nullable String key) {
        return keyIndex.get(key);
    }

    @Override
    public boolean add(E e) {
        String key = getKey.apply(e);

        if (keyIndex.containsKey(key))
            throw new IllegalArgumentException("Key '" + key + "' duplication");

        int index = size();
        keyIndex.put(key, index);
        indexObj.put(index, e);
        return true;
    }

    @Override
    public E remove(int index) {
        for (Map.Entry<String, Integer> entry : keyIndex.entrySet()) {
            String hashMapKey = entry.getKey();
            Integer objKey = entry.getValue();
            if (objKey == index) {
                indexObj.remove(index);
                keyIndex.remove(hashMapKey);
                break;
            }
        }

        return get(index);
    }

    @Override
    public boolean removeIf(@NonNull Predicate<? super E> filter) {
        return false;
    }
}
