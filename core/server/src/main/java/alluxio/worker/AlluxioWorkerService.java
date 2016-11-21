/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.worker;

import alluxio.Server;
import alluxio.master.AlluxioMasterService;
import alluxio.wire.WorkerNetAddress;
import alluxio.worker.block.BlockWorker;
import alluxio.worker.file.FileSystemWorker;

import java.net.InetSocketAddress;
import java.util.ServiceLoader;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A worker in the Alluxio system.
 */
public interface AlluxioWorkerService extends Server {
  /**
   * Factory for creating {@link AlluxioMasterService}.
   */
  @ThreadSafe
  final class Factory {
    /** The worker service loaders. */
    private static ServiceLoader<WorkerFactory> sServiceLoader;

    /**
     * @return a new instance of {@link AlluxioWorkerService}
     */
    public static AlluxioWorkerService create() {
      return new DefaultAlluxioWorker();
    }

    /**
     * @return the (cached) worker service loader
     */
    static synchronized ServiceLoader<WorkerFactory> getServiceLoader() {
      if (sServiceLoader != null) {
        return sServiceLoader;
      }
      // Discover and register the available factories.
      // NOTE: ClassLoader is explicitly specified so we don't need to set ContextClassLoader.
      sServiceLoader =
          ServiceLoader.load(WorkerFactory.class, WorkerFactory.class.getClassLoader());
      return sServiceLoader;
    }

    private Factory() {} // prevent instantiation
  }

  /**
   * @return the connect information for this worker
   */
  WorkerNetAddress getAddress();

  /**
   * @return the block worker for this Alluxio worker
   */
  BlockWorker getBlockWorker();

  /**
   * @return the worker's data service bind host (used by unit test only)
   */
  String getDataBindHost();

  /**
   * @return the worker's data service port (used by unit test only)
   */
  int getDataLocalPort();

  /**
   * @return the file system worker for this Alluxio worker
   */
  FileSystemWorker getFileSystemWorker();

  /**
   * @return this worker's rpc address
   */
  InetSocketAddress getRpcAddress();

  /**
   * @return the start time of the worker in milliseconds
   */
  long getStartTimeMs();

  /**
   * @return the uptime of the worker in milliseconds
   */
  long getUptimeMs();

  /**
   * @return the worker web service bind host (used by unit test only)
   */
  String getWebBindHost();

  /**
   * @return the worker web service port (used by unit test only)
   */
  int getWebLocalPort();

  /**
   * Waits until the worker is ready to server requests.
   */
  void waitForReady();
}
