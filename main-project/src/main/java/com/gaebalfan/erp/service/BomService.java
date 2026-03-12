package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Bom;
import com.gaebalfan.erp.domain.BomItem;
import com.gaebalfan.erp.mapper.BomMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BomService {

    private final BomMapper mapper;

    public BomService(BomMapper mapper) {
        this.mapper = mapper;
    }

    public List<Bom> findAll() {
        return mapper.findAll();
    }

    public Bom findById(Long id) {
        return mapper.findById(id);
    }

    public List<BomItem> findItems(Long bomId) {
        return mapper.findItemsByBomId(bomId);
    }

    public void insertBom(Bom bom) {
        mapper.insertBom(bom);
    }

    public void insertItem(BomItem item) {
        mapper.insertBomItem(item);
    }

    public void deleteItem(Long bomItemId) {
        mapper.deleteBomItem(bomItemId);
    }
}
