package com.example.apidressing.controller;

import com.example.apidressing.gen.api.WardrobeApi;
import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api/v1")
public class WardrobeController implements WardrobeApi {


   @Override
    public ResponseEntity<List<ClothingItem>> listClothingItems(ClothingLayer layer) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<ClothingItem> addClothingItem(MultipartFile image, String name, ClothingLayer layer, Integer warmthLevel, String mainColorHex) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteClothingItem(Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
