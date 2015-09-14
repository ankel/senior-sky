package ankel.seniorsky.bridle.file;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import ankel.seniorsky.bridle.http.codec.request.GetRequest;
import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import ankel.seniorsky.bridle.http.codec.response.HttpNotAcceptable;
import ankel.seniorsky.bridle.http.codec.response.HttpNotFound;
import ankel.seniorsky.bridle.http.codec.response.HttpOk;
import ankel.seniorsky.bridle.http.codec.response.HttpRequestEntityTooLarge;
import ankel.seniorsky.bridle.http.codec.visitor.HttpRequestVisitor;

/**
 * A basic implementation of {@link HttpRequestVisitor} that uses the relative uri from the GET
 * request as the relative path and either list the folder or return the file itself, if exists.
 *
 * If retrievable, the entire file content is returned in the same HTTP response, and is
 * limited by {@link FileOperation#fileRetrievable(String)}
 *
 * Content type are set to be application/octet-stream for all file and text/plain for directory
 * listing.
 *
 * Also implemented Accept header checking to demonstrate the effects of headers on the dialogue.
 * This is not complete (missing If-... headers, etag, cache, etc)
 *
 * @author Binh Tran
 */
@RequiredArgsConstructor
public class FileOpsHttpRequestVisitor extends HttpRequestVisitor
{
  public static final Map<String, String> TEXT_CONTENT_TYPE_HEADER =
      ImmutableMap.of(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
  public static final Map<String, String> OCTET_STREAM_TYPE_HEADER =
      ImmutableMap.of(HttpHeaders.CONTENT_TYPE, MediaType.OCTET_STREAM.toString());

  private final FileOperation fileOperation;

  @Override
  public AbstractHttpResponse visit(final GetRequest getRequest)
  {
    final String path = fileOperation.uriToSystemDependentPath(getRequest.getUri());

    if (!fileOperation.pathExists(path))
    {
      return new HttpNotFound(AbstractHttpResponse.EMPTY_HEADERS, AbstractHttpResponse.EMPTY_BODY);
    }
    else if (fileOperation.isDirectory(path))
    {
      if (checkAcceptType(getRequest.getHeaders().get("accept"), MediaType.ANY_TEXT_TYPE))
      {
        return new HttpOk(
            TEXT_CONTENT_TYPE_HEADER,
            listDir(path).getBytes(ISO_8859));
      }
      else
      {
        return new HttpNotAcceptable(
            TEXT_CONTENT_TYPE_HEADER,
            AbstractHttpResponse.EMPTY_BODY);
      }
    }
    else  // is file
    {
      if (checkAcceptType(getRequest.getHeaders().get("accept"), MediaType.OCTET_STREAM))
      {
        if (fileOperation.fileRetrievable(path))
        {
          return new HttpOk(
              OCTET_STREAM_TYPE_HEADER,
              fileOperation.getFile(path));
        }
        else
        {
          return new HttpRequestEntityTooLarge(
              AbstractHttpResponse.EMPTY_HEADERS,
              "Requested file is too large".getBytes(ISO_8859));
        }
      }
      else
      {
        return new HttpNotAcceptable(
            OCTET_STREAM_TYPE_HEADER,
            AbstractHttpResponse.EMPTY_BODY);
      }
    }
  }

  private String listDir(final String path)
  {
    return fileOperation.ls(path)
        .stream()
        .collect(Collectors.joining("\r\n", "Directory content: \r\n", ""));
  }

  private boolean checkAcceptType(final List<String> accept, final MediaType typeRange)
  {
    return accept.stream()
        .map(MediaType::parse)
        .filter(mediaType -> mediaType.is(typeRange))
        .findAny()
        .isPresent();
  }
}
