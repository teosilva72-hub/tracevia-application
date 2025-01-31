package br.com.tracevia.webapp.dao.occ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.model.SelectItem;

import br.com.tracevia.webapp.controller.global.UserAccountBean;
import br.com.tracevia.webapp.methods.DateTimeApplication;
import br.com.tracevia.webapp.methods.TranslationMethods;
import br.com.tracevia.webapp.model.occ.OccurrencesData2;
import br.com.tracevia.webapp.util.ConnectionFactory;

public class OccurrencesDAO2 {
	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;

	//CREATE OCCURRENCE
	public String cadastroOcorrencia (OccurrencesData2 data ) throws Exception {

		String occ_number = null;
		
		//script BD
		String query = "INSERT INTO occ_data(occ_number, type, origin, state_occurrence, start_date, start_hour, start_minute, end_date, end_hour, end_minute, "
				+ "cause, cause_description, kilometer, highway, local_state, direction, lane, others, local_condition, traffic, "
				+ "characteristic, interference, signaling, conductor_condition, descrTitleDescr, descrDescr, envolvTypo, envolvWhen, envolvDescr, procedureName,"
				+ " procedureDescr, trafficHour, trafficMinute, trafficKm, trafficTrackInterrupted, damageDate, damegeType, damageGravity, damageDescr, actionType,"
				+ " actionStart, actionEnd, actionDuration, actionDescr, actionStartData, actionStartHour, actionStartMinute, actionEndData, actionEndHour, actionEndMinute,"
				+ " trackStartDate, trackStartHour, trackStartMinute, trackEndData, trackEndHour, trackEndMinute, damageDescriptionInternal, causeDescrInter, descriptionInter, involvedInter,"
				+ " actionInter, damage_amount, statusAction, damageUnitySelect,  typeHour1 , typeHour2, typeHour3, typeHour4, typeHour5, typeHour6,"
				+ " num , brand , model , color , plate, tel , quant_veic, toll, folio , "
				+ "report, sinistro, kmregistro, kminicial, kmfinal, hrchega, politica, nome, idade ,"
				+ " saude, motivo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, "
				+ " ?, ?, ? ,? ,? ,? ,? ,? ,? ,? ,"
				+ "? ,? ,? ,? ,? ,? ,? ,? ,? ,?)";

		DateTimeApplication dtm = new DateTimeApplication();

		try {

			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);
			
			//passando valores para os atributos BD
			ps.setString(1, data.getData_number());
			ps.setString(2, data.getType());
			ps.setString(3, data.getOrigin());
			ps.setString(4, data.getState_occurrences());
			ps.setString(5, data.getStart_date());
			ps.setString(6, data.getStart_hour());
			ps.setString(7, data.getStart_minute());
			ps.setString(8, data.getEnd_date());
			ps.setString(9, data.getEnd_hour());
			ps.setString(10, data.getEnd_minute());
			ps.setString(11, data.getCause());
			ps.setString(12, data.getCause_description());
			ps.setString(13, data.getKilometer());
			ps.setString(14, data.getHighway());
			ps.setString(15, data.getLocal_state());
			ps.setString(16, data.getDirection());
			ps.setString(17, data.getLane());
			ps.setString(18, data.getOthers());
			ps.setString(19, data.getLocal_condition());
			ps.setString(20, data.getTraffic());
			ps.setString(21, data.getCharacteristic());
			ps.setString(22, data.getInterference());
			ps.setString(23, data.getSignaling());
			ps.setString(24, data.getConductor_condition());
			ps.setString(25, data.getDescription_title());
			ps.setString(26, data.getDescription_text());
			ps.setString(27, data.getInvolved_type());
			ps.setString(28, data.getInvolved_When());
			ps.setString(29, data.getInvolved_description());
			ps.setString(30, data.getProcedure_name());
			ps.setString(31, data.getProcedure_description());
			ps.setString(32, data.getTraffic_hours());
			ps.setString(33, data.getTraffic_minutes());
			ps.setString(34, data.getTraffic_extension());
			ps.setString(35, data.getTraffic_stopped());
			ps.setString(36, data.getDamage_date());
			ps.setString(37, data.getDamage_type_damage());
			ps.setString(38, data.getDamage_gravity());
			ps.setString(39, data.getDemage_description());
			ps.setString(40, data.getAction_type());
			ps.setString(41, data.getAction_start());
			ps.setString(42, data.getAction_end());
			ps.setString(43, data.getAction_duration());
			ps.setString(44, data.getAction_description());
			ps.setString(45, data.getActionStartData()); 
			ps.setString(46, data.getActionStartHour()); 
			ps.setString(47, data.getActionStartMinute()); 
			ps.setString(48, data.getActionEndData()); 
			ps.setString(49, data.getActionEndHour()); 
			ps.setString(50, data.getActionEndMinute()); 
			ps.setString(51, data.getTrackStartDate()); 
			ps.setString(52, data.getTrackStartHour()); 
			ps.setString(53, data.getTrackStartMinute()); 
			ps.setString(54, data.getTrackEndDate()); 
			ps.setString(55, data.getTrackEndHour()); 
			ps.setString(56, data.getTrackEndMinute()); 
			ps.setString(57, data.getDamageDescriptionInternal());
			ps.setString(58, data.getCauseDescrInter());
			ps.setString(59, data.getDescriptionInter());
			ps.setString(60, data.getInvolvedInter());
			ps.setString(61, data.getActionInter());
			ps.setString(62, data.getDamage_amount()); 
			ps.setString(63, data.getStatusAction()); 
			ps.setString(64, data.getDamageUnity());
			ps.setString(65, data.getTypeHour1());
			ps.setString(66, data.getTypeHour2());
			ps.setString(67, data.getTypeHour3());
			ps.setString(68, data.getTypeHour4());
			ps.setString(69, data.getTypeHour5());
			ps.setString(70, data.getTypeHour6());
			ps.setString(71, data.getNum());
			ps.setString(72, data.getBrand());
			ps.setString(73, data.getModel());
			ps.setString(74, data.getColor());
			ps.setString(75, data.getPlate());
			ps.setString(76, data.getTel());
			ps.setString(77, data.getQuant_vehicle());
			ps.setString(78, data.getPedagio());
			ps.setString(79, data.getFolio());
			ps.setString(80, data.getReport());
			ps.setString(81, data.getSinistro());
			ps.setString(82, data.getKmregistro());
			ps.setString(83, data.getKminicial());
			ps.setString(84, data.getKmfinal());
			ps.setString(85, data.getHrReg());
			ps.setString(86, data.getPolitica());
			ps.setString(87, data.getNome());
			ps.setString(88, data.getIdade());
			ps.setString(89, data.getSaude());
			ps.setString(90, data.getMotivo());
	
			int res = ps.executeUpdate();
			System.out.println(data.getStart_hour()+" hora" );
			System.out.println( "Teste hora") ;
			//se a vari�vel for maior do que 0, acessamos a essa fun��o
			if(res > 0) {
				
				//pengando o �ltimo valor do banco de dados
				String query2 = "Select MAX(occ_number) AS last_Id  FROM occ_data";

				ps = conn.prepareStatement(query2);

				rs = ps.executeQuery();

				if(rs != null) {
					while(rs.next()) {	
						//atribuindo para a variavel occ_number o �ltimo valor do banco de dados
						occ_number = rs.getString("last_Id");

					}
				}   	 

			}

		}catch (SQLException inserirOcorrencia){
			throw new Exception("Erro ao inserir dados: " + inserirOcorrencia);
		}finally {
			ConnectionFactory.closeConnection(conn, ps);
		}
		//retordo o valor do m�todo como o �ltimo id do banco de dados
		return occ_number;

	}
	
	public ArrayList<SelectItem> dropDownFieldValues (String field) throws Exception {	

		String query = "SELECT detail_id, value_ FROM tracevia_app.occ_details WHERE field = ? and active = 1";

		ArrayList<SelectItem> listarDropDownValue = new ArrayList<SelectItem>();
		/*System.out.println(query);*/
		TranslationMethods occTranslation = new TranslationMethods();
		
		try {

			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);
			ps.setString(1, field);

			rs = ps.executeQuery();

			if(rs != null) {
				while(rs.next()) {
					SelectItem listarDrop = new SelectItem();
					listarDrop.setValue(rs.getInt(1));
					listarDrop.setLabel(occTranslation.occurrencesTranslator(rs.getString(2)));
															
					listarDropDownValue.add(listarDrop);
				}
			}

		}finally {

			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return listarDropDownValue;
	}
	
	//m�todo editTable, esse � o m�todo onde passamos os valores para os atributos para fazer o bloqueio e desbloqueio da tabela
	public boolean editTable(boolean editTable, String name_user, int accessLevel, String id ) throws Exception {
		
		boolean status = false;
		
		//script para pegar os valores e passar valores
		String query = "UPDATE occ_data SET editTable = ?, nameUser = ?, accessLevel = ? WHERE occ_number = ?";

		DateTimeApplication dtm = new DateTimeApplication();
		
		try {
			//passando ou pegando os valores dos atributos
			conn = ConnectionFactory.connectToTraceviaApp();

			ps = conn.prepareStatement(query);

			ps.setBoolean(1, editTable);
			ps.setString(2, name_user);
			ps.setInt(3, accessLevel);
			ps.setString(4, id);

			ps.executeUpdate();


		}catch (SQLException alterarOcorrencia){

			throw new Exception("Erro ao alterar dados: " + alterarOcorrencia);

		}finally {

			ConnectionFactory.closeConnection(conn, ps);

		}		

		return status;

	}
	public boolean lastUser(String id, String lastData, String user) throws Exception {
		
		boolean status = false;
		
		String query = "UPDATE occ_data SET lastDateUser = ?, lastUser = ? WHERE occ_number = ?";
		DateTimeApplication dtm = new DateTimeApplication();
		try {
			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);
			ps.setString(1, lastData);
			ps.setString(2, user);
			ps.setString(3, id);
			
			int answer = ps.executeUpdate();

			if(answer > 0) 
				status = true;
		}catch (SQLException alterarOcorrencia){
			throw new Exception("Erro ao alterar dados: " + alterarOcorrencia);
		}finally {
			ConnectionFactory.closeConnection(conn, ps);
		}	
		
		return status;
	}
	//m�todo atualizar ocorr�ncia 
	public boolean atualizarOcorrencia(OccurrencesData2 data) throws Exception {
		// System.out.println("DATA: "+data.getData_number()+"\nType: "+data.getAction_type());
		boolean status = false;
		//script dos atributos que ser�o atualizados as informa�oes do banco de dados
		String query = "UPDATE occ_data SET type = ? , origin = ?, state_occurrence = ?, start_date = ?, start_hour = ?, " +
				"start_minute = ?, end_date = ?, end_hour = ?, end_minute = ?, cause = ?,"
				+ "cause_description = ?, kilometer = ?, highway = ?, local_state = ?, direction = ?,"
				+ "lane = ?, others = ?, local_condition = ?, traffic = ?, characteristic = ?, " 
				+ "interference = ? ,signaling = ?, conductor_condition = ?, descrTitleDescr = ?, descrDescr = ?, "
				+ "envolvTypo = ?, envolvWhen = ?, envolvDescr = ?, procedureName = ?, procedureDescr = ?, " 
				+ "trafficHour = ?, trafficMinute = ?, trafficKm = ?, trafficTrackInterrupted= ?, damageDate= ?, " 
				+ "damegeType = ?, damageGravity = ?, damageDescr = ?, actionType = ?, actionStart = ?," 
				+ "actionEnd = ?, actionDuration = ?, actionDescr = ?, actionStartData = ?, actionStartHour = ?,"
				+ "actionStartMinute = ?, actionEndData = ?, actionEndHour = ?, actionEndMinute = ?, trackStartDate = ?, "
				+ "trackStartHour = ?, trackStartMinute = ?, trackEndData = ?, trackEndHour = ?, trackEndMinute = ?,"
				+ "damageDescriptionInternal = ?, causeDescrInter = ?, descriptionInter = ?, involvedInter = ?, actionInter = ?," 
				+ "damage_amount = ?, statusAction = ?, damageUnitySelect = ? , typeHour1 = ?, typeHour2 = ?, "
				+ "typeHour3 = ?, typeHour4 = ?, typeHour5 = ?, typeHour6 = ?, num = ?, " 
				+ "brand = ?, model = ? , color = ?, plate = ?, tel = ?, "
				+ "quant_veic = ?, toll = ?, folio = ? ,report = ?, sinistro = ?,"
				+ "kmregistro= ?, 	kminicial = ?,  kmfinal= ?, politica= ?, nome = ?,"
				+ "idade = ?, saude = ?, motivo = ? WHERE occ_number = ?";

		DateTimeApplication dtm = new DateTimeApplication();
		
		try {
			//atributos que ser�o atualizados quando o m�todo for chamado
			
			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);

			ps.setString(1, data.getType());
			ps.setString(2, data.getOrigin());
			ps.setString(3, data.getState_occurrences());
			ps.setString(4, data.getStart_date());
			ps.setString(5, data.getStart_hour());
			ps.setString(6, data.getStart_minute());
			ps.setString(7, data.getEnd_date());
			ps.setString(8, data.getEnd_hour());
			ps.setString(9, data.getEnd_minute());
			ps.setString(10, data.getCause());
			ps.setString(11, data.getCause_description());
			ps.setString(12, data.getKilometer());
			ps.setString(13, data.getHighway());
			ps.setString(14, data.getLocal_state());
			ps.setString(15, data.getDirection());
			ps.setString(16, data.getLane());
			ps.setString(17, data.getOthers());
			ps.setString(18, data.getLocal_condition());
			ps.setString(19, data.getTraffic());
			ps.setString(20, data.getCharacteristic());
			ps.setString(21, data.getInterference());
			ps.setString(22, data.getSignaling());
			ps.setString(23, data.getConductor_condition());
			ps.setString(24, data.getDescription_title());
			ps.setString(25, data.getDescription_text());
			ps.setString(26, data.getInvolved_type());
			ps.setString(27, data.getInvolved_When());
			ps.setString(28, data.getInvolved_description());
			ps.setString(29, data.getProcedure_name());
			ps.setString(30, data.getProcedure_description());
			ps.setString(31, data.getTraffic_hours());
			ps.setString(32, data.getTraffic_minutes());
			ps.setString(33, data.getTraffic_extension());
			ps.setString(34, data.getTraffic_stopped());
			ps.setString(35, data.getDamage_date());
			ps.setString(36, data.getDamage_type_damage());
			ps.setString(37, data.getDamage_gravity());
			ps.setString(38, data.getDemage_description());
			ps.setString(39, data.getAction_type());
			ps.setString(40, data.getAction_start());
			ps.setString(41, data.getAction_end());
			ps.setString(42, data.getAction_duration());
			ps.setString(43, data.getAction_description());
			ps.setString(44, data.getActionStartData());
			ps.setString(45, data.getActionStartHour());
			ps.setString(46, data.getActionStartMinute());
			ps.setString(47, data.getActionEndData());
			ps.setString(48, data.getActionEndHour());
			ps.setString(49, data.getActionEndMinute());
			ps.setString(50, data.getTrackStartDate());
			ps.setString(51, data.getTrackStartHour());
			ps.setString(52, data.getTrackStartMinute());
			ps.setString(53, data.getTrackEndDate());
			ps.setString(54, data.getTrackEndHour());
			ps.setString(55, data.getTrackEndMinute());
			ps.setString(56, data.getDamageDescriptionInternal());
			ps.setString(57, data.getCauseDescrInter());
			ps.setString(58, data.getDescriptionInter());
			ps.setString(59, data.getInvolvedInter());
			ps.setString(60, data.getActionInter());
			ps.setString(61, data.getDamage_amount());
			ps.setString(62, data.getStatusAction());
			ps.setString(63, data.getDamageUnity());
			ps.setString(64, data.getTypeHour1());
			ps.setString(65, data.getTypeHour2());
			ps.setString(66, data.getTypeHour3());
			ps.setString(67, data.getTypeHour4());
			ps.setString(68, data.getTypeHour5());
			ps.setString(69, data.getTypeHour6());
			ps.setString(70, data.getNum());
			ps.setString(71, data.getBrand());
			ps.setString(72, data.getModel());
			ps.setString(73, data.getColor());
			ps.setString(74, data.getPlate());
			ps.setString(75, data.getTel());
			ps.setString(76, data.getQuant_vehicle());
			ps.setString(77, data.getPedagio());
			ps.setString(78, data.getFolio());
			ps.setString(79, data.getReport());
			ps.setString(80, data.getSinistro());
			ps.setString(81, data.getKmregistro());		
			ps.setString(82, data.getKminicial());
			ps.setString(83, data.getKmfinal());
			ps.setString(84, data.getPolitica());
			ps.setString(85, data.getNome());
			ps.setString(86, data.getIdade());
			ps.setString(87, data.getSaude());
			ps.setString(88, data.getMotivo());
			ps.setString(89, data.getData_number());
			
			System.out.println(data.getStart_hour()+" hora" );
		
			int answer = ps.executeUpdate();

			if(answer > 0) 
				status = true;

		}catch (SQLException alterarOcorrencia){
			throw new Exception("Erro ao alterar dados: " + alterarOcorrencia);
		}finally {
			ConnectionFactory.closeConnection(conn, ps);
		}		

		return status;
	}

	private char[] actionStartData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//pegando o �ltimo registro do banco de dados
	public int GetId() throws Exception{

		String sql = "SELECT max(occ_number) FROM occ_data";

		int value = 0; 
		
		//tentar
		try {
			
			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs != null) {
				while(rs.next()) {

					value = rs.getInt(1);	   			
				}
			}
		}finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		
		//retornando o valor do �ltimo a id para a variavel (value)
		return value;

	}
	
	//m�todo PDF
	public OccurrencesData2 submitPdf(int PdfGet) throws Exception {
		TranslationMethods trad = new TranslationMethods();
		OccurrencesData2 occ = new OccurrencesData2();

		//script onde pegamos os valores dos atributos
		String pdf = "SELECT  dt.value_, dt1.value_, dt2.value_, dt3.value_, dt4.value_, dt5.value_, dt6.value_, dt7.value_, dt8.value_, dt9.value_, dt10.value_, " + 
				"dt11.value_, dt12.value_, dt13.value_, dt14.value_, dt15.value_, dt16.value_, dt17.value_, dt18.value_, dt19.value_, dt20.value_ "+ 
				"FROM occ_data d "+ 
				"LEFT JOIN occ_details dt ON d.type = dt.detail_id " + 
				"LEFT JOIN occ_details dt1 ON d.origin = dt1.detail_id " + 
				"LEFT JOIN occ_details dt2 ON d.state_occurrence = dt2.detail_id " + 
				"LEFT JOIN occ_details dt3 ON d.cause = dt3.detail_id " + 
				"LEFT JOIN occ_details dt4 ON d.highway = dt4.detail_id " + 
				"LEFT JOIN occ_details dt5 ON d.local_state = dt5.detail_id " + 
				"LEFT JOIN occ_details dt6 ON d.direction = dt6.detail_id " + 
				"LEFT JOIN occ_details dt7 ON d.lane = dt7.detail_id " + 
				"LEFT JOIN occ_details dt8 ON d.local_condition = dt8.detail_id " + 
				"LEFT JOIN occ_details dt9 ON d.traffic = dt9.detail_id " + 
				"LEFT JOIN occ_details dt10 ON d.interference = dt10.detail_id " + 
				"LEFT JOIN occ_details dt11 ON d.signaling = dt11.detail_id " + 
				"LEFT JOIN occ_details dt12 ON d.conductor_condition = dt12.detail_id " + 
				"LEFT JOIN occ_details dt13 ON d.envolvTypo = dt13.detail_id " + 
				"LEFT JOIN occ_details dt14 ON d.trafficTrackInterrupted = dt14.detail_id " + 
				"LEFT JOIN occ_details dt15 ON d.damegeType = dt15.detail_id " + 
				"LEFT JOIN occ_details dt16 ON d.damageGravity = dt16.detail_id " + 
				"LEFT JOIN occ_details dt17 ON d.damageUnitySelect = dt17.detail_id " + 
				"LEFT JOIN occ_details dt18 ON d.actionType = dt18.detail_id " + 
				"LEFT JOIN occ_details dt19 ON d.statusAction = dt19.detail_id "  +
				"LEFT JOIN occ_details dt20 ON d.characteristic = dt20.detail_id " +
				"WHERE occ_number = ? ";

		try {

			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(pdf);
			ps.setInt(1, PdfGet);
			rs = ps.executeQuery();

			if(rs != null) {
				while(rs.next()) {
					//valores dos atributos dentro do banco de dados
					occ.setType(rs.getString(1));
					occ.setOrigin(rs.getString(2));
					occ.setState_occurrences(rs.getString(3));
					occ.setCause(rs.getString(4));
					occ.setHighway(trad.occurrencesTranslator(rs.getString(5)));
					occ.setLocal_state(rs.getString(6));
					occ.setDirection(trad.occurrencesTranslator(rs.getString(7)));
					occ.setLane(rs.getString(8));
					occ.setLocal_condition(trad.occurrencesTranslator(rs.getString(9)));
					occ.setTraffic(trad.occurrencesTranslator(rs.getString(10)));
					occ.setInterference(trad.occurrencesTranslator(rs.getString(11)));
					occ.setSignaling(trad.occurrencesTranslator(rs.getString(12)));
					occ.setConductor_condition(trad.occurrencesTranslator(rs.getString(13)));
					occ.setInvolved_type(trad.occurrencesTranslator(rs.getString(14)));
					occ.setTraffic_stopped(trad.occurrencesTranslator(rs.getString(15)));
					occ.setDamage_type_damage(trad.occurrencesTranslator(rs.getString(16)));
					occ.setDamage_gravity(trad.occurrencesTranslator(rs.getString(17)));
					occ.setDamageUnity(trad.occurrencesTranslator(rs.getString(18)));
					occ.setAction_type(trad.occurrencesTranslator(rs.getString(19)));
					occ.setStatusAction(trad.occurrencesTranslator(rs.getString(20)));
					occ.setCharacteristic(rs.getString(21));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}	
		//passando os valores dos atributos para dentro da var
		return occ;
	}
	public UserAccountBean user_id(int user) throws Exception {

		UserAccountBean occ = new UserAccountBean();
		String userId = "SELECT user_id from users_register ;";

		try {

			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(userId);
			ps.setInt(1, user);
			rs = ps.executeQuery();

			if(rs != null) {
				while(rs.next()) {

					occ.getUser().setUser_id(rs.getInt(1));

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}

		return occ;
	}
	public ArrayList<OccurrencesData2> listarOcorrencias() throws Exception {

		String query = "SELECT d.occ_number, dt.value_, CONCAT(start_date, '-', d.start_hour, ':', d.start_minute, d.typeHour1) 'date_time', " +
				"dt1.value_, dt2.value_ FROM occ_data d " +
				"INNER JOIN occ_details dt ON d.type = dt.detail_id " +
				"INNER JOIN occ_details dt1 ON d.cause = dt1.detail_id " +
				"INNER JOIN occ_details dt2 ON d.state_occurrence = dt2.detail_id";	

		ArrayList<OccurrencesData2> listarOcc = new ArrayList<OccurrencesData2>();
		//System.out.println(query);
		try {
			TranslationMethods occTranslation = new TranslationMethods();
			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			if(rs != null) {
				while(rs.next()) {
					OccurrencesData2 occ = new OccurrencesData2();
					occ.setData_number(String.valueOf(rs.getInt(1)));		
					occ.setType(occTranslation.listOcc(rs.getString(2)));
					occ.setDate_time(rs.getString(3));
					occ.setCause(occTranslation.listOcc(rs.getString(4)));
					occ.setState_occurrences(occTranslation.listOcc(rs.getString(5)));

					listarOcc.add(occ);
					//System.out.println(rs.getString(5));
				}
			}
		}finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return listarOcc;
	}

	public boolean updateFilePath(String path, String occNumber) throws Exception {

		boolean status = false;

		String query = "UPDATE occ_data SET local_files = ? WHERE occ_number = ?";	

		try {

			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);

			ps.setString(1, path);
			ps.setString(2, occNumber);

			int res = ps.executeUpdate();

			if(res > 0)
				status = true;

		}catch (SQLException alterarPath){
			throw new Exception("Erro ao alterar path: " + alterarPath);
		}finally {
			ConnectionFactory.closeConnection(conn, ps);
		}	

		return status;
	}

	//m�todo buscar ocorr�ncia por id
	public OccurrencesData2 buscarOcorrenciaPorId(int id) throws Exception {

		OccurrencesData2 occ = new OccurrencesData2();
		
		//Script dos atributos que as infor��es ser�o requisitadas
		String query = "SELECT occ_number, type, origin, state_occurrence, start_date, start_hour, start_minute, end_date, end_hour, end_minute, " + 
				" cause, cause_description, kilometer, highway, local_state, direction, lane, others, local_condition, traffic, " + 
				" characteristic, interference, signaling, conductor_condition, descrTitleDescr, descrDescr, envolvTypo, envolvWhen, envolvDescr, procedureName," + 
				" procedureDescr, trafficHour, trafficMinute, trafficKm, trafficTrackInterrupted, damageDate, damegeType, damageGravity, damageDescr, actionType," + 
				" actionStart, actionEnd, actionDuration, actionDescr, actionStartData, actionStartHour, actionStartMinute, actionEndData, actionEndHour, actionEndMinute, " + 
				" trackStartDate, trackStartHour, trackStartMinute, trackEndData, trackEndHour, trackEndMinute, damageDescriptionInternal, causeDescrInter, descriptionInter, involvedInter," + 
				" actionInter, damage_amount, statusAction, damageUnitySelect,  typeHour1, typeHour2, typeHour3, typeHour4, typeHour5, typeHour6," + 
				" num, brand, model, color, plate, tel, quant_veic, toll, folio, report, "
				+ "sinistro, kmregistro, kminicial, kmfinal, hrchega, politica, nome, idade, saude, motivo " +
				"FROM occ_data WHERE occ_number = ?";
		
		System.out.println("chegou dao");


		DateTimeApplication dtm = new DateTimeApplication();

		try {

			conn = ConnectionFactory.connectToTraceviaApp();
			ps = conn.prepareStatement(query);
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if(rs != null) {
				while(rs.next()) {
					//atributos onde as informa��es est�o armazenadas
					occ.setData_number(rs.getString(1));
					occ.setType(rs.getString(2));
					occ.setOrigin(rs.getString(3)); 
					occ.setState_occurrences(rs.getString(4));
					occ.setStart_date(rs.getString(5));
					occ.setStart_hour(rs.getString(6));
					occ.setStart_minute(rs.getString(7));
					occ.setEnd_date(rs.getString(8));
					occ.setEnd_hour(rs.getString(9));
					occ.setEnd_minute(rs.getString(10));
					occ.setCause(rs.getString(11));
					occ.setCause_description(rs.getString(12));
					occ.setKilometer(rs.getString(13));
					occ.setHighway(rs.getString(14));
					occ.setLocal_state(rs.getString(15));
					occ.setDirection(rs.getString(16));
					occ.setLane(rs.getString(17));
					occ.setOthers(rs.getString(18));
					occ.setLocal_condition(rs.getString(19));
					occ.setTraffic(rs.getString(20));
					occ.setCharacteristic(rs.getString(21));
					occ.setInterference(rs.getString(22));
					occ.setSignaling(rs.getString(23));
					occ.setConductor_condition(rs.getString(24));
					occ.setDescription_title(rs.getString(25));
					occ.setDescription_text(rs.getString(26));
					occ.setInvolved_type(rs.getString(27));
					occ.setInvolved_When(rs.getString(28));
					occ.setInvolved_description(rs.getString(29));
					occ.setProcedure_name(rs.getString(30));
					occ.setProcedure_description(rs.getString(31));
					occ.setTraffic_hours(rs.getString(32));
					occ.setTraffic_minutes(rs.getString(33));
					occ.setTraffic_extension(rs.getString(34));
					occ.setTraffic_stopped(rs.getString(35));
					occ.setDamage_date(rs.getString(36));
					occ.setDamage_type_damage(rs.getString(37));
					occ.setDamage_gravity(rs.getString(38));
					occ.setDemage_description(rs.getString(39));
					occ.setAction_type(rs.getString(40));
					occ.setAction_start(rs.getString(41));
					occ.setAction_end(rs.getString(42));
					occ.setAction_duration(rs.getString(43));
					occ.setAction_description(rs.getString(44));
					occ.setActionStartData(rs.getString(45));
					occ.setActionStartHour(rs.getString(46));
					occ.setActionStartMinute(rs.getString(47));
					occ.setActionEndData(rs.getString(48));
					occ.setActionEndHour(rs.getString(49));
					occ.setActionEndMinute(rs.getString(50));
					occ.setTrackStartDate(rs.getString(51));
					occ.setTrackStartHour(rs.getString(52));
					occ.setTrackStartMinute(rs.getString(53));
					occ.setTrackEndDate(rs.getString(54));
					occ.setTrackEndHour(rs.getString(55));
					occ.setTrackEndMinute(rs.getString(56));
					occ.setDamageDescriptionInternal(rs.getString(57));
					occ.setCauseDescrInter(rs.getString(58));
					occ.setDescriptionInter(rs.getString(59));
					occ.setInvolvedInter(rs.getString(60));
					occ.setActionInter(rs.getString(61));
					occ.setDamage_amount(rs.getString(62));
					occ.setStatusAction(rs.getString(63));
					occ.setDamageUnity(rs.getString(64));
					occ.setTypeHour1(rs.getString(65));
					occ.setTypeHour2(rs.getString(66));
					occ.setTypeHour3(rs.getString(67));
					occ.setTypeHour4(rs.getString(68));
					occ.setTypeHour5(rs.getString(69));
					occ.setTypeHour6(rs.getString(70));
					occ.setNum(rs.getString(71));
					occ.setBrand(rs.getString(72));
					occ.setModel(rs.getString(73));
					occ.setColor(rs.getString(74));
					occ.setPlate(rs.getString(75));
					occ.setTel(rs.getString(76));
					occ.setQuant_vehicle(rs.getString(77));
					occ.setPedagio(rs.getString(78));
					occ.setFolio(rs.getString(79));
					occ.setReport(rs.getString(80));
					occ.setSinistro(rs.getString(81));
					occ.setKmregistro(rs.getString(82));
					occ.setKminicial(rs.getString(83));
					occ.setKmfinal(rs.getString(84));
					occ.setHrchega(rs.getString(85));
					occ.setPolitica(rs.getString(86));
					occ.setNome(rs.getString(87));
					occ.setIdade(rs.getString(88));
					occ.setSaude(rs.getString(89));
					occ.setMotivo(rs.getString(90));
					System.out.println(occ.getData_number()+" rrrdao");
				}
			

			}
		}catch(SQLException sqlbuscar) {
			sqlbuscar.printStackTrace();

		}finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		//passando os valores dos atributos para a vari�vel occ
		return occ;

	}
}
