package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * Factory des élément dao 
 * @author oltenos
 *
 */
public class DaoFactory extends AbstractFactory{
	/**
	 * URL de connexion
	 */
	private static String url = "jdbc:mysql://localhost:3306/translator";
	/**
	 * Nom du user
	 */
	private static String user = "utilisateur";
	/**
	 * Mot de passe de l'utilisateur
	 */
	private static String passwd = "utilisateur";
	/**
	 * Objet Connection
	 */
	private static Connection conn;
	/**
	 * Instance
	 */
	private static DaoFactory instance = new DaoFactory();

	/**
	 * Constructeur privé
	 */
	private DaoFactory() {
		try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

        }
		try {
			conn = DriverManager.getConnection(url, user, passwd);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return l'instance de DaoFactory
	 */
	public static DaoFactory getInstance() {
		if (conn == null)
			instance = new DaoFactory();

		return instance;
	}
	
	/**
	 * Permet d'obtenir la connection à la base de données
	 * @return la connection à la base de données
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection(url, user, passwd);
			conn.setAutoCommit(false);
			Statement statement = conn.createStatement();
			statement.executeQuery("SET NAMES 'utf8';");
		}
		return conn;
	}

	/**
	 * Permet d'obtenir un objet DaoSubtitles
	 * @return un nouvel objet DaoSubtitles
	 */
	@Override
	public DaoSubtitles getDaoSubtitles() {
		return new DaoSubtitles();
	}
	
	/**
	 * Permet d'obtenir un objet DaoLanguages
	 * @return un nouvel objet DaoLanguages
	 */
	@Override
	public DaoLanguages getDaoLanguages() {
		return new DaoLanguages();
	}

};
