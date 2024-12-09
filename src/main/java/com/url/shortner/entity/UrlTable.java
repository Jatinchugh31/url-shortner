package com.url.shortner.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@Table
@Data
public class UrlTable {

    @PrimaryKey
    UUID urlId;
    String originalUrl;
    String shortUrl;
    Instant expireTime;

}
