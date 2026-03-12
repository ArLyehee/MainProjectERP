package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Bom;
import com.gaebalfan.erp.domain.BomItem;
import com.gaebalfan.erp.service.BomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

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
    public ResponseEntity<Bom> insertBom(@RequestBody Map<String, Object> body) {
        Bom bom = new Bom();
        bom.setProductId(Long.parseLong(body.get("productId").toString()));
        bom.setBomName(body.get("bomName") != null ? body.get("bomName").toString() : "");
        bom.setVersion(body.get("version") != null ? body.get("version").toString() : "1.0");
        service.insertBom(bom);
        return ResponseEntity.ok(bom);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Void> insertItem(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        BomItem item = new BomItem();
        item.setBomId(id);
        item.setComponentProductId(Long.parseLong(body.get("componentProductId").toString()));
        item.setQuantity(new BigDecimal(body.get("quantity").toString()));
        service.insertItem(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        service.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }
}
