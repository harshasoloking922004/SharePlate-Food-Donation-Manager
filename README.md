# 🍃 SharePlate – Food Surplus Donation Manager

<p align="center">
  <b>Don't Waste Food. Share Food. Save Lives.</b>
</p>

## 📌 Project Overview

SharePlate is a Java-based food surplus donation management system that connects food donors, such as restaurants, college canteens, and event organizers, with NGOs.

The application helps donors share surplus food information and allows NGOs to find, collect, and distribute available food to people in need. It also provides live GPS location tracking to improve pickup and delivery coordination.

## 🎯 Objectives

- Reduce food wastage through digital food donations.
- Connect food donors with NGOs.
- Provide secure user registration and login.
- Allow donors to add and manage food donations.
- Enable NGOs to search and collect available food.
- Support live GPS location sharing and tracking.

## ✨ Key Features

### 1. User Authentication
- User registration and login.
- Separate DONOR and NGO roles.
- Password hashing for improved security.

### 2. Donor Dashboard
- Add surplus food donations.
- Enter food name, category, quantity, pickup location, and deadline.
- View and manage donation records.
- Monitor donation statistics.

### 3. NGO Dashboard
- View available food donations.
- Search by food name or category.
- View donor and pickup information.
- Collect available donations.
- Enter a custom delivery address.
- View collected donations.

### 4. Live GPS Tracking
- Share current location using browser GPS.
- Display donor and NGO locations on an interactive map.
- View updated location information.
- Use temporary tracking launch codes.
- Support location sharing during pickup and delivery.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Core application development |
| Java Swing | Desktop graphical interface |
| JDBC | Database connectivity |
| MySQL | Data storage |
| Spring Boot | Live tracking REST API |
| HTML | Tracking webpage structure |
| CSS | Webpage styling |
| JavaScript | Tracking functionality |
| Leaflet.js | Interactive maps |
| OpenStreetMap | Map data |
| Maven | Dependency and build management |

## 🏗️ System Architecture

The project consists of three major layers:

1. **Presentation Layer:** Java Swing donor and NGO dashboards and the live tracking webpage.
2. **Application Layer:** Java business logic, JDBC operations, and Spring Boot tracking API.
3. **Database Layer:** MySQL database for users, donations, collections, and tracking information.

## 🔄 Application Workflow

1. Donor registers and logs in.
2. Donor adds surplus food details.
3. Donation information is stored in MySQL.
4. NGO logs in and views available donations.
5. NGO selects a donation and enters a delivery address.
6. Donation status is updated to collected.
7. A tracking session is created.
8. Donor and NGO can share their GPS locations.
9. Locations are displayed on the live tracking map.

## 🗄️ Database

SharePlate uses MySQL to manage application data.

Main data areas include:
- User accounts
- Food donations
- Collection information
- Tracking sessions
- Location information

## ⚙️ System Requirements

- Java Development Kit (JDK 21 or compatible configured version)
- MySQL Server
- Apache Maven
- Visual Studio Code or another Java IDE
- Modern web browser
- Internet connection for live map services and location tracking

## 🚀 Installation and Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/harshasoloking922004/SharePlate-Food-Donation-Manager.git
```

### Step 2: Open the Project

```bash
cd SharePlate-Food-Donation-Manager
```

### Step 3: Configure MySQL

1. Start your MySQL server.
2. Create the SharePlate database.
3. Configure your database connection details in the project.
4. Execute the required SQL table creation scripts.

### Step 4: Build the Project

```bash
mvn clean compile
```

### Step 5: Copy Dependencies

```bash
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
```

### Step 6: Run the Desktop Application

```powershell
java -cp "target/classes;target/dependency/*" com.shareplate.SharePlateApp
```

### Step 7: Run the Live Tracking API

Open another terminal:

```powershell
java -cp "target/classes;target/dependency/*" com.shareplate.TrackingApiApplication
```

Open the tracking webpage in your browser:

```text
http://localhost:8080/tracking.html
```

> **Note:** The database configuration and SQL scripts must match your local project. Run the tracking API separately when using live tracking. Browser GPS requires location permission and a secure context such as localhost.

## 🔐 Security Features

- Password hashing.
- Role-based user access.
- Temporary tracking launch codes.
- Tracking access validation.
- Browser-controlled location permissions.

## 🌱 Future Enhancements

- Android and iOS mobile applications.
- Push notifications for new food donations.
- AI-based food demand prediction.
- Automatic donor and NGO matching.
- Route optimization.
- Advanced donation analytics and reports.

## 👥 Project Team

**Project:** SharePlate – Food Surplus Donation Manager

**Team Size:** 6 Members

Add your six team members' names and roll numbers here.

| S.No | Team Member | Roll Number |
|---|---|---|
| 1 | Member 1 | Roll Number |
| 2 | Member 2 | Roll Number |
| 3 | Member 3 | Roll Number |
| 4 | Member 4 | Roll Number |
| 5 | Member 5 | Roll Number |
| 6 | Member 6 | Roll Number |

## 🎓 Project Information

- **Project Type:** Advanced Java Project
- **Domain:** Social Impact / Food Donation Management
- **Department:** Computer Science and Engineering
- **Academic Year:** 2026–2027

## ❤️ Conclusion

SharePlate aims to reduce food wastage by connecting food donors with NGOs through a centralized digital platform. By combining Java, MySQL, and live GPS tracking, the project makes food donation and collection management easier, more organized, and more transparent.

---

<p align="center">
  <b>🍃 SharePlate – Every Meal Matters!</b>
</p>