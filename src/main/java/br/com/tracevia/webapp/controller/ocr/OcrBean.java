package br.com.tracevia.webapp.controller.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import br.com.tracevia.webapp.dao.global.EquipmentsDAO;
import br.com.tracevia.webapp.dao.ocr.OCRDAO;
import br.com.tracevia.webapp.model.global.Equipments;
import br.com.tracevia.webapp.model.ocr.OCR;
import br.com.tracevia.webapp.util.ImageUtil;
import br.com.tracevia.webapp.util.LocaleUtil;

@ManagedBean(name="OcrRealtime")
@ViewScoped
public class OcrBean{
	
	private List<SelectItem> cams;
	private String filterCam;
	private OCR data;
	private OCRDAO dao;	
	private EquipmentsDAO equipDAO;
	List<? extends Equipments> listOcr; 
	LocaleUtil localeOCR;
	
	String cam, imageVeh, imagePlt, noImage;
					
	public List<SelectItem> getCams() {
		return cams;
	}
	
	public void setCams(List<SelectItem> cams) {
		this.cams = cams;
	}
	
	public String getFilterCam() {
		return filterCam;
	}
	
	public void setFilterCam(String filterCam) {
		this.filterCam = filterCam;
	}
	
	public List<OCR> list;

	public List<OCR> getList() {
		return list;
	}
	
	public void setList(List<OCR> list) {
		this.list = list;
	}
		
	public OCR getData() {
		return data;
	}
		
	public void setData(OCR data) {
		this.data = data;
	}
	
	public List<? extends Equipments> getListOcr() {
		return listOcr;
	}
	
	@PostConstruct
	public void initialize() {
		
		localeOCR = new LocaleUtil();	
		localeOCR.getResourceBundle(LocaleUtil.LABELS_OCR);
		
		dao = new OCRDAO();
		
	    noImage = "no-image.jpg";
		
		String unknownImage = ImageUtil.getInternalImagePath("images", "unknown", noImage);
						
		imageVeh = unknownImage;
		imagePlt = unknownImage;
		
		RequestContext.getCurrentInstance().execute("filtro()");
		
		setFilterCam("Todos");

		updateView();
			
		
		equipDAO = new EquipmentsDAO();		
		cams = new ArrayList<SelectItem>();			
		
		try {
			
			listOcr = equipDAO.equipmentSelectOptions("ocr");
			
		} catch (Exception e1) {			
			e1.printStackTrace();
		}
		
		//filtro câmera
		
		for (Equipments e : listOcr) {
			SelectItem s = new SelectItem();

			s.setValue(e.getNome());
			s.setLabel(e.getNome());
			
			cams.add(s);				
		}

		//Inicializar buscas por registros
		
		try {
			
			data = dao.lastRegister();	
			
			data.getId();
			data.getDataHour();
			data.getCam();
			data.getPlaca();
			data.setPlateImage(ImageUtil.encodeToBase64(data.getPlateImage()));
			data.setVehicleImage(ImageUtil.encodeToBase64(data.getVehicleImage()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
			
	  }
	
	public void updateView() {

		try {
			
			TimeUnit.SECONDS.sleep(3);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RequestContext.getCurrentInstance().execute("updateView()");

		
		try {

			String cam = getFilterCam();
			
			if(cam.equals("Todos")) {
				
				data = dao.lastRegister();
				
				if(data.getCam() == null) {
					
					data.setId(localeOCR.getStringKey("ocr_no_id_label"));
					data.setDataHour(localeOCR.getStringKey("ocr_no_date_label"));
					data.setCam(localeOCR.getStringKey("ocr_no_cam_label"));
					data.setPlaca(localeOCR.getStringKey("ocr_no_plate_label"));
					data.setPlateImage(ImageUtil.encodeToBase64(imagePlt));
					data.setVehicleImage(ImageUtil.encodeToBase64(imageVeh));								
					
				}else { 
												
				data.getId();
				data.getDataHour();
				data.getCam();
				data.getPlaca();
				data.setPlateImage(ImageUtil.encodeToBase64(data.getPlateImage()));
				data.setVehicleImage(ImageUtil.encodeToBase64(data.getVehicleImage()));
													
				}				
				
			} else {
				
				data = dao.searchCam(cam);
										
				if(data.getCam() == null) {
					
					data.setId(localeOCR.getStringKey("ocr_no_id_label"));
					data.setDataHour(localeOCR.getStringKey("ocr_no_date_label"));
					data.setCam(localeOCR.getStringKey("ocr_no_cam_label"));
					data.setPlaca(localeOCR.getStringKey("ocr_no_plate_label"));
					data.setPlateImage(ImageUtil.encodeToBase64(imagePlt));
					data.setVehicleImage(ImageUtil.encodeToBase64(imageVeh));
															
				}else { 
														
				data.getId();
				data.getDataHour();
				data.getCam();
				data.getPlaca();
				data.setPlateImage(ImageUtil.encodeToBase64(data.getPlateImage()));
				data.setVehicleImage(ImageUtil.encodeToBase64(data.getVehicleImage()));
											
				}			
			  }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void filtro() {
		
		setFilterCam(filterCam);
		
	}
	
	
	
}