package com.spms.item_service.service;

import com.spms.item_service.dto.ItemDto;
import com.spms.item_service.modules.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ItemService {

    Item createItem(ItemDto itemDto, MultipartFile image) throws IOException;

    List<Item> getAllItems();

    Item getItemById(String id);

    List<Item> getItemsByCategory(String category);   // "LOST" or "FOUND"

    List<Item> getItemsByStatus(String status);

    List<Item> getItemsByUser(String userId);

    Item updateItemStatus(String id, String status);

    void deleteItem(String id) throws IOException;
}