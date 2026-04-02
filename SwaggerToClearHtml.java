package org.sg.helper;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

//
/**
 * Convert swaggerJson to easy HTML
 * 
 * by BENJAMIN
 */
public class SwaggerToClearHtml {
    public static void generateHtml(String swaggerJson,String outFile) throws Exception {
        // 1. 获取本地 Swagger JSON
//        TestRestTemplate restTemplate = new TestRestTemplate();
//        ResponseEntity<String> response = restTemplate.getForEntity(
//                "http://localhost:8080/v2/api-docs", String.class);
//        String swaggerJson = response.getBody();
        
//        SGRequestResult r=SGHttpHelper.HttpGet("http://localhost:8080/v2/api-docs?group=shop", null);
//        if(!r.success) {
//        	throw new Exception("文档地址异常");
//        }
//        String swaggerJson=r.content;
        // 2. 保存 JSON 到文件（可选，方便查看）
        Files.write(Paths.get("target/swagger.json"), swaggerJson.getBytes());

        // 3. 使用简单的模板生成 HTML（这里只生成基本框架，你可以自己扩展）
        String html = buildHtmlFromJson(swaggerJson);

        // 4. 写入文件
        File output = new File(outFile);
        output.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(html);
        }

        System.out.println("文档已生成：" + output.getAbsolutePath());
    }

    private static String buildHtmlFromJson(String swaggerJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(swaggerJson);
        
        // 这里只是示例，你可以根据自己需要生成更美观的 HTML
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><title>API文档</title>")
        .append("<style type=\"text/css\">")
        .append("table, th , td{border: 1px solid grey; white-space:nowrap;}")
        .append("</style>")
        .append("</head><body>");
        sb.append("<h1>").append(root.path("info").path("title").asText()).append("</h1>");
        sb.append("<p>").append(root.path("info").path("description").asText()).append("</p >");
        
        // 遍历 paths
        JsonNode paths = root.path("paths");
        JsonNode definitions = root.path("definitions");
        paths.fields().forEachRemaining(entry -> {
            String path = entry.getKey();
            JsonNode methods = entry.getValue();            
            sb.append("<hr />");
            methods.fields().forEachRemaining(method -> {
                String httpMethod = method.getKey();
                JsonNode details = method.getValue();
                sb.append("<h2>").append(httpMethod.toUpperCase()).append(": ").append(path).append("</h2>");

                // 摘要和描述
                String summary = details.path("summary").asText("");
                String description = details.path("description").asText("");
                if (!summary.isEmpty()) sb.append("<p><strong>摘要：</strong>").append(summary).append("</p >");
                if (!description.isEmpty()) sb.append("<p><strong>说明：</strong>").append(description).append("</p >");
                
                ArrayList<String> consumes=new ArrayList<String>();
                details.path("consumes").forEach(c->{
                	consumes.add(c.toString());
                });
                sb.append("<h3>").append("接收参数(")
                .append(String.join(",",consumes))
                .append("):</h3>");
                
                //参数
                JsonNode params=details.path("parameters");

                if(params.isEmpty()) {
                	sb.append("null");
                }else {
            	sb.append("<table>");
            	sb.append("<thead>");
            	sb.append("<tr>");

            	//long的参数会多一个format字段,所以不是所有属性的字段数量都一样
            	LinkedHashMap<String,Boolean> pMap=new LinkedHashMap<String,Boolean>();
                params.forEach(param -> {
                	param.fields().forEachRemaining(pi->{
                		pMap.put(pi.getKey(), true);
                	});
                });
                boolean hasFormat=pMap.containsKey("format");
                for (Map.Entry<String, Boolean> m1 : pMap.entrySet()) {
                	if(!"format".equals(m1.getKey())) {
                    	if("type".equals(m1.getKey())) {
                    		sb.append("<th>").append(hasFormat?"type(format)":"type").append("</th>");
                    	}else {
                    		sb.append("<th>").append(m1.getKey()).append("</th>");
                    	}
                	}
                }
                
            	sb.append("</tr>");
            	sb.append("</thead>");
            	sb.append("<tbody>");
                
                params.forEach(param -> {
                	sb.append("<tr>");

                    for (Map.Entry<String, Boolean> m1 : pMap.entrySet()) {
                    	if(!"format".equals(m1.getKey())) {
                        	if("type".equals(m1.getKey())) {
                                sb.append("<td>").append(param.path(m1.getKey()));
                                String format=param.path("format").toString();
                                if(!format.isEmpty()) {
                                	sb.append("("+format+")");
                                }
                                sb.append("</td>");
                        	}else {
                                sb.append("<td>").append(param.path(m1.getKey())).append("</td>");	
                        	}	
                    	}
                    }
                    
                	sb.append("</tr>");
                });
            	sb.append("</tbody>");
            	sb.append("</table>");
                }                               
                
                //原来swagger的标准是,每一种响应码可以分别对应一种不同的结果类型, 
                //需要配合@ApiResponses注解
                ArrayList<String> produces=new ArrayList<String>();
                details.path("produces").forEach(c->{
                	produces.add(c.toString());
                });
                sb.append("<h3>").append("返回类型(")
                .append(String.join(",",produces))
                .append("):</h3>");
                details.path("responses").fields().forEachRemaining(rf->{
                	//响应码
                    String code = rf.getKey();
                    String msg = rf.getValue().path("description").asText();
//                    sb.append("<p><strong>").append(code).append("：</strong>").append(msg).append("</p >");
                    
                    JsonNode key=rf.getValue().path("schema")
                    		;
                    
                    getDefinitionTable(definitions,key,sb,code,msg);    
                });
                
//            	//返回值
//            	JsonNode params=details.path("parameters");
                // 这里可以继续解析参数、响应等
            });
        });
        
        sb.append("</body></html>");
        return sb.toString();
    }
    private static void getDefinitionTable(JsonNode node,JsonNode schema,StringBuilder sb
    		,String code,String msg
    		) {
//    	//注意字符串两边有双引号
    	String schemaType=schema.path("type").toString();
    	boolean isArray="\"array\"".equals(schemaType);
    	boolean isObject="".equals(schemaType)&&!schema.path("$ref").toString().isEmpty();//这种情况一定有ref;
    	String key=null;
    	boolean hasRef=(!schema.path("$ref").toString().isEmpty())
    			||!schema.path("items").path("$ref").toString().isEmpty();
    	if(isArray) {
    		key=schema.path("items").path("$ref").toString().isEmpty()
    			?schema.path("items").path("type").toString()
    			:schema.path("items").path("$ref").toString().replace("#/definitions/", "");
    	}else if(isObject) {
    		key=schema.path("$ref").toString().replace("#/definitions/", "");
    	}else {
    		key=schemaType;
    	}

    	sb.append("<p><strong>").append(code+":</strong>").append(" "+msg);
    	if(!key.isEmpty()) {
    		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>数据类型:</strong>")
    		.append(isArray?("["+key+"]"):key);
    	}
    	sb.append("</p>");
    	
//    	//未见声明array时的结构,以后isArray可能报错
    	if((isObject||isArray)&&hasRef) {
        	String key2=key.substring(1, key.length()-1);
        	JsonNode def=node.path(key2);
        	//String type=def.path("type").toString();
        	
        	sb.append("<table>");
        	sb.append("<thead>");
        	sb.append("<tr>");
        	sb.append("<th>").append(key).append("字段:").append("</th>");

        	LinkedHashMap<String,Boolean> pMap=new LinkedHashMap<String,Boolean>();
    		def.path("properties")
    		.fields().forEachRemaining(pi->{
                pi.getValue().fields().forEachRemaining(pfi->{
                	pMap.put(pfi.getKey(), null);
                });
    		});
        	boolean hasDescription=pMap.containsKey("description");
        	boolean hasExample=pMap.containsKey("example");
        	
    		def.path("properties")
    		.fields().forEachRemaining(pi->{
                sb.append("<th>").append(pi.getKey());
                if(!pi.getValue().path("example").toString().isEmpty()) {
                	sb.append("(example)");
                }
                sb.append("</th>");
    		});
    		
        	sb.append("</tr>");
        	sb.append("</thead>");
        	sb.append("<tbody>");

    		//类型行
        	sb.append("<tr>");
        	sb.append("<td>").append("字段类型:").append("</td>");
    		def.path("properties")
    		.fields().forEachRemaining(pi->{
                sb.append("<td>").append(pi.getValue().path("type"));
                String format=pi.getValue().path("format").toString();
                if(!format.isEmpty()) {
                	sb.append("("+format+")");
                }
                sb.append("</td>");
    		});
        	sb.append("</tr>");
        	//说明行(如果有)
    		if(hasDescription||hasExample) {
            	sb.append("<tr>");
            	sb.append("<td>").append("说明:").append("</td>");
        		def.path("properties")
        		.fields().forEachRemaining(pi->{
                    sb.append("<td>").append(pi.getValue().path("description"));
                    String example=pi.getValue().path("example").toString();
                    if(!example.isEmpty()) {
                    	sb.append("("+example+")");
                    }
                    sb.append("</td>");
        		});
            	sb.append("</tr>");
    		}
    	}
    	sb.append("</tbody>");
    	sb.append("</table>");    	
    }
}