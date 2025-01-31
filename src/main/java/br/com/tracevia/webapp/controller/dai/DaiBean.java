package br.com.tracevia.webapp.controller.dai;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;

import br.com.tracevia.webapp.controller.global.ListEquipments;
import br.com.tracevia.webapp.methods.TranslationMethods;
import br.com.tracevia.webapp.model.global.DirectionModel;
import br.com.tracevia.webapp.model.global.Equipments;
import br.com.tracevia.webapp.model.global.RoadConcessionaire;
import br.com.tracevia.webapp.util.ImageUtil;
import br.com.tracevia.webapp.util.LocaleUtil;

@ManagedBean(name="daiBean")
@ViewScoped
public class DaiBean {
	LocaleUtil localeDai;
	public List<Traffic> traffics;
	public Traffic traffic;
	public static List<? extends Equipments> listDai;
	public static List<DirectionModel> listDirection;
	private String logo;
	
	static String noImage = "no-image.jpg";
	String ftpFolder = "C:\\Cameras\\DAI\\";
	
	@ManagedProperty("#{listEquipsBean}")
	ListEquipments equips;
				
	public void setEquips(ListEquipments equips) {
		this.equips = equips;
	}	
			
	public ListEquipments getEquips() {
		return equips;
	}

	public String getLogo() {
		return logo;
	}
	public Traffic getTraffic() {
		return traffic;
	}

	public void setTraffic(Traffic traffic) {
		this.traffic = traffic;
	}

	public List<Traffic> getTraffics() {
		return traffics;
	}

	@PostConstruct
	public void initalize(){
		SimpleDateFormat formattter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
					
		try {
											
			listDai = equips.getDaiList();
			traffic = new Traffic();
			
			getAllFile(formattter.format(date));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<? extends Equipments> getListDai() {
		return listDai;
	}

	public static class Traffic {
		int id, equipId;
		public String 	incident,
						date,
						channel,
						lane,
						direction,
						hour,
						name,
						km = "unknown";

		Path file;

		public Path getFile() {
			return file;
		}

		Traffic(Path path, int idx) throws IOException, ParseException {
			
			file = path;
			
			String[] pathS = path.toString().split("\\\\");
			String[] info = pathS[pathS.length-1].split("\\.")[0].split("_");
			Date date_new = new SimpleDateFormat("yyyyMMdd").parse(info[4]);
			Date hour_new = new SimpleDateFormat("HHmmssSSS").parse(info[5]);
			SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat hour_formatter = new SimpleDateFormat("HH:mm:ss:SSS");
			
			id = idx;
			incident = info[0];
			channel = info[1];
			lane = info[2];			
			date = date_formatter.format(date_new);
			hour = hour_formatter.format(hour_new);
			name = info[6];
			//direction = info[3];
											
			for (Equipments dai : listDai)
				if (dai.getNome().equals(name)) {
					km = dai.getKm();
					equipId = dai.getEquip_id();					
					direction = dai.getDirectionTo(); //direction = info[3];	
					break;
				}	  
		}
		
		Traffic() {
			incident = "";
			date = "";
			channel = "";
			lane = "";
			direction = "";
			hour = "";
			name = "";
		}
		
		public int getId() {
			return id;
		}
		
		public int getEquipId() {
			return equipId;
		}
		
		public String getKm() {
			return km;
		}
		
		public String getLane() {
			return lane;
		}

		public String getDirection() {	
			return direction;
		}
		
		public String getChannel() {
			return channel;
		}
		 
		public String getIncident() {
			TranslationMethods trad = new TranslationMethods();
			return trad.daiLabels(incident);
		}

		public String getDate() {
			return date;
		}

		public String getHour() {
			return hour;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			try {
				if (file != null) {	
					 return ImageUtil.encodeToBase64(file.toString());					
				} else {
					return ImageUtil.getInternalImagePathAndEncodeToBase64("images", "unknown", noImage);	
				}
			} catch (Exception e) {}
			return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
		}

		static List<Traffic> all_traffic(List<Path> list, int idx) throws IOException, ParseException {
			List<Traffic> new_list = new ArrayList<>();
			for (final Path path : list) {
				new_list.add(new Traffic(path, idx));
				idx++;
			}
			
			return new_list;
		}

		static List<Traffic> all_traffic(List<Path> list, int idx, String filter_name, String filter_channel, String filter_lane) throws IOException, ParseException {
			List<Traffic> new_list = new ArrayList<>();
			for (final Path path : list) {
				try { 
					Traffic traffic = new Traffic(path, idx);
					if (
							(traffic.name.contains(filter_name) ||
									filter_name.isEmpty()) && 
							(traffic.channel.contains(filter_channel) ||
									filter_channel.isEmpty()) && 
							(traffic.lane.contains(filter_lane) ||
									filter_lane.isEmpty()))
						new_list.add(traffic);
					else
						continue;
					idx++;
				} catch (Exception e) { continue; };
			}
			
			return new_list;
		}
	}
	
	public void getTrafficById(int id) {
		traffic = traffics.get(id);
	}

	public void getSpecificFile() throws IOException, ParseException {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		SimpleDateFormat date_parse = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		Date date = date_parse.parse(params.get("filterDate"));

		getSpecificFile(date);
	}
	
	public void getSpecificFile(Date date) throws IOException, ParseException {
		SimpleDateFormat date_formatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat hour_formatter = new SimpleDateFormat("HH:mm:ss:SSS");

		String hour = hour_formatter.format(date);
		List<Path> list = getAllFolders(date_formatter.format(date));

		for (final Path path : list) {
			List<Path> files = listAllFiles(path.toString());
			for (final Path file : files) {
				Traffic verify_traffic = new Traffic(file, 0);
				
				if (verify_traffic.hour.equals(hour)) {
					traffic = verify_traffic;
				}
			}
		}

	}
	
	public void getAllFile(String date) throws IOException, ParseException {
		List<Path> list = getAllFolders(date);
		List<Traffic> new_list = new ArrayList<>();
		for (final Path path : list) {
			List<Path> files = listAllFiles(path.toString());
			new_list.addAll(Traffic.all_traffic(files, new_list.size()));
		}
		
		traffics = new_list;
	}

	public void getFilesFiltered() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		SimpleDateFormat date_formatter = new SimpleDateFormat("yyyyMMdd");

		String date = params.get("dateSearch");
		String name = params.get("nameSearch");
		// String lane = params.get("laneSearch");
		// String channel = params.get("channelSearch");

		try {
			List<Path> list = getAllFolders((date.isEmpty() ? date_formatter.format(new Date()) : date.replaceAll("-", "")));
			List<Traffic> new_list = new ArrayList<>();
			for (final Path path : list) {
				List<Path> files = listAllFiles(path.toString());
				new_list.addAll(Traffic.all_traffic(files, new_list.size(), name, "", ""));
			}
			
			traffics = new_list;
		} catch (IOException e) {
			traffics = new ArrayList<>();
			
			e.printStackTrace();
		} catch (ParseException e) {
			traffics = new ArrayList<>();
			
			e.printStackTrace();
		}

	}

	public List<Path> getAllFolders(String date) {		
		List<Path> allPath = new ArrayList<>();
		String[] allEquip = listFolder(ftpFolder);
		if (allEquip != null)
			for (final String path : allEquip) {			
				try {
					allPath.addAll(listAllFiles(ftpFolder.concat(path).concat("\\Traffic Incident\\").concat(date)));
				} catch (IOException e) {}
			}
		
		return allPath;
	}

	public static String[] listFolder(String folder) {

        File file = new File(folder);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
        return directories;

    }

	public static List<Path> listAllFiles(String pathS) throws IOException {

        List<Path> result;
        Path path = Paths.get(pathS); 
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;

    }

	public void pdf() {
		
		try {
			TranslationMethods trad = new TranslationMethods();
			localeDai = new LocaleUtil();	
			localeDai.getResourceBundle(LocaleUtil.LABELS_DAI);
			RequestContext.getCurrentInstance().execute("getTr()");
			Document document = new Document();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			document.open();
			document.setPageSize(PageSize.A4);
			Paragraph pTitulo = new Paragraph(new Phrase(20F,localeDai.getStringKey("dai_report")));
			ColumnText tl = new ColumnText(writer.getDirectContent());
			Paragraph tx = new Paragraph();
			tl.setSimpleColumn(400,820,200,50);
			tx.add(pTitulo);
			tl.addElement(tx);
			tl.go();
			
			Rectangle rowPage = new Rectangle(577, 40, 10, 790); //linha da pagina 

			rowPage.setBorderColor(BaseColor.BLACK);
			rowPage.setBorderWidth(2);
			rowPage.setBorder(Rectangle.BOX);
			document.add(rowPage);
			//final da linda da pagina
			ColumnText ct = new ColumnText(writer.getDirectContent());
			ct.setSimpleColumn(700,0,200,30);
			Paragraph p = new Paragraph();
			p.add("                              Pag 1");//paragrafo Evento
			ct.addElement(p);
			ct.go();
			
			logo = ImageUtil.getInternalImagePath("images", "files", RoadConcessionaire.externalImagePath);
			
			if(!logo.equals("")) {
				Image tuxpanL = Image.getInstance(logo);
				tuxpanL.setAbsolutePosition(420, 800);
				tuxpanL.scaleAbsolute (100, 30);
				document.add(tuxpanL);
			}
			
			document.add(new Paragraph("\n\n"));
			document.add(new Paragraph(localeDai.getStringKey("camera")+": "+traffic.name));
			document.add(new Paragraph(localeDai.getStringKey("way")+": "+trad.daiLabels(traffic.direction)));
			document.add(new Paragraph(localeDai.getStringKey("track")+": "+traffic.lane));
			document.add(new Paragraph(localeDai.getStringKey("channel")+": "+traffic.channel));
			document.add(new Paragraph(localeDai.getStringKey("date")+": "+traffic.date));
			document.add(new Paragraph(localeDai.getStringKey("time")+": "+traffic.hour));
			document.add(new Paragraph(localeDai.getStringKey("incident")+": "+trad.daiLabels(traffic.incident)));
			
			File img1 = new File(traffic.file.toString());
			
			if(img1.exists()) {				
				Image imgX = Image.getInstance(img1.getPath());
				imgX.setAbsolutePosition(50, 200);
				imgX.scaleAbsolute (500, 400);
				document.add(imgX);
			}else {
				
				String blankImage = ImageUtil.getInternalImagePath("images", "unknown", noImage);
				Image imgX = Image.getInstance(blankImage);
				imgX.setAbsolutePosition(100, 280);
				imgX.scaleAbsolute (200, 150);
				document.add(imgX);
			}
			

			document.close();
			
			// DOWNLOAD

			externalContext.setResponseContentType("application/pdf");
			externalContext.setResponseHeader("Content-Disposition","attachment; filename=\"dai_"+traffic.date+".pdf\"");

			externalContext.setResponseContentLength(baos.size());

			OutputStream responseOutputStream = externalContext.getResponseOutputStream();  
			baos.writeTo(responseOutputStream);
			responseOutputStream.flush();
			responseOutputStream.close();


			facesContext.responseComplete();  

			// DOWNLOAD
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// --------------------------------------------------

}
