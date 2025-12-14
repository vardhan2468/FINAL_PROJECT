# Production Deployment Guide

## Learning Management System - Production Deployment

### Prerequisites

Before deploying to production, ensure you have:

- Java 17 or higher installed
- MySQL 8.0+ database server
- Minimum 2GB RAM
- SSL/TLS certificates (recommended)

---

## Environment Variables

### Required Environment Variables

Set the following environment variables before starting the application:

```bash
# Database Configuration
export DB_URL="jdbc:mysql://your-db-host:3306/lms_db?useSSL=true&requireSSL=true&serverTimezone=UTC"
export DB_USERNAME="your_database_username"
export DB_PASSWORD="your_secure_database_password"

# JWT Configuration (CRITICAL: Use a strong, random secret)
export JWT_SECRET="your-very-secure-random-secret-key-minimum-32-characters-long"
export JWT_EXPIRATION="36000000"  # 10 hours in milliseconds

# CORS Configuration
export CORS_ORIGINS="https://yourdomain.com,https://www.yourdomain.com"

# Optional: Connection Pool Configuration
export DB_POOL_SIZE="20"
export DB_MIN_IDLE="10"

# Optional: Server Port
export PORT="8080"

# Optional: Logging
export LOG_FILE="/var/log/lms/application.log"
```

### Generating Secure JWT Secret

Generate a secure random secret key:

```bash
# Using openssl
openssl rand -base64 64

# Using Python
python3 -c "import secrets; print(secrets.token_urlsafe(64))"

# Using Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

---

## Database Setup

### 1. Create Production Database

```sql
CREATE DATABASE lms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create database user with limited privileges
CREATE USER 'lms_user'@'%' IDENTIFIED BY 'secure_password_here';
GRANT SELECT, INSERT, UPDATE, DELETE ON lms_db.* TO 'lms_user'@'%';
FLUSH PRIVILEGES;
```

### 2. Run Database Migrations

```bash
# Ensure ddl-auto is set to 'validate' in production
# Run Flyway or Liquibase migrations if using them
# Or manually run SQL scripts to create schema
```

### 3. Create Initial Admin User

```sql
USE lms_db;

-- Password: admin123 (BCrypt hashed)
INSERT INTO users (name, email, password, role) 
VALUES ('Admin User', 'admin@yourdomain.com', 
        '$2a$10$yYZ9X1H8WH2qQVX9rQYx0uyYx3xh8xvx3xvx3xvx3xvx3xvx3xvx', 
        'ADMIN');
```

---

## Building for Production

### 1. Build JAR File

```bash
# Clean and build
./mvnw clean package -DskipTests

# Or with tests
./mvnw clean package

# JAR file will be in: target/lms-0.0.1-SNAPSHOT.jar
```

### 2. Optimize JAR Size (Optional)

```bash
# Use Maven with production profile
./mvnw clean package -Pprod -DskipTests
```

---

## Deployment Options

### Option 1: Direct Java Execution

```bash
# Set environment variables
export DB_URL="jdbc:mysql://..."
export DB_USERNAME="lms_user"
export DB_PASSWORD="secure_password"
export JWT_SECRET="your-64-char-secret"

# Run with production profile
java -jar \
  -Dspring.profiles.active=prod \
  -Xms512m \
  -Xmx2g \
  target/lms-0.0.1-SNAPSHOT.jar
```

### Option 2: Systemd Service (Linux)

Create `/etc/systemd/system/lms.service`:

```ini
[Unit]
Description=Learning Management System
After=network.target mysql.service

[Service]
Type=simple
User=lms
Group=lms
WorkingDirectory=/opt/lms
ExecStart=/usr/bin/java \
  -Dspring.profiles.active=prod \
  -Xms512m -Xmx2g \
  -jar /opt/lms/lms-0.0.1-SNAPSHOT.jar

Environment="DB_URL=jdbc:mysql://localhost:3306/lms_db"
Environment="DB_USERNAME=lms_user"
Environment="DB_PASSWORD=secure_password"
Environment="JWT_SECRET=your-secret-here"

Restart=always
RestartSec=10

StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable lms
sudo systemctl start lms
sudo systemctl status lms
```

### Option 3: Docker Container

Create `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/lms-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx2g"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

Build and run:

```bash
# Build image
docker build -t lms-app:latest .

# Run container
docker run -d \
  --name lms \
  -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host:3306/lms_db" \
  -e DB_USERNAME="lms_user" \
  -e DB_PASSWORD="secure_password" \
  -e JWT_SECRET="your-secret" \
  --restart unless-stopped \
  lms-app:latest
```

### Option 4: Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: lms_db
      MYSQL_USER: lms_user
      MYSQL_PASSWORD: secure_password
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    networks:
      - lms-network

  lms-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://mysql:3306/lms_db?useSSL=false&serverTimezone=UTC
      DB_USERNAME: lms_user
      DB_PASSWORD: secure_password
      JWT_SECRET: ${JWT_SECRET}
      CORS_ORIGINS: https://yourdomain.com
    depends_on:
      - mysql
    networks:
      - lms-network
    restart: unless-stopped

volumes:
  mysql_data:

networks:
  lms-network:
    driver: bridge
```

---

## Reverse Proxy Setup (Nginx)

Create `/etc/nginx/sites-available/lms`:

```nginx
upstream lms_backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security Headers
    add_header X-Frame-Options "DENY" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Proxy to Spring Boot
    location / {
        proxy_pass http://lms_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://lms_backend;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

Enable and reload:

```bash
sudo ln -s /etc/nginx/sites-available/lms /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## SSL/TLS with Let's Encrypt

```bash
# Install certbot
sudo apt install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Auto-renewal is configured automatically
sudo certbot renew --dry-run
```

---

## Monitoring and Logging

### Application Logs

```bash
# View logs with systemd
sudo journalctl -u lms -f

# View log file
tail -f /var/log/lms/application.log
```

### Health Check Endpoint

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Response:
# {"status":"UP"}
```

### Monitoring with Prometheus (Optional)

Add to `application-prod.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

---

## Security Checklist

- [ ] Strong JWT secret (min 64 characters)
- [ ] Database credentials secured
- [ ] SSL/TLS certificates installed
- [ ] CORS configured for specific domains only
- [ ] Firewall configured (only allow 80, 443)
- [ ] Database not exposed publicly
- [ ] Regular security updates applied
- [ ] Backup strategy implemented
- [ ] Log rotation configured
- [ ] Rate limiting enabled (if needed)

---

## Performance Optimization

### JVM Tuning

```bash
java -jar \
  -Xms1g \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/lms/heapdump.hprof \
  app.jar
```

### Database Connection Pool

Already optimized in `application-prod.properties`:
- Maximum pool size: 20
- Minimum idle: 10
- Connection timeout: 30s

---

## Backup and Recovery

### Database Backup

```bash
# Daily backup script
mysqldump -u lms_user -p lms_db > backup-$(date +%Y%m%d).sql

# Restore
mysql -u lms_user -p lms_db < backup-20250114.sql
```

### Automated Backups (Cron)

```bash
# Add to crontab
0 2 * * * /opt/lms/backup.sh
```

---

## Troubleshooting

### Check Application Status

```bash
# Systemd service
sudo systemctl status lms

# Docker container
docker logs lms -f

# Port listening
sudo netstat -tlnp | grep 8080
```

### Common Issues

**Issue: Application won't start**
- Check environment variables are set
- Verify database connectivity
- Check logs for detailed errors

**Issue: 401 Unauthorized**
- Verify JWT secret matches
- Check token expiration time
- Ensure Authorization header is sent

**Issue: Database connection failed**
- Check database is running
- Verify credentials
- Check network connectivity

---

## Support and Maintenance

### Regular Maintenance Tasks

1. Monitor disk space and logs
2. Review security logs weekly
3. Update dependencies monthly
4. Test backups regularly
5. Monitor application performance

### Updates

```bash
# Pull latest code
git pull origin main

# Rebuild
./mvnw clean package -DskipTests

# Restart service
sudo systemctl restart lms
```

---

## Contact

For issues or questions, contact your development team.

**Last Updated:** December 14, 2025
