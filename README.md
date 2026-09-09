# Microservices Shop

Hệ thống bán hàng trực tuyến được xây dựng theo kiến trúc microservices. Frontend Angular giao tiếp với các backend thông qua API Gateway; các service dùng cơ sở dữ liệu riêng và Order Service phát sự kiện đặt hàng qua Kafka.

## Kiến trúc

```text
Angular Frontend :4200
        |
        v
API Gateway :9000  ----> Keycloak :8181
        |
        +--> Product Service :8080 --> MongoDB :27017
        +--> Order Service :8081 ----> MySQL :3308
        |          |
        |          +-----------------> Kafka :9092
        +--> Inventory Service :8082 -> MySQL :3307
```

### Thành phần

| Thành phần | Công nghệ | Cổng mặc định | Mục đích |
|---|---|---:|---|
| `microservices-shop-frontend-master` | Angular 18, Tailwind CSS | `4200` | Giao diện người dùng |
| `api-gateway` | Spring Boot, Spring Cloud Gateway MVC | `9000` | Điểm vào chung, định tuyến, JWT, Swagger aggregation, circuit breaker |
| `product-service` | Spring Boot, Spring Data MongoDB | `8080` | Quản lý sản phẩm |
| `order-service` | Spring Boot, MySQL, Kafka | `8081` | Tạo đơn hàng và phát sự kiện `order-placed` |
| `inventory-service` | Spring Boot, MySQL | `8082` | Kiểm tra và quản lý tồn kho |
| Keycloak | Keycloak 26.7, MySQL 8.4 | `8181` | Đăng nhập và cấp JWT |
| MongoDB | MongoDB 8 | `27017` | Cơ sở dữ liệu Product Service |
| MySQL Inventory | MySQL 8.4 | `3307` | Cơ sở dữ liệu Inventory Service |
| MySQL Order | MySQL 8.4 | `3308` | Cơ sở dữ liệu Order Service |
| Kafka | Confluent Platform 7.8 | `9092` | Message broker cho sự kiện đặt hàng |
| Kafka UI | Provectus Kafka UI | `8086` | Theo dõi Kafka trên trình duyệt |

## Yêu cầu môi trường

- JDK 21
- Docker Desktop và Docker Compose
- Node.js và npm (khuyến nghị Node.js LTS)
- Maven không bắt buộc vì mỗi service có Maven Wrapper (`mvnw.cmd`)

Kiểm tra nhanh:

```powershell
java -version
node --version
npm --version
docker --version
docker compose version
```

## Cài đặt và khởi động

Mở các terminal PowerShell riêng trong thư mục gốc dự án.

### 1. Khởi động hạ tầng

```powershell
cd api-gateway
docker compose up -d

cd ..\product-service
docker compose up -d

cd ..\inventory-service
docker compose up -d

cd ..\order-service
docker compose up -d
```

Các lệnh trên khởi động Keycloak, MongoDB, hai MySQL, Zookeeper, Kafka, Schema Registry và Kafka UI.

Kiểm tra container:

```powershell
docker ps
```

Dừng hạ tầng khi không dùng:

```powershell
docker compose down
```

Chạy lệnh `down` trong từng thư mục service tương ứng. Dữ liệu được lưu trong Docker volumes; muốn xóa cả dữ liệu dùng `docker compose down -v`.

### 2. Cấu hình Keycloak

Truy cập [http://localhost:8181](http://localhost:8181) và đăng nhập bằng tài khoản quản trị mặc định:

- Username: `admin`
- Password: `admin`

Tạo realm có tên chính xác `spring-microservices-security-realm`, sau đó tạo OIDC client:

- Client ID: `angular-client`
- Client type: OpenID Connect
- Client authentication: Off (public client)
- Standard flow: On
- Valid redirect URIs: `http://localhost:4200/*`
- Web origins: `http://localhost:4200`

Tạo ít nhất một user trong realm để đăng nhập frontend. Cấu hình Angular hiện trỏ tới:

```text
http://localhost:8181/realms/spring-microservices-security-realm
```

Thư mục `api-gateway/docker/keycloak/realms` hiện không có file realm import, vì vậy cần tạo realm/client thủ công như trên hoặc bổ sung file export realm trước khi dùng `--import-realm`.

### 3. Chạy backend

Chạy từng service ở một terminal riêng:

```powershell
cd product-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd order-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd inventory-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd api-gateway
.\mvnw.cmd spring-boot:run
```

Trên macOS/Linux thay `./mvnw.cmd` bằng `./mvnw`.

Có thể build và chạy file JAR thay cho Maven Run:

```powershell
.\mvnw.cmd clean package
java -jar target\*.jar
```

Thực hiện trong thư mục của từng service.

### 4. Chạy frontend

```powershell
cd microservices-shop-frontend-master
npm install
npm start
```

Mở [http://localhost:4200](http://localhost:4200), đăng nhập bằng user đã tạo trong Keycloak.

## Endpoint chính

Tất cả API nghiệp vụ nên được gọi qua Gateway:

- Product: `http://localhost:9000/api/product/**`
- Order: `http://localhost:9000/api/order/**`
- Inventory: `http://localhost:9000/api/inventory/**`

Swagger UI tổng hợp:

- [http://localhost:9000/swagger-ui.html](http://localhost:9000/swagger-ui.html)

Swagger riêng từng service:

- Product: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Order: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Inventory: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

Actuator Gateway: [http://localhost:9000/actuator](http://localhost:9000/actuator)

Kafka UI: [http://localhost:8086](http://localhost:8086)

## Luồng nghiệp vụ chính

1. Người dùng đăng nhập qua Keycloak trên frontend.
2. Frontend nhận access token và gửi token trong header `Authorization: Bearer ...`.
3. Gateway xác thực JWT rồi định tuyến request tới Product, Order hoặc Inventory Service.
4. Order Service kiểm tra tồn kho qua Inventory Service.
5. Khi đặt hàng thành công, Order Service phát sự kiện `order-placed` lên Kafka.

## Xử lý sự cố thường gặp

- Không đăng nhập được: kiểm tra realm, client ID `angular-client`, redirect URI và user trong Keycloak.
- Gateway trả `401`: kiểm tra Keycloak đang chạy ở cổng `8181` và issuer URI khớp tên realm.
- Service không kết nối database: kiểm tra container tương ứng và các cổng `27017`, `3307`, `3308`.
- Order Service lỗi Kafka: kiểm tra `broker`, `schema-registry` đang chạy và Kafka UI hiển thị cluster local.
- Frontend gọi API lỗi CORS: chạy frontend đúng tại `http://localhost:4200`; Gateway hiện chỉ cho phép origin này.
- Dữ liệu khởi tạo không cập nhật: các script trong `docker-entrypoint-initdb.d` chỉ chạy khi volume database được tạo lần đầu.

## Kiểm thử

Backend:

```powershell
cd <service-folder>
.\mvnw.cmd test
```

Frontend:

```powershell
cd microservices-shop-frontend-master
npm test
```

## Cấu trúc thư mục

```text
.
├── api-gateway/
├── product-service/
├── order-service/
├── inventory-service/
├── microservices-shop-frontend-master/
└── README.md
```

Các thông tin cấu hình trong README phản ánh cấu hình local hiện tại. Khi triển khai production, cần thay mật khẩu mặc định, giới hạn Actuator, cấu hình CORS, dùng HTTPS và đưa secret vào biến môi trường hoặc secret manager.
