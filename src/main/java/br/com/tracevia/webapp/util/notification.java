package br.com.tracevia.webapp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.primefaces.context.RequestContext;

import br.com.tracevia.webapp.dao.global.NotificationsDAO;
import br.com.tracevia.webapp.dao.occ.OccurrencesDAO;
import br.com.tracevia.webapp.methods.DateTimeApplication;
import br.com.tracevia.webapp.methods.TranslationMethods;
import br.com.tracevia.webapp.model.global.Notifications;
import br.com.tracevia.webapp.model.global.RoadConcessionaire;

@ManagedBean(name="notification")
@RequestScoped
public class notification {

	//vars
	boolean stat_battery, door, status;
	int presence;
	String temp, nameEquip, open, status1;
	int idEquip;
	//vars

	//getters and setters
		private String dateHour;
	
	public String getNameEquip() {
		return nameEquip;
	}

	public void setNameEquip(String nameEquip) {
		this.nameEquip = nameEquip;
	}

	public int getIdEquip() {
		return idEquip;
	}

	public void setIdEquip(int idEquip) {
		this.idEquip = idEquip;
	}
	//getters and setters
	private Connection conn;		
	private PreparedStatement ps;
	private ResultSet rs;

	private List<Notifications> notifications;

	LocaleUtil locale;
	DateTimeApplication dta;

	

	public void sendEmailUser() throws Exception {
teste();

		Notifications not = new Notifications();

		//Send e-mail
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();	
		date();
		EmailUtil x = new EmailUtil();
		TranslationMethods trad = new TranslationMethods();
		System.out.println("e-mail enviado");
		//variables email
		String to = "mateus.silva@tracevia.com.br"; //email
		
		String Subject = trad.notificationEmail("Subject Matter"); //Subject Matter
		
		String msgStatus = "<style body{color: black;}></style>"+
				"<b>"+trad.notificationEmail("msg")+" "+
				"<br><br></b>"+
				dateHour+"   | "+
				trad.notificationEmail("Reason")+"  "+
				trad.notificationEmail("status")+"<b>  "+
				trad.notificationEmail("Off-line")+"</b> "+"<br><br>" + 
				"<b>"+trad.notificationEmail("msg1")+"<br>"+
				trad.notificationEmail("msg2")+"</b><br><br>"+
				trad.notificationEmail("msg3")+
				"<br><br>"+trad.notificationEmail("msg4"); //contents
		//send email
		x.sendEmailHtml(to, Subject, msgStatus);

	}
	public String date() throws Exception {
		TranslationMethods x = new TranslationMethods();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		//passando os valores predeterminados para as variveis
		int day1 = LocalDateTime.now().getDayOfMonth();
		String dia = String.valueOf(day1);
		int year1 = LocalDateTime.now().getYear();
		String ano = String.valueOf(year1);
		int month1 = LocalDateTime.now().getMonthValue();
		String mes = String.valueOf(month1);
		int hora = LocalDateTime.now().getHour();
		String hour = String.valueOf(hora);
		int min = LocalDateTime.now().getMinute();
		String minute = String.valueOf(min);
		int sec = LocalDateTime.now().getSecond();
		String second = String.valueOf(sec);
		if(min < 10) minute = "0"+min;
		dateHour = x.notificationEmail("date")+" <b>"+dia+"/"+mes+"     "+" "+hora+":"+minute+"</b>";

		return dateHour;
	}
	 public List<Notifications> teste() throws Exception{
			
			List<Notifications> lista = new ArrayList<Notifications>();
				  		
		   String select = "SELECT st.equip_id , st.equip_name, st.equip_type, st.battery_status, st.battery_last_status, st.battery_viewed, st.battery_datetime,  "
		   		+ " st.door_status, st.door_last_status, st.door_viewed, st.door_datetime, st.energy_status, st.energy_last_status, st.energy_viewed, st.energy_datetime,  "
		   		+ "st.online_status, st.online_last_status, st.online_viewed, st.online_datetime, "
				+ "st.presence_status, st.presence_last_status, st.presence_viewed, st.presence_datetime, "
				+ "st.temperature_status, st.temperature_last_status, st.temperature_viewed, st.temperature_datetime  "
				+ "FROM notifications_status st ";
										
					    try {
					    				    				    	
					    conn = ConnectionFactory.useConnection(RoadConcessionaire.roadConcessionaire);
						
						ps = conn.prepareStatement(select);		
						
						rs = ps.executeQuery();
						if (rs.isBeforeFirst()) {
							
							while (rs.next()) {
								System.out.println(rs.next());
								System.out.println("testando");
								System.out.println(rs.getBoolean("st.online_status"));
								System.out.println(rs.getInt("st.equip_id"));
								Notifications not = null;
								 not.setEquipId(rs.getInt("st.equip_id"));
									      lista.add(not);								
								   
								   
								  
								   						
						       }
						    }					    		

					} catch (SQLException e) {
						e.printStackTrace();
					}finally {ConnectionFactory.closeConnection(conn, ps, rs);}

			  return lista;
		  } 	
}