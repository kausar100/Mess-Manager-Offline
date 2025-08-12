# Mess Manager Offline App

A **fully offline** mess management app built with **Kotlin** using **Jetpack Compose**, following **MVVM architecture** and **best practices** with **Hilt** for dependency injection and **Room** for local database management.  
The app is designed to simplify mess management — from tracking members and meals to handling bazar expenses, utility bills, and generating monthly reports.

---

## 📱 Download APK

[![Download APK](https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android)](apk/MessManager.apk)


## 🛠 Tech Stack

- **Kotlin**
- **Jetpack Compose** – Modern declarative UI
- **MVVM Architecture**
- **Hilt** – Dependency Injection
- **Room Database** – Offline data persistence
- **Coroutines & Flow** – Asynchronous programming

---

## ✨ Features

### 📊 Dashboard
- 4 main cards: **Manage Members**, **Daily Meals**, **Bazar List**, **Utility Costs**
- Quick access to **Monthly Report**

### 👥 Manage Members
- View list of members with:
  - Name
  - Contact Number
  - Total Deposit
- Add new member
- Add deposit for existing member
- Delete member

### 🛒 Bazar List
- Add monthly bazar expenses
- View and delete individual bazar entries

### 🍽 Daily Meals
- Record meals for each member:
  - Breakfast
  - Lunch
  - Dinner
- Add entries for future days

### 💡 Utility Costs
- View list of utility bills with amounts
- Delete utility bills

### 📑 Monthly Report
- View overall monthly statistics:
  - Total Bazar Cost
  - Total Utility Cost
  - Meal Rate
- Member-wise details:
  - Deposit
  - Total Meals
  - Meal Cost
  - Utility Share
  - Final Settlement (Will Get / Will Give)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable version)
- Gradle (bundled with Android Studio)
- Minimum SDK: 26+
- Target & Compile SDK: 35

### Installation
1. Clone this repository:
   ```bash
   git clone https://github.com/kausar100/Mess-Manager-Offline.git

