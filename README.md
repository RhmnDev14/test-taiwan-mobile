# test-taiwan-mobile

Sistem autentikasi pengguna berbasis **REST API** menggunakan Spring Boot. Mendukung registrasi dan login menggunakan Email + Password dengan enkripsi BCrypt.

---

## 🛠️ Tech Stack

| Komponen | Detail |
|---|---|
| **Language** | Java 17 (OpenJDK) |
| **Framework** | Spring Boot 3.2.5 |
| **Security** | Spring Security + BCryptPasswordEncoder |
| **Validation** | Spring Boot Validation (Jakarta Bean Validation) |
| **API Docs** | Springdoc OpenAPI 2.5.0 (Swagger UI) |
| **Storage** | In-Memory (`ConcurrentHashMap`) |
| **Testing** | JUnit 5 + Mockito + MockMvc |
| **Build Tool** | Maven |

---

## 📁 Struktur Proyek

```
src/main/java/com/auth/
├── UserAuthApplication.java          ← Entry point
├── config/
│   ├── SecurityConfig.java           ← BCrypt bean + Security rules
│   └── OpenApiConfig.java            ← Swagger metadata + startup log
├── controller/
│   └── AuthController.java           ← REST Endpoints
├── service/
│   ├── AuthService.java              ← Interface
│   └── AuthServiceImpl.java          ← Business logic
├── store/
│   ├── UserStoreRepository.java      ← Interface
│   └── UserStore.java                ← In-memory storage
├── model/
│   └── User.java                     ← User entity
├── dto/
│   ├── RegisterRequest.java          ← Request body register
│   ├── LoginRequest.java             ← Request body login
│   └── ApiResponse.java              ← Response wrapper
└── exception/
    └── GlobalExceptionHandler.java   ← Centralized error handling
```

---

## 🌐 API yang Tersedia

### Base URL
```
http://localhost:8080
```

---

### 1. `POST /register` — Registrasi Pengguna Baru

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "mypassword"
}
```

**Validasi:**
- `email` → wajib diisi, harus format email yang valid
- `password` → wajib diisi, minimal 6 karakter

**Responses:**

| HTTP Status | Kondisi | Response Body |
|---|---|---|
| `201 Created` | Registrasi berhasil | `{"success": true, "message": "Registration successful"}` |
| `400 Bad Request` | Email sudah terdaftar | `{"success": false, "message": "Email is already registered: ..."}` |
| `400 Bad Request` | Validasi gagal (format/panjang) | `{"success": false, "message": "email: Email format is invalid"}` |

---

### 2. `POST /login` — Login Pengguna

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "mypassword"
}
```

**Responses:**

| HTTP Status | Kondisi | Response Body |
|---|---|---|
| `200 OK` | Login berhasil | `{"success": true, "message": "Login successful"}` |
| `401 Unauthorized` | Email/password salah | `{"success": false, "message": "Invalid email or password"}` |
| `400 Bad Request` | Validasi gagal | `{"success": false, "message": "..."}` |

---

## 📊 Flowchart

### Alur Registrasi — `POST /register`

```
[Client] ──POST /register──► [AuthController]
                                     │
                              Validasi @Valid
                             (email format, min password)
                                     │
                          ┌──────────┴──────────┐
                      GAGAL                   VALID
                          │                      │
               400 Bad Request          [AuthService.register()]
                                               │
                                  Cek email di UserStore
                                               │
                              ┌────────────────┴────────────────┐
                          Email ADA                         Email BELUM ADA
                              │                                  │
                   400 Bad Request                    BCrypt.encode(password)
               "Email is already registered"                     │
                                                    Simpan User ke UserStore
                                                                 │
                                                       201 Created ✅
```

---

### Alur Login — `POST /login`

```
[Client] ──POST /login──► [AuthController]
                                  │
                           Validasi @Valid
                          (email format, not blank)
                                  │
                       ┌──────────┴──────────┐
                   GAGAL                   VALID
                       │                      │
            400 Bad Request         [AuthService.login()]
                                            │
                               Cari email di UserStore
                                            │
                           ┌────────────────┴────────────────┐
                       TIDAK ADA                           ADA
                           │                               │
                  401 Unauthorized              BCrypt.matches(input, hash)
             "Invalid email or password"                   │
                                              ┌────────────┴────────────┐
                                          TIDAK COCOK               COCOK
                                              │                        │
                                   401 Unauthorized            200 OK ✅
                                 "Invalid email or password"  "Login successful"
```

---

## 📖 Dokumentasi API (Swagger UI)

Setelah aplikasi berjalan, buka di browser:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON spec:
```
http://localhost:8080/v3/api-docs
```

---

## 🧪 Unit Test

| Test Class | Jumlah Test | Layer |
|---|---|---|
| `UserStoreTest` | 6 | Repository |
| `AuthServiceTest` | 8 | Service |
| `AuthControllerTest` | 9 | Controller (MockMvc) |
| **Total** | **23** | |

Jalankan semua test:
```bash
mvn test
```

---

## 🚀 Cara Running

### Prasyarat

- Java 17+ terinstall
- Maven terinstall (`mvn -v` untuk cek)

### 1. Clone Repository

```bash
git clone https://github.com/RhmnDev14/test-taiwan-mobile.git
cd test-taiwan-mobile
```

### 2. Jalankan Aplikasi

```bash
mvn spring-boot:run
```

Server berjalan di: `http://localhost:8080`

Saat startup, URL Swagger akan otomatis terlog di console:
```
===========================================================
  Application is running!
  Swagger UI   : http://localhost:8080/swagger-ui/index.html
  API Docs     : http://localhost:8080/v3/api-docs
===========================================================
```

### 3. Test Manual dengan cURL

**Register:**
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "mypassword"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "mypassword"}'
```

### 4. Build JAR

```bash
mvn clean package
java -jar target/user-auth-1.0.0.jar
```

---

## 🔐 Keamanan Password

Password **tidak pernah disimpan dalam bentuk plain text**. Setiap password dienkripsi menggunakan **BCrypt** sebelum disimpan:

```
"mypassword" ──BCrypt.encode()──► "$2a$12$eImiTXuW..." (hash)
```

Saat login, password input dibandingkan dengan hash menggunakan `BCrypt.matches()` — hash tidak pernah di-decode.
