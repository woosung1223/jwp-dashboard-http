package org.apache.coyote.http11;

public class DefaultHandler extends Handler {

    @Override
    public Response handle(Request request) {
        String response = "Hello world!";

        String contentType = ContentTypeParser.parse(response);
        int contentLength = response.getBytes().length;

        return Response.from(request.getHttpVersion(), "200 OK",
                contentType, contentLength, response);
    }
}
