#!/bin/bash

# MySQL Database Setup Script for Random Videos 139 Website
# This script automates the database setup process

set -e  # Exit on any error

echo "=== Random Videos 139 Website - MySQL Database Setup ==="
echo

# Configuration
DB_NAME="randomvideos139"
DB_USER="randomvideos139_user"
DB_PASS="RandomVideos139_SecurePass2024!"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if MySQL is installed and running
check_mysql() {
    print_status "Checking MySQL installation..."
    
    if ! command -v mysql &> /dev/null; then
        print_error "MySQL client not found. Please install MySQL."
        exit 1
    fi
    
    if ! systemctl is-active --quiet mysql; then
        print_warning "MySQL service is not running. Attempting to start..."
        sudo systemctl start mysql
        if ! systemctl is-active --quiet mysql; then
            print_error "Failed to start MySQL service."
            exit 1
        fi
    fi
    
    print_status "MySQL is running."
}

# Prompt for MySQL root password
get_root_password() {
    echo -n "Enter MySQL root password: "
    read -s MYSQL_ROOT_PASS
    echo
}

# Test MySQL connection
test_connection() {
    print_status "Testing MySQL connection..."
    
    if ! mysql -u root -p"$MYSQL_ROOT_PASS" -e "SELECT 1;" &> /dev/null; then
        print_error "Failed to connect to MySQL. Please check your root password."
        exit 1
    fi
    
    print_status "MySQL connection successful."
}

# Create database and tables
setup_database() {
    print_status "Creating database and tables..."
    
    mysql -u root -p"$MYSQL_ROOT_PASS" < "$SCRIPT_DIR/schema.sql"
    
    if [ $? -eq 0 ]; then
        print_status "Database schema created successfully."
    else
        print_error "Failed to create database schema."
        exit 1
    fi
}

# Create application user
setup_user() {
    print_status "Creating application user..."
    
    mysql -u root -p"$MYSQL_ROOT_PASS" < "$SCRIPT_DIR/create_user.sql"
    
    if [ $? -eq 0 ]; then
        print_status "Application user created successfully."
    else
        print_error "Failed to create application user."
        exit 1
    fi
}

# Test application user connection
test_app_user() {
    print_status "Testing application user connection..."
    
    if mysql -u "$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME; SELECT COUNT(*) FROM channel_stats;" &> /dev/null; then
        print_status "Application user connection successful."
    else
        print_error "Failed to connect with application user."
        exit 1
    fi
}

# Generate application.properties template
generate_config() {
    print_status "Generating application.properties template..."
    
    cat > "$SCRIPT_DIR/application.properties.template" << EOF
# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/$DB_NAME?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASS
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# YouTube API Configuration
youtube.api.key=YOUR_YOUTUBE_API_KEY_HERE
youtube.channel.id=UClf_sqXl4kMB4nX9I49SnZQ
youtube.channel.handle=@randomvideos1392

# Scheduling Configuration
spring.task.scheduling.pool.size=2

# Cache Configuration
spring.cache.type=simple

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized

# Logging Configuration
logging.level.com.randomvideos139=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
EOF

    print_status "Configuration template created at: $SCRIPT_DIR/application.properties.template"
}

# Display summary
show_summary() {
    echo
    echo "=== Setup Complete ==="
    echo
    print_status "Database: $DB_NAME"
    print_status "User: $DB_USER"
    print_status "Password: $DB_PASS"
    echo
    print_warning "Next steps:"
    echo "1. Copy the configuration from application.properties.template to your main application.properties"
    echo "2. Update the YouTube API key in application.properties"
    echo "3. Build and run your Spring Boot application"
    echo
    print_warning "Security reminder:"
    echo "- Change the default database password in production"
    echo "- Use environment variables for sensitive configuration"
    echo "- Restrict MySQL network access as needed"
    echo
}

# Main execution
main() {
    echo "This script will set up the MySQL database for Random Videos 139 Website."
    echo "It will create the database, tables, and application user."
    echo
    
    read -p "Do you want to continue? (y/N): " -n 1 -r
    echo
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Setup cancelled."
        exit 0
    fi
    
    check_mysql
    get_root_password
    test_connection
    setup_database
    setup_user
    test_app_user
    generate_config
    show_summary
    
    print_status "Database setup completed successfully!"
}

# Run main function
main "$@"

