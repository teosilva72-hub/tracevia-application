package br.com.tracevia.webapp.model.wim;

import br.com.tracevia.webapp.model.global.Equipments;

public class WIM extends Equipments {

	public WIM() {
		super();		
	}

	public WIM(int equip_id, String table_id, String equip_type, String creation_date, String creation_username,
			String update_date, String update_username, String nome, String estrada, String cidade, String km,
			int linearPosX, int linearPosY, int mapPosX, int mapPosY, int mapWidth, int height, int linearWidth,
			int dlgPosX, int dlgPosY, int status, int last_status, boolean notificacao, boolean visible) {
		
		super(equip_id, table_id, equip_type, creation_date, creation_username, update_date, update_username, nome, estrada,
				cidade, km, linearPosX, linearPosY, mapPosX, mapPosY, mapWidth, height, linearWidth, dlgPosX, dlgPosY, status,
				notificacao, visible);
		// TODO Auto-generated constructor stub
	}



	
}
