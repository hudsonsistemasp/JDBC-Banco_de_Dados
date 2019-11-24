package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import db.DB;
import db.DbException;

public class Program {

	public static void main(String[] args) {
		
		//Esses recursos não são controladdos pela jvm do java, logo devemos fechar para evitar consumo de memória e outras coisas.
		Connection conn = null;
		Statement st = null;//Objeto que leva a sintaxe SQL
		ResultSet rs = null;//Vem com o resultado da consulta e em formato de "tabela". Logo podemos apontar o campo desejado
		String sql = "select * from department";
		
		try {
				
				
			conn = DB.getConnection();
			/*-----------------SELECT = createStatement----------------------*/
				
				st = conn.createStatement();//Esse é o comando dedicado a fazer o SELECT no banco, didacamente dizendo;
				rs = st.executeQuery(sql);
				
				/*1-next() retorna false, se já estiver no último registro. Dai encerra o loop
				 2-Para acessar o campo do resultSet, eu referencio o tipo que o campo é e o nome do campo como está no banco de dados, ou pelo index*/
				while (rs.next()) {
					System.out.println(rs.getInt(1) + ", " + rs.getString(2));
					System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
				}
				/*------------------FIM SELECT---------------------------------*/
				
				
				
				
				/*-----------------INSERT = prepareStatement----------------------*/
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
				PreparedStatement psInsert = null;
				sql = "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId)"
						+ "VALUES (?, ?, ?, ?, ?)";
				psInsert = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				
				
				/*Agora para eu trocar meus placeHolder, as interrogações, pelos valores, faço o apontamento de acordo com o tipo de cada campo*/
				//Lê se: o primeiro interrogação(placeholer), que é do tipo STRING no banco, recebe "Lionel Messi"
				psInsert.setString(1, "Lamil Gremis");
				psInsert.setString(2, "gressi@email.com.br");//Placeholder 2'?'
				//Placeholder 3'?': Para jogar a data para um banco, não se deve usar o java.util.date, e sim o java.sql.date
				psInsert.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
				psInsert.setDouble(4, 4000.0);//O placeholder 4'?', refere-se ao BaseSalary no banco que é double, logo o tipo é double aqui
				psInsert.setInt(5, 2);//Placeholder 5'?'
				
				
				//Esse é o comando dedicado a fazer o INSERT no banco e a var para ver a qtde de registros que foram inseridos, quando necessitar de respostas
				int qtdeRegistrosInseridos = psInsert.executeUpdate();
				
				if (qtdeRegistrosInseridos > 0) {
					//Para recuperar os IDS gerados no INSERT
					ResultSet rsId = psInsert.getGeneratedKeys();
					System.out.println("Linhas inseridas: " + qtdeRegistrosInseridos);
					while(rsId.next()) {
						int id = rsId.getInt(1);
						System.out.println("Id: " + id);
					}	
				}
				else {
					System.out.println("Nenhuma linha incrementada no banco de dados");
				}
				/*------------------FIM INSERT---------------------------------*/	
				
				
				
				/*-----------------UPDATE = prepareStatement usando commit e rollback----------------------*/
				try {
					PreparedStatement psUpdate = null;
					sql = "UPDATE seller SET BaseSalary = BaseSalary + ? WHERE DepartmentID = ?";//Vamos adicionar valor ao salário base 
					psUpdate = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					psUpdate.setDouble(1, 10000.0);
					psUpdate.setInt(2, 1);
					
					conn.commit();
					
					int qtdeRegistrosUpdated = psUpdate.executeUpdate();
					if(qtdeRegistrosUpdated > 0) {
						System.out.println("Quantidade de registro(s) atualizado(s): " + qtdeRegistrosUpdated);
					}
					else {
						System.out.println("Alerta: Nenhum dado atualizado!");
					}
				}catch(SQLException e){
					try {
						conn.rollback();
						throw new DbException("Rollback feito! Causa do erro: " + e.getMessage());
					}catch(SQLException er) {
						throw new DbException("Danger: Rollback falhou! Causa do falha: " + er.getMessage());
					}
					
				}
				/*------------------FIM UPDATE---------------------------------*/
				
				
				/*-----------------DELETE = prepareStatement----------------------*/
				//Criar uma exception, DbIntegrityException, para tratar a situação de integridade referencial
				PreparedStatement psDelete = null;
				//sql = "DELETE from seller WHERE Name = ? ";
				sql = "DELETE from department WHERE Id = ? ";
				psDelete = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				//psDelete.setString(1, "Lamil Gremis");
				psDelete.setInt(1, 2);//O Id 2 já tem registro filho na table seller e a integridade referencial será mantida, logo não será deletado
				
				int qtdeRegistrosDeleted = psDelete.executeUpdate();
				if(qtdeRegistrosDeleted > 0) {
					System.out.println("Quantidade de registro(s) deletado(s): " + qtdeRegistrosDeleted);
				}
				else {
					System.out.println("Alerta: Nenhum dado deletado!");
				}				
				
				System.out.println();
				
				/*------------------FIM DELETE---------------------------------*/
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
			catch(ParseException e) {
				throw new DbException(e.getMessage());
			}
			finally {
				DB.closeResultSet(rs);//Objeto do SELECT, mas usado no INSERT para recuperar os ID´s inseridos
				DB.closeStatement(st);//Objeto do SELECT
				DB.closeConnection();
			}
		
		
	}

}
