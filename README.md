# ₿ Cryptocurrency Portfolio Tracker

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://java.com)
[![JavaFX](https://img.shields.io/badge/JavaFX-Desktop%20App-0096D6?style=for-the-badge)](https://openjfx.io)
[![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://mysql.com)
[![Binance API](https://img.shields.io/badge/Binance-API-F3BA2F?style=for-the-badge&logo=binance&logoColor=black)](https://binance.com/api)
[![License: MIT](https://img.shields.io/badge/License-MIT-22C55E?style=for-the-badge)](LICENSE)

---

Most crypto portfolio apps require you to connect an exchange account or trust a third-party platform with your data. This project takes a different approach — a lightweight JavaFX desktop application that pulls live price data from public APIs, stores your portfolio locally in MySQL, and calculates your profit, loss, and return on investment automatically.

No account connection required. Everything runs on your own machine.

---

## 🔗 Repository

**GitHub → [github.com/shraddha-gidde/Cryptocurrency-Portfolio-Tracker-in-Java](https://github.com/shraddha-gidde/Cryptocurrency-Portfolio-Tracker-in-Java)**

---

## ✨ Features

- **Real-time price feeds** — pulls live prices from the Binance REST API with CoinGecko as a fallback when Binance is unavailable
- **Five-minute caching layer** — stores the last fetched price in a HashMap for five minutes to avoid hitting API rate limits on every UI interaction
- **Automatic P and L calculation** — calculates profit or loss per asset as `(current price − purchase price) × quantity` and updates on every price refresh
- **ROI per asset** — shows return on investment so you can see which holdings are performing and which are not
- **JDBC and MySQL persistence** — every portfolio entry is saved to a local MySQL database so your data survives app restarts
- **O(1) coin lookup** — uses a `HashMap<String, Asset>` internally so price updates and lookups stay fast regardless of how many coins are in the portfolio
- **Sorted portfolio display** — uses an `ArrayList<Asset>` with a custom `Comparator` to render holdings sorted by value
- **Clean OOP architecture** — separate model, service, and controller layers so the code is easy to read, test, and extend

---

## ⚙️ How It Works

**Model layer** (`model/Asset.java`, `model/Portfolio.java`)

Each holding is an `Asset` object with fields for coin symbol, quantity, purchase price, current price, P and L, and ROI. The `Portfolio` class holds an `ArrayList<Asset>` and exposes methods like `getTotalValue()` and `getTotalPnL()` that aggregate across all holdings.

**Service layer** (`service/PriceService.java`, `service/PortfolioService.java`)

`PriceService` handles all API calls. It first tries the Binance REST endpoint. If that fails or returns no data, it falls back to CoinGecko. Responses are cached in a `HashMap<String, CachedPrice>` with a five-minute TTL — the next call within five minutes returns the cached value without making a network request.

`PortfolioService` handles the business logic — calculating P and L and ROI for each asset using the current price from `PriceService`.

**Persistence layer** (`database/DatabaseManager.java`)

Connects to a local MySQL database using JDBC. All CRUD operations use `PreparedStatement` to prevent SQL injection. The schema stores coin symbol, quantity, purchase price, purchase date, and a creation timestamp.

**UI layer** (`controller/PortfolioController.java`)

A JavaFX controller that drives the `TableView`, handles button clicks, calls the service layer, and updates the UI. The controller does not contain any business logic — it only handles what the user sees and does.

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| JavaFX | Desktop GUI — TableView, Charts, Input forms |
| MySQL 8.0+ | Local persistent storage for portfolio data |
| JDBC | Java database connectivity |
| Binance REST API | Primary live price source |
| CoinGecko REST API | Fallback price source |
| Maven | Build and dependency management |
| HashMap and ArrayList | In-memory data structures for fast lookups and sorted rendering |

---

## 📊 Example Portfolio View

| Coin | Quantity | Purchase Price | Current Price | P and L | ROI |
|---|---|---|---|---|---|
| BTC | 0.05 | $42,000 | $67,000 | +$1,250 | +59.5% |
| ETH | 1.2 | $2,200 | $3,500 | +$1,560 | +59.1% |
| SOL | 10 | $95 | $145 | +$500 | +52.6% |

> These are example values. Your actual portfolio data is stored in your local MySQL database and updated with live prices every five minutes.

---

## 📁 Project Structure

```
Cryptocurrency-Portfolio-Tracker-in-Java/
│
├── src/main/java/
│   ├── Main.java                        # Application entry point
│   │
│   ├── model/
│   │   ├── Asset.java                   # Coin holding data model
│   │   └── Portfolio.java               # Portfolio aggregation and totals
│   │
│   ├── service/
│   │   ├── PriceService.java            # Binance and CoinGecko API calls + caching
│   │   └── PortfolioService.java        # P and L and ROI calculation logic
│   │
│   ├── database/
│   │   └── DatabaseManager.java         # JDBC connection and CRUD operations
│   │
│   └── controller/
│       └── PortfolioController.java     # JavaFX UI controller
│
├── src/main/resources/
│   └── fxml/                            # JavaFX layout files
│
├── docs/
│   └── screenshots/
│       ├── portfolio_main.png           # Add your screenshot here
│       ├── add_coin.png                 # Add your screenshot here
│       └── pnl_breakdown.png           # Add your screenshot here
│
├── pom.xml                              # Maven build config
└── README.md
```

---

## 🚀 Installation

**What you need before starting:**
- Java 17 or higher — [download here](https://adoptium.net)
- JavaFX SDK 17 or higher — [download here](https://gluonhq.com/products/javafx/)
- MySQL 8.0 or higher — [download here](https://dev.mysql.com/downloads/)
- Maven — [download here](https://maven.apache.org/download.cgi)

**Clone the repository**

```bash
git clone https://github.com/shraddha-gidde/Cryptocurrency-Portfolio-Tracker-in-Java.git
cd Cryptocurrency-Portfolio-Tracker-in-Java
```

---

## 🗄️ Database Setup

**Create the database in MySQL:**

```sql
CREATE DATABASE crypto_tracker;
USE crypto_tracker;
```

**Run the schema file to create the tables:**

```bash
mysql -u root -p crypto_tracker < docs/schema.sql
```

**Update your database credentials in the code:**

Open `src/main/java/database/DatabaseManager.java` and update these three lines:

```java
private static final String DB_URL  = "jdbc:mysql://localhost:3306/crypto_tracker";
private static final String DB_USER = "your_mysql_username";
private static final String DB_PASS = "your_mysql_password";
```

---

## ▶️ How to Run

**Build and run with Maven:**

```bash
mvn clean install
mvn javafx:run
```

**How to use the app:**
1. Click **Add Coin** and enter the coin symbol (e.g. BTC), quantity, and purchase price
2. The app fetches the current price and calculates your P and L automatically
3. Click **Refresh Prices** to update all prices manually
4. Prices auto-refresh every five minutes in the background

---

## 🔮 Future Improvements

- [ ] Portfolio value history chart using JavaFX `LineChart`
- [ ] Price alert notifications when a coin hits a user-defined target price
- [ ] Support for multiple named portfolios (e.g. long-term vs trading)
- [ ] Export portfolio report as PDF

---

## 👩‍💻 Author

**Shraddha Gidde**
B.Tech — Artificial Intelligence and Data Science
MIT World Peace University, Pune

[![Portfolio](https://img.shields.io/badge/Portfolio-shraddha--gidde.netlify.app-2563EB?style=flat-square)](https://shraddha-gidde.netlify.app)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin)](https://linkedin.com/in/shraddha-gidde-063506242)
[![GitHub](https://img.shields.io/badge/GitHub-shraddha--gidde-181717?style=flat-square&logo=github)](https://github.com/Shraddhaaa05)

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
