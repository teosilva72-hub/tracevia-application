package br.com.tracevia.webapp.controller.hit;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import br.com.tracevia.webapp.dao.global.EquipmentsDAO;
import br.com.tracevia.webapp.dao.hit.HitDAO;
import br.com.tracevia.webapp.model.global.EquipmentDataSource;
import br.com.tracevia.webapp.model.hit.HIT;

@ManagedBean(name="HitController")
@ViewScoped
public class ControllerHIT implements Serializable {
	
	/**
	 * SERIAL ID
	 */
	private static final long serialVersionUID = -8833810376364970972L;
	
	private HIT hit;
	private HitDAO hitDao;
	private EquipmentsDAO equipDAO;
	private ArrayList<EquipmentDataSource> listar;
	private ArrayList<HIT> hitList;
	private String msgOne, msgTwo, msgThree;
	private int id;
	private int equipId;
	
	@PostConstruct
	public void init() {
		
		hit = new HIT();
		hit.setEquip_type("Galibo");
		equipDAO = new EquipmentsDAO();
		listHit();
	
	}
		public boolean listHit() {
			boolean check = false;
			hitDao = new HitDAO();
			try {
				listar = hitDao.listHit();
				if(listar.size() > 0) 
					check = true;		
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!check) {
				//MSG SUCCESS
			}else {
				//MSG ERROR
			}
			return check;
		}
		public boolean equipHit() {
			boolean check = listHit();
			if(!check) {
				//MSG error
			}else {
				//MSG success
				hitDao = new HitDAO();
				try {
					hit = hitDao.listMsg(id);
					RequestContext.getCurrentInstance().execute(String.format("dadosTable('%s','%s','%s','%s')", hit.getMsg1(), hit.getMsg2(), hit.getMsg3(), hit.getEquip_type()));
					RequestContext.getCurrentInstance().execute(String.format("iconHit('%s')", hit.getImg()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return check;
		}
		
		
		
		public HIT getHit() {
			return hit;
		}
		public void setHit(HIT hit) {
			this.hit = hit;
		}
		public ArrayList<HIT> getHitList() {
			return hitList;
		}
		public void setHitList(ArrayList<HIT> hitList) {
			this.hitList = hitList;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public ArrayList<EquipmentDataSource> getListar() {
			return listar;
		}
		public void setListar(ArrayList<EquipmentDataSource> listar) {
			this.listar = listar;
		}
		public int getEquipId() {
			return equipId;
		}
		public void setEquipId(int equipId) {
			this.equipId = equipId;
		}
		
		public String getMsgOne() {
			return msgOne;
		}
		public void setMsgOne(String msgOne) {
			this.msgOne = msgOne;
		}
		public String getMsgTwo() {
			return msgTwo;
		}
		public void setMsgTwo(String msgTwo) {
			this.msgTwo = msgTwo;
		}
		public String getMsgThree() {
			return msgThree;
		}
		public void setMsgThree(String msgThree) {
			this.msgThree = msgThree;
		}
		
}
