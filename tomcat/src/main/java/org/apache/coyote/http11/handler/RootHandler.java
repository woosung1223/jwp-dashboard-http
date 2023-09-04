package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.HttpHeaders.CONTENT_TYPE;

import java.util.Map;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RootHandler extends Handler {

    @Override
    public Response handle(Request request) {
        String resource = "Hello world!";

        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentTypeParser.parse(resource),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }
}
