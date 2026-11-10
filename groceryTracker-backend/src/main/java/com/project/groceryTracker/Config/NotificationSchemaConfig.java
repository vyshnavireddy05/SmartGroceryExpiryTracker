package com.project.groceryTracker.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class NotificationSchemaConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationSchemaConfig.class);

    @Bean
    public ApplicationRunner makeNotificationInventoryNullable(
            JdbcTemplate jdbcTemplate) {

        return args -> {
            jdbcTemplate.execute(
                    "ALTER TABLE notifications MODIFY inventory_id BIGINT NULL"
            );
            logger.info(
                    "Ensured notifications.inventory_id is nullable so inventory rows can be deleted without dropping notification history"
            );
        };
    }
}
