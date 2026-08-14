package com.spms.item_service.service.Impl;

import com.spms.item_service.dto.ItemDto;
import com.spms.item_service.modules.Item;
import com.spms.item_service.repositroy.ItemRepo;
import com.spms.item_service.service.GcsService;
import com.spms.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private GcsService gcsService;

    @Override
    public Item createItem(ItemDto itemDto, MultipartFile image) throws IOException {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setCategory(itemDto.getCategory());
        item.setLocation(itemDto.getLocation());
        item.setReportedByUserId(itemDto.getReportedByUserId());
        item.setContactEmail(itemDto.getContactEmail());
        item.setContactPhone(itemDto.getContactPhone());

        // GCS ekata upload karanna try karanawa, key na nam / fail una nam skip karala
        // image ekak nathuwa item eka save karanawa (app eka crash wenna denne ne)
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = gcsService.uploadFile(image);
                item.setImageUrl(imageUrl);
            } catch (Exception e) {
                System.out.println("!!! GCS upload eka skip una (key na wenna puluwan): "
                        + e.getClass().getSimpleName() + " - " + e.getMessage());
                // item.setImageUrl(null) - default eka thamai, ithin ehema thiyenna denawa
            }
        }

        return itemRepo.save(item);
    }


    @Override
    public List<Item> getAllItems() {
        return itemRepo.findAll();
    }

    @Override
    public Item getItemById(String id) {
        return itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    @Override
    public List<Item> getItemsByCategory(String category) {
        return itemRepo.findByCategory(category.toUpperCase());
    }

    @Override
    public List<Item> getItemsByStatus(String status) {
        return itemRepo.findByStatus(status.toUpperCase());
    }

    @Override
    public List<Item> getItemsByUser(String userId) {
        return itemRepo.findByReportedByUserId(userId);
    }

    @Override
    public Item updateItemStatus(String id, String status) {
        Item item = getItemById(id);
        item.setStatus(status.toUpperCase());
        return itemRepo.save(item);
    }

    @Override
    public void deleteItem(String id) throws IOException {
        Item item = getItemById(id);
        // Delete image from GCS if exists
        if (item.getImageUrl() != null) {
            gcsService.deleteFile(item.getImageUrl());
        }
        itemRepo.deleteById(id);
    }
}