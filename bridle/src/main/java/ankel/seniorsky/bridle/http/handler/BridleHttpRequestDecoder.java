package ankel.seniorsky.bridle.http.handler;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import ankel.seniorsky.bridle.http.codec.request.GetRequest;
import ankel.seniorsky.bridle.http.codec.request.HttpRequest;
import ankel.seniorsky.bridle.http.codec.request.InvalidRequest;
import ankel.seniorsky.bridle.http.codec.request.InvalidRequest.Reason;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Very basic decoder that turns {@link ByteBuf} into {@link HttpRequest}
 *
 * Limitations:
 * * Only support GET
 * * Not processing body. This should be reasonable given the above.
 * * Headers are not fully parsed (q-value, non-text value, etc(. This may lead to non-standard
 * behavior.
 * * Only support ISO-8859-1, which should be sufficient for text values.
 *
 * @author Binh Tran
 */
@Slf4j
public class BridleHttpRequestDecoder extends ByteToMessageDecoder
{
  // loosely based on http://tools.ietf.org/html/rfc2616#section-5.1
  public static final Pattern REQUEST_LINE = Pattern.compile(
      "(?<method>\\w+) (?<uri>[^ ]+) (?<version>HTTP/\\d+\\.\\d+)");

  // We don't support absolute url / authority in the uri part
  public static final Pattern RELATIVE_URI_ONLY = Pattern.compile("(/[\\w./_-]+)*/?");

  public static final Splitter HEADER_COLON_SPLITTER = Splitter.on(": ").trimResults();

  public static final Pattern HEADER_FIELD_CONTENT = Pattern.compile("([^;,]+)(?:;[^,]+)?,?");

  private static final Charset ISO_8859 = Charset.forName("ISO-8859-1");
  private static final char CR = '\r';
  private static final byte[] LF_CR_LF = "\n\r\n".getBytes(ISO_8859);

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out)
      throws Exception
  {
    if (!requestHeaderFullyAvailable(in))
    {
      return;
    }

    final Optional<String> requestLine = getNextLine(in);

    log.debug("Request line: [{}]", requestLine);

    // pre process

    final HttpRequest request;

    if (!requestLine.isPresent())
    {
      request = new InvalidRequest(Reason.BAD_REQUEST_LINE);
    }
    else
    {

      final Matcher matcher = REQUEST_LINE.matcher(requestLine.get());

      if (!matcher.matches())
      {
        request = new InvalidRequest(Reason.BAD_REQUEST_LINE);
      }
      else
      {
        switch (matcher.group("method"))
        {
          case "GET":
            final Matcher uriMatcher = RELATIVE_URI_ONLY.matcher(matcher.group("uri"));
            if (uriMatcher.matches())
            {
              request = new GetRequest(matcher.group("uri"));
            }
            else
            {
              request = new InvalidRequest(Reason.NOT_RELATIVE_URI);
            }
            break;
          default:
            request = new InvalidRequest(Reason.METHOD_NOT_ALLOWED);
        }
      }
    }

    out.add(request);

    // Stop processing if request is invalid
    if (request instanceof InvalidRequest)
    {
      log.debug("Request: [{}]", request);
      return;
    }

    // The request is a GET request, we can safely ignore the body. Parse headers.
    final GetRequest getRequest = (GetRequest) request;
    while (in.isReadable())
    {
      final Optional<String> nextLine = getNextLine(in);
      if (!nextLine.isPresent())
      {
        return;
      }

      final List<String> fields = HEADER_COLON_SPLITTER.splitToList(nextLine.get());
      if (fields.size() != 2)
      {
        // May not be the best thing to do?
        return;
      }
      final List<String> headerValues =
          getRequest.getHeaders()
              .computeIfAbsent(fields.get(0).toLowerCase(),
                  (k) -> new ArrayList<>());

      final Matcher headerFieldMatcher = HEADER_FIELD_CONTENT.matcher(fields.get(1));

      while (headerFieldMatcher.find())
      {
        headerValues.add(headerFieldMatcher.group(1));
      }
    }

    log.debug("Request: [{}]", request);
  }

  private boolean requestHeaderFullyAvailable(final ByteBuf in)
  {
    in.markReaderIndex();
    final byte[] threeBytes = new byte[3];
    while (in.isReadable())
    {
      if (in.readByte() == CR && in.readableBytes() >= 3)
      {
        in.readBytes(threeBytes);
        if (Arrays.equals(LF_CR_LF, threeBytes))
        {
          in.resetReaderIndex();
          return true;
        }
        else
        {
          // push back the last 3 bytes
          in.readerIndex(in.readerIndex() - 3);
        }
      }
    }
    in.resetReaderIndex();
    return false;
  }

  private Optional<String> getNextLine(final ByteBuf in)
  {
    int nextCRLF = in.forEachByte(ByteBufProcessor.FIND_CRLF);
    if (nextCRLF == -1)
    {
      // approaching end of readable section
      if (in.forEachByte(ByteBufProcessor.FIND_NON_CRLF) == in.writerIndex())
      {
        // Nothing but CRLF (if any) until the end of readable-section
        return Optional.empty();
      }
      else
      {
        // Still something left but there's no CRLF for this line
        int lineLength = in.writerIndex() - in.readerIndex();
        byte[] requestLineBytes = new byte[lineLength];
        in.readBytes(requestLineBytes);
        return Optional
            .of(new String(requestLineBytes, ISO_8859).trim())
            .map(Strings::emptyToNull);
      }
    }
    else
    {
      // Skip CRLF
      int lineLength = nextCRLF - in.readerIndex();
      byte[] requestLineBytes = new byte[lineLength];
      in.readBytes(requestLineBytes);

      // next line, if possible
      if (in.isReadable())
      {
        int nextLineIndex = in.forEachByte(ByteBufProcessor.FIND_NON_CRLF);
        in.readerIndex(nextLineIndex == -1 ? in.writerIndex() : nextLineIndex);
      }

      return Optional
          .of(new String(requestLineBytes, ISO_8859).trim())
          .map(Strings::emptyToNull);
    }
  }
}
