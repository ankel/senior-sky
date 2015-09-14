package ankel.seniorsky.bridle.file;

import java.util.List;

/**
 * Abstract the operations required for the server
 *
 * @author Binh Tran
 */
public interface FileOperation
{
  boolean isDirectory(final String path);

  boolean pathExists(final String path);

  boolean fileRetrievable(final String path);

  byte[] getFile(final String path);

  List<String> ls(final String path);

  String uriToSystemDependentPath(final String uri);
}
