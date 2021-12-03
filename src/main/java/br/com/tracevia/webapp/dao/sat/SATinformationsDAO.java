package br.com.tracevia.webapp.dao.sat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.tracevia.webapp.methods.DateTimeApplication;
import br.com.tracevia.webapp.model.global.RoadConcessionaire;
import br.com.tracevia.webapp.model.sat.SAT;
import br.com.tracevia.webapp.util.ConnectionFactory;

public class SATinformationsDAO {
	
	private Connection conn;		
	protected ConnectionFactory connection = new ConnectionFactory();
	private PreparedStatement ps;
	private ResultSet rs;
		
	public List<SAT> dataInfo30() throws Exception {
			
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("DATALIST30: "+currentDate);
					
		String select = "SELECT d.NOME_ESTACAO, d.DATA_HORA, " +
		
		"CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3  THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S2', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S2' " +
				    
	    "FROM "+RoadConcessionaire.tableDados15+" d " +
	    "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " +
	    "WHERE DATA_HORA = DATE_SUB( ? , INTERVAL 30 MINUTE) AND eq.visible = 1 AND d.VEL_MEDIA_TOTAL <> 0 " +
	    "GROUP BY d.NOME_ESTACAO " +
	    "ORDER BY d.DATA_HORA ASC ";
					
	  try {
			
		    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);			
			ps.setString(1, currentDate);		
						
			rs = ps.executeQuery();
			
			//System.out.println("DATA LIST 30 SQL: "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					SAT sat = new SAT();

					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setQuantidadeS1(rs.getInt("VOLUME_TOTAL_S1"));						
					sat.setVelocidadeS1(rs.getInt("VEL_MEDIA_TOTAL_S1"));	
					sat.setQuantidadeS2(rs.getInt("VOLUME_TOTAL_S2"));	
					sat.setVelocidadeS2(rs.getInt("VEL_MEDIA_TOTAL_S2"));	
															
					list.add(sat);
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return list;
		
	}
	
	
	public List<SAT> dataInfo45() throws Exception {
		
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
	//	System.out.println("DATA LIST 45: "+currentDate);
					
		String select = "SELECT d.NOME_ESTACAO, d.DATA_HORA, " +
		
		"CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3  THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S2', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S2' " +
				    
	    "FROM "+RoadConcessionaire.tableDados15+" d " +
	    "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " +
	    "WHERE DATA_HORA = DATE_SUB( ? , INTERVAL 45 MINUTE) AND eq.visible = 1 AND d.VEL_MEDIA_TOTAL <> 0 " +
	    "GROUP BY d.NOME_ESTACAO " +
	    "ORDER BY d.DATA_HORA ASC";
					
	  try {
			
		  conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
		  
			ps = conn.prepareStatement(select);			
			ps.setString(1, currentDate);		
						
			rs = ps.executeQuery();
			
			//System.out.println("DATA LIST 45 SQL : "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					SAT sat = new SAT();

					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setQuantidadeS1(rs.getInt("VOLUME_TOTAL_S1"));						
					sat.setVelocidadeS1(rs.getInt("VEL_MEDIA_TOTAL_S1"));	
					sat.setQuantidadeS2(rs.getInt("VOLUME_TOTAL_S2"));	
					sat.setVelocidadeS2(rs.getInt("VEL_MEDIA_TOTAL_S2"));	
															
					list.add(sat);
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return list;
		
	}
	
   public List<SAT> dataInfo08() throws Exception {
	   
		//LIMIT OF EQUIPS TO CHECK
		int limit = satNumbers();
		
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("DATALIST08: "+currentDate);
					
		String select = "SELECT d.NOME_ESTACAO, d.DATA_HORA, " +
		
		"CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3  THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S2', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S2' " +
				    
	    "FROM "+RoadConcessionaire.tableDados15+" d " +
	    "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " +
	    "WHERE DATA_HORA between DATE_SUB( ? , INTERVAL 8 HOUR) AND ? AND eq.visible = 1 AND d.VEL_MEDIA_TOTAL <> 0 " +
	    "GROUP BY d.NOME_ESTACAO, d.DATA_HORA " +
	    "ORDER BY d.DATA_HORA ASC " +
	    "LIMIT "+ limit +" ";
					
	  try {
			
		  conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
		  
			ps = conn.prepareStatement(select);			
			ps.setString(1, currentDate);	
			ps.setString(2, currentDate);		
						
			rs = ps.executeQuery();
			
			//System.out.println("DATA LIST 08 SQL: "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					SAT sat = new SAT();

					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setQuantidadeS1(rs.getInt("VOLUME_TOTAL_S1"));						
					sat.setVelocidadeS1(rs.getInt("VEL_MEDIA_TOTAL_S1"));	
					sat.setQuantidadeS2(rs.getInt("VOLUME_TOTAL_S2"));	
					sat.setVelocidadeS2(rs.getInt("VEL_MEDIA_TOTAL_S2"));	
															
					list.add(sat);
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return list;
		
	}
	
	public SAT dataInfoByData45(int equip) throws Exception {
		
		SAT sat = new SAT();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("DATA 45 IND: "+currentDate);
					
		String select = "SELECT d.NOME_ESTACAO, d.DATA_HORA, " +
		
		"CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3  THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S2', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S2' " +
				    
	    "FROM "+RoadConcessionaire.tableDados15+" d " +
	    "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " +
	    "WHERE eq.equip_id = ? AND DATA_HORA = DATE_SUB( ? , INTERVAL 45 MINUTE) AND eq.visible = 1 AND d.VEL_MEDIA_TOTAL <> 0 ";
	  					
	  try {
			
		  conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);
			ps.setInt(1, equip);	
			ps.setString(2, currentDate);		
						
			rs = ps.executeQuery();
			
			//System.out.println("DATA IND 45 SQL: "+select);
			
			if (rs != null) {
				while (rs.next()) {
				
					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setQuantidadeS1(rs.getInt("VOLUME_TOTAL_S1"));						
					sat.setVelocidadeS1(rs.getInt("VEL_MEDIA_TOTAL_S1"));	
					sat.setQuantidadeS2(rs.getInt("VOLUME_TOTAL_S2"));	
					sat.setVelocidadeS2(rs.getInt("VEL_MEDIA_TOTAL_S2"));	
									
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return sat;
		
	}
	
	//LISTAR TODOS EQUIPAMENTOS POR DADOS NOS ULTIMAS 8 HORAS (DELAY DE 15 MINUTOS)
     public SAT dataInfoByData08(int equip) throws Exception {
		
		SAT sat = new SAT();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("DATA IND 08: "+currentDate);
					
		String select = "SELECT d.NOME_ESTACAO, d.DATA_HORA, " +
		
		"CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3  THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2, d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VOLUME_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VOLUME_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(SUM(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VOLUME_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VOLUME_TOTAL_S2', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 OR eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
		"WHEN eq.number_lanes = 5 OR eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 OR eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 1 OR d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S1', " +

	    "CASE " +
	    "WHEN eq.number_lanes = 2 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2, d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 3 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 2 OR d.NOME_FAIXA = 3 , d.VEL_MEDIA_TOTAL , NULL)), 0), 0) " +
		"WHEN eq.number_lanes = 4 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 3 OR d.NOME_FAIXA = 4, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 5 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 6 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 4 OR d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 7 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +
	    "WHEN eq.number_lanes = 8 THEN IFNULL(ROUND(AVG(IF(d.NOME_FAIXA = 5 OR d.NOME_FAIXA = 6 OR d.NOME_FAIXA = 7 OR d.NOME_FAIXA = 8, d.VEL_MEDIA_TOTAL , NULL )), 0), 0) " +

	    "ELSE 0 " +
	    "END 'VEL_MEDIA_TOTAL_S2' " +
				    
	    "FROM "+RoadConcessionaire.tableDados15+" d " +
	    "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " +
	    "WHERE eq.equip_id = ? AND DATA_HORA between DATE_SUB( ? , INTERVAL 8 HOUR) AND ? AND eq.visible = 1 AND d.VEL_MEDIA_TOTAL <> 0 " +
		"GROUP BY d.DATA_HORA " +
        "ORDER BY d.DATA_HORA DESC LIMIT 1 ";
	  					
	  try {
			
		  conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);
			ps.setInt(1, equip);	
			ps.setString(2, currentDate);	
			ps.setString(3, currentDate);	
						
			rs = ps.executeQuery();
			
			//System.out.println("DATA 08 SQL: "+select);
			
			if (rs != null) {
				while (rs.next()) {
				
					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setQuantidadeS1(rs.getInt("VOLUME_TOTAL_S1"));						
					sat.setVelocidadeS1(rs.getInt("VEL_MEDIA_TOTAL_S1"));	
					sat.setQuantidadeS2(rs.getInt("VOLUME_TOTAL_S2"));	
					sat.setVelocidadeS2(rs.getInt("VEL_MEDIA_TOTAL_S2"));	
									
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return sat;
		
	}
	
	
	/////////////////////////////////
	//// SAT STATUS BY DATA
    ////////////////////////////////
	
    //LISTAR TODOS EQUIPAMENTOS POR STATUS NOS ULTIMOS 30 MINUTOS (DELAY DE 15 MINUTOS)
	public List<SAT> statusByData30() throws Exception {
	
	List<SAT> list = new ArrayList<SAT>();
	DateTimeApplication dta = new DateTimeApplication();
	
	String currentDate = null;
		
	Calendar calendar = Calendar.getInstance();	
	int minute = calendar.get(Calendar.MINUTE);
	
	//Obter datas formatadas para os dados
	currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
	
	//System.out.println("DATALISTSTAT 30: "+currentDate);
				
	String select = "SELECT d.NOME_ESTACAO, COUNT(*) AS STATUS FROM "+RoadConcessionaire.tableDados15+" d " + 
			        "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " + 
			        "WHERE d.DATA_HORA = DATE_SUB( ? , INTERVAL 30 MINUTE) AND eq.visible = 1 " +
			        "GROUP BY d.NOME_ESTACAO " +			       
				    "ORDER BY d.DATA_HORA";
	
	try {
		
		    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);				
			ps.setString(1, currentDate);		
						
			rs = ps.executeQuery();
			
			//System.out.println("STATUS DATA 45 LIST: "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					SAT sat = new SAT();

					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));					
					sat.setStatus(rs.getInt("STATUS"));	
				
					list.add(sat);
													
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

	
			return list;
			
      }
	
	//LISTAR TODOS EQUIPAMENTOS POR STATUS NOS ULTIMAS 8 HORAS (DELAY DE 15 MINUTOS)
	public List<SAT> statusByData08() throws Exception {
		
		//LIMIT OF EQUIPS TO CHECK
		int limit = satNumbers();
		
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
	
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
				
		// Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("STATUS DATA 08 LIST: "+currentDate);
					
		String select ="SELECT d.NOME_ESTACAO, COUNT(*) AS STATUS FROM "+RoadConcessionaire.tableDados15+" d " + 
				       "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " + 
				       "WHERE d.DATA_HORA BETWEEN DATE_SUB( ? , INTERVAL 8 HOUR) AND ? AND eq.visible = 1 " +
				       "GROUP BY d.NOME_ESTACAO, d.DATA_HORA " +
				       "ORDER BY d.DATA_HORA ASC " +
				       "LIMIT "+ limit +" ";
				    		
		try {
			
			    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
				
				ps = conn.prepareStatement(select);					
				ps.setString(1, currentDate);	
				ps.setString(2, currentDate);	
				
				rs = ps.executeQuery();
				
				//System.out.println("STATUS 08 LIST SQL: "+select);
				
				if (rs != null) {
					while (rs.next()) {
						
						SAT sat = new SAT();

						sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));					
						sat.setStatus(rs.getInt("STATUS"));	
					
						list.add(sat);
														
					}				
				 }			

			} catch (SQLException e) {
				e.printStackTrace();
			}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

		
				return list;
				
	      }
	
  //LISTAR TODOS EQUIPAMENTOS POR STATUS NOS ULTIMOS 45 MINUTOS (DELAY DE 15 MINUTOS)	
  public List<SAT> statusByData45() throws Exception {
		
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("STAT LIST 45: "+currentDate);
					
		String select = "SELECT d.NOME_ESTACAO, COUNT(*) AS STATUS FROM "+RoadConcessionaire.tableDados15+" d " + 
				       "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " + 
				       "WHERE d.DATA_HORA = DATE_SUB( ? , INTERVAL 45 MINUTE) AND eq.visible = 1 " +
				       "GROUP BY d.NOME_ESTACAO " +
					   "ORDER BY d.DATA_HORA ASC";
		
		try {
			
			    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
				
				ps = conn.prepareStatement(select);					
				ps.setString(1, currentDate);		
							
				rs = ps.executeQuery();
				
				//System.out.println("STATUS 45 LIST SQL: "+select);
				
				if (rs != null) {
					while (rs.next()) {
						
						SAT sat = new SAT();

						sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));					
						sat.setStatus(rs.getInt("STATUS"));	
					
						list.add(sat);
														
					}				
				 }			

			} catch (SQLException e) {
				e.printStackTrace();
			}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

		
				return list;
				
	      }
	
    //LISTAR UM EQUIPAMENTO POR STATUS NOS ULTIMOS 45 MINUTOS (DELAY DE 15 MINUTOS)
    public SAT statusByData45(int equip) throws Exception {
		
		SAT sat = new SAT();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("STAT IND 45: "+currentDate);
		
	String select = "SELECT d.NOME_ESTACAO, COUNT(*) AS STATUS FROM "+RoadConcessionaire.tableDados15+" d " + 
			        "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " + 
			        "WHERE eq.equip_id = ? AND d.DATA_HORA = DATE_SUB( ? , INTERVAL 45 MINUTE) AND eq.visible = 1 ";
			      	    	  					
	  try {
			
		    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);
			ps.setInt(1, equip);	
			ps.setString(2, currentDate);	
			
			//System.out.println("STATUS IND 45 SQL: "+select);
						
			rs = ps.executeQuery();
						
			if (rs != null) {
				while (rs.next()) {
				
					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setStatus(rs.getInt("STATUS"));						
														
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return sat;
		
	}	
    
    
  //LISTAR UM EQUIPAMENTO POR STATUS NOS ULTIMAS 8 HORAS (DELAY DE 15 MINUTOS)
    public SAT statusByData08(int equip) throws Exception {
    	    			
		SAT sat = new SAT();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null;
			
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//System.out.println("STAT IND 08: "+currentDate);
		
	String select = "SELECT d.NOME_ESTACAO, COUNT(*) AS STATUS FROM "+RoadConcessionaire.tableDados15+" d " + 
			        "INNER JOIN sat_equipment eq on (eq.equip_id = d.nome_estacao) " + 
			        "WHERE eq.equip_id = ? AND d.DATA_HORA BETWEEN DATE_SUB( ? , INTERVAL 8 HOUR) AND ? AND eq.visible = 1 " +
			        "GROUP BY d.DATA_HORA " + 			        
			        "ORDER BY d.DATA_HORA DESC LIMIT 1";
			      	    	  					
	  try {
			
		  conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);
			ps.setInt(1, equip);	
			ps.setString(2, currentDate);	
			ps.setString(3, currentDate);	
			
			//System.out.println("STATUS 08 IND SQL: "+select);
						
			rs = ps.executeQuery();
						
			if (rs != null) {
				while (rs.next()) {
				
					sat.setEquip_id(rs.getInt("d.NOME_ESTACAO"));
					sat.setStatus(rs.getInt("STATUS"));						
														
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return sat;
		
	}	
        
    
  //LISTAR UM EQUIPAMENTO POR STATUS NOS ULTIMAS 8 HORAS (DELAY DE 15 MINUTOS)
    public Integer satNumbers() throws Exception {
    	
    	int qtde = 0;
				
	String select = "SELECT COUNT(*) 'Qtde' FROM sat_equipment WHERE visible = 1";
			      	    	  					
	  try {
			
		  conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);									
			rs = ps.executeQuery();
						
			if (rs != null) {
				while (rs.next()) {
				
					qtde = rs.getInt(1);									
														
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return qtde;
		
	}	
	
	
    /////////////////////////////////
    //// SAT STATUS BY DATA
    ////////////////////////////////
	
	//////////////////////////////////////
	/// SAT STATUS
	////////////////////////////////////
		
	/*public List<SAT> SATstatus30() throws Exception {
		
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null, currentDateSub = null;
	
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//Obter datas formatadas para os dados
		currentDateSub = dta.getCurrentDateSubDados30(calendar, minute);
								
		String select = "SELECT EQ_ID, SUM(ONLINE_STATUS) 'STATUS' FROM "+RoadConcessionaire.tableStatus+" s " +
		"INNER JOIN sat_equipment eq on (eq.equip_id = s.EQ_ID) " +
		"WHERE s.DATA_HORA between DATE_SUB( ? , INTERVAL 30 MINUTE) AND ? AND eq.visible = 1 " +
		"GROUP BY s.EQ_ID " +
		"ORDER BY s.DATA_HORA ASC ";
						
		    try {
			
		    	conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
						
			ps = conn.prepareStatement(select);		
			ps.setString(1, currentDate);		
			ps.setString(2, currentDateSub);
			
			//System.out.println("30Status: "+select);
			//System.out.println("CUR: "+currentDate+"\nBEFORE: "+currentDateSub);
					
			rs = ps.executeQuery();
			
			//System.out.println("30MINSTT: "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					SAT sat = new SAT();

					sat.setEquip_id(rs.getInt("s.EQ_ID"));					
					sat.setStatus(rs.getInt("STATUS"));	
																							
					list.add(sat);
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return list;		
	}
	
    public List<SAT> SATstatus45() throws Exception {
		
		List<SAT> list = new ArrayList<SAT>();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null, currentDateSub = null;
	
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//Obter datas formatadas para os dados
		currentDateSub = dta.getCurrentDateSubDados45(calendar, minute);
				
		String select = "SELECT s.EQ_ID, SUM(s.ONLINE_STATUS) 'STATUS' FROM "+RoadConcessionaire.tableStatus+" s " +
		"INNER JOIN sat_equipment eq on (eq.equip_id = s.EQ_ID) " +
		"WHERE s.DATA_HORA between DATE_SUB( ? , INTERVAL 45 MINUTE) AND ? AND eq.visible = 1 " +
		"GROUP BY s.EQ_ID " +
		"ORDER BY s.DATA_HORA ASC ";
				
		    try {
			
		    	conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);			
			ps.setString(1, currentDate);		
			ps.setString(2, currentDateSub);
			
			//System.out.println("CUR: "+currentDate+"\nBEFORE: "+currentDateSub);
					
			rs = ps.executeQuery();
			
			//System.out.println("45 min: "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					SAT sat = new SAT();

					sat.setEquip_id(rs.getInt("s.EQ_ID"));					
					sat.setStatus(rs.getInt("STATUS"));	
					
					//System.out.println(sat.getStatus());
																		
					list.add(sat);
				}				
			 }			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return list;		
	}
	
	//FOR CCR
	public SAT SATstatus45Stat(int equip) throws Exception {
		
		SAT sat = new SAT();
		DateTimeApplication dta = new DateTimeApplication();
		
		String currentDate = null, currentDateSub = null;
	
		Calendar calendar = Calendar.getInstance();	
		int minute = calendar.get(Calendar.MINUTE);
		
		//Obter datas formatadas para os dados
		currentDate = dta.getCurrentDateDados15CCR(calendar, minute);
		
		//Obter datas formatadas para os dados
		currentDateSub = dta.getCurrentDateSubDados45(calendar, minute);
				
		String select = "SELECT s.EQ_ID, SUM(s.ONLINE_STATUS) 'STATUS' FROM "+RoadConcessionaire.tableStatus+" s " +
		"INNER JOIN sat_equipment eq on (eq.equip_id = s.EQ_ID) " +
		"WHERE eq.equip_id = ? AND s.DATA_HORA between DATE_SUB( ? , INTERVAL 45 MINUTE) AND ? AND eq.visible = 1 ";
						
		    try {
			
		    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
			ps = conn.prepareStatement(select);	
			ps.setInt(1, equip);	
			ps.setString(2, currentDate);		
			ps.setString(3, currentDateSub);
			
		    //System.out.println("BEF: "+currentDate+"\nBEF: "+currentDateSub);
					
			rs = ps.executeQuery();
			
			//System.out.println("45BEFORE: "+select);
			
			if (rs != null) {
				while (rs.next()) {
					
					sat.setEquip_id(rs.getInt("s.EQ_ID"));					
					sat.setStatus(rs.getInt("STATUS"));	
				
				}		
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

				
		return sat;		
	} */
			
}
