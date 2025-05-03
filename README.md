# Backend Take-Home Assignment: Fleet GPS Tracking Microservice

## Deskripsi

Proyek ini adalah implementasi backend sederhana untuk layanan pelacakan GPS armada kendaraan. Layanan ini menyediakan RESTful API untuk menerima log GPS dari kendaraan dan memungkinkan operator untuk mengambil lokasi terkini serta riwayat pergerakan kendaraan.

## Core Features (Implementasi)

- **Spring Boot REST API:** Dibangun menggunakan Spring Boot.
- **Data Model:**
  - `Vehicle`: ID, plate number, name, type.
  - `GPSLog`: vehicle reference, latitude, longitude, speed, timestamp.
- **Endpoints:**
  - `POST /api/gps`: Menerima log GPS dari kendaraan.
  - `GET /api/vehicles/{id}/last-location`: Mengembalikan data GPS terbaru untuk kendaraan.
  - `GET /api/vehicles/{id}/history?from=<timestamp>&to=<timestamp>`: Mengembalikan log dalam rentang waktu.
- **PostgreSQL Persistence:** Menggunakan PostgreSQL untuk menyimpan data melalui Spring Data JPA/Hibernate.
- **Basic Validation:** Validasi untuk memastikan latitude (-90 hingga 90), longitude (-180 hingga 180), dan speed (>= 0) berada dalam rentang yang valid.
- **Error Handling:** Mengembalikan pesan error yang berguna untuk input tidak valid atau data tidak ditemukan (misalnya, HTTP status 400 dan 404).

## Bonus Features (Jika Diimplementasikan)

- [ ] Speed Violation Service: Flag log dengan kecepatan > 100 km/h.
- [ ] JWT-based Token Authentication: Implementasi login sederhana dengan kredensial hardcoded.
- [ ] Scheduled Task: Membersihkan log GPS lebih tua dari X hari (configurable).

## Setup Instructions

1.  **Prerequisites:**

    - Java Development Kit (JDK) minimal versi [Versi JDK yang Anda gunakan].
    - Maven (jika Anda menggunakan Maven) atau Gradle (jika Anda menggunakan Gradle).
    - PostgreSQL terinstal dan berjalan.
    - Akses ke tool seperti `psql` atau `pgAdmin` untuk membuat database.
    - (Opsional) Postman atau tool serupa untuk menguji API.

2.  **Database Setup:**

    - Buat database baru di PostgreSQL dengan nama `fleet_tracking` (atau nama lain yang Anda inginkan).
    - Pastikan user PostgreSQL Anda (default `postgres` atau user lain) memiliki hak akses ke database ini.
    - Konfigurasi koneksi database di file `src/main/resources/application.properties`:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/fleet_tracking
      spring.datasource.username=postgres
      spring.datasource.password=your_database_password
      spring.datasource.driver-class-name=org.postgresql.Driver
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
      spring.jpa.show-sql=false
      spring.jpa.properties.hibernate.format_sql=true
      ```
      **Pastikan untuk mengganti `your_database_password` dengan password yang benar.**

3.  **Running the Application:**

    - **Menggunakan Maven:** Navigasi ke direktori root proyek di terminal dan jalankan perintah:
      ```bash
      ./mvnw spring-boot:run
      ```
    - **Menggunakan Gradle:** Navigasi ke direktori root proyek di terminal dan jalankan perintah:
      ```bash
      ./gradlew bootRun
      ```
    - Setelah aplikasi berjalan, Anda akan melihat log Spring Boot di console. Biasanya, server akan berjalan di `http://localhost:8080`.

4.  **Accessing API Documentation (Swagger UI):**
    - Buka browser web dan kunjungi URL: `http://localhost:8080/swagger-ui/index.html`

## API Endpoints

- **`POST /api/gps`**: Menerima log GPS.

  - **Parameters (x-www-form-urlencoded):**
    - `vehicleId` (Long, required): ID kendaraan.
    - `latitude` (Double, required): Lintang (-90.0 hingga 90.0).
    - `longitude` (Double, required): Bujur (-180.0 hingga 180.0).
    - `speed` (Double, required): Kecepatan (>= 0).
  - **Response:**
    - `201 Created`: GPS log diterima dan diproses.
    - `400 Bad Request`: Jika input tidak valid.

- **`GET /api/vehicles/{id}/last-location`**: Mendapatkan lokasi terakhir kendaraan.

  - **Path Parameter:**
    - `id` (Long, required): ID kendaraan.
  - **Response:**
    - `200 OK`: JSON berisi data `GPSLog` terbaru.
    - `404 Not Found`: Jika kendaraan atau log tidak ditemukan.

- **`GET /api/vehicles/{id}/history`**: Mendapatkan riwayat lokasi kendaraan.
  - **Path Parameter:**
    - `id` (Long, required): ID kendaraan.
  - **Query Parameters:**
    - `from` (LocalDateTime, required): Waktu awal rentang (format ISO 8601, contoh: `2025-05-03T10:00:00`).
    - `to` (LocalDateTime, required): Waktu akhir rentang (format ISO 8601, contoh: `2025-05-03T18:00:00`).
  - **Response:**
    - `200 OK`: JSON array berisi daftar objek `GPSLog` dalam rentang waktu.
    - `404 Not Found`: Jika kendaraan tidak ditemukan.


## Database Schema

Database yang digunakan adalah PostgreSQL. Hibernate dengan JPA akan mengelola skema database berdasarkan entity yang didefinisikan.

**Tabel: vehicle**

| Kolom        | Tipe Data | Constraints                 | Deskripsi                 |
| ------------ | --------- | --------------------------- | ------------------------- |
| id           | BIGINT    | PRIMARY KEY, AUTO_INCREMENT | ID unik kendaraan         |
| plate_number | VARCHAR   | NOT NULL                    | Nomor plat kendaraan      |
| name         | VARCHAR   |                             | Nama kendaraan (opsional) |
| type         | VARCHAR   |                             | Tipe kendaraan (opsional) |

**Tabel: gps_log**

| Kolom      | Tipe Data | Constraints                        | Deskripsi                |
| ---------- | --------- | ---------------------------------- | ------------------------ |
| id         | BIGINT    | PRIMARY KEY, AUTO_INCREMENT        | ID unik log GPS          |
| vehicle_id | BIGINT    | NOT NULL, FOREIGN KEY (vehicle.id) | ID kendaraan terkait     |
| latitude   | DOUBLE    | NOT NULL, >= -90.0, <= 90.0        | Lintang                  |
| longitude  | DOUBLE    | NOT NULL, >= -180.0, <= 180.0      | Bujur                    |
| speed      | DOUBLE    | NOT NULL, >= 0                     | Kecepatan (km/h)         |
| timestamp  | TIMESTAMP | NOT NULL                           | Waktu pencatatan log GPS |
