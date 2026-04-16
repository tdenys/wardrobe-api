package com.example.apidressing.controller;

import com.example.apidressing.gen.api.OutfitsApi;
import com.example.apidressing.gen.model.CreateOutfitRequest;
import com.example.apidressing.gen.model.Outfit;
import com.example.apidressing.gen.model.PagedOutfits;
import com.example.apidressing.service.OutfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OutfitsController implements OutfitsApi {

    private final OutfitService outfitService;

    @Override
    public ResponseEntity<PagedOutfits> listOutfits(Integer page, Integer size) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 20;
        return ResponseEntity.ok(outfitService.listOutfits(p, s));
    }

    @Override
    public ResponseEntity<Outfit> createOutfit(CreateOutfitRequest body) {
        // TODO: implémenter POST /outfits
        return ResponseEntity.status(501).build();
    }
}
