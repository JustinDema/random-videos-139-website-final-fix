# Deployment Guide for Random Videos 139 Website

## 🚀 Quick Start Deployment

### Prerequisites Installation

#### Ubuntu/Debian Systems:
```bash
# Update package list
sudo apt update

# Install Java 21
sudo apt install openjdk-21-jdk

# Install Maven
sudo apt install maven

# Install MySQL Server
sudo apt install mysql-server

# Secure MySQL installation
sudo mysql_secure_installation
```

#### CentOS/RHEL Systems:
```bash
# Install Java 21
sudo dnf install java-21-openjdk-devel

# Install Maven
sudo dnf install maven

# Install MySQL Server
sudo dnf install mysql-server
sudo systemctl start mysqld
sudo systemctl enable mysqld
```

### 🗄️ Database Setup

1. **Start MySQL Service:**
   ```bash
   sudo systemctl start mysql
   sudo systemctl enable mysql
   ```

2. **Run Database Setup:**
   ```bash
   cd database/
   chmod +x setup.sh
   ./setup.sh
   ```

3. **Manual Setup (Alternative):**
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/create_user.sql
   ```

### ⚙️ Application Configuration

1. **Update Application Properties:**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. **Edit Configuration:**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/randomvideos139?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=randomvideos139_user
   spring.datasource.password=YOUR_SECURE_PASSWORD
   
   # YouTube API Configuration
   youtube.api.key=YOUR_YOUTUBE_API_KEY
   youtube.channel.id=UClf_sqXl4kMB4nX9I49SnZQ
   youtube.channel.handle=@randomvideos1392
   ```

### 🔨 Build and Deploy

1. **Build Application:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Run Application:**
   ```bash
   java -jar target/website-0.0.1-SNAPSHOT.jar
   ```

3. **Access Application:**
   - Open browser to: `http://localhost:8080`
   - Admin endpoints: `http://localhost:8080/api/sync-status`

### 🐳 Docker Deployment (Optional)

1. **Create Dockerfile:**
   ```dockerfile
   FROM openjdk:21-jre-slim
   
   WORKDIR /app
   COPY target/website-0.0.1-SNAPSHOT.jar app.jar
   
   EXPOSE 8080
   
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Build and Run:**
   ```bash
   docker build -t randomvideos139-website .
   docker run -p 8080:8080 randomvideos139-website
   ```

### 🔧 Production Configuration

#### 1. Environment Variables
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/randomvideos139"
export SPRING_DATASOURCE_USERNAME="randomvideos139_user"
export SPRING_DATASOURCE_PASSWORD="your_secure_password"
export YOUTUBE_API_KEY="your_youtube_api_key"
```

#### 2. Systemd Service (Linux)
Create `/etc/systemd/system/randomvideos139.service`:
```ini
[Unit]
Description=Random Videos 139 Website
After=network.target mysql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/randomvideos139
ExecStart=/usr/bin/java -jar website-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

Environment=SPRING_PROFILES_ACTIVE=production
Environment=SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/randomvideos139
Environment=SPRING_DATASOURCE_USERNAME=randomvideos139_user
Environment=SPRING_DATASOURCE_PASSWORD=your_secure_password
Environment=YOUTUBE_API_KEY=your_youtube_api_key

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable randomvideos139
sudo systemctl start randomvideos139
```

#### 3. Nginx Reverse Proxy
Create `/etc/nginx/sites-available/randomvideos139`:
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/randomvideos139 /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 📊 Monitoring and Maintenance

#### 1. Application Logs
```bash
# View logs
tail -f /var/log/randomvideos139/application.log

# Or if running with systemd
journalctl -u randomvideos139 -f
```

#### 2. Database Monitoring
```sql
-- Check synchronization status
SELECT * FROM update_stats;

-- View recent updates
SELECT * FROM update_history ORDER BY update_timestamp DESC LIMIT 10;

-- Check data health
SELECT 
    (SELECT COUNT(*) FROM videos) as total_videos,
    (SELECT COUNT(*) FROM playlists) as total_playlists,
    (SELECT COUNT(*) FROM channel_stats) as channel_records;
```

#### 3. Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Sync status
curl http://localhost:8080/api/sync-status

# Trigger manual sync
curl http://localhost:8080/api/trigger-sync
```

### 🛡️ Security Hardening

#### 1. Database Security
```sql
-- Change default password
ALTER USER 'randomvideos139_user'@'localhost' IDENTIFIED BY 'new_secure_password';

-- Restrict access
REVOKE ALL PRIVILEGES ON *.* FROM 'randomvideos139_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON randomvideos139.* TO 'randomvideos139_user'@'localhost';
```

#### 2. Application Security
```properties
# Production security settings
server.error.whitelabel.enabled=false
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never
```

#### 3. Firewall Configuration
```bash
# Allow only necessary ports
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw enable
```

### 🔄 Backup and Recovery

#### 1. Database Backup
```bash
# Create backup
mysqldump -u root -p randomvideos139 > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p randomvideos139 > $BACKUP_DIR/randomvideos139_$DATE.sql
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

#### 2. Application Backup
```bash
# Backup application files
tar -czf randomvideos139_app_$(date +%Y%m%d).tar.gz /opt/randomvideos139/
```

### 🚨 Troubleshooting

#### Common Issues:

1. **Port Already in Use:**
   ```bash
   # Find process using port 8080
   sudo lsof -i :8080
   # Kill process if needed
   sudo kill -9 <PID>
   ```

2. **Database Connection Issues:**
   ```bash
   # Check MySQL status
   sudo systemctl status mysql
   # Test connection
   mysql -u randomvideos139_user -p randomvideos139
   ```

3. **YouTube API Quota Exceeded:**
   - Check API usage in Google Cloud Console
   - Implement rate limiting if needed
   - Consider caching strategies

4. **Memory Issues:**
   ```bash
   # Increase JVM heap size
   java -Xmx2g -jar website-0.0.1-SNAPSHOT.jar
   ```

### 📈 Performance Optimization

#### 1. JVM Tuning
```bash
java -Xms1g -Xmx2g -XX:+UseG1GC -jar website-0.0.1-SNAPSHOT.jar
```

#### 2. Database Optimization
```sql
-- Add indexes for better performance
CREATE INDEX idx_videos_published_views ON videos(published_at DESC, view_count DESC);
CREATE INDEX idx_update_history_type_timestamp ON update_history(update_type, update_timestamp DESC);
```

#### 3. Application Caching
```properties
# Enable caching
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=1h
```

---

**Note**: This deployment guide covers both development and production scenarios. Always test in a staging environment before deploying to production.

