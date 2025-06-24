-- MySQL User Setup Script for Random Videos 139 Website
-- This script creates a dedicated user for the application

-- Create application user
CREATE USER IF NOT EXISTS 'randomvideos139_user'@'localhost' IDENTIFIED BY 'RandomVideos139_SecurePass2024!';
CREATE USER IF NOT EXISTS 'randomvideos139_user'@'%' IDENTIFIED BY 'RandomVideos139_SecurePass2024!';

-- Grant privileges to the application user
GRANT SELECT, INSERT, UPDATE, DELETE ON randomvideos139.* TO 'randomvideos139_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON randomvideos139.* TO 'randomvideos139_user'@'%';

-- Grant specific privileges for views
GRANT CREATE VIEW ON randomvideos139.* TO 'randomvideos139_user'@'localhost';
GRANT CREATE VIEW ON randomvideos139.* TO 'randomvideos139_user'@'%';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Show created users (for verification)
SELECT User, Host FROM mysql.user WHERE User = 'randomvideos139_user';

