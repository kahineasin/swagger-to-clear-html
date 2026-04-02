# swagger-to-clear-html (SwaggerToClearHtml.java)
只需一个类，就能将swaggerJson转换为简洁而全面的HTML

# 作者留言
如果你想把swaggerJson转换为本地文档,但苦于swagger2markup等三方库的烦恼,建议你复制此类到你的项目, 不会使你失望.
-- BENJAMIN from China

# 使用方式
1. 保证 jackson 依赖, 复制 [SwaggerToClearHtml](SwaggerToClearHtml.java) 到你的java项目 
2. 运行 http://localhost:8080/v2/api-docs, 或者复制已有的swaggerJson 
3. 调用 

```java
SwaggerToClearHtml.generateHtml(swaggerJson ,"docs/api.html")
```

# 兼容性
1. 依赖com.fasterxml.jackson.core.jackson-core 2.13.5
2. 测试环境springfox-swagger2 2.6.1, swagger-core 1.5.16
3. 你可以通过 [swaggerJson.json](swaggerJson.json) 来了解输入参数的结构
