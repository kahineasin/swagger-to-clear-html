# swagger-to-clear-html (SwaggerToClearHtml.java)
[中文](README.zh-CN.md) | English

**One Java class** to convert Swagger JSON into a clean, comprehensive HTML documentation — without any unstable third-party plugins.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## ✨ Why this tool?

Tired of `swagger2markup` dependency hell? Frustrated by Swagger UI not working in production?  
This tool is for you: copy **one single Java file** into your test source folder, and generate a **readable, offline HTML** directly from your running Spring Boot app's `/v2/api-docs`.

- ✅ No Maven plugin configuration
- ✅ No repository hassle
- ✅ Full control over the output format
- ✅ Shows **multiple response models** for different HTTP status codes (200, 201, 333, ...)
- ✅ Displays `enum`, `format`, `example`, and field descriptions
- ✅ Works for both `@RestController` (JSON) and `@Controller` (HTML view) endpoints

## 🚀 Quick Start

### 1. Add dependency (if not already present)

This tool uses **Jackson** (or Fastjson — see the code). The provided example uses Jackson, so add:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.5</version>
    <scope>test</scope>
</dependency>
```

2. Copy the class

Place SwaggerToClearHtml.java into your src/test/java directory (any package).

3. Run the test

```java
@SpringBootTest
public class SwaggerToClearHtmlTest {
    @Test
    public void generateDoc() throws Exception {
        SGRequestResult r=SGHttpHelper.HttpGet("http://localhost:8080/v2/api-docs?group=shop", null); //use anyway to got http
        SwaggerToClearHtml.generateHtml(r.content,"docs/api.html");
    }
}
```

Then start your Spring Boot app and run the test. The HTML file will be created under docs/api.html.

4. Open the file

Open docs/api.html in any browser. That's it — no server needed.

📋 Requirements

· Spring Boot with Springfox 2.6.1 (or any version that outputs standard Swagger 2.0 JSON)
· Java 8+
· Jackson (for JSON parsing) – or modify the code to use your preferred JSON library

🖼️ Example Output

See the screenshots folder or the api.html example file included in the repo.

🤝 Contributing

Issues and PRs are welcome. Keep it one class.

📄 License

MIT

👤 Author

BENJAMIN from China
5 years of backend development experience, tired of bad documentation tools.


