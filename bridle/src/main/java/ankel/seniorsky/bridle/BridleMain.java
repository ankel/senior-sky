package ankel.seniorsky.bridle;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * @author Binh Tran
 */
public class BridleMain
{
  public static void main(final String args[]) throws Exception
  {
    if (args.length != 2)
    {
      System.out.println("Missing arguments. Usage: BridleMain <root_path> <port>");
      System.exit(-1);
    }

    final String rootPath = args[0];

    Path root = FileSystems.getDefault().getPath(rootPath);
    if (!root.toFile().exists())
    {
      System.out.println("Root path doesn't exists!");
      System.exit(-2);
    }

    final int port = Integer.parseInt(args[1]);
    final ExecutorService executorService = Executors.newSingleThreadExecutor(
        new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("bridle-server-runner-%s")
            .build());

    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

    final BridleServer server = new BridleServer(root, port);

    executorService.execute(server);
    executorService.execute(executorService::shutdown);

    System.out.println("Press ENTER to exit");
    System.in.read();

    System.out.println("Server shutting down...");
    server.shutdown();
  }
}
