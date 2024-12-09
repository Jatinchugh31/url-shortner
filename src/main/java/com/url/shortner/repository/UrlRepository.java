package com.url.shortner.repository;

import com.url.shortner.entity.UrlTable;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends CrudRepository<UrlTable, UUID> {

  @AllowFiltering
  Optional<UrlTable> findByShortUrl(String url);
}
