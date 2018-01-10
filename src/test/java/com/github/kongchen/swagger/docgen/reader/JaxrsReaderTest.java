package com.github.kongchen.swagger.docgen.reader;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kongchen.swagger.docgen.spring.SpringResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.ext.SwaggerExtension;
import io.swagger.jaxrs.ext.SwaggerExtensions;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.Parameter;
import org.apache.maven.plugin.logging.Log;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class JaxrsReaderTest {
    @Mock
    private Log log;

    private JaxrsReader reader;

    List<SwaggerExtension> extensions = SwaggerExtensions.getExtensions();

    @BeforeMethod
    public void setup() {
        reader = new JaxrsReader(new Swagger(), log);
    }

    @AfterMethod
    public void resetExtenstions() {
        SwaggerExtensions.setExtensions(extensions);
    }

    @Test
    public void ignoreClassIfNoApiAnnotation() {
        Swagger result = reader.read(NotAnnotatedApi.class);

        assertEmptySwaggerResponse(result);
    }

    @Test
    public void ignoreApiIfHiddenAttributeIsTrue() {
        Swagger result = reader.read(HiddenApi.class);

        assertEmptySwaggerResponse(result);
    }

    @Test
    public void includeApiIfHiddenParameterIsTrueAndApiHiddenAttributeIsTrue() {
        Swagger result = reader.read(HiddenApi.class, "", null, true, new String[0], new String[0], new HashMap<String, Tag>(), new ArrayList<Parameter>());

        assertNotNull(result, "No Swagger object created");
        assertFalse(result.getTags().isEmpty(), "Should contain api tags");
        assertFalse(result.getPaths().isEmpty(), "Should contain operation paths");
    }

    @Test
    public void discoverApiOperation() {
        Tag expectedTag = new Tag();
        expectedTag.name("atag");
        Swagger result = reader.read(AnApi.class);

        assertSwaggerResponseContents(expectedTag, result);
    }

    @Test void discoverService() throws JsonProcessingException {
        Swagger result1 = reader.read(AnService.class);
        result1 = reader.read(AnApi.class);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(result1);
        System.out.println("================ Swagger2 Json ================");
        System.out.println(json);
        System.out.println("================ Swagger2 Json End ================");
        assertNotNull(result1, "No Swagger object created");
    }

    @Test
    public void createNewSwaggerInstanceIfNoneProvided() {
        JaxrsReader nullReader = new JaxrsReader(null, log);
        Tag expectedTag = new Tag();
        expectedTag.name("atag");
        Swagger result = nullReader.read(AnApi.class);

        assertSwaggerResponseContents(expectedTag, result);
    }

    private void assertEmptySwaggerResponse(Swagger result) {
        assertNotNull(result, "No Swagger object created");
        assertNull(result.getTags(), "Should not have any tags");
        assertNull(result.getPaths(), "Should not have any paths");
    }

    private void assertSwaggerResponseContents(Tag expectedTag, Swagger result) {
        assertNotNull(result, "No Swagger object created");
        assertFalse(result.getTags().isEmpty(), "Should contain api tags");
        assertTrue(result.getTags().contains(expectedTag), "Expected tag missing");
        assertFalse(result.getPaths().isEmpty(), "Should contain operation paths");
        String path = "/apath/getOperation";
        assertTrue(result.getPaths().containsKey(path), "Path missing from paths map");
        io.swagger.models.Path r = result.getPaths().get(path);
        assertFalse(r.getOperations().isEmpty(), "Should be a get operation");
    }

    @Api(tags = "atag")
    @Path("/apath")
    static class AnApi {
        @ApiOperation(value = "Get a model.")
        @GET
        @Path("getOperation")
        public Response getOperation(int xxx) {
            return Response.ok().build();
        }
    }

    @Api(hidden = true, tags = "atag")
    @Path("/hidden/path")
    static class HiddenApi {
        @ApiOperation(value = "Get a model.")
        @GET
        public Response getOperation() {
            return Response.ok().build();
        }
    }

    @Path("/apath")
    static class NotAnnotatedApi {
    }

    @Api("theService")
    static class AnService
    {
        @ApiOperation(value="static now", notes="get current time")
        public static Date now()
        {
            return new Date();
        }

        @ApiOperation(value="add", notes="x+y=?",response = Swagger.class)
        public int add(MultivaluedMap<String,String> kv,    //这个是不显示的
                       @ApiParam(name = "x",value="this is x") int x,
                       @ApiParam(name = "参数名称", value = "这是说明") int y
                       ,@ApiParam(name = "SpringResource") SpringResource ss

                       )
        {
            return x+y;
        }
    }
}
