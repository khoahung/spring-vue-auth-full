
# Full demo projects: auth-service, product-service, fe-vue

Projects are configured to use HS256 HMAC JWT for simplicity in the demo (shared secret).
IMPORTANT: In production, use RS256 or OAuth2/OIDC and store secrets securely.

## How to run

1. Build/run auth-service:
   - cd auth-service
   - mvn -q spring-boot:run
   (or build: mvn -q package && java -jar target/*.jar)

2. Build/run product-service:
   - cd product-service
   - mvn -q spring-boot:run

3. Run frontend:
   - cd fe-vue
   - npm install
   - npm run dev

Default endpoints:
- Auth service: http://localhost:8081/auth/register, /auth/login, /auth/refresh
- Product service: http://localhost:8082/api/products (protected), /public/ping

Demo default JWT secret is set in both application.yml files. Change before production.
