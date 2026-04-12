package com.example.apidressing.repository;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ClothingItemRepository {

    private final Map<Long, ClothingItem> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public ClothingItem save(ClothingItem item) {
        if (item.getId() == null) {
            item.id(idSequence.getAndIncrement());
        }
        store.put(item.getId(), item);
        return item;
    }

    public List<ClothingItem> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<ClothingItem> findByLayer(ClothingLayer layer) {
        return store.values().stream()
                .filter(item -> layer.equals(item.getLayer()))
                .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}