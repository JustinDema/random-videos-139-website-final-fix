# MySQL Database Setup Guide for Random Videos 139 Website

## Prerequisites

1. **MySQL Server 8.0 or higher** installed and running
2. **MySQL root access** or administrative privileges
3. **Java 21** installed
4. **Maven 3.6+** installed

## Database Setup Instructions

### Step 1: Create Database and Tables

1. **Connect to MySQL as root:**
   ```bash
   mysql -u root -p
   ```

2. **Run the schema creation script:**
   ```sql
   source /path/to/database/schema.sql
   ```
   
   Or execute the file directly:
   ```bash
   mysql -u root -p < database/schema.sql
   ```

### Step 2: Create Application User (Recommended)

1. **Run the user creation script:**
   ```sql
   source /path/to/database/create_user.sql
   ```
   
   Or execute the file directly:
   ```bash
   mysql -u root -p < database/create_user.sql
   ```

### Step 3: Configure Application Properties

Update `src/main/resources/application.properties` with your MySQL configuration:

```properties
# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/randomvideos139?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=randomvideos139_user
spring.datasource.password=RandomVideos139_SecurePass2024!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

### Step 4: Update YouTube API Configuration

Make sure to update the YouTube API configuration in `application.properties`:

```properties
# YouTube API Configuration
youtube.api.key=YOUR_YOUTUBE_API_KEY_HERE
youtube.channel.id=UClf_sqXl4kMB4nX9I49SnZQ
youtube.channel.handle=@randomvideos1392
```

## Database Schema Overview

### Tables Created:

1. **channel_stats** - Stores YouTube channel statistics and information
2. **videos** - Stores individual video data with metadata
3. **playlists** - Stores playlist information
4. **playlist_video_ids** - Junction table for playlist-video relationships
5. **update_history** - Tracks data synchronization history

### Views Created:

1. **latest_videos** - Shows videos marked as latest
2. **popular_videos** - Shows videos marked as popular
3. **public_playlists** - Shows only public playlists
4. **update_stats** - Provides update statistics summary

### Indexes:

- Performance indexes on frequently queried columns
- Foreign key relationships for data integrity
- Composite indexes for complex queries

## Security Considerations

1. **Change Default Password**: Update the default password in `create_user.sql` before running
2. **Use Environment Variables**: Consider using environment variables for sensitive configuration
3. **Network Security**: Restrict MySQL access to necessary hosts only
4. **Regular Backups**: Implement regular database backup procedures

## Backup and Restore

### Create Backup:
```bash
mysqldump -u root -p randomvideos139 > randomvideos139_backup.sql
```

### Restore from Backup:
```bash
mysql -u root -p randomvideos139 < randomvideos139_backup.sql
```

## Monitoring and Maintenance

### Check Update History:
```sql
SELECT * FROM update_stats;
```

### View Recent Updates:
```sql
SELECT * FROM update_history ORDER BY update_timestamp DESC LIMIT 10;
```

### Check Data Counts:
```sql
SELECT 
    (SELECT COUNT(*) FROM videos) as total_videos,
    (SELECT COUNT(*) FROM playlists) as total_playlists,
    (SELECT COUNT(*) FROM videos WHERE is_latest = TRUE) as latest_videos,
    (SELECT COUNT(*) FROM videos WHERE is_popular = TRUE) as popular_videos;
```

## Troubleshooting

### Common Issues:

1. **Connection Refused**: Check if MySQL service is running
2. **Access Denied**: Verify user credentials and privileges
3. **Table Not Found**: Ensure schema.sql was executed successfully
4. **Charset Issues**: Verify UTF-8 configuration in MySQL

### Useful Commands:

```sql
-- Check MySQL version
SELECT VERSION();

-- Show all databases
SHOW DATABASES;

-- Show tables in randomvideos139
USE randomvideos139;
SHOW TABLES;

-- Check table structure
DESCRIBE videos;

-- Check user privileges
SHOW GRANTS FOR 'randomvideos139_user'@'localhost';
```

## Performance Optimization

### Recommended MySQL Configuration:

Add to `/etc/mysql/mysql.conf.d/mysqld.cnf`:

```ini
[mysqld]
# InnoDB settings
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2

# Query cache (if using MySQL 5.7)
query_cache_type = 1
query_cache_size = 128M

# Connection settings
max_connections = 200
wait_timeout = 600

# Character set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

Restart MySQL after configuration changes:
```bash
sudo systemctl restart mysql
```

