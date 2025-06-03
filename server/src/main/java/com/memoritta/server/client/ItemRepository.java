package com.memoritta.server.client;

import com.memoritta.server.dao.ItemDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends MongoRepository<ItemDao, UUID> {
    Optional<ItemDao> findById(UUID id);
    List<ItemDao> findByCreatedBy(UUID userId);
    List<ItemDao> findByDescriptionBarcode(String barcode);
}