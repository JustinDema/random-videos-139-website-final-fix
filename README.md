# Random Videos 139 Website - Enhanced MySQL Version

A comprehensive Spring Boot web application for the Random Videos 139 YouTube channel, featuring MySQL database integration, enhanced video statistics, search functionality, and embedded video player.

## 🚀 New Features & Enhancements

### ✅ **Database Migration**
- **MySQL Integration**: Migrated from H2 to MySQL with optimized schema
- **Corrected Channel Data**: Fixed YouTube channel ID (`UClf_sqXl4kMB4nX9I49SnZQ`) and handle (`@randomvideos1392`)
- **Enhanced Statistics**: Comprehensive tracking of views, likes, comments, and subscriber counts

### ✅ **Precise Number Formatting**
- **Accurate Display**: Shows `1.96K` instead of `2.0K` for subscriber count
- **Detailed Formatting**: Displays `1.26M` instead of `1.3M` for view counts
- **Smart Precision**: Automatically adjusts decimal places based on number size

### ✅ **Enhanced Navigation**
- **Modern Navbar**: Fixed navigation with Home, Popular Videos, All Videos, Playlists, and About Me
- **Responsive Design**: Mobile-friendly navigation with hamburger menu
- **Active States**: Clear indication of current page

### ✅ **Advanced Video Features**
- **Embedded Player**: Watch videos directly on the website using YouTube embed
- **Detailed Statistics**: Display views, likes, comments, and duration for each video
- **Search & Filter**: Search videos by title with sorting options
- **Grid/List Views**: Toggle between different viewing modes
- **Pagination**: Efficient browsing of large video collections

### ✅ **Comprehensive Data Sync**
- **Smart Updates**: Update existing videos instead of creating duplicates
- **Enhanced API Calls**: Fetch complete video statistics including likes and comments
- **Scheduled Sync**: Automated updates every 12 hours
- **Update Tracking**: Monitor sync history and performance

### ✅ **About Me Page**
- **Creator Information**: Detailed information about Justin, the AI Music Creator
- **Process Explanation**: How character songs are created using AI
- **Social Links**: Direct links to all social media platforms
- **Channel Heritage**: Story behind the "139" name

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.2, Java 17
- **Database**: MySQL 8.0
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **APIs**: YouTube Data API v3
- **Styling**: Custom CSS with modern design principles
- **Build Tool**: Maven

## 📊 Database Schema

### Tables
- **`channel_stats`**: Channel information and statistics
- **`videos`**: Complete video data with statistics
- **`playlists`**: Channel playlists information
- **`update_history`**: Sync operation tracking

### Key Features
- **Optimized Indexes**: Fast queries for video search and sorting
- **Foreign Key Constraints**: Data integrity
- **Automatic Timestamps**: Track creation and update times
- **Views and Procedures**: Efficient data operations

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd random-videos-139-website
   ```

2. **Set up MySQL database**
   ```bash
   # Run the setup script
   cd database
   chmod +x setup.sh
   ./setup.sh
   
   # Or manually execute SQL files
   mysql -u root -p < create_user.sql
   mysql -u rv139_user -p rv139_db < schema.sql
   ```

3. **Configure application**
   ```bash
   # Update src/main/resources/application.properties
   # Set your MySQL credentials and YouTube API key
   ```

4. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the application**
   - Open http://localhost:8080
   - The application will automatically sync data from YouTube

## ⚙️ Configuration

### Database Configuration
```properties
# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/rv139_db
spring.datasource.username=rv139_user
spring.datasource.password=your_password

# JPA Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### YouTube API Configuration
```properties
# YouTube API
youtube.api.key=YOUR_YOUTUBE_API_KEY
youtube.channel.id=UClf_sqXl4kMB4nX9I49SnZQ
youtube.channel.handle=@randomvideos1392
```

### Scheduling Configuration
```properties
# Sync Settings
app.sync.full-sync-cron=0 0 6,18 * * *  # 6 AM and 6 PM
app.sync.channel-stats-cron=0 0 */2 * * *  # Every 2 hours
app.sync.health-check-cron=0 */30 * * * *  # Every 30 minutes
```

## 🎯 Features Overview

### Homepage
- **Channel Statistics**: Real-time subscriber, video, and view counts
- **Latest Videos**: Most recent uploads with thumbnails and metadata
- **Popular Videos**: Most viewed content
- **Featured Playlists**: Curated collections
- **About Section**: Channel description and creator information

### All Videos Page
- **Search Functionality**: Find videos by title
- **Advanced Sorting**: By date, views, likes, or title
- **View Modes**: Grid or list layout
- **Pagination**: Efficient browsing
- **Detailed Metadata**: Views, likes, comments, and duration

### Video Detail Page
- **Embedded Player**: Watch directly on the website
- **Complete Information**: Full description, tags, and statistics
- **Related Videos**: Discover more content
- **Social Sharing**: Share videos easily
- **Responsive Design**: Works on all devices

### About Me Page
- **Creator Profile**: Information about Justin
- **Creative Process**: How AI character songs are made
- **Social Links**: Connect on all platforms
- **Channel Story**: The meaning behind "139"

## 📱 Responsive Design

- **Mobile-First**: Optimized for mobile devices
- **Tablet Support**: Perfect layout for tablets
- **Desktop Enhanced**: Rich experience on large screens
- **Touch-Friendly**: Easy navigation on touch devices

## 🔧 API Endpoints

### Public Endpoints
- `GET /` - Homepage
- `GET /popular-videos` - Popular videos page
- `GET /all-videos` - All videos with search and pagination
- `GET /playlists` - Playlists page
- `GET /about` - About me page
- `GET /video/{videoId}` - Video detail page

### Admin Endpoints (if implemented)
- `POST /api/sync/all` - Trigger full sync
- `POST /api/sync/channel` - Sync channel stats
- `GET /api/sync/status` - Get sync status

## 🎨 Design Features

### Modern UI/UX
- **Dark Theme**: Eye-friendly dark design
- **Smooth Animations**: Engaging micro-interactions
- **Hover Effects**: Interactive feedback
- **Loading States**: Clear progress indicators

### Accessibility
- **Keyboard Navigation**: Full keyboard support
- **Screen Reader Friendly**: Proper ARIA labels
- **High Contrast**: Support for accessibility needs
- **Focus Indicators**: Clear focus states

## 📈 Performance Optimizations

### Database
- **Indexed Queries**: Fast search and sorting
- **Connection Pooling**: Efficient database connections
- **Query Optimization**: Minimal database calls

### Frontend
- **Lazy Loading**: Images load as needed
- **Caching**: Browser caching for static assets
- **Minified Assets**: Optimized CSS and JavaScript
- **Responsive Images**: Appropriate sizes for devices

## 🔒 Security Features

- **SQL Injection Protection**: Parameterized queries
- **XSS Prevention**: Proper output encoding
- **CSRF Protection**: Spring Security integration
- **Input Validation**: Server-side validation
- **Rate Limiting**: API call throttling

## 📊 Monitoring & Logging

### Application Monitoring
- **Health Checks**: Endpoint health monitoring
- **Sync Tracking**: Update history and performance
- **Error Logging**: Comprehensive error tracking
- **Performance Metrics**: Response time monitoring

### Database Monitoring
- **Connection Health**: Database connectivity
- **Query Performance**: Slow query detection
- **Storage Usage**: Database size tracking

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Production Deployment
```bash
# Build JAR
mvn clean package

# Run with production profile
java -jar target/random-videos-139-website-1.0.0.jar --spring.profiles.active=prod
```

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/random-videos-139-website-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **YouTube Data API**: For providing comprehensive video data
- **Spring Boot Community**: For the excellent framework
- **Bootstrap & Font Awesome**: For UI components and icons
- **Random Videos 139 Community**: For inspiration and feedback

## 📞 Support

For support and questions:
- **GitHub Issues**: Create an issue for bugs or feature requests
- **Email**: Contact through the website
- **Social Media**: Follow @randomvideos1392 on various platforms

---

**Random Videos 139** - Creating unique AI character songs daily! 🎵✨

