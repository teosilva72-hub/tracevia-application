function getTr(){
	var table = $('#colas-table').DataTable({
		language: {
			"search": "",
			searchPlaceholder: "Buscar"
		},		
		"select": true,
		"autoWidth": true,			  	   	
		"scrollY": "40vh",
		"paging": false,
		"bInfo" : false
	});
	$('#colas-table tbody').on( 'click', 'tr', function () {
   var event = $(table.row( this ).data()[0]).text();
	document.getElementById("event").value = event
	
	$( "[id$=getId]" ).click();
	disPdfDetail()
	
} );
}
function dataPicker(){
	var  dateY = $('#dateInitial')
	var  dateX = $('#dateFinal')
    dateY.on('click', function() {
		date.mask('9999-99-99')
    });
	dateX.on('click', function() {
		date.mask('9999-99-99')
    });
	dateY.datepicker({ 
		dateFormat: "yy-mm-dd",  
		changeYear: true,
		changeMonth: true,
	});
	dateX.datepicker({ 
		dateFormat: "yy-mm-dd",  
		changeYear: true,
		changeMonth: true,
	})
}
function validador(){
	var dateI = document.getElementById("dateInitial").value
	var hourI = document.getElementById("hourInitial").value
	var minuteI = document.getElementById("minuteInitial").value
	var dateF = document.getElementById("dateFinal").value
	var hourF = document.getElementById("hourFinal").value
	var minuteF = document.getElementById("minuteFinal").value
	dataPicker()
	disabledBtn()
	if(dateI == "" || hourI =="" || minuteI ==""
	   ||dateF=="" || hourF =="" || minuteF ==""){
		$(".ll").addClass("error")
		
	}else{
		$(".ll").addClass("ok")
		
  			$("#btnSearch").click();
		$('#modalPesquisa').modal('hide')
	}
}
function modalHide(){
	$('[id$=popup]').modal('hide')
	$('.modal-backdrop').addClass('hide')
}


function disPdfDetail(){
	$('[id$=btnPdf]').prop('disabled', false);
	$('[id$=btnDetalhes]').prop('disabled', false);
}
function disabledBtn(){
	$('[id$=btnPdf]').prop('disabled', true);
	$('[id$=btnDetalhes]').prop('disabled', true);
}
$(document).ready(function () {
	dataPicker()
	disabledBtn()
	
});