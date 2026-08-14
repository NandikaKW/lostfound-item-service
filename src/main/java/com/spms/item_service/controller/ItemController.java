package com.spms.item_service.controller;

import com.spms.item_service.dto.ItemDto;
import com.spms.item_service.modules.Item;
import com.spms.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createItem(
            @RequestParam("name")              String name,
            @RequestParam("description")       String description,
            @RequestParam("category")          String category,
            @RequestParam("location")          String location,
            @RequestParam(value = "reportedByUserId", required = false) String reportedByUserId,
            @RequestParam(value = "contactEmail",     required = false) String contactEmail,
            @RequestParam(value = "contactPhone",     required = false) String contactPhone,
            @RequestParam(value = "image",            required = false) MultipartFile image
    ) {
        System.out.println("=== createItem req ekak awa ===");
        System.out.println("name=" + name + " category=" + category + " location=" + location);
        System.out.println("image present? " + (image != null && !image.isEmpty()));
        if (image != null) {
            System.out.println("image originalFilename=" + image.getOriginalFilename()
                    + " size=" + image.getSize() + " contentType=" + image.getContentType());
        }

        try {
            ItemDto dto = new ItemDto();
            dto.setName(name);
            dto.setDescription(description);
            dto.setCategory(category);
            dto.setLocation(location);
            dto.setReportedByUserId(reportedByUserId);
            dto.setContactEmail(contactEmail);
            dto.setContactPhone(contactPhone);

            Item created = itemService.createItem(dto, image);
            System.out.println("=== item hariyata save una, id=" + created.getId() + " ===");
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        }  catch (Exception e) {
        System.out.println("createItem eken exception ekak awa: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Item creation failed: " + e.getMessage());
    }
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(itemService.getItemById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Item>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Item>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(itemService.getItemsByStatus(status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Item>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(itemService.getItemsByUser(userId));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        try {
            return ResponseEntity.ok(itemService.updateItemStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable String id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ─── HEALTH CHECK ─────────────────────────────────────────────────────────

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("item-service is running ✓");
    }
}