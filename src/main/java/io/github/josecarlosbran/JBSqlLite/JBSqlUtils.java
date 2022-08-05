package io.github.josecarlosbran.JBSqlLite;

import io.github.josecarlosbran.JBSqlLite.Enumerations.DataBase;
import io.github.josecarlosbran.JBSqlLite.Exceptions.DataBaseUndefind;
import io.github.josecarlosbran.JBSqlLite.Exceptions.PropertiesDBUndefined;
import io.github.josecarlosbran.LogsJB.LogsJB;

import java.util.Objects;

public class JBSqlUtils extends Methods{


    public JBSqlUtils() throws DataBaseUndefind, PropertiesDBUndefined {
        super();
    }

    /**
     * Setea el nombre de la Base de Datos global a la que se conectaran los modelos que no tengan una configuración
     * personalizada.
     * @param BD Nombre de la Base de Datos.
     */
    public static void setDataBaseGlobal(String BD) {
        try{
            System.setProperty("DataBaseBD",BD);
            //System.out.println("SystemProperty Seteada: "+System.getProperty("DataBaseBD"));
        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Setea el nombre de la Base de Datos global: "+ e.toString());
            LogsJB.fatal("Tipo de Excepción : "+e.getClass());
            LogsJB.fatal("Causa de la Excepción : "+e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : "+e.getMessage());
            LogsJB.fatal("Trace de la Excepción : "+e.getStackTrace());
        }
    }

    /**
     * Setea la Contraseña del usuario global con la que se conectaran los modelos que no tengan una configuración personalizada.
     * @param password Contraseña del usuario con el cual se conectara a la BD's.
     */
    public static void setPasswordGlobal(String password) {
        try{
            System.setProperty("DataBasePassword",password);
            //System.out.println("SystemProperty Seteada: "+System.getProperty("DataBasePassword"));
        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Setea la contraseña del usuario de BD's global: "+ e.toString());
            LogsJB.fatal("Tipo de Excepción : "+e.getClass());
            LogsJB.fatal("Causa de la Excepción : "+e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : "+e.getMessage());
            LogsJB.fatal("Trace de la Excepción : "+e.getStackTrace());
        }
    }

    /**
     * Setea el Usuario global con la que se conectaran los modelos que no tengan una configuración personalizada.
     * @param user Usuario con el cual se conectara a la BD's.
     */
    public static void setUserGlobal(String user) {
        try{
            System.setProperty("DataBaseUser",user);
            //System.out.println("SystemProperty Seteada: "+System.getProperty("DataBaseUser"));

        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Setea el usuario de BD's global: "+ e.toString());
            LogsJB.fatal("Tipo de Excepción : "+e.getClass());
            LogsJB.fatal("Causa de la Excepción : "+e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : "+e.getMessage());
            LogsJB.fatal("Trace de la Excepción : "+e.getStackTrace());
        }
    }

    /**
     * Setea el puerto global con el que se conectaran los modelos que no tengan una configuración personalizada.
     * @param port Puerto en el cual se encuentra escuchando la BD's a la cual se pegaran los modelos.
     */
    public static void setPortGlobal(String port) {
        try{
            System.setProperty("DataBasePort",port);
            //System.out.println("SystemProperty Seteada: "+System.getProperty("DataBasePort"));

        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Setea el Puerto de BD's global: "+ e.toString());
            LogsJB.fatal("Tipo de Excepción : "+e.getClass());
            LogsJB.fatal("Causa de la Excepción : "+e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : "+e.getMessage());
            LogsJB.fatal("Trace de la Excepción : "+e.getStackTrace());
        }
    }

    /**
     * Setea el host en el cual se encuentra la BD's global a la cual se conectaran los modelos que no tengan una configuración personalizada.
     * @param host Host en el cual se encuentra la BD's a la que nos queremos conectar.
     */
    public static void setHostGlobal(String host) {
        try{
            System.setProperty("DataBaseHost",host);
            //System.out.println("SystemProperty Seteada: "+System.getProperty("DataBaseHost"));
        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Setea el Host de la BD's global: "+ e.toString());
            LogsJB.fatal("Tipo de Excepción : "+e.getClass());
            LogsJB.fatal("Causa de la Excepción : "+e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : "+e.getMessage());
            LogsJB.fatal("Trace de la Excepción : "+e.getStackTrace());
        }

    }

    /**
     * Setea el tipo de BD's global a la cual se estaran conectando los modelos que no tengan una configuración personalizada.
     * @param dataBase Tipo de BD's a la cual se estaran los modelos que no tengan una configuración personalizada, los tipos disponibles son
     *                 MySQL,
     *                 SQLServer,
     *                 PostgreSQL,
     *                 SQLite.
     */
    public static void setDataBaseTypeGlobal(DataBase dataBase) {
        try{
            System.setProperty("DataBase",dataBase.name());
            //System.out.println("SystemProperty Seteada: "+System.getProperty("DataBase"));
        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Setea el Tipo de BD's global: "+ e.toString());
            LogsJB.fatal("Tipo de Excepción : "+e.getClass());
            LogsJB.fatal("Causa de la Excepción : "+e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : "+e.getMessage());
            LogsJB.fatal("Trace de la Excepción : "+e.getStackTrace());
        }

    }

}
