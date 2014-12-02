package mware_lib;


import bank_access.AccountImplBase;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.*;

/**
 * mware_lib.ObjectBroker
 *
 * - Front-End der Middleware -
 */
public class ObjectBroker {

    private SkeletonManager skeletonManager;
    private NameServiceImpl nameService;
    private ServerSocket serverSocket;
    private Logger logger;
    private FileHandler fileHandler;

    /**
     * Erstellt einen mware_lib.ObjectBroker
     *
     * Das hier zurückgelieferte Objekt ist der zentrale Einstiegspunkt
     * der Middleware aus Anwendersicht sein.
     * Parameter: Host und Port, bei dem die Dienste (Namensdienst)
     * kontaktiert werden sollen. Mit debug sollen Testausgaben
     * der Middleware ein- oder ausgeschaltet werden können.
     *
     * @param serviceHost   Host vom Namensdienst
     * @param listenPort    Port vom Namensdienst
     * @param debug         Flag aktiviert Testausgaben
     * @return  mware_lib.ObjectBroker
     */
    public static ObjectBroker init(String serviceHost, int listenPort, boolean debug)   {
        return new ObjectBroker(serviceHost, listenPort, debug);
    }

    /**
     * Liefert den Namensdienst (Stellvetreterobjekt).
     * @return mware_lib.NameService
     */
    public NameService getNameService() {
        return this.nameService;
    }

    /**
     * Beendet die Benutzung der Middleware in dieser Anwendung.
     */
    public void shutdown() {
        try {
            nameService.shutdown();
            skeletonManager.shutdown();

        } catch (IOException e) {
            logger.log(Level.SEVERE,e.toString());
        } finally {
            nameService = null;
            skeletonManager = null;
        }
    }

    /**
     * Konstruktor
     *
     * @param serviceHost   Host vom Namensdienst
     * @param listenPort    Port vom Namensdienst
     * @param debug         Flag aktiviert Testausgaben
     */
    public ObjectBroker(String serviceHost, int listenPort, boolean debug) {
        try {
            logger = Logger.getLogger(ObjectBroker.class.getName() );
            if(debug) {
                fileHandler = new FileHandler("log/Middleware.log");
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                logger.addHandler(fileHandler);
            }

            logger.info("Logger erstellt");

            serverSocket = new ServerSocket(0);
            this.nameService = NameServiceImpl.init(serviceHost, listenPort, serverSocket, logger);
            skeletonManager = SkeletonManagerImpl.init(nameService, logger);
            (new Thread(skeletonManager)).start();

        } catch (IOException e) {
            logger.log(Level.SEVERE,e.toString());
        }  catch (SecurityException e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }


    public static void main(String[] args){
        ObjectBroker ob = ObjectBroker.init("localhost",10002,false);
        NameService ns = ob.getNameService();

        TestClass testC = new TestClass();
        ns.rebind(testC, "myTestClass");
        Object obj = ns.resolve("myTestClass");


    }

    private static class TestClass{
        private String theField;
    }
}
