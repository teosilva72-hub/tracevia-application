package br.com.tracevia.webapp.dao.sat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.tracevia.webapp.dao.global.EquipmentsDAO;
import br.com.tracevia.webapp.log.SystemLog;
import br.com.tracevia.webapp.model.global.RoadConcessionaire;
import br.com.tracevia.webapp.model.sat.FluxoVeiculos;
import br.com.tracevia.webapp.util.ConnectionFactory;

public class FluxoVeiculosDAO {

	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	
	String direction1, direction2; 
		
	// --------------------------------------------------------------------------------------------------------------
		
	// LOGS FOLDER
	
	private final String errorFolder = SystemLog.ERROR.concat("monthly-flow-dao\\");
	
	// --------------------------------------------------------------------------------------------------------------

	public FluxoVeiculosDAO() throws Exception {

		SystemLog.createLogFolder(errorFolder);
		
	}	
	
	// --------------------------------------------------------------------------------------------------------------
	
	/** Método para obter dados para construir o relatório do fluxo mensal
	 * @author Wellington da Silva criado em 01/03/2019 10:06 - atualizado em 06/01/2022 18:00	 
	 * @version 1.2
	 * @since 1.0 
	 * @param startDate data de inicio
	 * @param endDate data de término	
	 * @param equipId id do equipamento
	 * @param numberLanes número de linhas do equipamento
	 * @param laneDir1 primeira faixa do equipamento 
	 * @return lista de objetos do tipo FluxoVeiculos
	 *  
	 */	

	public List<FluxoVeiculos> getVehicles(String startDate, String endDate, String equipId, String laneDir1) {		

		List<FluxoVeiculos> lista = new ArrayList<FluxoVeiculos>();		
											
			setDirections(laneDir1); // DEFINE DIRECTIONS FIRST

			String select = " "; // initialize variable						

			select = "SELECT DATE_FORMAT(v.data, '%Y-%m-%d') AS date, " +
			"DATE_FORMAT((SEC_TO_TIME(TIME_TO_SEC(v.data)- TIME_TO_SEC(v.data)%(15*60))),'%H:%i') AS intervals, " +
					
				 "CASE " + 
					"WHEN eq.number_lanes = 2 THEN " + 							
							"IFNULL(ROUND(SUM(IF(((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
															
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2) THEN " + 			
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
									"END " +
								
					"WHEN eq.number_lanes = 4 THEN " + 			    			
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
																
					"WHEN eq.number_lanes = 5 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
									"END " +
								
					"WHEN eq.number_lanes = 6 THEN " +			 		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane=3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
											
					"WHEN eq.number_lanes = 7 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3 AND eq.dir_lane1 = eq.dir_lane4) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3 OR v.lane = 4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
									"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane=3 OR v.lane=4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +   
								"ELSE 0 END 'AUTO_S1', " +
								
				"CASE " +
					"WHEN eq.number_lanes = 2 THEN " + 							
							"IFNULL(ROUND(SUM(IF(((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
															
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2) THEN " + 			
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
									"END " +
								
					"WHEN eq.number_lanes = 4 THEN " + 			    		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
															
					"WHEN eq.number_lanes = 5 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
									"END " +	
								
					"WHEN eq.number_lanes = 6 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane=3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
													
					"WHEN eq.number_lanes = 7 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3 AND eq.dir_lane1 = eq.dir_lane4) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3 OR v.lane = 4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
									"END " +
											
					"WHEN eq.number_lanes = 8 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane=3 OR v.lane=4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								 "ELSE 0 END 'MOTO_S1', " +
				
				"CASE " +
					"WHEN eq.number_lanes = 2 THEN " + 							
							"IFNULL(ROUND(SUM(IF(((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
														
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2) THEN " + 			
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
									"END " + 
								
					"WHEN eq.number_lanes = 4 THEN " + 			    		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
																
					"WHEN eq.number_lanes = 5 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
									"END " +	
								
					"WHEN eq.number_lanes = 6 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane=3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
														
					"WHEN eq.number_lanes = 7 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3 AND eq.dir_lane1 = eq.dir_lane4) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3 OR v.lane = 4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " +  
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
									"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=1 OR v.lane=2 OR v.lane=3 OR v.lane=4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " +   
								"ELSE 0 END 'COM_S1', " +
				"CASE " + 				   
					"WHEN eq.number_lanes = 2 THEN " + 							
					"IFNULL(ROUND(SUM(IF((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"'), 1, NULL)),0),0) " + 
						
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2) THEN " + 			
							"IFNULL(ROUND(SUM(IF((v.lane = 1 OR v.lane = 2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"'), 1, NULL)),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"'), 1, NULL)),0),0) " + 
									"END " +
						
					"WHEN eq.number_lanes = 4 THEN " + 			    		
						"IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"') AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"'), 1, NULL)),0),0) " + 
													
					"WHEN eq.number_lanes = 5 THEN " + 
					"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3) THEN " + 
						"IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"'), 1, NULL )),0),0) " + 
							"ELSE IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"'), 1, NULL )),0),0) " + 
								"END " +	
							
					"WHEN eq.number_lanes = 6 THEN " + 					 		
						"IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2 OR v.lane=3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"'), 1, NULL )),0),0) " +  
													
					"WHEN eq.number_lanes = 7 THEN " + 
					"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3 AND eq.dir_lane1 = eq.dir_lane4) THEN " + 
						"IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2 OR v.lane = 3 OR v.lane = 4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"'), 1, NULL )),0),0) " + 
							"ELSE IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"'), 1, NULL )),0),0) " + 
								"END " + 
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
						"IFNULL(ROUND(SUM(IF((v.lane=1 OR v.lane=2 OR v.lane=3 OR v.lane=4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"'), 1, NULL )),0),0) " + 
						 "ELSE 0 END 'TOTAL_S1', " +
						 
				 "CASE " +			
					"WHEN eq.number_lanes = 2 THEN " + 							
					"IFNULL(ROUND(AVG(NULLIF(IF((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
					
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane1 = eq.dir_lane2) THEN " + 			
							"IFNULL(ROUND(AVG(NULLIF(IF((v.lane = 1 OR v.lane = 2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
								"ELSE IFNULL(ROUND(AVG(NULLIF(IF((v.lane = 1) AND (eq.dir_lane1 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
									"END " +
						
					"WHEN eq.number_lanes = 4 THEN " + 			    		
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
												
					"WHEN eq.number_lanes = 5 THEN " + 
					"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3) THEN " + 
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
							"ELSE IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
								"END " +	
							
					"WHEN eq.number_lanes = 6 THEN " + 					 		
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2 OR v.lane=3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"'), v.speed, NULL),0)),0),0) " +  
												
					"WHEN eq.number_lanes = 7 THEN " + 
					"CASE WHEN (eq.dir_lane1 = eq.dir_lane2 AND eq.dir_lane1 = eq.dir_lane3 AND eq.dir_lane1 = eq.dir_lane4) THEN " + 
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2 OR v.lane = 3 OR v.lane = 4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
							"ELSE IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2 OR v.lane = 3) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
								"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=1 OR v.lane=2 OR v.lane=3 OR v.lane=4) AND (eq.dir_lane1 = '"+direction1+"' OR eq.dir_lane2 = '"+direction1+"' OR eq.dir_lane3 = '"+direction1+"' OR eq.dir_lane4 = '"+direction1+"'), v.speed, NULL),0)),0),0) " + 
							"ELSE 0 END 'SPEED_S1', " +										
						
				"CASE " + 
					"WHEN eq.number_lanes = 2 THEN " + 							
							"IFNULL(ROUND(SUM(IF(((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
										
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane2 = eq.dir_lane3) THEN " + 			
							"IFNULL(ROUND(SUM(IF(((v.lane=2 OR v.lane=3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane = 3) AND (eq.dir_lane3 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
									"END " + 
								
					"WHEN eq.number_lanes = 4 THEN " + 			    			
							"IFNULL(ROUND(SUM(IF(((v.lane=3 OR v.lane=4) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
																
					"WHEN eq.number_lanes = 5 THEN " + 
							"CASE WHEN (eq.dir_lane3 = eq.dir_lane4 AND eq.dir_lane1 = eq.dir_lane5) THEN " + 
								"IFNULL(ROUND(SUM(IF(((v.lane=3 OR v.lane=4 OR v.lane = 5) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
									"ELSE IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
										"END " + 	
									
					"WHEN eq.number_lanes = 6 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5 OR v.lane=6) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
														
					"WHEN eq.number_lanes = 7 THEN " + 
						"CASE WHEN (eq.dir_lane4 = eq.dir_lane5 AND eq.dir_lane4 = eq.dir_lane6 AND eq.dir_lane4 = eq.dir_lane7) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5 OR v.lane = 6 OR v.lane = 7) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=5 OR v.lane=6 OR v.lane = 7) AND (eq.dir_lane5= '"+direction2+"' OR eq.dir_lane2 OR '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " +  
									"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=5 OR v.lane=6 OR v.lane=7 OR v.lane=8) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"' OR eq.dir_lane8 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classLight+"' OR v.classe = '"+RoadConcessionaire.classUnknown+"' OR v.classe = '"+RoadConcessionaire.classNotIdentifiedAxl2+"' OR ((v.classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross < 3501) OR (v.classe = '"+RoadConcessionaire.classTrailer+"' AND gross < 3501 )))), 1, NULL )),0),0) " + 
								"ELSE 0 END 'AUTO_S2', " +
									
				"CASE " + 
					"WHEN eq.number_lanes = 2 THEN " + 							
							"IFNULL(ROUND(SUM(IF(((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane2 = eq.dir_lane3) THEN " + 			
							"IFNULL(ROUND(SUM(IF(((v.lane=2 OR v.lane=3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane = 3) AND (eq.dir_lane3 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
									"END " +
								
					"WHEN eq.number_lanes = 4 THEN " + 			    			
							"IFNULL(ROUND(SUM(IF(((v.lane=3 OR v.lane=4) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
																
					"WHEN eq.number_lanes = 5 THEN " + 
							"CASE WHEN (eq.dir_lane3 = eq.dir_lane4 AND eq.dir_lane1 = eq.dir_lane5) THEN " + 
								"IFNULL(ROUND(SUM(IF(((v.lane=3 OR v.lane=4 OR v.lane = 5) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
									"ELSE IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
										"END " +	
									
					"WHEN eq.number_lanes = 6 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5 OR v.lane=6) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
														
						"WHEN eq.number_lanes = 7 THEN " + 
						"CASE WHEN (eq.dir_lane4 = eq.dir_lane5 AND eq.dir_lane4 = eq.dir_lane6 AND eq.dir_lane4 = eq.dir_lane7) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5 OR v.lane = 6 OR v.lane = 7) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=5 OR v.lane=6 OR v.lane = 7) AND (eq.dir_lane5= '"+direction2+"' OR eq.dir_lane2 OR '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " +
									"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=5 OR v.lane=6 OR v.lane=7 OR v.lane=8) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"' OR eq.dir_lane8 = '"+direction2+"') AND (v.classe = '"+RoadConcessionaire.classMotorcycle+"')), 1, NULL )),0),0) " + 
								"ELSE 0 END 'MOTO_S2', " +
										
				 "CASE " + 
					"WHEN eq.number_lanes = 2 THEN " + 							
							"IFNULL(ROUND(SUM(IF(((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
						
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane2 = eq.dir_lane3) THEN " + 			
							"IFNULL(ROUND(SUM(IF(((v.lane=2 OR v.lane=3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane = 3) AND (eq.dir_lane3 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
									"END " +
							
					"WHEN eq.number_lanes = 4 THEN " + 			    			
						"IFNULL(ROUND(SUM(IF(((v.lane=3 OR v.lane=4) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
														
					"WHEN eq.number_lanes = 5 THEN " + 
						"CASE WHEN (eq.dir_lane3 = eq.dir_lane4 AND eq.dir_lane1 = eq.dir_lane5) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=3 OR v.lane=4 OR v.lane = 5) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
									"END " +	
								
					"WHEN eq.number_lanes = 6 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5 OR v.lane=6) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
														
					"WHEN eq.number_lanes = 7 THEN " + 
						"CASE WHEN (eq.dir_lane4 = eq.dir_lane5 AND eq.dir_lane4 = eq.dir_lane6 AND eq.dir_lane4 = eq.dir_lane7) THEN " + 
							"IFNULL(ROUND(SUM(IF(((v.lane=4 OR v.lane=5 OR v.lane = 6 OR v.lane = 7) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF(((v.lane=5 OR v.lane=6 OR v.lane = 7) AND (eq.dir_lane5= '"+direction2+"' OR eq.dir_lane2 OR '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " + 
									"END " +
				
					"WHEN eq.number_lanes = 8 THEN " + 					 		
							"IFNULL(ROUND(SUM(IF(((v.lane=5 OR v.lane=6 OR v.lane=7 OR v.lane=8) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"' OR eq.dir_lane8 = '"+direction2+"') AND ((v.classe <> '"+RoadConcessionaire.classLight+"' AND v.classe <> '"+RoadConcessionaire.classUnknown+"' AND v.classe <> '"+RoadConcessionaire.classNotIdentifiedAxl2+"' AND v.classe <> '"+RoadConcessionaire.classMotorcycle+"' AND v.classe <> '"+RoadConcessionaire.classSemiTrailer+"' AND v.classe <> '"+RoadConcessionaire.classTrailer+"') OR (classe = '"+RoadConcessionaire.classSemiTrailer+"' AND gross > 3500) OR (classe = '"+RoadConcessionaire.classTrailer+"' AND gross > 3500))), 1, NULL )),0),0) " +  
								"ELSE 0 END 'COM_S2', " +
								   
				 "CASE " + 
					"WHEN eq.number_lanes = 2 THEN " + 							
					"IFNULL(ROUND(SUM(IF((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"'), 1, NULL)),0),0) " + 
										
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane2 = eq.dir_lane3) THEN " + 			
							"IFNULL(ROUND(SUM(IF((v.lane = 2 OR v.lane = 3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"'), 1, NULL)),0),0) " + 
								"ELSE IFNULL(ROUND(SUM(IF((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"'), 1, NULL)),0),0) " + 
									"END " +
						
					"WHEN eq.number_lanes = 4 THEN " + 			    		
						"IFNULL(ROUND(SUM(IF((v.lane=2 OR v.lane=3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"'), 1, NULL )),0),0) " + 
													
					"WHEN eq.number_lanes = 5 THEN " + 
					"CASE WHEN (eq.dir_lane4 = eq.dir_lane4 AND eq.dir_lane3 = eq.dir_lane5) THEN " + 
						"IFNULL(ROUND(SUM(IF((v.lane=3 OR v.lane=4 OR v.lane = 5) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"'), 1, NULL )),0),0) " + 
							"ELSE IFNULL(ROUND(SUM(IF((v.lane=4 OR v.lane=5) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"'), 1, NULL )),0),0) " + 
								"END " +	
							
					"WHEN eq.number_lanes = 6 THEN " + 					 		
						"IFNULL(ROUND(SUM(IF((v.lane=4 OR v.lane=5 OR v.lane=6) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"'), 1, NULL )),0),0) " + 
													
					"WHEN eq.number_lanes = 7 THEN " + 
					"CASE WHEN (eq.dir_lane4 = eq.dir_lane5 AND eq.dir_lane4 = eq.dir_lane6 AND eq.dir_lane4 = eq.dir_lane7) THEN " + 
						"IFNULL(ROUND(SUM(IF((v.lane=4 OR v.lane=5 OR v.lane = 6 OR v.lane = 7) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"'), 1, NULL )),0),0) " + 
							"ELSE IFNULL(ROUND(SUM(IF((v.lane=5 OR v.lane=6 OR v.lane = 7) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"'), 1, NULL )),0),0) " + 
								"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
						"IFNULL(ROUND(SUM(IF((v.lane=5 OR v.lane=6 OR v.lane=7 OR v.lane=8) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"' OR eq.dir_lane8 = '"+direction2+"'), 1, NULL )),0),0) " + 
						 "ELSE 0 END 'TOTAL_S2', " +
							
				"CASE " + 
					"WHEN eq.number_lanes = 2 THEN " + 							
					"IFNULL(ROUND(AVG(NULLIF(IF((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
						
					"WHEN eq.number_lanes = 3 THEN " + 
						"CASE WHEN (eq.dir_lane2 = eq.dir_lane3) THEN " + 			
							"IFNULL(ROUND(AVG(NULLIF(IF((v.lane = 2 OR v.lane = 3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
								"ELSE IFNULL(ROUND(AVG(NULLIF(IF((v.lane = 2) AND (eq.dir_lane2 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
								"END "+
						
					"WHEN eq.number_lanes = 4 THEN " + 			    		
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=2 OR v.lane=3) AND (eq.dir_lane2 = '"+direction2+"' OR eq.dir_lane3 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
													
					"WHEN eq.number_lanes = 5 THEN " + 
					"CASE WHEN (eq.dir_lane4 = eq.dir_lane4 AND eq.dir_lane3 = eq.dir_lane5) THEN " + 
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=3 OR v.lane=4 OR v.lane = 5) AND (eq.dir_lane3 = '"+direction2+"' OR eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
							"ELSE IFNULL(ROUND(AVG(NULLIF(IF((v.lane=4 OR v.lane=5) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
								"END " +
							
					"WHEN eq.number_lanes = 6 THEN " + 					 		
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=4 OR v.lane=5 OR v.lane=6) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"'), v.speed, NULL),0)),0),0) " +  
													
					"WHEN eq.number_lanes = 7 THEN " + 
					"CASE WHEN (eq.dir_lane4 = eq.dir_lane5 AND eq.dir_lane4 = eq.dir_lane6 AND eq.dir_lane4 = eq.dir_lane7) THEN " + 
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=4 OR v.lane=5 OR v.lane = 6 OR v.lane = 7) AND (eq.dir_lane4 = '"+direction2+"' OR eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
							"ELSE IFNULL(ROUND(AVG(NULLIF(IF((v.lane=5 OR v.lane=6 OR v.lane = 7) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
								"END " +
					
					"WHEN eq.number_lanes = 8 THEN " + 					 		
						"IFNULL(ROUND(AVG(NULLIF(IF((v.lane=5 OR v.lane=6 OR v.lane=7 OR v.lane=8) AND (eq.dir_lane5 = '"+direction2+"' OR eq.dir_lane6 = '"+direction2+"' OR eq.dir_lane7 = '"+direction2+"' OR eq.dir_lane8 = '"+direction2+"'), v.speed, NULL),0)),0),0) " + 
						 "ELSE 0 END 'SPEED_S2' " + 				 		 					 													
		 								
			 			"FROM tb_vbv v " +
						"INNER JOIN sat_equipment eq ON eq.equip_id = v.siteID " + 
						"WHERE v.data BETWEEN ? AND ? AND v.siteID = ? " +
						"GROUP BY DATE(v.data), intervals " +
						"ORDER BY DATE(v.data) ASC";
			
					try {
				
						conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
			
						ps = conn.prepareStatement(select);
						ps.setString(1, startDate);
						ps.setString(2, endDate);
						ps.setString(3, equipId);
																	
						rs = ps.executeQuery();
												
							if (rs.isBeforeFirst()) {
								while (rs.next()) {
				
									FluxoVeiculos veh = new FluxoVeiculos();														
				
									veh.setDate(rs.getString("date"));	
									veh.setInterval(rs.getString("intervals"));
									veh.setMoto1(rs.getInt("MOTO_S1"));
									veh.setAuto1(rs.getInt("AUTO_S1"));
									veh.setCom1(rs.getInt("COM_S1"));
									veh.setTotal1(rs.getInt("TOTAL_S1"));
									veh.setSpeed1(rs.getInt("SPEED_S1"));				
									veh.setMoto2(rs.getInt("MOTO_S2"));
									veh.setAuto2(rs.getInt("AUTO_S2"));
									veh.setCom2(rs.getInt("COM_S2"));	
									veh.setTotal2(rs.getInt("TOTAL_S2"));
									veh.setSpeed2(rs.getInt("SPEED_S2"));
													
									lista.add(veh);							    
								}							
							}
					
					} catch (SQLException sqle) {
						
						StringWriter errors = new StringWriter();
						sqle.printStackTrace(new PrintWriter(errors));
						
						SystemLog.logErrorSQL(errorFolder.concat("error_get_vehicle"), EquipmentsDAO.class.getCanonicalName(), sqle.getErrorCode(), sqle.getSQLState(), sqle.getMessage(), errors.toString());
												
					}finally {
						
						ConnectionFactory.closeConnection(conn, ps);
					
					}
				
					return lista;
			}
	
	// --------------------------------------------------------------------------------------------------------------
	
	/** Método para ordenar os sentidos de um equipamento
	 * @author Wellington da Silva criado em 01/03/2019 10:06 - atualizado em 06/01/2022 18:00	
	 * @version 1.2
	 * @since 1.0 
	 * @param lane1 primeira faixa do equipamento
	 *  
	 */	
	
	public void setDirections(String lane1) {
		
		switch(lane1) {
		
		case "N": direction1 = "N"; direction2 = "S"; break;
		case "S": direction1 = "S"; direction2 = "N"; break;
		case "L": direction1 = "L"; direction2 = "O"; break;
		case "O": direction1 = "O"; direction2 = "L"; break;
		
		
		}
	}
	
   // --------------------------------------------------------------------------------------------------------------
}