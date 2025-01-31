package br.com.tracevia.webapp.cfg;

public enum ModulesEnum {
	
	CFTV("CFTV"),
	COLAS("COLAS"),
	COMMS("COMMS"),
	DAI("DAI"),
	OCR("OCR"),
	METEO("METEO"),
	OCC("OCC"),
	DMS("DMS"),
	SAT("SAT"),
	SOS("SOS"),
	SPEED("SPEED"),
	VIDEOWALL("VIDEOWALL"),
	WIM("WIM"),
	HIT("HIT");
		
	 private String module; 	 
	 
    ModulesEnum(String module) {
        this.module = module;
    }
 
    public String getModule() {
        return module;
    }	

}
