package br.com.tracevia.webapp.dao.global;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.tracevia.webapp.cfg.NotificationAlarm;
import br.com.tracevia.webapp.log.SystemLog;
import br.com.tracevia.webapp.methods.DateTimeApplication;
import br.com.tracevia.webapp.model.global.NotificationsAlert;
import br.com.tracevia.webapp.model.global.NotificationsCount;
import br.com.tracevia.webapp.model.global.SQL_Tracevia;
import br.com.tracevia.webapp.model.global.ColumnsSql.RowResult;
import br.com.tracevia.webapp.model.global.ResultSql.MapResult;
import br.com.tracevia.webapp.util.LocaleUtil;

public class NotificationsDAO {

	SQL_Tracevia conn = new SQL_Tracevia();

	LocaleUtil locale;

	private final String errorFolder = SystemLog.ERROR.concat("notifications\\");

	public NotificationsDAO() {

		locale = new LocaleUtil();
		locale.getResourceBundle(LocaleUtil.LABELS_NOTIFICATIONS);

		SystemLog.createLogFolder(errorFolder);

	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Método para obter quantidade de notificações no sistema
	 * 
	 * @author Wellington 11/12/2021
	 * @version 1.0
	 * @since 1.0
	 * @return quantidade de notificações
	 */

	public NotificationsCount notificationsCount() {

		NotificationsCount notif = new NotificationsCount();

		String query = "SELECT IFNULL(SUM((battery_status ^ 1) + (online_status ^ 1)), 0) 'TOTAL', " +
		// "IFNULL(SUM(IF(battery_status = 0, 1, 0)), 0) 'BATTERY', " +
				"IFNULL(SUM(online_status ^ 1), 0) 'ONLINE' " +
				"FROM notifications_status";

		try {

			conn.start(1);
			conn.prepare_my(query);
			conn.prepare_ms(query.replaceAll("IFNULL", "ISNULL"));

			MapResult result = conn.executeQuery();

			if (result.hasNext()) {
				for (RowResult rs : result) {

					notif.setTotal(rs.getInt("TOTAL"));
					notif.setBattery(1);
					notif.setConnection(rs.getInt("ONLINE"));
					notif.setDoor(0);
					notif.setEnergy(0);
					notif.setEvents(0);
					notif.setPresence(0);
					notif.setTemperature(0);
				}
			}
		} catch (Exception sqle) {
			StringWriter errors = new StringWriter();
			sqle.printStackTrace(new PrintWriter(errors));

			SystemLog.logErrorSQL(errorFolder.concat("notifications_dao"), NotificationsDAO.class.getCanonicalName(),
					sqle.hashCode(), sqle.toString(), sqle.getMessage(), errors.toString());

		} finally {
			conn.close();
		}

		return notif;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Método para listar notificações no sistema
	 * 
	 * @author Wellington 11/12/2021
	 * @version 1.0
	 * @since 1.0
	 * @return lista com notificações ativas
	 * @throws ParseException
	 */
	public List<NotificationsAlert> notifications() throws ParseException {
		DateTimeApplication dt = new DateTimeApplication();
		List<NotificationsAlert> lista = new ArrayList<NotificationsAlert>();

		String query = "SELECT equip_id , equip_name, equip_type, equip_ip, equip_km, " +
		// "battery_status, battery_last_status, battery_datetime, " +
				"online_status, online_last_status, online_datetime, timer " +
				"FROM notifications_status WHERE equip_type IN ('SAT')";

		try {

			conn.start(1);
			conn.prepare(query);

			MapResult result = conn.executeQuery();

			// System.out.println(query);

			if (result.hasNext()) {
				for (RowResult rs : result) {

					// ----------------------------------
					// BATTERY STATUS
					// -------------------------------

					// LOW BATTERY
					/*
					 * if (rs.getBoolean("battery_status") == false &&
					 * rs.getBoolean("battery_last_status") == true)
					 * {
					 * not = new NotificationsAlert();
					 * 
					 * not.setEquipId(rs.getInt("equip_id"));
					 * not.setAlarmType(NotificationAlarm.LOW_BATTERY.getAlarm());
					 * not.setEquipType(rs.getString("equip_type"));
					 * not.setEquipIP(rs.getString("equip_ip"));
					 * not.setEquipName(rs.getString("equip_name"));
					 * not.setKm(rs.getString("equip_km"));
					 * not.setDateTime(rs.getString("battery_datetime").equals("") ?
					 * dt.currentDateTime() : dt.dateTimeFormat(rs.getString("battery_datetime")));
					 * not.setDescription(locale.getStringKey("notification_low_battery"));
					 * not.setViewedBgColor("dropdown-nofit-alert");
					 * not.setBatteryStatus(rs.getBoolean("battery_status"));
					 * not.setBatteryLastStatus(rs.getBoolean("battery_last_status"));
					 * not.setTimer(rs.getInt("timer"));
					 * 
					 * lista.add(not);
					 * }
					 */

					// ----------------------------------
					// ONLINE STATUS

					if (rs.getBoolean("online_status") == false && rs.getBoolean("online_last_status") == true) {

						NotificationsAlert not = new NotificationsAlert();

						not.setEquipId(rs.getInt("equip_id"));
						not.setAlarmType(NotificationAlarm.OFFLINE.getAlarm());
						not.setEquipType(rs.getString("equip_type"));
						not.setEquipIP(rs.getString("equip_ip"));
						not.setEquipName(rs.getString("equip_name"));
						not.setKm(rs.getString("equip_km"));
						not.setDateTime(rs.getString("online_datetime").equals("") ? dt.currentDateTime()
								: dt.dateTimeFormat(rs.getString("online_datetime")));
						not.setDescription(locale.getStringKey("notification_offline"));
						not.setViewedBgColor("dropdown-nofit-alert");
						not.setOnlineStatus(rs.getBoolean("online_status"));
						not.setOnlineLastStatus(rs.getBoolean("online_last_status"));
						not.setTimer(rs.getInt("timer"));

						lista.add(not);

					}
				}
			}
		} catch (Exception sqle) {
			StringWriter errors = new StringWriter();
			sqle.printStackTrace(new PrintWriter(errors));

			SystemLog.logErrorSQL(errorFolder.concat("notifications_dao"), NotificationsDAO.class.getCanonicalName(),
					sqle.hashCode(), sqle.toString(), sqle.getMessage(), errors.toString());

		} finally {
			conn.close();
		}

		return lista;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Método para atualizar notificações no sistema
	 * 
	 * @author Wellington 11/12/2021
	 * @version 1.0
	 * @since 1.0
	 * @param statusCode código da notificação
	 * @param id         id do equipamento
	 * @param ipAddress  endereço de ip
	 * @param equipType  tipo do equipamento
	 * @param datetime   data e hora do registro
	 * @param status     valor do estatus
	 * @param lastStatus valor do last status
	 * @return verdadeiro em caso de sucesso
	 * @throws Exception
	 */
	public boolean updateStatus(int statusCode, int id, String equipType, String datetime, boolean status,
			boolean lastStatus) throws Exception {

		boolean response = false;

		String update = " ";

		switch (statusCode) {

			case 1:
			case 2:
				update = "UPDATE notifications_status SET battery_status = ?, battery_last_status = ?, battery_datetime = ?  WHERE equip_id = ? AND equip_type = ?";
				break;
			case 3:
			case 4:
				update = "UPDATE notifications_status SET door_status = ?, door_last_status = ?, door_datetime = ?  WHERE equip_id = ? AND equip_type = ?";
				break;
			case 5:
			case 6:
				update = "UPDATE notifications_status SET energy_status = ?, energy_last_status = ?, energy_datetime = ?  WHERE equip_id = ? AND equip_type = ?";
				break;
			case 7:
			case 8:
				update = "UPDATE notifications_status SET online_status = ?, online_last_status = ?, online_datetime = ?  WHERE equip_id = ? AND equip_type = ?";
				break;
			case 9:
			case 10:
				update = "UPDATE notifications_status SET presence_status = ?, presence_last_status = ?, presence_datetime = ?  WHERE equip_id = ? AND equip_type = ?";
				break;
			case 11:
			case 12:
				update = "UPDATE notifications_status SET temperature_status = ?, temperature_last_status = ?, temperature_datetime = ?  WHERE equip_id = ? AND equip_type = ?";
				break;

		}

		try {

			conn.start(1);
			conn.prepare(update);

			conn.setBoolean(1, status);
			conn.setBoolean(2, lastStatus);
			conn.setString(3, datetime);
			conn.setInt(4, id);
			conn.setString(5, equipType);

			long state = conn.executeUpdate();

			if (state > 0)
				response = true;

		} catch (Exception sqle) {
			StringWriter errors = new StringWriter();
			sqle.printStackTrace(new PrintWriter(errors));

			SystemLog.logErrorSQL(errorFolder.concat("notifications_dao"), NotificationsDAO.class.getCanonicalName(),
					sqle.hashCode(), sqle.toString(), sqle.getMessage(), errors.toString());

		} finally {
			conn.close();
		}

		return response;

	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
      * Método para inserir notificações no historico do sistema
	  * @author Wellington 11/12/2021
	  * @version 1.0
	  * @since 1.0
      * @param equip_id id do equipamento
      * @param datetime data e hora do registro
      * @param type tipo do equipamento
      * @param state_id código de estado
      * @return verdadeiro em caso de sucesso
      * @throws Exception
      */
     public boolean insertNotificationHistory(int equip_id, String datetime, String type, int state_id) throws Exception {
   	 	  
   	  boolean state = false;
   		  
   	  String insert = "INSERT INTO notifications_history (equip_id, datetime_, equip_type, state_id) VALUES(?, ?, ?, ?)";
   	  
   	  try {
   		  
   			conn.start(1);
			conn.prepare(insert);
   			
   			//passando valores para os atributos BD
   			conn.setInt(1, equip_id);   			
   			conn.setString(2, datetime);
   			conn.setString(3, type);
   			conn.setInt(4, state_id);
   			
   			long res = conn.executeUpdate();
   						
   			if(res > 0) 
   				state = true;
   	      
   	     }			
   		 catch (Exception sqle)
         { 	 	        	 
            StringWriter errors = new StringWriter();
 			sqle.printStackTrace(new PrintWriter(errors));	

 			SystemLog.logErrorSQL(errorFolder.concat("notifications_dao"), NotificationsDAO.class.getCanonicalName(), sqle.hashCode(), sqle.toString(), sqle.getMessage(), errors.toString());

         }
         finally
         {
        	  conn.close();             
         }
   		
   		return state;
   	  
     }

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<NotificationsAlert> notificationStatus(String type) throws Exception {

		List<NotificationsAlert> lista = new ArrayList<NotificationsAlert>();

		String select = "SELECT equip_id, online_status, online_last_status FROM notifications_status WHERE equip_type = ? ";

		try {

			conn.start(1);
			conn.prepare(select);
			conn.setString(1, type);

			MapResult result = conn.executeQuery();

			if (result.hasNext()) {
				for (RowResult rs : result) {

					NotificationsAlert not = new NotificationsAlert();

					not.setEquipId(rs.getInt("equip_id"));
					not.setOnlineStatus(rs.getBoolean("online_status")); // STATUS
					not.setOnlineLastStatus(rs.getBoolean("online_last_status")); // STATUS

					lista.add(not);
				}
			}
		} catch (Exception sqle) {
			StringWriter errors = new StringWriter();
			sqle.printStackTrace(new PrintWriter(errors));

			SystemLog.logErrorSQL(errorFolder.concat("notifications_dao"), NotificationsDAO.class.getCanonicalName(),
					sqle.hashCode(), sqle.toString(), sqle.getMessage(), errors.toString());

		} finally {
			conn.close();
		}

		return lista;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
