package br.com.tracevia.webapp.model.global;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.tracevia.webapp.model.cftv.CFTV;
import br.com.tracevia.webapp.model.colas.Colas;
import br.com.tracevia.webapp.model.comms.COMMS;
import br.com.tracevia.webapp.model.dai.DAI;
import br.com.tracevia.webapp.model.dms.DMS;
import br.com.tracevia.webapp.model.ocr.OCR;
import br.com.tracevia.webapp.model.meteo.mto.MTO;
import br.com.tracevia.webapp.model.meteo.sv.SV;
import br.com.tracevia.webapp.model.sat.SAT;
import br.com.tracevia.webapp.model.sos.SOS;
import br.com.tracevia.webapp.model.speed.Speed;
import br.com.tracevia.webapp.model.wim.WIM;

@ManagedBean(name="listEquips")
@ViewScoped
public class ListEquipments {
	
	List<listEquips> equips;		
	LoadStartupModules load;
	
	public List<listEquips> getEquips() {
		return equips;
	}
	
	public void setEquips(List<listEquips> equips) {
		this.equips = equips;
	}
	
	public LoadStartupModules getLoad() {
		return load;
	}

	public void setLoad(LoadStartupModules load) {
		this.load = load;
	}
	
	@PostConstruct
	public void initalize() {
		
		CreateMapEquipment();
		
	}
	
	public class listEquips {
		private boolean value;
		private boolean mainMenu;
		private List<? extends Equipments> list;
		private double voltage;
		
		listEquips(boolean value, List<? extends Equipments> list, double voltage) {
			this.value = value;			
			this.list = list;
			this.voltage = voltage;
		}
		
		
		listEquips(boolean value, boolean mainMenu, List<? extends Equipments> list, double voltage) {
			this.value = value;	
			this.mainMenu = mainMenu;
			this.list = list;
			this.voltage = voltage;
		}
		
		public boolean getValue() {
			return value;
		}
							
		public boolean isMainMenu() {
			return mainMenu;
		}


		public void setMainMenu(boolean mainMenu) {
			this.mainMenu = mainMenu;
		}


		public void setValue(boolean value) {
			this.value = value;
		}


		public List<? extends Equipments> getList() {
			return list;
		}
		
		public double getVoltagem() {
			return voltage;		

	   }
	}
	
	public void CreateMapEquipment() {
		
		equips = new ArrayList<listEquips>();
		
		load = new LoadStartupModules();
		load.startupComponents();
						
		try {	
			
			try {
						
				CFTV cftv =  new CFTV();
				Colas colas = new Colas();
				COMMS comms = new COMMS();
				DAI dai = new DAI();
				DMS dms = new DMS();
				OCR ocr =  new OCR();
				MTO mto =  new MTO();
				SV sv = new SV();
				SAT sat = new SAT();
				SOS sos = new SOS();
				Speed speed =  new Speed();			
				WIM wim =  new WIM();
				
				if(load.isEn_cftv())			
					equips.add(new listEquips(load.isEn_cftv(), cftv.listEquipments("cftv"), load.getVoltage_cftv()));
					
					if(load.isEn_colas())
					equips.add(new listEquips(load.isEn_colas(), colas.listEquipments("colas"), load.getVoltage_colas()));
					
					if(load.isEn_comms())
					equips.add(new listEquips(load.isEn_comms(), comms.listEquipments("comms"), load.getVoltage_comms()));
					
					if(load.isEn_dai())
					equips.add(new listEquips(load.isEn_dai(), dai.listEquipments("dai"), load.getVoltage_dai()));
					
					if(load.isEn_pmv())
					equips.add(new listEquips(load.isEn_pmv(), dms.listDMSEquipments(), load.getVoltage_pmv()));
					
					if(load.isEn_ocr())
					equips.add(new listEquips(load.isEn_ocr(), ocr.listEquipments("ocr"), load.getVoltage_ocr()));
					
					if(load.isEn_mto())
					equips.add(new listEquips(load.isEn_mto(), load.isEn_meteo(), mto.listEquipments("mto"), load.getVoltage_mto()));
											
					if(load.isEn_sv())
					equips.add(new listEquips(load.isEn_sv(), load.isEn_meteo(), sv.listEquipments("sv"), load.getVoltage_sv()));
											
					if(load.isEn_sat())
					equips.add(new listEquips(load.isEn_sat(), sat.listSatEquipments(), load.getVoltage_sat()));
					
					if(load.isEn_sos())
					equips.add(new listEquips(load.isEn_sos(), sos.listEquipments("sos"), load.getVoltage_sos()));
					
					if(load.isEn_speed())
					equips.add(new listEquips(load.isEn_speed(), speed.listEquipments("speed"), load.getVoltage_speed()));
									
					if(load.isEn_wim())
					equips.add(new listEquips(load.isEn_wim(), wim.listEquipments("wim"), load.getVoltage_wim()));
				
					
            }catch(IndexOutOfBoundsException ex) {}
		
		}catch(Exception ex) {}	
		
	}
	  
}
