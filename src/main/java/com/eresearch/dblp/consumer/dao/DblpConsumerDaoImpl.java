package com.eresearch.dblp.consumer.dao;

import org.springframework.stereotype.Component;

@Component
public class DblpConsumerDaoImpl implements DblpConsumerDao {

    @Override
    public String getInsertQueryForSearchResultsTable() {
        return "INSERT INTO dblp_consumer.search_results(author_name, dblp_author_name, author_results, creation_timestamp) VALUES (?, ?, ?, ?)";
    }

    @Override
    public String getDeleteQueryForSearchResultsTable() {
        return "DELETE FROM dblp_consumer.search_results";
    }

    @Override
    public String getResetAutoIncrementForSearchResultsTable() {
        return "ALTER TABLE dblp_consumer.search_results AUTO_INCREMENT = 1";
    }

    @Override
    public String getSelectQueryForSearchResultsTable() {
        return "SELECT * FROM dblp_consumer.search_results";
    }

    @Override
    public String getCreationQueryForSearchResultsTable() {
        return "CREATE TABLE IF NOT EXISTS dblp_consumer.search_results(id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT, author_name VARCHAR(255) DEFAULT NULL, dblp_author_name VARCHAR(255) DEFAULT NULL, author_results LONGTEXT, creation_timestamp TIMESTAMP NULL DEFAULT NULL, PRIMARY KEY (id), KEY author_name_idx (author_name)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }

    @Override
    public String getDropQueryForSearchResultsTable() {
        return "DROP TABLE IF EXISTS dblp_consumer.search_results";
    }
}
