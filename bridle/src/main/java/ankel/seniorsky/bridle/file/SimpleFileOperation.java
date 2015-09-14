package ankel.seniorsky.bridle.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Simple implementation of {@link FileOperation} that delegate most functionality to
 * java.nio.file.*
 *
 * Files that are larger than {@code maxSize} are not retrievable to avoid memory issues.
 *
 * @author Binh Tran
 */
@Slf4j
public class SimpleFileOperation implements FileOperation
{
  private final long maxSize;
  private final Path root;

  public SimpleFileOperation(final Path root, final long maxSize)
  {
    this.root = root;
    this.maxSize = maxSize;
  }

  @Override
  public boolean isDirectory(final String path)
  {
    return Files.isDirectory(root.resolve(path));
  }

  @Override
  public boolean pathExists(final String path)
  {
    return Files.exists(root.resolve(path));
  }

  @Override
  public boolean fileRetrievable(final String path)
  {
    try
    {
      return pathExists(path) && !isDirectory(path) && Files.size(root.resolve(path)) < maxSize;
    }
    catch (IOException e)
    {
      log.error("Exception while checking for file size at [{}]/[{}]", root.toString(), path, e);
      return false;
    }
  }

  @Override
  public byte[] getFile(final String path)
  {
    if (!fileRetrievable(path))
    {
      throw new IllegalArgumentException(
          String.format("Cannot retrieve file [%s]/[%s]",
              root.toString(), path));
    }

    try
    {
      return Files.readAllBytes(root.resolve(path));
    }
    catch (IOException e)
    {
      throw new RuntimeException(
          String.format("Cannot read file [%s]/[%s]", root.toString(), path), e);
    }
  }

  @Override
  public List<String> ls(final String path)
  {
    if (!isDirectory(path))
    {
      return ImmutableList.of();
    }

    return Lists.newArrayList(root.resolve(path).toFile().list());
  }

  @Override
  public String uriToSystemDependentPath(final String uri)
  {
    return StringUtils.removeStart(uri, "/").replace("/", File.separator);
  }
}
