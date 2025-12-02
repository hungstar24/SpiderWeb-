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

---
## User Guide 
## 1) Requirements
- JDK 23
- Maven
- Internet connection

## 2) Get API Key & CX

### A) Create API Key (Google Cloud)
1. Open **Google Cloud Console**
2. Create / Select a project
3. Go to **APIs & Services → Library**
4. Enable: **Custom Search API**
5. Go to **APIs & Services → Credentials**
6. Click **Create Credentials → API key**
7. Copy your API key (`AIza...`)

> Tip: You can restrict the key later for security.

### B) Create CX (Search Engine ID)
1. Open **Programmable Search Engine** control panel
2. Create a new search engine:
   - Recommended: **Search the entire web**
   - Or add specific websites (optional)
3. Open the search engine settings
4. Copy the **Search engine ID (cx)**

## 3) Run the project
Open terminal in the folder that contains `pom.xml`:
mvn clean compile exec:exec

## 4) First-time setup inside the app
Click the ⚙ button

Paste your API Key

Paste your CX

Click Save

The app stores these values locally using Preferences.

## 5) Search
Enter Keyword 1 (required)

Optionally enter Keyword 2 and Keyword 3

Click:

Tìm kiếm (Search)

Tìm giá sản phẩm (Price Search)

Results appear on the right side.

## 6) Filter by domain
Click a domain in the left list to filter results

Select Tất cả to show all results again

## 7) Open a result
Double-click any result to open it in your default browser

## 8) Troubleshooting
“API Error” or no results
Check API Key and CX are correct

Make sure Custom Search API is enabled in Google Cloud

Check quota/billing limits in Google Cloud

Maven error: Could not find main class
Ensure exec.mainClass in pom.xml points to your main class, e.g.:
com.mycompany.spiderweb.SpiderWebApp

--- 
## Video demo
