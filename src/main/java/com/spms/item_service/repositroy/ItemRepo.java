package com.spms.item_service.repositroy;

import com.spms.item_service.modules.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepo extends MongoRepository<Item, String> {

    List<Item> findByCategory(String category);

    List<Item> findByStatus(String status);

    List<Item> findByCategoryAndStatus(String category, String status);

    List<Item> findByReportedByUserId(String userId);
}