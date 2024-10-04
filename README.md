Data Migration between MySQL and PostgreSQL
===
Application for migration data from MySQL to Postgres using Spring Batch. Import-job is currently scheduled to run every minute. During each execution it performs import only for new data, which wasn't processed before (ignoring duplicates).

### Setting up and running:
1. All essential settings are stored in the .env file
2. Build application by running `mvn clean package`
3. Set up docker stack by running `docker-compose up`

### Populating initial data:
1. File starships.json (located in .docker/load_on_db) contains sample data
2. It is possible to trigger this particular service couple of times by running `docker-compose up -d load_on_db`. It wil reproduce uploading without restarting of the whole stack. In this way it is possible to test how application handles duplicated data.

### Some extra notes:
1. CustomItemProcessor.class currently stores information about all processed items. In production this cache should be definitely removed outside.
2. Error handling is performed by CustomSkipListener.class. To test this functionality had to modify DB initialization script for Postgres: `ALTER TABLE vessels ALTER COLUMN name SET NOT NULL`. So all we need to do - access MySQL DB, set one of the values in the "name" column to NULL and wait up to 1 minute before import job will be executed.
3. For future performance optimization the JdbcBatchItemWriter which is currently used for writing to Postgres could be replaced with some other writer (e.g. CustomItemWriter - see in importData package). But in this case we should have a different error-handling logic.
4. Logging is performed to console and to file both. To get logfile from container just run `docker cp app-server:/app/log ~/dataProcessor ` (assuming you have /dataProcessor in your home dir)
