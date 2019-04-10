package com.eresearch.dblp.consumer.dao;

public interface DblpConsumerDao {

    String getInsertQueryForSearchResultsTable();

    /*
    NOTE: this should only used for scheduler (db-cleaner).
     */
    String getDeleteQueryForSearchResultsTable();

    /*
    NOTE: this should only used for scheduler (db-cleaner).
     */
    String getResetAutoIncrementForSearchResultsTable();

    String getSelectQueryForSearchResultsTable();

    String getCreationQueryForSearchResultsTable();

    String getDropQueryForSearchResultsTable();
}
