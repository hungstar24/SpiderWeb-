# SpiderWeb — Internet Search App 

SpiderWeb is a Java Swing desktop application that searches the Internet using **Google Programmable Search Engine**
(also known as **Google Custom Search JSON API**). The app provides a clean GUI to enter keywords, display search results,
filter by domain, and open links directly in a browser.

---

## Why this project exists
Instead of using a browser manually, SpiderWeb demonstrates how to:
- call a real-world REST API (Google CSE) from Java,
- parse JSON responses,
- present results in a desktop UI,
- store configuration safely (API Key & CX) without hard-coding secrets.

---

## Main Features
- ✅ Search with **1–3 keywords**
- ✅ “Price Search” mode (adds keyword **giá** to the query)
- ✅ Results list: title, snippet, domain, link
- ✅ Domain list: click to filter results
- ✅ Double-click a result to open in browser
- ✅ Settings dialog (⚙) to store API Key & CX locally using `Preferences`
- ✅ Modern UI using FlatLaf

---

## Tech Stack
- **JDK 23**
- **Maven**
- **Swing** (desktop UI)
- `java.net.http.HttpClient` (HTTP requests)
- **Jackson Databind** (JSON parsing)
- **FlatLaf** (modern Look & Feel)

---

## Project Structure (Maven)
SpiderWeb/
pom.xml
src/main/java/com/mycompany/spiderweb/

SpiderWebApp.java

CseClient.java

Models.java

Renderers.java

SettingsDialog.java

Pretty.java

Utils.java

**README.md**
---
