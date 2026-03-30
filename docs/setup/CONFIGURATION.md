# Configuration Setup Guide

## Quick Start

### 1. Create Your Configuration File

```bash
cd src/main/resources
cp application.properties.example application.properties
```

### 2. Update Required Values

Open `application.properties` and replace these **REQUIRED** values:

#### Database (Required)
```properties
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

#### JWT Secret (Required)
Generate a new secret key:

```java
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Your JWT Secret: " + secret);
    }
}
```

Then update:
```properties
app.jwt.secret=YOUR_GENERATED_SECRET_HERE
```

### 3. Optional Services

These services are optional. The application will work without them, but some features will be disabled:

#### Face++ API (Optional)
**Required for:** Face recognition authentication

1. Sign up at: https://www.faceplusplus.com/
2. Get API Key and Secret from dashboard
3. Update in `application.properties`:
```properties
faceplus.api.key=YOUR_KEY
faceplus.api.secret=YOUR_SECRET
```

**Free tier:** 1,000 API calls/month

#### Cloudinary (Optional)
**Required for:** Image storage (profile photos, documents)

1. Sign up at: https://cloudinary.com/
2. Get credentials from dashboard
3. Update in `application.properties`:
```properties
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

**Free tier:** 25GB storage, 25GB bandwidth/month

#### VNPay (Optional)
**Required for:** Payment gateway integration

1. Sign up at: https://vnpay.vn/ (production) or https://sandbox.vnpayment.vn/ (testing)
2. Get TMN Code and Hash Secret
3. Update in `application.properties`:
```properties
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.hash-secret=YOUR_HASH_SECRET
```

## Security Best Practices

### ✅ DO:
- Keep `application.properties` in `.gitignore`
- Use environment variables for production
- Rotate secrets regularly
- Use different secrets for dev/staging/production
- Generate new JWT secret for each environment

### ❌ DON'T:
- Commit `application.properties` to Git
- Share secrets in chat/email
- Use default/example secrets in production
- Reuse secrets across environments

## Environment Variables (Production)

For production, use environment variables instead of `application.properties`:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/ibanking
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export APP_JWT_SECRET=your_production_jwt_secret
export FACEPLUS_API_KEY=your_faceplus_key
export FACEPLUS_API_SECRET=your_faceplus_secret
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
export VNPAY_TMN_CODE=your_tmn_code
export VNPAY_HASH_SECRET=your_hash_secret
```

## Troubleshooting

### "Cannot connect to database"
- Check PostgreSQL is running: `sudo systemctl status postgresql`
- Verify database exists: `psql -U postgres -l`
- Check credentials in `application.properties`

### "JWT token invalid"
- Ensure `app.jwt.secret` is set correctly
- Check token hasn't expired (24 hours default)
- Verify secret matches between environments

### "Face++ API error"
- Check API key and secret are correct
- Verify you haven't exceeded free tier limit (1000 calls/month)
- Check internet connectivity

### "Cloudinary upload failed"
- Verify credentials are correct
- Check file size (max 10MB)
- Ensure you haven't exceeded free tier quota

## Configuration Profiles

You can create environment-specific configurations:

```
application.properties              # Default
application-dev.properties          # Development
application-staging.properties      # Staging
application-prod.properties         # Production
```

Run with specific profile:
```bash
java -jar app.jar --spring.profiles.active=prod
```

## Need Help?

- Check main README.md for full documentation
- Open an issue on GitHub
- Contact: support@ibanking.com
