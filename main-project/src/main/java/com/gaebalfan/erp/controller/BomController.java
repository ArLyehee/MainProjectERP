package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Bom;
import com.gaebalfan.erp.domain.BomItem;
import com.gaebalfan.erp.service.BomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bom")
public class BomController {

    private final BomService service;

    public BomController(BomService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Bom>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<BomItem>> findItems(@PathVariable Long id) {
        return ResponseEntity.ok(service.findItems(id));
    }

    @PostMapping
    public ResponseEntity<Bom> insertBom(@RequestBody Bom bom) {
        service.insertBom(bom);
        return ResponseEntity.ok(bom);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Void> insertItem(@PathVariable Long id, @RequestBody BomItem item) {
        item.setBomId(id);
        service.insertItem(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        service.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }
}
