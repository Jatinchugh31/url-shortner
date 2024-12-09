package com.url.shortner.service;

import com.url.shortner.entity.UrlTable;
import com.url.shortner.repository.UrlRepository;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UrlService {
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE62.length();
    private static final Map<String, String> urlMap = new HashMap<>(); // For mapping short -> long URLs
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);


    @Autowired
    UrlRepository urlRepository;
    public String createShortUrl(String longUrl)  {
        UrlTable urlTable = new UrlTable();
        Random random = new Random();
        try {

            String shortUrl = buildShortUrl(longUrl);
            while (findByShortUrl(shortUrl) != null){
                shortUrl = buildShortUrl(longUrl+random.nextInt() );
            }
            urlTable.setUrlId(UUID.randomUUID());
            urlTable.setShortUrl(shortUrl);
            urlTable.setOriginalUrl(longUrl);
            urlRepository.save(urlTable);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return  urlTable.getShortUrl();
    }

    @Cacheable(cacheNames = "shortUrlCache",key = "#shortUrl")
    public UrlTable findByShortUrl(String shortUrl) {
         log.info("findByShortUrl: {}", shortUrl);
         return  loadFromDataBase(shortUrl);
    }

    public UrlTable loadFromDataBase(String shortUrl) {
        log.info("loadFromDataBase: {}", shortUrl);

        UrlTable urlTable =  urlRepository.findByShortUrl(shortUrl).orElse(null);
      return  urlTable;
    }

    private String buildShortUrl(String longUrl) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(longUrl.getBytes());
        StringBuilder hashHex = new StringBuilder();
        for (byte b : hashBytes) {
            hashHex.append(String.format("%02x", b));
        }

        // Step 2: Take the first 6 bytes (12 hex characters)
        String shortHashHex = hashHex.substring(0, 12);

        // Step 3: Convert hex to decimal
        BigInteger decimalValue = new BigInteger(shortHashHex, 16);

        // Step 4: Convert decimal to Base62
        StringBuilder shortURL = new StringBuilder();
        while (decimalValue.compareTo(BigInteger.ZERO) > 0) {
            shortURL.append(BASE62.charAt(decimalValue.mod(BigInteger.valueOf(BASE)).intValue()));
            decimalValue = decimalValue.divide(BigInteger.valueOf(BASE));
        }

        // Reverse the string to get the correct Base62 encoding
        String shortUrl = shortURL.reverse().toString();
        return shortUrl;
    }


    public ResponseEntity<Void> findAndRedirect(String shortUrl) {
        UrlTable originalUrl = findByShortUrl(shortUrl);
        return ResponseEntity.status(302).location(URI.create(originalUrl.getOriginalUrl())).build();
    }
}
