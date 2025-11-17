# 🎮 Tessellate

<div align="center">

### *Relive the Classic Dot-Connect Game from Our 2000s Childhood*

A minimalist **JavaFX** game that reimagines the nostalgic school-day challenge — connecting dots, forming triangles, and battling friends for geometric glory.

![JavaFX](https://img.shields.io/badge/JavaFX-25-orange?style=for-the-badge&logo=java)
![Status](https://img.shields.io/badge/Status-Active-green?style=for-the-badge)
![Language](https://img.shields.io/badge/Language-Java-blue?style=for-the-badge&logo=openjdk)

[⬇️ Download](#installation) • [✨ Features](#-features) • [🚀 Setup](#-setup) • [🧠 Rules](#-rules) • [🧩 Future Plans](#-future-plans) • [🎓 About](#-about-this-project)  


</div>

---

## 🌟 What is *Tessellate*?

*Tessellate* is a tribute to one of the most beloved childhood games — the **dot-connect game** every 2000s kid played during school breaks.  
The goal is simple: **connect dots without crossing lines** and **form triangles** to claim territory. Every triangle earns you points.  
The player with the most triangles wins — pure geometry, pure strategy, pure nostalgia.

---

## 📸 Gameplay Preview

<p align="center">
  <a href="https://github.com/TJ-Paul/Tessellate/blob/main/src/Tesselate_gameplay.png">
    <img src="code/src/Tesselate_gameplay.png" alt="Tessellate Gameplay" width="600" style="border-radius:12px;box-shadow:0 0 10px rgba(0,0,0,0.2);"/>
  </a>
</p>
<p align="center"><em>Classic dot-connection revived — clean, simple, and strategic.</em></p>

---

## ✨ Features

### 🎨 **Core Gameplay**
– **Connect Dots (Edges):** Click two dots to draw a connecting edge.  
– **No Intersections:** Edges cannot cross each other — strategy matters!  
– **Triangle Formation:** When a triangle is formed, the player scores a point.  
– **Turn-Based Play:** Two players alternate turns until all possible edges are drawn.

### 🧩 **Game Logic & Rules**
– **Non-Collinearity:** No three dots can lie on the same line — triangles only!  
– **Edge Validation:** Every move is checked to ensure it doesn’t intersect existing lines.  
– **Score System:** Points dynamically update as triangles form.  
– **Winning Condition:** The player with the highest triangle count wins.

### ⚙️ **Technical Highlights**
– **Built with JavaFX 25** for a modern, responsive UI  
– **Clean MVC structure** for maintainable code  
– **Random Dot Generator** for endless replayability  
– **Lightweight & Fast:** Runs smoothly on any modern JDK setup

---

## 🧠 Rules

– You must connect exactly three dots to form a triangle.  
– Any structure resembling a triangle with more than three dots will not be considered a valid triangle.  
– You cannot connect two dots if the connecting edge passes over a third dot in between.  
– Edges cannot intersect existing ones — intersections are invalid.  
– Intersecting triangles are not allowed; each must occupy unique, non-overlapping space.

---

## 🧩 Future Plans

– Add multiplayer over local network  
– Implement AI bot player with difficulty levels  
– Custom dot density  
– Maintain a leaderboard system

---

## 🎓 About This Project

*Tessellate* is developed by **Turjjo Paul**, a student of  
**Bangladesh University of Engineering and Technology (BUET)**, Department of Computer Science and Engineering.

It showcases:  
– JavaFX GUI game logic design  
– Geometry-based computational validation  
– Event-driven interactive gameplay  
– Clean object-oriented architecture

This project is a blend of childhood nostalgia and modern Java development — bringing back a timeless game to digital life.

---

## 📞 Contact & Support

– 🐛 **Report Issues:** [GitHub Issues](https://github.com/TJ-Paul/Tessellate/issues)  
– 💡 **Feature Requests:** Open a suggestion anytime  
– 📧 **Email:** [tjpaul770@gmail.com](mailto:tjpaul770@gmail.com)  
– 💼 **LinkedIn:** [Turjjo Paul](https://www.linkedin.com/in/turjjo-paul/)  
– 🐙 **GitHub:** [@TJ-Paul](https://github.com/TJ-Paul)

---

## 🚀 Setup

### Prerequisites
*Tessellate* requires **Java JDK 25 or higher.**

👉 **[Download JDK 25](https://www.oracle.com/java/technologies/downloads/)**

### Installation

```bash
git clone https://github.com/TJ-Paul/Tessellate.git
cd Tessellate

download the app_nocode
make sure you have java jdk_25 or higher installed on your device
(check: java --version)
double click on RUN.bat
enjoy
