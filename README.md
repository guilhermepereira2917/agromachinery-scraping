# Oystr Agromachinery Scraping

Oystr Agromachinery Scraping is a Spring Boot application that collects information about agricultural machinery from multiple websites. It was developed as a technical challenge for [Oystr](https://oystr.com.br).

The application automatically selects the appropriate scraper for each website and extracts data such as:
- Model
- Contract Type
- Manufacturer
- Year of manufacture
- Price
- Location
- Photos

Currently it supports the following websites:
- [Agrofy](https://www.agrofy.com.br)
- [Mercado Máquinas](https://www.mercadomaquinas.com.br)
- [Tratores e Colheitadeiras](https://www.tratoresecolheitadeiras.com.br)

# Functionalities
- Automatic site detection: The scraper identifies the correct parser for each supported website.
- Concurrent scraping: Multiple URLs are processed in parallel using a configurable thread pool.
- Robust error handling: Logs warnings when a scraper is not available or a URL fails to fetch, without stopping other tasks.
- Configurable parameters: User-agent, timeout, thread count, and list of URLs can be set via application.yml.
- Extensible design: Easy to add new scrapers for additional websites.

# How to Run
Prerequisites:
- Java 21
- Maven

## Running directly with Maven
```bash
mvn spring-boot:run
```

## Running the packaged JAR
### Build the project
```bash
mvn clean package
```

### Run the JAR
```bash
java -jar target/agromachinery-scraping-0.0.1-SNAPSHOT.jar
```

### Optional: Override configurations
```bash
java -jar target/agromachinery-scraping-0.0.1-SNAPSHOT.jar \
  --scraper.threads-count=5 \
  --scraper.await-termination-seconds=60 \
  --scraper.urls=https://www.agrofy.com.br/trator-magnum-315.html,https://www.agrofy.com.br/trator-john-deere-8320r-204540.html
```

# Configuration
The scraper can be customized via the application.yml file. You can also override these properties at runtime via command-line arguments.

| Property                            | Description                                      |
| ----------------------------------- | ------------------------------------------------ |
| `scraper.user-agent`                | User-Agent header for HTTP requests              |
| `scraper.timeout`                   | Connection timeout in milliseconds               |
| `scraper.threads-count`             | Number of threads to use for concurrent scraping |
| `scraper.await-termination-seconds` | Maximum seconds to wait for all tasks to finish  |
| `scraper.urls`                      | List of URLs to scrape                           |

> **Note:** If the configured `User-Agent` does not work (e.g., you are getting `403 Forbidden` on HTTP requests), make sure to look for [a more updated one](https://www.zenrows.com/blog/user-agent-web-scraping#importance).

# Tests
The project includes unit tests to ensure the scraper and utility classes work as expected.

## Run all tests with Maven
```bash
mvn test
```

Tests are written using JUnit 5 and Mockito for mocking dependencies.

# Technologies
- Java 21
- Spring Boot 3
- Jsoup
- Jackson (ObjectMapper)
- JUnit 5
- Mockito
- Maven

## Example Output
When the scraper runs, it logs each fetched machine. Example:
```console
INFO  --- Executor service shutdown initiated...
INFO  --- Fetched machine: Machine {
  model='JOHN DEERE S550 2022/2022',
  contractType=SALE,
  make='JOHN DEERE',
  year=2022,
  workedHours=1168,
  city='FERNANDOPOLIS/SP',
  price=R$ 1.380.000,00,
  photo='https://d36qmzp7jiean8.cloudfront.net/cliente_009472/veiculos/1279673_whatsapp%20image%202025-07-19%20at%2013.01.30_big.webp?r=B6JS7hzFIUGbSVfAuGKZJA',
  photoBase64='data:image/png;base64,UklGRgBSIgBXRUJQVlA4TPRRIgAv/0...',
  url='https://www.tratoresecolheitadeiras.com.br/veiculo/fernandopolis/sp/colheitadeira/john-deere/john-deere-s550/2022/-rotor-axial/cabine-cabinado/agro-novaes-maquinas-agricolas/1279673'
}
```
