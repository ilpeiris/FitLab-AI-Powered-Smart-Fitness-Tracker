# FitLab | ML-Powered Fitness Tracking Platform

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Tomcat](https://img.shields.io/badge/Tomcat-9.0-F8DC75?style=flat-square&logo=apachetomcat&logoColor=black)
![WEKA](https://img.shields.io/badge/WEKA-3.7.13-5C8A00?style=flat-square)
![SQLite](https://img.shields.io/badge/SQLite-3.45-003B57?style=flat-square&logo=sqlite&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-4.x-FF6384?style=flat-square&logo=chartdotjs&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Build](https://img.shields.io/badge/Build-WAR-blue?style=flat-square)

> A full-stack Java 17 web application featuring an end-to-end WEKA ML pipeline from custom dataset creation through multi-model training, evaluation, and serialized deployment with **dual AI integration**: a real-time JAX-RS REST inference API and a post-submission prediction comparison engine. Built on MVC + DAO architecture with jBCrypt authentication and a Chart.js analytics dashboard. Packaged as a deployable WAR artifact targeting Apache Tomcat 9.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [ML Pipeline](#ml-pipeline)
- [System Architecture](#system-architecture)
- [REST API](#rest-api)
- [Security](#security)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Build & Deployment](#build--deployment)
- [Screenshots](#screenshots)
- [Roadmap](#roadmap)

---

## Overview

FitLab is a fitness tracking platform built around a machine learning core. Users log workouts with numeric metrics duration, distance, and calories and the system uses a trained **WEKA NaiveBayes classifier** to predict the activity type in real time as they type, before they submit anything.

The ML layer runs as a **JAX-RS REST API** decoupled from the main servlet-based application. The model is loaded once at application startup by an `AppLifecycleListener`, held as a shared static instance via `MLModelManager`, and served to every prediction request with zero cold-start overhead.

After submission, users see a **side-by-side comparison** of their manually selected activity type versus the AI's independent prediction a UX pattern used in AI-assisted data entry applications to surface model confidence and build trust.

The platform also provides a **Chart.js analytics dashboard** with aggregate fitness statistics and a calorie-over-time trend line.

---

## Key Features

### 🤖 End-to-End ML Pipeline
- Custom ARFF training dataset 72 workout sessions balanced across 4 activity classes
- Three classifiers trained and benchmarked: **NaiveBayes**, **J48 Decision Tree**, **RandomForest**
- Full evaluation per model: accuracy, confusion matrix, Kappa statistic, MAE, RMSE
- **NaiveBayes** selected as the production model matched RandomForest's 100% test accuracy with lower memory footprint and faster inference latency
- Model serialized to `activity_model.model` and deployed inside `WEB-INF/` for runtime access
- Standalone `ModelTrainer.java` program for offline retraining fully decoupled from the web application

### ⚡ Dual AI Integration
- **Real-time prediction** - JAX-RS endpoint `/api/predict` fires on every input keystroke via Fetch API; no page reload, zero latency feedback
- **Post-submission comparison** - after saving a workout, the user sees their manual activity selection next to the AI's independent classification side by side

### 🏗️ Enterprise Java Architecture
- **MVC pattern** - POJOs (Model), JSP + JSTL (View), Java Servlets (Controller)
- **DAO pattern** - `UserDAO`, `WorkoutDAO` fully abstract all database operations from business logic
- **Singleton pattern** - `DatabaseManager` as a static connection utility; `MLModelManager` for shared classifier instance
- **Front Controller pattern** - one dedicated servlet per functional domain
- **ServletContextListener** - `AppLifecycleListener` handles all startup initialization including database path resolution and ML model loading

### 🔌 JAX-RS REST API
- `PredictionResource.java` mapped to `@Path("/predict")` via Jersey
- Stateless, JSON-only responses
- Fully decoupled from the monolithic servlet layer
- Configured via `web.xml` Jersey `ServletContainer` routing all `/api/*` traffic

### 📊 Analytics Dashboard
- Chart.js line chart, calories burned over time
- KPI cards, total workouts, total calories, total distance
- All aggregate statistics calculated server-side via `DashboardServlet`

### 🔒 Security Layer
- jBCrypt password hashing (12 rounds) on all stored credentials
- PreparedStatements on all database interactions zero SQL injection surface
- Server-side session validation on every protected servlet
- JSTL automatic output escaping XSS prevention at the view layer
- Password change requires current password verification before hash update

### 🔎 Advanced Workout Filtering
- Server-side dynamic `WHERE` clause construction for activity type and date filters no full table scans
- Java Streams API for secondary in-memory duration filtering functional pipeline pattern

---

## Tech Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Backend | Java | 17 | Core application language |
| Web Framework | Java Servlet API | 4.0.1 | HTTP request handling, routing |
| View Layer | JSP + JSTL | 2.2 / 1.2 | Server-rendered templates |
| REST API | JAX-RS (Jersey) | 2.x | Real-time ML prediction endpoint |
| Machine Learning | WEKA | 3.7.13 | Classifier training, evaluation, inference |
| Database | SQLite | 3.45.1 | Relational data persistence via JDBC |
| Security | jBCrypt | 0.4 | Adaptive password hashing |
| Data Viz | Chart.js | CDN | Dashboard analytics charts |
| Frontend | HTML5 + CSS3 + Vanilla JS | - | UI, Fetch API, event handling |
| Build | Apache Maven | 3.8+ | Dependency management, WAR packaging |
| Server | Apache Tomcat | 9.0 | Servlet 4.0 container |

---

## ML Pipeline

### Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    OFFLINE TRAINING                         │
│                                                             │
│  workouts-training.arff (58 instances)                      │
│             │                                               │
│             ▼                                               │
│      ModelTrainer.java                                      │
│             │                                               │
│    ┌────────┼────────┐                                      │
│    ▼        ▼        ▼                                      │
│ NaiveBayes  J48   RandomForest   ← trained in parallel      │
│    │        │        │                                      │
│    └────────┼────────┘                                      │
│             ▼                                               │
│  workouts-testing.arff (14 instances) ← evaluation          │
│             │                                               │
│             ▼                                               │
│  Best model selected → activity_model.model (serialized)    │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼ deployed to WEB-INF/
┌─────────────────────────────────────────────────────────────┐
│                    RUNTIME INFERENCE                        │
│                                                             │
│  AppLifecycleListener (startup)                             │
│             │                                               │
│             ▼                                               │
│  MLModelManager.load(activity_model.model)                  │
│             │  ← loaded ONCE, shared across all requests    │
│             ▼                                               │
│  User types → Fetch API → GET /api/predict                  │
│             │                                               │
│             ▼                                               │
│  PredictionResource → DenseInstance → classifyInstance()    │
│             │                                               │
│             ▼                                               │
│  JSON: {"prediction": "Running"} → UI update (no reload)   │
└─────────────────────────────────────────────────────────────┘
```

### Model Comparison Results

Three classifiers were trained on the same dataset and evaluated on a held-out test set of 14 instances:

| Model | Accuracy | Kappa | MAE | RMSE |
|---|---|---|---|---|
| **NaiveBayes** ✅ | **100%** | **1.0** | **0.0056** | **0.0152** |
| RandomForest | 100% | 1.0 | 0.0329 | 0.0917 |
| J48 Decision Tree | 92.86% | 0.9 | 0.0357 | 0.189 |

**NaiveBayes selected** over RandomForest despite equal accuracy lower MAE (0.0056 vs 0.0329), lower RMSE (0.0152 vs 0.0917), faster inference latency, and lower memory footprint make it the better choice for a request-time inference environment.

### Confusion Matrix - NaiveBayes (Final Model)

```
Predicted →   Running  Cycling  Walking  Gym Workout
Actual ↓
Running            4        0        0            0
Cycling            0        3        0            0
Walking            0        0        2            0
Gym Workout        0        0        0            5

Precision: 100%  |  Recall: 100%  |  F1: 1.0  |  Kappa: 1.0
```

### Dataset

| Split | File | Instances |
|---|---|---|
| Training | `workouts-training.arff` | 58 |
| Testing | `workouts-testing.arff` | 14 |
| **Total** | - | **72** |

Features: `duration_mins` (numeric), `distance_km` (numeric), `calories_burned` (numeric)
Target: `activity_type`  `{Running, Cycling, Walking, Gym Workout}`

### Retraining the Model

```bash
mvn exec:java -Dexec.mainClass="com.fitlab.ml.ModelTrainer"
```

This compares all three classifiers, prints full evaluation metrics to console, and serializes the best performer to `activity_model.model`. Copy to `WEB-INF/` before redeploying.

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     VIEW LAYER                              │
│   JSP + JSTL · CSS3 Glassmorphism · Chart.js · Fetch API   │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP
         ┌──────────────────┴──────────────────┐
         │                                     │
┌────────▼────────┐                  ┌─────────▼─────────┐
│ SERVLET LAYER   │                  │   JAX-RS LAYER    │
│ (Monolithic MVC)│                  │  (REST API /api/) │
│                 │                  │                   │
│ LoginServlet    │                  │ PredictionResource│
│ RegisterServlet │                  │ @Path("/predict") │
│ DashboardServlet│                  │ → MLModelManager  │
│ WorkoutServlet  │                  │ → JSON response   │
│ ProfileServlet  │                  └───────────────────┘
└────────┬────────┘
         │
┌────────▼────────────────────────────────────────────────────┐
│                      DAO LAYER                              │
│   UserDAO · WorkoutDAO · DatabaseManager (Singleton)        │
└────────┬────────────────────────────────────────────────────┘
         │ JDBC PreparedStatements
┌────────▼────────┐
│   SQLite 3.45   │
│   fitlab.db     │
└─────────────────┘

Startup:
AppLifecycleListener → DatabaseManager.setPath() + MLModelManager.load()
```

### Design Patterns

| Pattern | Implementation | Where |
|---|---|---|
| MVC | Entities / JSP Views / Servlet Controllers | Entire application |
| DAO | `UserDAO`, `WorkoutDAO` | Data layer |
| Singleton | `DatabaseManager`, `MLModelManager` | Infrastructure layer |
| Front Controller | One servlet per domain | `/login`, `/workouts`, `/dashboard` |
| Observer | JS input event listeners → `fetchPrediction()` | `workouts.jsp` |
| Startup Listener | `AppLifecycleListener implements ServletContextListener` | Application lifecycle |

---

## REST API

The prediction API is a separate JAX-RS (Jersey) layer, decoupled from the servlet monolith. All endpoints return `Content-Type: application/json`.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/predict?duration=&distance=&calories=` | Returns ML activity classification |

### Request

```
GET /api/predict?duration=30&distance=5&calories=320
```

### Response

```json
{
  "prediction": "Running"
}
```

### Error Response

```json
{
  "error": "Missing or invalid parameters"
}
```

### Jersey Configuration (`web.xml`)

```xml
<servlet>
  <servlet-name>Jersey</servlet-name>
  <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
  <init-param>
    <param-name>jersey.config.server.provider.packages</param-name>
    <param-value>com.fitlab.api</param-value>
  </init-param>
</servlet>
<servlet-mapping>
  <servlet-name>Jersey</servlet-name>
  <url-pattern>/api/*</url-pattern>
</servlet-mapping>
```

---

## Security

### Authentication Flow

```
POST /register
  → RegisterServlet.doPost()
  → BCrypt.hashpw(password, gensalt(12))
  → UserDAO.registerUser() → INSERT INTO Users

POST /login
  → LoginServlet.doPost()
  → UserDAO.loginUser() → BCrypt.checkpw(input, stored_hash)
  → session.setAttribute("user", userObject)
  → redirect to /dashboard
```

### Per-Request Authorization

Every protected servlet performs session validation at the entry point of `doGet()` and `doPost()` no centralized filter, so each controller owns its own security gate:

```java
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
}
```

### Security Features Summary

| Feature | Implementation | Standard |
|---|---|---|
| Password hashing | jBCrypt 12 rounds | Industry standard adaptive hash |
| SQL injection prevention | `PreparedStatement` throughout | Zero raw string interpolation |
| XSS prevention | JSTL `<c:out>` auto-escaping | View-layer output sanitization |
| Session hijack prevention | Current password required for password change | Re-authentication on sensitive action |
| Direct URL access | Session check on every servlet | Server-side enforcement |

### Known Security Gaps (Roadmap)

- `/api/predict` endpoint has no authentication any unauthenticated caller can query the model. JWT token guard is on the roadmap.
- No CSRF token protection on form submissions.

---

## Database Schema

SQLite, auto-initialized on first user registration via `DatabaseManager`.

```sql
CREATE TABLE Users (
    user_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username      TEXT    UNIQUE NOT NULL,
    password_hash TEXT    NOT NULL,           -- BCrypt 12 rounds
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Workouts (
    workout_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL REFERENCES Users(user_id),
    activity_type   TEXT    NOT NULL,          -- Running | Cycling | Walking | Gym Workout
    duration_mins   INTEGER NOT NULL,
    distance_km     REAL    NOT NULL,
    calories_burned INTEGER NOT NULL,
    workout_date    TEXT    NOT NULL,          -- YYYY-MM-DD
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Relationships

```
Users ──< Workouts
  PK: user_id       FK: user_id → Users.user_id
```

---

## Project Structure

```
fitlab/
│
├── pom.xml                                # Maven - dependencies, WAR packaging
├── activity_model.model                   # Trained WEKA classifier (root - for retraining)
│
└── src/main/
    │
    ├── java/com/fitlab/
    │   │
    │   ├── User.java                      # User entity POJO
    │   ├── Workout.java                   # Workout entity POJO (8 fields)
    │   ├── AppLifecycleListener.java      # @WebListener - startup: DB path + ML load
    │   │
    │   ├── dao/
    │   │   ├── DatabaseManager.java       # Singleton - dynamic SQLite path resolution
    │   │   ├── UserDAO.java               # register, login, updatePassword, verifyPassword
    │   │   └── WorkoutDAO.java            # addWorkout, getByUser, update, delete, filter
    │   │
    │   ├── ml/
    │   │   ├── ModelTrainer.java          # Offline training - runs standalone via Maven exec
    │   │   └── MLModelManager.java        # Singleton - holds loaded WEKA classifier
    │   │
    │   ├── servlets/
    │   │   ├── LoginServlet.java          # POST /login
    │   │   ├── RegisterServlet.java       # POST /register
    │   │   ├── DashboardServlet.java      # GET /dashboard - stats + Chart.js data
    │   │   ├── WorkoutServlet.java        # GET/POST /workouts - CRUD + AI comparison
    │   │   ├── EditWorkoutServlet.java    # GET/POST /edit-workout
    │   │   ├── ProfileServlet.java        # GET/POST /profile - password change
    │   │   └── LogoutServlet.java         # GET /logout - session invalidation
    │   │
    │   └── api/
    │       └── PredictionResource.java    # @Path("/predict") - JAX-RS inference endpoint
    │
    ├── resources/data/
    │   ├── workouts-training.arff         # 58 training instances (WEKA format)
    │   └── workouts-testing.arff          # 14 test instances (WEKA format)
    │
    └── webapp/
        ├── login.jsp
        ├── register.jsp
        ├── dashboard.jsp                  # Chart.js KPI dashboard
        ├── workouts.jsp                   # Workout log + real-time AI prediction UI
        ├── edit-workout.jsp
        ├── profile.jsp
        ├── fitlab.db                      # SQLite database file
        │
        ├── css/
        │   └── style.css                  # Glassmorphism UI - Bootstrap-free
        │
        └── WEB-INF/
            ├── web.xml                    # Jersey config, welcome-file, servlet mappings
            └── activity_model.model       # Serialized classifier - runtime inference
```

---

## Getting Started

### Prerequisites

- Java JDK 17+
- Apache Maven 3.6+
- Apache Tomcat 9.0+ (Servlet 4.0 compatible)

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/fitlab-weka-ml-platform.git
cd fitlab-weka-ml-platform
```

**2. Build the WAR**
```bash
mvn clean package
```
Output: `target/fitlab.war`

**3. Deploy to Tomcat**

Option A - Manual:
```bash
cp target/fitlab.war /path/to/tomcat/webapps/
/path/to/tomcat/bin/startup.sh
```

Option B - In-place for development:
```bash
# Configure Tomcat manager credentials in tomcat-users.xml, then:
mvn tomcat7:deploy
```

**4. Open in browser**
```
http://localhost:8080/fitlab/
```

The database schema is auto-created on first registration. No manual SQL setup required.

### Default Test Accounts

After registering via the UI, the app is ready to use. For development purposes you can seed a test user directly:

```sql
-- Insert a test user (password: password123 - bcrypt hash shown below)
INSERT INTO Users (username, password_hash)
VALUES ('testuser', '$2a$12$...');
```

Or simply register through the UI registration is open with no email verification.

---

## Build & Deployment

### Build Commands

```bash
# Clean build
mvn clean package

# Compile only (no WAR)
mvn clean compile

# Retrain ML model
mvn exec:java -Dexec.mainClass="com.fitlab.ml.ModelTrainer"

# After retraining, copy model to WEB-INF before rebuild
cp activity_model.model src/main/webapp/WEB-INF/
mvn package
```

### Verify WAR Contents

```bash
jar -tf target/fitlab.war | grep -E "\.class|\.model|\.jsp|\.xml"
```

### Cloud Deployment

The WAR artifact is portable across any Servlet 4.0 container:

```bash
# AWS Elastic Beanstalk
eb init && eb deploy

# Heroku (with webapp-runner)
heroku create
heroku deploy:jar target/fitlab.war --app your-app-name
```

### Environment Configuration

No environment variables required all configuration is handled at runtime by `AppLifecycleListener` via `context.getRealPath()`. The database path and ML model path are resolved dynamically, making the WAR portable without any server-specific configuration files.

---

## Screenshots

| Login | Dashboard | Workout Log |
|---|---|---|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) | ![Workouts](screenshots/workouts.png) |

| Real-Time AI Prediction | AI vs User Comparison | Edit Workout |
|---|---|---|
| ![AI Prediction](screenshots/ai-prediction.png) | ![AI Comparison](screenshots/ai-comparison.png) | ![Edit](screenshots/edit-workout.png) |

| Profile | ML Training Console |
|---|---|
| ![Profile](screenshots/profile.png) | <img src="screenshots/model-training.png" width="500"/> |

---

## Roadmap

- [ ] JWT token authentication on `/api/predict` endpoint
- [ ] CSRF token protection on all form submissions
- [ ] Migrate from SQLite to PostgreSQL for concurrent write support
- [ ] Pagination on workout history (currently loads full user dataset)
- [ ] Move duration filtering server-side (currently hybrid client/server)
- [ ] PHPUnit-style unit tests for DAO layer (JUnit 5 + H2 in-memory DB)
- [ ] Docker + docker-compose for single-command local environment
- [ ] Expand ARFF dataset and retrain with cross-validation for more robust accuracy estimation
- [ ] REST API versioning (`/api/v1/`)
- [ ] JWT-based stateless auth for horizontal scaling support

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Use `PreparedStatement` for all database interactions no raw string concatenation into SQL
4. Add session validation at the top of every new servlet's `doGet()` and `doPost()`
5. Any new endpoint under `/api/` must go through `PredictionResource.java` or a new JAX-RS resource class not a servlet
6. If retraining the model, run `ModelTrainer`, confirm evaluation metrics, copy `.model` to `WEB-INF/`, then rebuild
7. Open a pull request with a description of what changed and why

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
