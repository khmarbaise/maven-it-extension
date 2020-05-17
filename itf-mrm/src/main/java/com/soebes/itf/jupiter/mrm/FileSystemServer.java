package com.soebes.itf.jupiter.mrm;

/*
 * Copyright 2011 Stephen Connolly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.mojo.mrm.api.FileSystem;
import org.codehaus.mojo.mrm.servlet.FileSystemServlet;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A file system server.
 */
public class FileSystemServer
{
    /**
     * Guard for {@link #starting}, {@link #started}, {@link #finishing}, {@link #finished}, {@link #boundPort}
     * and {@link #problem}.
     */
    private final Object lock = new Object();

    /**
     * Flag to indicate that the thread is starting up.
     * <p/>
     * Guarded by {@link #lock}.
     */
    private boolean starting = false;

    /**
     * Flag to indicate that the thread is ready to serve requests.
     * <p/>
     * Guarded by {@link #lock}.
     */
    private boolean started = false;

    /**
     * Flag to indicate that the thread is terminated.
     * <p/>
     * Guarded by {@link #lock}.
     */
    private boolean finished = false;

    /**
     * Flag to indicate that the thread should terminate.
     * <p/>
     * Guarded by {@link #lock}.
     */
    private boolean finishing = false;

    /**
     * The port that the server is bound to.
     * <p/>
     * Guarded by {@link #lock}.
     */
    private int boundPort = 0;

    /**
     * The port that the server is bound to.
     * <p/>
     * Guarded by {@link #lock}.
     */
    private Exception problem = null;

    /**
     * The name of the file system (used to name the thread).
     */
    private final String name;

    /**
     * The file system to serve.
     */
    private final FileSystem fileSystem;

    /**
     * The port to try and serve on.
     */
    private final int requestedPort;
    
    /**
     * The path to settingsFile containing the configuration to connect to this repository manager.
     */
    private final String settingsServletPath;

    /**
     * Creates a new file system server that will serve a {@link FileSystem} over HTTP on the specified port.
     *
     * @param name       The name of the file system server thread.
     * @param port       The port to server on or <code>0</code> to pick a random, but available, port.
     * @param fileSystem the file system to serve.
     */
    public FileSystemServer( String name, int port, FileSystem fileSystem, String settingsServletPath )
    {
        this.name = name;
        this.fileSystem = fileSystem;
        this.requestedPort = port;
        this.settingsServletPath = settingsServletPath;
    }

    /**
     * Ensures that the file system server is started (if already starting, will block until started, otherwise starts
     * the file system server and blocks until started)
     *
     */
    public void ensureStarted() throws Exception {
        synchronized ( lock )
        {
            if ( started || starting )
            {
                return;
            }
            starting = true;
            started = false;
            finished = false;
            finishing = false;
        }
        Thread worker = new Thread( new Worker(), "FileSystemServer[" + name + "]" );
        worker.setDaemon( true );
        worker.start();
        synchronized ( lock )
        {
            while ( starting && !started && !finished && !finishing )
            {
                lock.wait();
            }
            if ( problem != null )
            {
                throw problem;
            }
        }
    }


    /**
     * Returns <code>true</code> if and only if the file system server is finished.
     *
     * @return <code>true</code> if and only if the file system server is finished.
     */
    public boolean isFinished()
    {
        synchronized ( lock )
        {
            return finished;
        }
    }

    /**
     * Returns <code>true</code> if and only if the file system server is started.
     *
     * @return <code>true</code> if and only if the file system server is started.
     */
    public boolean isStarted()
    {
        synchronized ( lock )
        {
            return finished;
        }
    }

    /**
     * Signal the file system server to shut down.
     */
    public void finish()
    {
        synchronized ( lock )
        {
            finishing = true;
            lock.notifyAll();
        }
    }

    /**
     * Blocks until the file system server has actually shut down.
     *
     * @throws InterruptedException  if interrupted.
     */
    public void waitForFinished()
        throws InterruptedException
    {
        synchronized ( lock )
        {
            while ( !finished )
            {
                lock.wait();
            }
        }
    }

    /**
     * Gets the port that the file system server is/will server on.
     *
     * @return the port that the file system server is/will server on.
     */
    public int getPort()
    {
        synchronized ( lock )
        {
            return started ? boundPort : requestedPort;
        }
    }

    /**
     * Gets the root url that the file system server is/will server on.
     *
     * @return the root url that the file system server is/will server on.
     */
    public String getUrl()
    {
        return "http://localhost:" + getPort();
    }

    /**
     * Same as {@link #getUrl()}, but now for remote users
     * 
     * @return the scheme + raw IP address + port 
     * @throws UnknownHostException if the local host name could not be resolved into an address. 
     */
    public String getRemoteUrl() throws UnknownHostException
    {
        return "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + getPort();
    }
    
    /**
     * The work to monitor and control the Jetty instance that hosts the file system.
     */
    private final class Worker
        implements Runnable
    {
        /**
         * {@inheritDoc}
         */
        public void run()
        {
            try
            {
                Server server = new Server( requestedPort );
                try
                {
                    Context root = new Context( server, "/", Context.SESSIONS );
                    root.addServlet( new ServletHolder( new FileSystemServlet( fileSystem, settingsServletPath ) ), "/*" );
                    server.start();
                    synchronized ( lock )
                    {
                        boundPort = 0;
                        Connector[] connectors = server.getConnectors();
                        for ( int i = 0; i < connectors.length; i++ )
                        {
                            if ( connectors[i].getLocalPort() > 0 )
                            {
                                boundPort = connectors[i].getLocalPort();
                                break;
                            }
                        }
                        starting = false;
                        started = true;
                        lock.notifyAll();
                    }
                } catch ( InterruptedException e )
                {
                    synchronized ( lock )
                    {
                        problem = e;
                    }
                    throw e;
                } catch ( Exception e )
                {
                    synchronized ( lock )
                    {
                        problem = e;
                    }
                    throw e;
                }
                synchronized ( lock )
                {
                    while ( !finishing )
                    {
                        try
                        {
                            lock.wait( 500 );
                        }
                        catch ( InterruptedException e )
                        {
                            // ignore
                        }
                    }
                }
                server.stop();
                server.join();
            } catch ( Exception e )
            {
                // ignore
            } finally
            {
                synchronized ( lock )
                {
                    started = false;
                    starting = false;
                    finishing = false;
                    finished = true;
                    boundPort = 0;
                    lock.notifyAll();
                }
            }
        }
    }

}
