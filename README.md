# BiciklRent

Veb aplikacija za upravljanje iznajmljivanjem bicikala, razvijena kao projekat na predmetu
Napredne Java tehnologije (NJT).

## Tehnologije

**Backend**
- Java 17, Spring Boot 4.1
- Spring Data JPA / Hibernate
- MySQL
- Liquibase (verzionisanje seme baze)
- JWT (jjwt) za autentifikaciju
- OpenPDF za generisanje PDF potvrda
- Spring Mail za slanje email-a
- Lombok

**Frontend**
- React 19
- Vite
- React Router

## Funkcionalnosti

**Neautentifikovani posetilac**
- Pregled ponude bicikala sa filtriranjem po periodu dostupnosti
- Pretraga i sortiranje bicikala
- Pregled recenzija za bicikl

**Klijent**
- Registracija uz verifikaciju email adrese (6-cifreni kod)
- Prijava na sistem
- Iznajmljivanje bicikla sa simulacijom placanja
- Automatska PDF potvrda poslata na email
- Pregled sopstvenih iznajmljivanja
- Ostavljanje ocene i komentara (recenzije) za bicikl

**Zaposleni**
- Prijava na sistem
- Upravljanje katalogom bicikala (dodavanje, izmena, brisanje)
- Upravljanje klijentima (izmena, brisanje)
- Pregled statistike poslovanja

## Pokretanje projekta

### Zahtevi
- Java 17+
- Maven
- Node.js 18+
- MySQL 8+

### 1. Baza podataka

Napravi praznu bazu:

```sql
CREATE DATABASE njtbicikl;
```

### 2. Backend

Kopiraj sablon konfiguracije i popuni svoje vrednosti:

```bash
cd backend/src/main/resources
cp application.properties.example application.properties
```

Otvori `application.properties` i podesi:
- `spring.datasource.username` / `password` - MySQL kredencijali
- `jwt.secret` - dugacak slucajan string (min 32 karaktera)
- `spring.mail.username` / `password` - Gmail adresa i App Password

Pokreni backend:

```bash
cd backend
mvn spring-boot:run
```

Backend radi na `http://localhost:8080`. Liquibase automatski kreira sve tabele
i unosi pocetne podatke pri prvom pokretanju.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend radi na `http://localhost:5173`.

## Test korisnici

Iz pocetnih (seed) podataka:

| Tip | Username | Lozinka |
|---|---|---|
| Klijent | klijent | klijent123 |
| Zaposleni | zaposleni | zaposleni123 |

## Struktura projekta

```
backend/
  src/main/java/com/projekat/backend/
    controller/    - REST kontroleri
    service/       - poslovna logika
    repository/    - JPA repozitorijumi
    entity/        - JPA entiteti
    dto/           - Data Transfer Objects
    security/      - JWT utility
    exception/     - globalno rukovanje greskama
    config/        - Liquibase konfiguracija
  src/main/resources/
    db/changelog/  - Liquibase changeset-ovi

frontend/
  src/
    pages/         - stranice aplikacije
    api/           - konfiguracija konekcije sa backend-om
    utils/         - pomocne funkcije
    assets/        - slike (SVG ilustracije bicikala)
```