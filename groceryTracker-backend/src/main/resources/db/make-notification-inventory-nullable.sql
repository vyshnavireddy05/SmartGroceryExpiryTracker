-- Required when notifications.inventory_id was created as NOT NULL.
-- Hibernate ddl-auto=update often will not relax an existing NOT NULL column.
-- The application also runs this ALTER on startup.
ALTER TABLE notifications MODIFY inventory_id BIGINT NULL;
