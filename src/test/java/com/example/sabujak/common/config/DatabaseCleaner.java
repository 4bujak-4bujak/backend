package com.example.sabujak.common.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseCleaner {

    private static final String SHOW_TABLES_QUERY = "SHOW TABLES";
    private static final String FOREIGN_KEY_CHECK_FORMAT = "SET FOREIGN_KEY_CHECKS %d";
    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";

    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void findDatabaseTableNames() {
        List<Object[]> tableInfos = entityManager.createNativeQuery(SHOW_TABLES_QUERY).getResultList();
        tableInfos.forEach(
                tableInfo -> tableNames.add((String) tableInfo[0])
        );
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, 0)).executeUpdate();
        tableNames.forEach(tableName ->
                entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate()
        );
        entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, 1)).executeUpdate();
    }
}
