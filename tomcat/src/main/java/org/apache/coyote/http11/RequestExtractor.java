package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.RequestBodyNotProvidedException;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.request.RequestURI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class RequestExtractor {

    private static final int HTTP_METHOD_LOCATION = 0;
    private static final int REQUEST_URI_LOCATION = 1;
    private static final int HTTP_VERSION_LOCATION = 2;

    private static final String REQUEST_LINE_DELIMITER = " ";

    private RequestExtractor() {
    }

    public static HttpRequest extract(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();
        Headers headers = extractHeaders(reader);
        RequestBody requestBody = extractBodyIfExists(reader, headers);

        String[] splitRequestLine = requestLine.split(REQUEST_LINE_DELIMITER);

        return new HttpRequest(HttpMethod.from(splitRequestLine[HTTP_METHOD_LOCATION]),
                RequestURI.from(splitRequestLine[REQUEST_URI_LOCATION]),
                HttpVersion.from(splitRequestLine[HTTP_VERSION_LOCATION]),
                headers, requestBody);
    }

    private static Headers extractHeaders(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            lines.add(line);
        }
        return convertLinesToHeaders(lines);
    }

    private static Headers convertLinesToHeaders(List<String> lines) {
        Map<String, String> headers = new HashMap<>();

        lines.stream()
                .map(each -> each.split(": "))
                .forEach(each -> headers.put(each[0], each[1]));

        return new Headers(headers);
    }

    private static RequestBody extractBodyIfExists(BufferedReader reader,
                                                   Headers headers) throws IOException {
        String contentLength = headers.get(HttpHeaders.CONTENT_LENGTH.value());
        if (Objects.nonNull(contentLength)) {
            return extractBody(reader, Integer.parseInt(contentLength));
        }
        return RequestBody.ofEmpty();
    }

    private static RequestBody extractBody(BufferedReader reader, int contentLength) throws IOException {
        if (reader.ready()) {
            char[] buffer = new char[contentLength];
            reader.read(buffer);
            String content = new String(buffer).trim();
            return new RequestBody(content);
        }
        throw new RequestBodyNotProvidedException();
    }
}
