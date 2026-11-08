package com.csit321G6.groufings.tudlo.submission;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/item-scores")
public class ItemScoreController extends CrudController<ItemScoreDto.Request, ItemScoreDto.Response> {

    private final ItemScoreService service;

    public ItemScoreController(ItemScoreService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, ItemScoreDto.Request, ItemScoreDto.Response> service() {
        return service;
    }
}
