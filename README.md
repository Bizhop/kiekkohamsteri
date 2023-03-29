# Kiekkohamsteri
App backend for disc golf catalogue

# Tech
- Java 11
- Maven 3
- Docker with compose plugin

# Usage
For quick build and run use `./buildAndRun.sh`
### Details:
- build: `mvn clean package` or `mvn clean package -DskipTests` without tests
- docker build: `docker build -t kiekkohamsteri-backend .`
- run: `docker compose up`

# Mandatory environment variables
- `HAMSTERI_JWT_SECRET` 
  - jwt encoding/decoding. Must be at base64 encoded byte array, at least 512 bytes.
- `CLOUDINARY_URL`
  - for saving images
  - see [cloudinary.com](https://cloudinary.com/) for more info

# Api documentation
springdoc-openapi is used for generating API documentation
## Swagger UI
http://localhost:8080/swagger-ui/index.html
## openapi yaml
http://localhost:8080/v3/api-docs.yaml
