package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*1-Para que façamos a conexão com o MySQL precisamos baixar o driver no site do Mysql e baixar o Connector/J
  EU colo na pasta da prórpia pasta de instalação do banco para que fique universal para qualquer aplicação: 
  C:\Program Files (x86)\MySQL\Connector J 8.0\mysql-connector-java-8.0.18.jar 
 2- Criar uma User Library contendo o .jar do driver do MySQL: Window-> Preferences-> Java-> Build Path->User Libraries
 3- Dar o nome de MySQLConnector para a User Library e clicar no 'Add external JARs
 
  4- Criar um novo projeto e adicionar a biblioteca ao projeto: No momento em que criar o projeto e dar o nome, next-> Aba Libraries-> 
     Add Library-> User Library-> Escolhe a User Library que criamos 'MySQLConnector' e Finish. Dar prosseguimento normal e finish
     
   5- Criar o arquivo db.properties contendo as credenciais de acesso ao banco de dados e a url que aponta para ele.
   
   6- Criar os métodos statics para conectar e desconectar no banco de dados, nessa classe*/

public class DB {
	
	//Criar o objeto de conexão, Connection. Conexão do JDBC
	private static Connection conn = null;
	
	public static Connection getConnection() {
		if (conn == null) {
			Properties props = loadProperties();
			String url = props.getProperty("dburl");//Exception: adiconar no fina da url-> ?useTimezone=true&serverTimezone=UTC
			try {
				conn = DriverManager.getConnection(url, props);
				System.out.println("Opened");
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	//Fechar a conexão com o banco
	public static void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
				System.out.println("Closed!");
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	
	//Basicamente, esse método vai ler o arquivo db.properties(item 5) e guardar num objeto do tipo Properties
	private static Properties loadProperties() {
		try(FileInputStream fis = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fis);//Aqui que o arquivo acima é lido e guardado dentro do objeto
			return props;
		}
		catch(FileNotFoundException e) {
			throw new DbException(e.getMessage());
		}
		catch(IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
}
