package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Bom;
import com.gaebalfan.erp.domain.BomItem;
import com.gaebalfan.erp.domain.Product;
import com.gaebalfan.erp.mapper.BomMapper;
import com.gaebalfan.erp.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductMapper mapper;
    private final BomMapper     bomMapper;

    public ProductService(ProductMapper mapper, BomMapper bomMapper) {
        this.mapper    = mapper;
        this.bomMapper = bomMapper;
    }

    public List<Product> findAll() {
        return mapper.findAll();
    }

    public List<Product> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }

    public int count(String q) {
        return mapper.count(q);
    }

    public Product findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Product obj) {
        mapper.insert(obj);
    }

    @Transactional
    public void update(Product obj) {
        mapper.update(obj);
        recalcParentCosts(obj.getProductId());
    }

    /**
     * 부품의 원가가 변경되면, 이 부품을 사용하는 완제품의 원가를 BOM 기준으로 재계산합니다.
     */
    private void recalcParentCosts(Long componentProductId) {
        List<Bom> parentBoms = bomMapper.findBomsByComponentProductId(componentProductId);
        for (Bom bom : parentBoms) {
            List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
            BigDecimal total = BigDecimal.ZERO;
            for (BomItem item : items) {
                Product comp = mapper.findById(item.getComponentProductId());
                BigDecimal compCost = (comp != null && comp.getCostPrice() != null) ? comp.getCostPrice() : BigDecimal.ZERO;
                total = total.add(item.getQuantity().multiply(compCost));
            }
            mapper.updateCostPrice(bom.getProductId(), total);
        }
    }

    public void delete(Long id) {
        mapper.delete(id);
    }
}
