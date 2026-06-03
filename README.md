# Hospital OPD

A basic Spring Boot OPD application for patient registration, nurse pre-tests, and doctor checkups.

## Features

- Register a patient and create an OPD token.
- Track visit status: registered, pre-test done, and checkup done.
- Capture nurse vitals such as temperature, pulse, blood pressure, weight, SpO2, and notes.
- Capture doctor diagnosis, prescription, and follow-up advice.
- View the OPD dashboard and detailed visit records.
- Download full patient and OPD visit reports as Excel or PDF.
- Uses an in-memory H2 database for quick local development.

## Run

```powershell
mvn spring-boot:run
```

Open `http://localhost:8080`.

Local demo login users:

- Admin: `admin` / `admin123`
- Reception: `reception` / `reception123`
- Nurse: `nurse` / `nurse123`
- Doctor: `doctor` / `doctor123`

Report downloads:

- Excel: `http://localhost:8080/reports/patients.xlsx`
- PDF: `http://localhost:8080/reports/patients.pdf`

The local H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:hospitalopd`, username `sa`, and an empty password.

## Production MySQL Deployment

Create a MySQL database and user:

```sql
CREATE DATABASE hospitalopd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'hospitalopd'@'%' IDENTIFIED BY 'change-this-password';
GRANT ALL PRIVILEGES ON hospitalopd.* TO 'hospitalopd'@'%';
FLUSH PRIVILEGES;
```

Run with the production profile and environment variables:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:mysql://localhost:3306/hospitalopd?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="hospitalopd"
$env:DB_PASSWORD="change-this-password"
$env:ADMIN_USERNAME="admin"
$env:ADMIN_PASSWORD="change-admin-password"
$env:RECEPTION_USERNAME="reception"
$env:RECEPTION_PASSWORD="change-reception-password"
$env:NURSE_USERNAME="nurse"
$env:NURSE_PASSWORD="change-nurse-password"
$env:DOCTOR_USERNAME="doctor"
$env:DOCTOR_PASSWORD="change-doctor-password"
java -jar target\hospital-opd-0.0.1-SNAPSHOT.jar
```

Role access:

- `ADMIN`: full access, including H2 console in local mode.
- `RECEPTION`: dashboard, registration, visit details, Excel/PDF reports.
- `NURSE`: dashboard, visit details, nurse pre-test queue and vitals entry.
- `DOCTOR`: dashboard, visit details, doctor queue, diagnosis, medicines, prescription.
