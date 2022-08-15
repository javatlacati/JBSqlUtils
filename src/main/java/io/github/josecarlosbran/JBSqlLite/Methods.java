package io.github.josecarlosbran.JBSqlLite;

import io.github.josecarlosbran.JBSqlLite.Enumerations.Constraint;
import io.github.josecarlosbran.JBSqlLite.Enumerations.DataBase;
import io.github.josecarlosbran.JBSqlLite.Enumerations.DataType;
import io.github.josecarlosbran.JBSqlLite.Exceptions.ConexionUndefind;
import io.github.josecarlosbran.JBSqlLite.Exceptions.DataBaseUndefind;
import io.github.josecarlosbran.JBSqlLite.Exceptions.PropertiesDBUndefined;
import io.github.josecarlosbran.JBSqlLite.Utilities.ColumnsSQL;
import io.github.josecarlosbran.JBSqlLite.Utilities.TablesSQL;
import io.github.josecarlosbran.JBSqlLite.Utilities.UtilitiesJB;
import io.github.josecarlosbran.LogsJB.LogsJB;
import org.apache.commons.lang3.StringUtils;

import javax.print.attribute.ResolutionSyntax;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.josecarlosbran.JBSqlLite.Utilities.UtilitiesJB.getBooleanfromInt;
import static io.github.josecarlosbran.JBSqlLite.Utilities.UtilitiesJB.getIntFromBoolean;

public class Methods extends Conexion {
    public Methods() throws DataBaseUndefind, PropertiesDBUndefined {
        super();
    }

    //https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-connect-drivermanager.html#connector-j-examples-connection-drivermanager
    //https://docs.microsoft.com/en-us/sql/connect/jdbc/using-the-jdbc-driver?view=sql-server-ver16
    //https://www.dev2qa.com/how-to-load-jdbc-configuration-from-properties-file-example/
    //https://www.tutorialspoint.com/how-to-connect-to-postgresql-database-using-a-jdbc-program


    /**
     * Obtiene la conexión del modelo a la BD's con las propiedades definidas.
     * @return Retorna la conexión del modelo a la BD's con las propiedades definidas.
     */
    public Connection getConnection() {
        Connection connect = null;
        try {
            String url = null;
            if (this.getDataBaseType() == DataBase.PostgreSQL) {
                //Carga el controlador de PostgreSQL
                url = null;
                connect = null;
                Class.forName("org.postgresql.Driver");
                url = "jdbc:" + this.getDataBaseType().getDBType() + "://" +
                        this.getHost() + ":" + this.getPort() + "/" + this.getBD();
                String usuario = this.getUser();
                String password = this.getPassword();
                connect = DriverManager.getConnection(url, usuario, password);
            } else if (this.getDataBaseType() == DataBase.MySQL) {
                url = null;
                connect = null;
                //Carga el controlador de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                url = "jdbc:" + this.getDataBaseType().getDBType() + "://" +
                        this.getHost() + ":" + this.getPort() + "/" + this.getBD();
                String usuario = this.getUser();
                String password = this.getPassword();
                connect = DriverManager.getConnection(url, usuario, password);
            } else if (this.getDataBaseType() == DataBase.SQLServer) {
                url = null;
                connect = null;
                //Carga el controlador de SQLServer
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                url = "jdbc:" + this.getDataBaseType().getDBType() + "://" +
                        this.getHost() + ":" + this.getPort() + ";databaseName=" + this.getBD() + ";TrustServerCertificate=True";
                String usuario = this.getUser();
                String password = this.getPassword();
                connect = DriverManager.getConnection(url, usuario, password);
            } else if (this.getDataBaseType() == DataBase.SQLite) {
                url = null;
                connect = null;
                url = "jdbc:" + this.getDataBaseType().getDBType() + ":" + this.getBD();
                connect = DriverManager.getConnection(url);
            }

            if (!Objects.isNull(connect)) {
                LogsJB.info("Conexión a BD's " + this.getBD() + " Realizada exitosamente");
                //this.setConnect(connect);
                //tableExist(connect);
            }
        } catch (Exception e) {
            LogsJB.fatal("Excepción disparada al obtener la conexión a la BD's proporcionada: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
        return connect;
    }



    /**
     * Cierra la conexión a BD's
     * @param connect Conexión que se desea cerrar
     */
    public void closeConnection(Connection connect) {
        try {
            if (Objects.isNull(connect)) {
                //Si la propiedad del sistema no esta definida, Lanza una Exepción
                throw new ConexionUndefind("No se a conectado el modelo a la BD's");
            }
            if (!connect.isClosed()) {
                connect.close();
                LogsJB.info("Conexión a BD's cerrada");
            } else {
                LogsJB.info("Conexión a BD's ya estaba cerrada");
            }
        } catch (ConexionUndefind e) {
            LogsJB.warning("El modelo no estaba conectado a la BD's por lo cual no se cerrara la conexión");
        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada cerrar la conexión a la BD's: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
    }

    /**
     * Cierra la conexión a BD's del modelo.
     */
    public synchronized void closeConnection() {
        try {
            if (!this.getConnect().isClosed()) {
                this.getConnect().close();
                LogsJB.info("Conexión a BD's cerrada");
            } else {
                LogsJB.info("Conexión a BD's ya estaba cerrada");
            }
        }catch (ConexionUndefind e) {
            LogsJB.warning("El modelo no estaba conectado a la BD's por lo cual no se cerrara la conexión");
        }catch (Exception e) {
            LogsJB.fatal("Excepción disparada cerrar la conexión a la BD's: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
    }

    /**
     * Verifica la existencia de la tabla correspondiente al modelo en BD's
     * @return True si la tabla correspondiente al modelo existe en BD's, de lo contrario False.
     */
    protected Boolean tableExist() {
        Boolean result = false;
        try {
            Callable<Boolean> VerificarExistencia = () -> {
                try {
                    LogsJB.trace("Comienza a verificar la existencia de la tabla");
                    Connection connect = this.getConnection();
                    DatabaseMetaData metaData = connect.getMetaData();
                    ResultSet tables = metaData.getTables(null, null, "%", null);
                    //Obtener las tablas disponibles
                    TablesSQL.getTablas().clear();
                    LogsJB.trace("Revisara el resultSet");
                    while (tables.next()) {
                        TablesSQL temp = new TablesSQL();
                        temp.setTABLE_CAT(tables.getString(1));
                        temp.setTABLE_SCHEM(tables.getString(2));
                        temp.setTABLE_NAME(tables.getString(3));
                        temp.setTABLE_TYPE(tables.getString(4));
                        temp.setREMARKS(tables.getString(5));


                        if (this.getDataBaseType() != DataBase.SQLServer) {
                            temp.setTYPE_CAT(tables.getString(6));
                            temp.setTYPE_SCHEM(tables.getString(7));
                            temp.setTYPE_NAME(tables.getString(8));
                            temp.setSELF_REFERENCING_COL_NAME(tables.getString(9));
                            temp.setREF_GENERATION(tables.getString(10));

                        }

                        TablesSQL.getTablas().add(temp);
                        String NameModel = this.getClass().getSimpleName();
                        String NameTable = temp.getTABLE_NAME();
                        if (NameModel.equalsIgnoreCase(NameTable)) {
                            this.setTableExist(Boolean.TRUE);
                            this.setTableName(NameTable);
                            LogsJB.info("La tabla correspondiente a este modelo, existe en BD's");
                            tables.close();
                            this.closeConnection(connect);
                            getColumnsTable();
                            return true;
                        }
                    }
                    LogsJB.trace("Termino de Revisarar el resultSet");
                    tables.close();
                    if (!this.getTableExist()) {
                        LogsJB.info("La tabla correspondiente a este modelo, No existe en BD's");

                        this.closeConnection(connect);

                        return false;
                    }


                } catch (Exception e) {
                    LogsJB.fatal("Excepción disparada en el método que verifica si existe la tabla correspondiente al modelo: " + e.toString());
                    LogsJB.fatal("Tipo de Excepción : " + e.getClass());
                    LogsJB.fatal("Causa de la Excepción : " + e.getCause());
                    LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
                    LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
                }
                return false;
            };
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<Boolean> future = executor.submit(VerificarExistencia);
            while (!future.isDone()) {

            }
            executor.shutdown();
            result = future.get();
        } catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que verifica si existe la tabla correspondiente al modelo: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
        return result;
    }

    /**
     * Obtiene las columnas que tiene la tabla correspondiente al modelo en BD's.
     */
    protected void getColumnsTable() {
        Runnable ObtenerColumnas = () -> {
            try {
                Connection connect = this.getConnection();
                DatabaseMetaData metaData = connect.getMetaData();
                ResultSet columnas = metaData.getColumns(null, null, this.getTableName(), null);
                //Obtener las tablas disponibles
                this.getColumnas().clear();
                while (columnas.next()) {
                    ColumnsSQL temp = new ColumnsSQL();
                    temp.setTABLE_CAT(columnas.getString(1));
                    temp.setTABLE_SCHEM(columnas.getString(2));
                    temp.setTABLE_NAME(columnas.getString(3));
                    temp.setCOLUMN_NAME(columnas.getString(4));
                    temp.setDATA_TYPE(columnas.getInt(5));
                    temp.setTYPE_NAME(columnas.getString(6));
                    temp.setCOLUMN_SIZE(columnas.getInt(7));

                    temp.setDECIMAL_DIGITS(columnas.getInt(9));
                    temp.setNUM_PREC_RADIX(columnas.getInt(10));
                    temp.setNULLABLE(columnas.getInt(11));
                    temp.setREMARKS(columnas.getString(12));
                    temp.setCOLUMN_DEF(columnas.getString(13));

                    temp.setCHAR_OCTET_LENGTH(columnas.getInt(16));
                    temp.setORDINAL_POSITION(columnas.getInt(17));
                    temp.setIS_NULLABLE(columnas.getString(18));
                    temp.setSCOPE_CATALOG(columnas.getString(19));
                    temp.setSCOPE_SCHEMA(columnas.getString(20));
                    temp.setSCOPE_TABLE(columnas.getString(21));
                    temp.setSOURCE_DATA_TYPE(columnas.getShort(22));
                    temp.setIS_AUTOINCREMENT(columnas.getString(23));
                    temp.setIS_GENERATEDCOLUMN(columnas.getString(24));
                    this.getColumnas().add(temp);
                    //Types.ARRAY

                }
                LogsJB.info("Información de las columnas de la tabla correspondiente al modelo obtenida");
                columnas.close();

                this.closeConnection(connect);

            } catch (Exception e) {
                LogsJB.fatal("Excepción disparada en el método que obtiene las columnas de la tabla que corresponde al modelo: " + e.toString());
                LogsJB.fatal("Tipo de Excepción : " + e.getClass());
                LogsJB.fatal("Causa de la Excepción : " + e.getCause());
                LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
                LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(ObtenerColumnas);
        executor.shutdown();


    }

    /**
     * Crea la tabla correspondiente al modelo en BD's si esta no existe.
     * @return True si la tabla correspondiente al modelo en BD's no existe y fue creada exitosamente,
     * False si la tabla correspondiente al modelo ya existe en BD's
     */
    public Boolean crateTable() {
        Boolean result = false;
        try {
            Callable<Boolean> createtabla = () -> {
                try {
                    if (this.tableExist()) {
                        LogsJB.info("La tabla correspondiente al modelo ya existe en la BD's, por lo cual no será creada.");
                        return false;
                    } else {
                        String sql = "CREATE TABLE " + this.getClass().getSimpleName() + "(";
                        List<Method> metodos = new LinkedList<>();
                        metodos = this.getMethodsGetOfModel(this.getMethodsModel());
                        for (int i = 0; i < metodos.size(); i++) {
                            //Obtengo el metodo
                            Method metodo = metodos.get(i);
                            //Obtengo la información de la columna
                            Column columnsSQL = (Column) metodo.invoke(this, null);
                            String columnName = metodo.getName();
                            columnName = StringUtils.remove(columnName, "get");
                            DataType columnType = columnsSQL.getDataTypeSQL();
                            Constraint[] columnRestriccion = columnsSQL.getRestriccion();
                            String restricciones = "";
                            String tipo_de_columna = columnType.toString();
                            if ((((this.getDataBaseType() == DataBase.PostgreSQL)) || ((this.getDataBaseType() == DataBase.MySQL))
                                    || ((this.getDataBaseType() == DataBase.SQLite))) &&
                                    (columnType == DataType.BIT)) {
                                tipo_de_columna = DataType.BOOLEAN.toString();
                            }
                            if (!Objects.isNull(columnRestriccion)) {
                                for (Constraint restriccion : columnRestriccion) {
                                    if ((DataBase.PostgreSQL == this.getDataBaseType()) &&
                                            (restriccion == Constraint.AUTO_INCREMENT)) {
                                        tipo_de_columna = DataType.SERIAL.name();
                                    } else if ((DataBase.SQLServer == this.getDataBaseType()) &&
                                            (restriccion == Constraint.AUTO_INCREMENT)) {
                                        //tipo_de_columna = DataType.IDENTITY.toString();
                                        restricciones = restricciones + DataType.IDENTITY.toString() + " ";
                                    } else if ((DataBase.SQLite == this.getDataBaseType()) &&
                                            (restriccion == Constraint.AUTO_INCREMENT)) {
                                        restricciones = restricciones + "";
                                    } else {
                                        restricciones = restricciones + restriccion.getRestriccion() + " ";
                                    }
                                }
                            }

                            String columna = columnName + " " + tipo_de_columna + " " + restricciones;


                            sql = sql + columna;
                            int temporal = metodos.size() - 1;
                            if (i < temporal) {
                                sql = sql + ", ";
                            } else if (i == temporal) {
                                sql = sql + ");";
                            }

                        }
                        Connection connect = this.getConnection();
                        Statement ejecutor = connect.createStatement();
                        LogsJB.info(sql);
                        if (!ejecutor.execute(sql)) {
                            LogsJB.info("Sentencia para crear tabla de la BD's ejecutada exitosamente");
                            LogsJB.info("Tabla " + this.getClass().getSimpleName() + " Creada exitosamente");
                            LogsJB.info(sql);

                                this.closeConnection(connect);

                            return true;
                        }
                        ejecutor.close();
                        this.closeConnection(connect);

                    }
                    return false;
                } catch (Exception e) {
                    LogsJB.fatal("Excepción disparada en el método que Crea la tabla correspondiente al modelo: " + e.toString());
                    LogsJB.fatal("Tipo de Excepción : " + e.getClass());
                    LogsJB.fatal("Causa de la Excepción : " + e.getCause());
                    LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
                    LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
                }
                return false;
            };

            ExecutorService ejecutor = Executors.newFixedThreadPool(1);
            Future<Boolean> future = ejecutor.submit(createtabla);
            while (!future.isDone()) {

            }
            ejecutor.shutdown();
            result = future.get();
        } catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Crea la tabla correspondiente al modelo: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
        return result;
    }

    /**
     * Elimina la tabla correspondiente al modelo en BD's
     * @return True si la tabla correspondiente al modelo en BD's existe y fue eliminada, de no existir la tabla correspondiente
     * en BD's retorna False.
     */
    public Boolean dropTableIfExist() {
        Boolean result = false;
        try {
            Callable<Boolean> dropTable = () -> {
                try {
                    if (this.tableExist()) {
                        String sql = "";
                        if (this.getDataBaseType() == DataBase.MySQL || this.getDataBaseType() == DataBase.PostgreSQL || this.getDataBaseType() == DataBase.SQLite) {
                            sql = "DROP TABLE IF EXISTS " + this.getClass().getSimpleName();
                            //+ " RESTRICT";
                        } else if (this.getDataBaseType() == DataBase.SQLServer) {
                            sql = "if exists (select * from INFORMATION_SCHEMA.TABLES where TABLE_NAME = '" +
                                    this.getClass().getSimpleName() +
                                    "' AND TABLE_SCHEMA = 'dbo')\n" +
                                    "    drop table dbo." +
                                    this.getClass().getSimpleName();
                            //+" RESTRICT;";
                        }
                        LogsJB.info(sql);
                        Connection connect = this.getConnection();
                        Statement ejecutor = connect.createStatement();

                        if (!ejecutor.execute(sql)) {
                            LogsJB.info("Sentencia para eliminar tabla de la BD's ejecutada exitosamente");
                            LogsJB.info("Tabla " + this.getClass().getSimpleName() + " Eliminada exitosamente");
                            LogsJB.info(sql);
                            //this.setTableExist(false);
                            return true;
                        }
                        ejecutor.close();
                        this.closeConnection(connect);

                    } else {
                        LogsJB.info("Tabla correspondiente al modelo no existe en BD's por eso no pudo ser eliminada");
                        return false;
                    }
                    return false;
                } catch (Exception e) {
                    LogsJB.fatal("Excepción disparada en el método que Elimina la tabla correspondiente al modelo: " + e.toString());
                    LogsJB.fatal("Tipo de Excepción : " + e.getClass());
                    LogsJB.fatal("Causa de la Excepción : " + e.getCause());
                    LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
                    LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
                }
                return false;
            };

            ExecutorService ejecutor = Executors.newFixedThreadPool(1);
            Future<Boolean> future = ejecutor.submit(dropTable);
            while (!future.isDone()) {

            }
            ejecutor.shutdown();
            result = future.get();
        } catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Elimina la tabla correspondiente al modelo: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
        return result;
    }

    public Boolean save() {
        Boolean result = false;
        try {
            Callable<Boolean> Save = () -> {
                try {
                    if (this.tableExist()) {

                        String sql = "INSERT INTO " + this.getClass().getSimpleName() + "(";
                        List<Method> metodos = new LinkedList<>();
                        metodos = this.getMethodsGetOfModel(this.getMethodsModel());
                        int datos = 0;
                        //Llena la información de las columnas que se insertaran
                        for (int i = 0; i < metodos.size(); i++) {
                            //Obtengo el metodo
                            Method metodo = metodos.get(i);
                            //Obtengo la información de la columna
                            Column columnsSQL = (Column) metodo.invoke(this, null);
                            String columnName = metodo.getName();
                            columnName = StringUtils.remove(columnName, "get");
                            if (Objects.isNull(columnsSQL.getValor())) {
                                continue;
                            }
                            datos++;
                            sql = sql + columnName;
                            int temporal = metodos.size() - 1;
                            if (i < temporal) {
                                sql = sql + ", ";
                            } else if (i == temporal) {
                                sql = sql + ") VALUES (";
                            }
                        }

                        //Llena los espacios con la información de los datos que serán agregados
                        for (int i = 0; i < datos; i++) {
                            sql = sql + "?";
                            int temporal = datos - 1;
                            if (i < temporal) {
                                sql = sql + ", ";
                            } else if (i == temporal) {
                                sql = sql + ");";
                            }
                        }
                        LogsJB.info(sql);
                        Connection connect = this.getConnection();
                        PreparedStatement ejecutor = connect.prepareStatement(sql);
                        //LogsJB.info("Creo la instancia del PreparedStatement");
                        //Llena el prepareStatement
                        int auxiliar = 1;
                        for (int i = 0; i < metodos.size(); i++) {
                            //Obtengo el metodo
                            Method metodo = metodos.get(i);
                            //Obtengo la información de la columna
                            Column columnsSQL = (Column) metodo.invoke(this, null);
                            if (Objects.isNull(columnsSQL.getValor())) {
                                continue;
                            }

                            /*
                            if ((columnsSQL.getDataTypeSQL() == DataType.CHAR) || (columnsSQL.getDataTypeSQL() == DataType.VARCHAR)
                                    || (columnsSQL.getDataTypeSQL() == DataType.LONGVARCHAR)) {
                                //Caracteres y cadenas de Texto
                                ejecutor.setString(auxiliar, (String) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.NUMERIC) || (columnsSQL.getDataTypeSQL() == DataType.DECIMAL)
                                    || (columnsSQL.getDataTypeSQL() == DataType.MONEY) || (columnsSQL.getDataTypeSQL() == DataType.SMALLMONEY)
                                    || (columnsSQL.getDataTypeSQL() == DataType.DOUBLE)) {
                                //Dinero y numericos que tienen decimales
                                ejecutor.setDouble(auxiliar, (Double) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.BIT)) {
                                //Valores Booleanos
                                ejecutor.setBoolean(auxiliar, (Boolean) columnsSQL.getValor());
                                //ejecutor.setObject(auxiliar, columnsSQL.getValor(), Types.BOOLEAN);
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.SMALLINT) || (columnsSQL.getDataTypeSQL() == DataType.TINYINT)
                                    || (columnsSQL.getDataTypeSQL() == DataType.INTEGER) || (columnsSQL.getDataTypeSQL() == DataType.IDENTITY)
                                    || (columnsSQL.getDataTypeSQL() == DataType.SERIAL)) {
                                //Valores Enteros
                                ejecutor.setInt(auxiliar, (Integer) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.REAL) || (columnsSQL.getDataTypeSQL() == DataType.FLOAT)) {
                                //Valores Flotantes
                                ejecutor.setFloat(auxiliar, (Float) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.BINARY) || (columnsSQL.getDataTypeSQL() == DataType.VARBINARY)
                                    || (columnsSQL.getDataTypeSQL() == DataType.LONGVARBINARY)) {
                                //Valores binarios
                                ejecutor.setBytes(auxiliar, (byte[]) columnsSQL.getValor());
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.DATE)) {
                                //DATE
                                ejecutor.setDate(auxiliar, (Date) columnsSQL.getValor());
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.TIME)) {
                                //Time
                                ejecutor.setTime(auxiliar, (Time) columnsSQL.getValor());
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.TIMESTAMP) || (columnsSQL.getDataTypeSQL() == DataType.DATETIME)
                                    || (columnsSQL.getDataTypeSQL() == DataType.SMALLDATETIME)
                                    || (columnsSQL.getDataTypeSQL() == DataType.DATETIME2)) {
                                //TimeStamp
                                ejecutor.setTimestamp(auxiliar, (Timestamp) columnsSQL.getValor());
                            } else {
                                ejecutor.setObject(auxiliar, columnsSQL.getValor());
                            }

                            */
                            convertJavaToSQL(columnsSQL, ejecutor, auxiliar);
                            auxiliar++;
                        }

                        if (ejecutor.executeUpdate() == 1) {
                            int filas = ejecutor.getUpdateCount();
                            LogsJB.info("Filas actualizadas: " + filas);
                        }

                        this.closeConnection(connect);

                        return true;
                    } else {
                        LogsJB.info("Tabla correspondiente al modelo no existe en BD's'");
                    }
                    return false;
                } catch (Exception e) {
                    LogsJB.fatal("Excepción disparada en el método que Guarda el modelo en la BD's: " + e.toString());
                    LogsJB.fatal("Tipo de Excepción : " + e.getClass());
                    LogsJB.fatal("Causa de la Excepción : " + e.getCause());
                    LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
                    LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
                }
                return false;
            };

            ExecutorService ejecutor = Executors.newFixedThreadPool(1);
            Future<Boolean> future = ejecutor.submit(Save);
            while (!future.isDone()) {

            }
            ejecutor.shutdown();
            result = future.get();
        } catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Guarda el modelo en la BD's: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
        return result;
    }


    /**
     * Almacena el modelo proporcionado.
     * @param modelo Modelo que será almacenado en BD's
     * @param <T> Expresión que hace que el metodo sea generico y pueda ser utilizado por cualquier objeto que herede la Clase JBSqlUtils
     */
    public <T extends JBSqlUtils> void saveModel(T modelo){
        try {
            modelo.setTaskIsReady(false);
            Runnable Save = () -> {
                try {
                    if (modelo.tableExist()) {

                        String sql = "INSERT INTO " + modelo.getClass().getSimpleName() + "(";
                        List<Method> metodos = new LinkedList<>();
                        metodos = modelo.getMethodsGetOfModel(modelo.getMethodsModel());
                        int datos = 0;
                        //Llena la información de las columnas que se insertaran
                        for (int i = 0; i < metodos.size(); i++) {
                            //Obtengo el metodo
                            Method metodo = metodos.get(i);
                            //Obtengo la información de la columna
                            Column columnsSQL = (Column) metodo.invoke(modelo, null);
                            String columnName = metodo.getName();
                            columnName = StringUtils.remove(columnName, "get");
                            if (Objects.isNull(columnsSQL.getValor())) {
                                continue;
                            }
                            datos++;
                            sql = sql + columnName;
                            int temporal = metodos.size() - 1;
                            if (i < temporal) {
                                sql = sql + ", ";
                            } else if (i == temporal) {
                                sql = sql + ") VALUES (";
                            }
                        }

                        //Llena los espacios con la información de los datos que serán agregados
                        for (int i = 0; i < datos; i++) {
                            sql = sql + "?";
                            int temporal = datos - 1;
                            if (i < temporal) {
                                sql = sql + ", ";
                            } else if (i == temporal) {
                                sql = sql + ");";
                            }
                        }
                        LogsJB.info(sql);
                        Connection connect = modelo.getConnection();
                        PreparedStatement ejecutor = connect.prepareStatement(sql);
                        //LogsJB.info("Creo la instancia del PreparedStatement");
                        //Llena el prepareStatement
                        int auxiliar = 1;
                        for (int i = 0; i < metodos.size(); i++) {
                            //Obtengo el metodo
                            Method metodo = metodos.get(i);
                            //Obtengo la información de la columna
                            Column columnsSQL = (Column) metodo.invoke(modelo, null);
                            if (Objects.isNull(columnsSQL.getValor())) {
                                continue;
                            }

                            /*
                            if ((columnsSQL.getDataTypeSQL() == DataType.CHAR) || (columnsSQL.getDataTypeSQL() == DataType.VARCHAR)
                                    || (columnsSQL.getDataTypeSQL() == DataType.LONGVARCHAR)) {
                                //Caracteres y cadenas de Texto
                                ejecutor.setString(auxiliar, (String) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.NUMERIC) || (columnsSQL.getDataTypeSQL() == DataType.DECIMAL)
                                    || (columnsSQL.getDataTypeSQL() == DataType.MONEY) || (columnsSQL.getDataTypeSQL() == DataType.SMALLMONEY)
                                    || (columnsSQL.getDataTypeSQL() == DataType.DOUBLE)) {
                                //Dinero y numericos que tienen decimales
                                ejecutor.setDouble(auxiliar, (Double) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.BIT)) {
                                //Valores Booleanos
                                ejecutor.setBoolean(auxiliar, (Boolean) columnsSQL.getValor());
                                //ejecutor.setObject(auxiliar, columnsSQL.getValor(), Types.BOOLEAN);
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.SMALLINT) || (columnsSQL.getDataTypeSQL() == DataType.TINYINT)
                                    || (columnsSQL.getDataTypeSQL() == DataType.INTEGER) || (columnsSQL.getDataTypeSQL() == DataType.IDENTITY)
                                    || (columnsSQL.getDataTypeSQL() == DataType.SERIAL)) {
                                //Valores Enteros
                                ejecutor.setInt(auxiliar, (Integer) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.REAL) || (columnsSQL.getDataTypeSQL() == DataType.FLOAT)) {
                                //Valores Flotantes
                                ejecutor.setFloat(auxiliar, (Float) columnsSQL.getValor());

                            } else if ((columnsSQL.getDataTypeSQL() == DataType.BINARY) || (columnsSQL.getDataTypeSQL() == DataType.VARBINARY)
                                    || (columnsSQL.getDataTypeSQL() == DataType.LONGVARBINARY)) {
                                //Valores binarios
                                ejecutor.setBytes(auxiliar, (byte[]) columnsSQL.getValor());
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.DATE)) {
                                //DATE
                                ejecutor.setDate(auxiliar, (Date) columnsSQL.getValor());
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.TIME)) {
                                //Time
                                ejecutor.setTime(auxiliar, (Time) columnsSQL.getValor());
                            } else if ((columnsSQL.getDataTypeSQL() == DataType.TIMESTAMP) || (columnsSQL.getDataTypeSQL() == DataType.DATETIME)
                                    || (columnsSQL.getDataTypeSQL() == DataType.SMALLDATETIME)
                                    || (columnsSQL.getDataTypeSQL() == DataType.DATETIME2)) {
                                //TimeStamp
                                ejecutor.setTimestamp(auxiliar, (Timestamp) columnsSQL.getValor());
                            } else {
                                ejecutor.setObject(auxiliar, columnsSQL.getValor());
                            }*/
                            convertJavaToSQL(columnsSQL, ejecutor, auxiliar);

                            auxiliar++;
                        }

                        if (ejecutor.executeUpdate() == 1) {
                            int filas = ejecutor.getUpdateCount();
                            LogsJB.info("Filas actualizadas: " + filas);
                        }

                        modelo.closeConnection(connect);

                    } else {
                        LogsJB.info("Tabla correspondiente al modelo no existe en BD's'");
                    }
                    modelo.setTaskIsReady(true);
                } catch (Exception e) {
                    LogsJB.fatal("Excepción disparada en el método que Guarda el modelo en la BD's: " + e.toString());
                    LogsJB.fatal("Tipo de Excepción : " + e.getClass());
                    LogsJB.fatal("Causa de la Excepción : " + e.getCause());
                    LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
                    LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
                }
            };
            ExecutorService ejecutor = Executors.newFixedThreadPool(1);
            ejecutor.submit(Save);
            ejecutor.shutdown();
        } catch (Exception e) {
            LogsJB.fatal("Excepción disparada en el método que Guarda el modelo en la BD's: " + e.toString());
            LogsJB.fatal("Tipo de Excepción : " + e.getClass());
            LogsJB.fatal("Causa de la Excepción : " + e.getCause());
            LogsJB.fatal("Mensaje de la Excepción : " + e.getMessage());
            LogsJB.fatal("Trace de la Excepción : " + e.getStackTrace());
        }
    }


    protected void convertJavaToSQL(Column columnsSQL, PreparedStatement ejecutor, int auxiliar) throws SQLException {

        if ((columnsSQL.getDataTypeSQL() == DataType.CHAR) || (columnsSQL.getDataTypeSQL() == DataType.VARCHAR)
                || (columnsSQL.getDataTypeSQL() == DataType.LONGVARCHAR)) {
            //Caracteres y cadenas de Texto
            ejecutor.setString(auxiliar, (String) columnsSQL.getValor());

        } else if ((columnsSQL.getDataTypeSQL() == DataType.NUMERIC) || (columnsSQL.getDataTypeSQL() == DataType.DECIMAL)
                || (columnsSQL.getDataTypeSQL() == DataType.MONEY) || (columnsSQL.getDataTypeSQL() == DataType.SMALLMONEY)
                || (columnsSQL.getDataTypeSQL() == DataType.DOUBLE)) {
            //Dinero y numericos que tienen decimales
            ejecutor.setDouble(auxiliar, (Double) columnsSQL.getValor());

        } else if ((columnsSQL.getDataTypeSQL() == DataType.BIT)) {
            //Valores Booleanos
            ejecutor.setBoolean(auxiliar, (Boolean) columnsSQL.getValor());
            //ejecutor.setObject(auxiliar, columnsSQL.getValor(), Types.BOOLEAN);
        } else if ((columnsSQL.getDataTypeSQL() == DataType.SMALLINT) || (columnsSQL.getDataTypeSQL() == DataType.TINYINT)
                || (columnsSQL.getDataTypeSQL() == DataType.INTEGER) || (columnsSQL.getDataTypeSQL() == DataType.IDENTITY)
                || (columnsSQL.getDataTypeSQL() == DataType.SERIAL)) {
            //Valores Enteros
            ejecutor.setInt(auxiliar, (Integer) columnsSQL.getValor());

        } else if ((columnsSQL.getDataTypeSQL() == DataType.REAL) || (columnsSQL.getDataTypeSQL() == DataType.FLOAT)) {
            //Valores Flotantes
            ejecutor.setFloat(auxiliar, (Float) columnsSQL.getValor());

        } else if ((columnsSQL.getDataTypeSQL() == DataType.BINARY) || (columnsSQL.getDataTypeSQL() == DataType.VARBINARY)
                || (columnsSQL.getDataTypeSQL() == DataType.LONGVARBINARY)) {
            //Valores binarios
            ejecutor.setBytes(auxiliar, (byte[]) columnsSQL.getValor());
        } else if ((columnsSQL.getDataTypeSQL() == DataType.DATE)) {
            //DATE
            ejecutor.setDate(auxiliar, (Date) columnsSQL.getValor());
        } else if ((columnsSQL.getDataTypeSQL() == DataType.TIME)) {
            //Time
            ejecutor.setTime(auxiliar, (Time) columnsSQL.getValor());
        } else if ((columnsSQL.getDataTypeSQL() == DataType.TIMESTAMP) || (columnsSQL.getDataTypeSQL() == DataType.DATETIME)
                || (columnsSQL.getDataTypeSQL() == DataType.SMALLDATETIME)
                || (columnsSQL.getDataTypeSQL() == DataType.DATETIME2)) {
            //TimeStamp
            ejecutor.setTimestamp(auxiliar, (Timestamp) columnsSQL.getValor());
        } else {
            ejecutor.setObject(auxiliar, columnsSQL.getValor());
        }
    }


    public void getWhereId(String id) {


    }

    void convertirSQLtoJava() {

    }

    //Obtener un objeto generico de una lista
    public <T> List<Method> getMethodsModel() {
        Method[] metodos = this.getClass().getMethods();
        List<Method> result = new LinkedList<>();
        // Los muestro en consola
        for (Method metodo : metodos) {
            String clase = metodo.getDeclaringClass().getSimpleName();
            String returntype = metodo.getReturnType().getSimpleName();

            if ((clase.equals("Object") || clase.equals("Conexion") || clase.equals("Methods") || clase.equals("JBSqlUtils")) && !returntype.equals("Column")) {

            } else {
                //System.out.println(metodo.getName() + "   " + metodo.getDeclaringClass() + "  " + returntype);
                result.add(metodo);
            }
            //System.out.println(metodo.getName()+"   "+metodo.getDeclaringClass()+"  "+returntype);
        }
        return result;
    }

    //Obtener unicamente los metodos get del modelo
    public <T> List<Method> getMethodsGetOfModel(List<Method> metodos) {
        // Los muestro en consola
        int i = 0;
        List<Method> result = metodos;
        while (i < result.size()) {
            Method metodo = result.get(i);
            String returntype = metodo.getReturnType().getSimpleName();
            String nombre = metodo.getName();
            if (returntype.equals("Column") && StringUtils.containsIgnoreCase(nombre, "Get")) {
                i++;
            } else {
                //System.out.println(metodo.getName() + "   " + metodo.getDeclaringClass() + "  " + returntype);
                result.remove(i);
            }
        }
        return result;
    }

    public <T> List<Method> getMethodsSetOfModel(List<Method> metodos) {
        List<Method> result = metodos;
        int i = 0;
        while (i < result.size()) {
            Method metodo = result.get(i);
            Parameter[] parametros = metodo.getParameters();
            String ParametroType = "";
            String nombre = metodo.getName();
            //System.out.println(metodo.getName() + "   " + metodo.getDeclaringClass() + "  " + ParametroType);
            if (StringUtils.containsIgnoreCase(nombre, "Set")) {
                ParametroType = parametros[0].getType().getSimpleName();
                //System.out.println(metodo.getName()+"   "+metodo.getDeclaringClass()+"  "+ParametroType);
                if (ParametroType.equals("Column")) {
                    if (parametros.length >= 1) {
                        //System.out.println(metodo.getName()+"   "+metodo.getDeclaringClass()+"  "+ParametroType);
                        i++;
                    }
                }
            } else {
                result.remove(i);
            }
        }
        return result;
    }

    public static <T, G> G getObject(T dato) {
        return (G) dato;
    }

    Function funcion = new Function<Integer, Boolean>() {
        public Boolean apply(Integer driver) {
            if (driver >= 1) {
                return true;
            }
            return false;
        }

        public Boolean apply(Double driver) {
            if (driver >= 1) {
                return true;
            }
            return false;
        }
    };


    public static <T, G> G Convertir_entero_a_boleano(T a, Function<T, G> funcion) {
        return funcion.apply(a);
    }

    public static <T, G> List<G> fromArrayToList(T[] a, Function<T, G> mapperFunction) {

        return Arrays.stream(a)
                .map(mapperFunction)
                .collect(Collectors.toList());
    }


}
