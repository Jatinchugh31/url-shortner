package com.url.shortner.controller;

import com.url.shortner.model.Request;
import com.url.shortner.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {

    @Autowired
    UrlService urlService;

    @PostMapping(value = "shorten",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShortUrl(@RequestBody Request longUrl) {
      String shortenUrl =   urlService.createShortUrl(longUrl.getLongUrl());
      return ResponseEntity.ok(shortenUrl);
    }

    @GetMapping("{shortUrl}")
    public ResponseEntity<Void> getShortUrls(@PathVariable("shortUrl") String shortUrl) {
           return urlService.findAndRedirect(shortUrl);
    }

}
