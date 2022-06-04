# Kiekkohamsteri
App backend for disc golf catalogue

# Tech
- Java 11
- Maven 3
- Docker with compose plugin

# Usage
- build: `mvn clean package`
- docker build: `docker build -t kiekkohamsteri-backend .`
- run: `docker compose up`

# Mandatory environment variables
- `HAMSTERI_JWT_SECRET` 
  - jwt encoding/decoding. Must be at least 512 bytes.
- `CLOUDINARY_URL`
  - for saving images
  - see [cloudinary.com](https://cloudinary.com/) for more info
