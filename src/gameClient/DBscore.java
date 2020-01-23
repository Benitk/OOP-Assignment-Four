package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class DBscore {
	private static int level[]= {0,1,3,5,9,11,13,16,19,20,23};
	private static int move[]= {290,580,580,500,580,580,580,290,580,290,1140};
	private static int grade[]= {145,450,720,570,510,1050,310,235,250,200,1000};
	private static int ID;
	private static int MyLevel;
	private static Object[][] myrecord=new Object [11][2];
	private static Object[][] myplace= {{0,0},{1,0},{3,0},{5,0},{9,0},{11,0},{13,0},{16,0},{19,0},{20,0},{23,0}};
	private static int countGame;
	public static final String jdbcUrl="jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser="student";
	public static final String jdbcUserPassword="OOP2020student";

	/** simply prints all the games as played by the users (in the database).
	 * 
	 */
	public static void printLog() {
		int max=0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT userID, MAX(score),levelID FROM Logs WHERE levelID=0 GROUP BY userID";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			while(resultSet.next())
			{
				System.out.println(resultSet.getInt("userID")+","+resultSet.getInt("MAX(score)")+","+resultSet.getInt("levelID"));
				
				
//				if(resultSet.getInt("score")>max ) 
//					max=resultSet.getInt("score");
//				
//					
//				System.out.println("Id: " + resultSet.getInt("UserID")+",Level: "+resultSet.getInt("levelID")+",Move: "
//				+resultSet.getInt("moves")+",grade: "+resultSet.getInt("score")+" ,"+resultSet.getDate("time"));
			}
			System.out.println(max);
			resultSet.close();
			statement.close();		
			connection.close();		
		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * this function returns the KML string as stored in the database (userID, level);
	 * @param id
	 * @param level
	 * @return
	 */
	public static String getKML(int id, int level) {
		String ans = null;
		String allCustomersQuery = "SELECT * FROM Users where userID="+id+";";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			if(resultSet!=null && resultSet.next()) {
				ans = resultSet.getString("kml_"+level);
			}
		}
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ans;
	}
	
	/*
	 * update my place in the class in all the levels
	 */
	public static void myplaceinKita(int id) {

		for(int i=0;i<level.length;i++) {
			int count=0;
			HashMap<Integer,Integer> players = new HashMap<>();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				String allCustomersQuery = "SELECT * FROM Logs WHERE levelID="+level[i]+" AND UserID<>0 AND UserID<>999";
				ResultSet resultSet = statement.executeQuery(allCustomersQuery);

				while(resultSet.next())
				{
					if(players.get(resultSet.getInt("UserID"))==null)
						if(resultSet.getInt("score")>grade[i] && resultSet.getInt("moves")<=move[i])
							players.put(resultSet.getInt("UserID"),resultSet.getInt("score"));

					if(players.get(resultSet.getInt("UserID"))!=null && resultSet.getInt("moves")<=move[i]) 
						if(players.get(resultSet.getInt("UserID"))<resultSet.getInt("score"))
							players.put(resultSet.getInt("UserID"),resultSet.getInt("score"));


				}
				resultSet.close();
				statement.close();		
				connection.close();		
			}

			catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Iterator<Integer> iter=players.values().iterator();
			while(iter.hasNext()) {
				if(players.get(id)!=null) {
					if(iter.next()<=players.get(id))
						count++;
				}
				else
					iter.next();

			}
			getMyplace()[i][1]=players.size()-count;

		}


	}
	/*
	 * update my score in all the levels,my number of games,and my level.
	 */
	public static void myscore(int id) {
		for(int i=0;i<myrecord.length;i++)
			myrecord[i][1]=0;
		int countgame=0;
		int mylevel=0;
		for(int i=0;i<level.length;i++) {
			int max=0;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				String allCustomersQuery = "SELECT * FROM Logs WHERE levelID="+level[i]+" AND UserID="+id;
				ResultSet resultSet = statement.executeQuery(allCustomersQuery);

				while(resultSet.next())
				{
					countgame++;
					if(resultSet.getInt("score")>max && resultSet.getInt("moves")<=move[i] && resultSet.getInt("score")>grade[i]) {
						max=resultSet.getInt("score");
						getMyrecord()[i][1]=max;
						getMyrecord()[i][0]=level[i];

					}
					
						
					if(resultSet.getInt("score")>grade[i])
						mylevel=level[i];
				}
				resultSet.close();
				statement.close();		
				connection.close();		
			}

			catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		setCountGame(countgame);
		setMyLevel(mylevel);
	}


	public static int getID() {
		return ID;
	}
	public static void setID(int iD) {
		ID = iD;
	}
	public static Object[][] getMyplace() {
		return myplace;
	}
	public static void setMyplace(Object[][] myplace) {
		DBscore.myplace = myplace;
	}
	public static Object[][] getMyrecord() {
		return myrecord;
	}
	public static void setMyrecord(Object[][] myrecord) {
		DBscore.myrecord = myrecord;
	}
	public static int getCountGame() {
		return countGame;
	}
	public static void setCountGame(int countGame) {
		DBscore.countGame = countGame;
	}
	public static int getMyLevel() {
		return MyLevel;
	}
	public static void setMyLevel(int myLevel) {
		MyLevel = myLevel;
	}

}
