package ankel.seniorsky.bridle.http.codec.response;

import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.ImmutableMap;

/**
 * An abstract HTTP Response, with basic implementation for {@code getBytes}
 *
 * @author Binh Tran
 */
public abstract class AbstractHttpResponse
{
  public static final String HTTP_STATUS_LINE = "HTTP/1.0 %d %s";

  public static final MapJoiner HTTP_HEADERS_JOINER = Joiner.on("\r\n")
      .withKeyValueSeparator(": ");

  public static final byte[] EMPTY_BODY = {};

  public static final Map<String, String> EMPTY_HEADERS = ImmutableMap.<String, String> of();

  private final int status;
  private final String phrase;
  private final Map<String, String> headers;
  private final byte[] body;

  protected AbstractHttpResponse(final int status, final String phrase,
      final Map<String, String> headers, final byte[] body)
  {
    this.status = status;
    this.phrase = phrase;
    this.headers = headers;
    this.body = body;
  }

  public String formatStatus()
  {
    return String.format(HTTP_STATUS_LINE, status, phrase);
  }

  public String joinHeader()
  {
    return HTTP_HEADERS_JOINER.join(headers);
  }

  public byte[] getBytes()
  {
    final StringBuilder responseStringBuilder = new StringBuilder();
    // Status line
    responseStringBuilder.append(formatStatus()).append("\r\n");

    // Headers
    responseStringBuilder.append(joinHeader());

    // Don't double newline
    if (!headers.isEmpty())
    {
      responseStringBuilder.append("\r\n");
    }

    // Add content-length header
    if (body.length > 0)
    {
      responseStringBuilder.append("Content-Length: ").append(body.length).append("\r\n");
    }

    // Empty line
    responseStringBuilder.append("\r\n");

    byte[] headerBytes = responseStringBuilder.toString().getBytes(Charset.forName("ISO-8859-1"));

    byte[] result = new byte[headerBytes.length + body.length];

    System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
    System.arraycopy(body, 0, result, headerBytes.length, body.length);

    return result;
  }
}
