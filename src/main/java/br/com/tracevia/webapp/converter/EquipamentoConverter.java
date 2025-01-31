package br.com.tracevia.webapp.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("equipConverter")
public class EquipamentoConverter implements Converter {

	  @Override  
	    public Object getAsObject(FacesContext context, UIComponent component,  
	            String value) {  
	        if (value == null || value.length() == 0) {  
	            return null;  
	        }  
	        return value;  
	    }  
	    @Override  
	    public String getAsString(FacesContext context, UIComponent component,  
	            Object value) {  
	        if (value == null) {  
	            return "";  
	        }  
	        return value.toString();  
	    }  
	}
