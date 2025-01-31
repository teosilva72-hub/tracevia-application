function getTr(){
	var table = $('#ocr-table').DataTable({
		language: {
			"search": "",
			searchPlaceholder: "Buscar"
		},		
		"select": true,
		"autoWidth": true,			  	   	
		"scrollY": "38vh",
		"paging": false,
		"bInfo" : false
	});
	$('.dataTables_length').removeClass('bs-select');
	$('.dataTables_filter').css('margin-left', '-100px')
	$('.dataTables_wrapper ').css('margin-left', '20px')
	
	$('#ocr-table tbody').on( 'click', 'tr', function () {
   var event = $(table.row( this ).data()[0]).text();
	document.getElementById("event").value = event
	var cam = $(table.row( this ).data()[2]).text();
	document.getElementById("camSet").value = cam
	if(event > 0){
		$('[id$=btnPdf]').prop('disabled', false);
		btnTable()
	}else{
		dataPicker()
		disabledBtn()
	}
	
} );
}
function loading(){
	var x = document.querySelector(".c-loader")
	x.style.display = "block"
	setTimeout(function() {
   $('.c-loader').fadeOut('fast');
}, 5000);
}
function btnTable(){
	$('[id$=btnTable]').click();
	preventDefault();
}
function dataPicker(){
	var  dtInitial = $('[id$=dateInitial]')
	//var dtFinal = $('[id$=dateFinal]')
	$('[id$=btnPdf]').prop('disabled', true);
    dtInitial.on('click', function() {
      $('[id$=dateInitial]').mask('9999-99-99')
    });
	/*dtFinal.on('click', function() {
	$('[id$=dateFinal]').mask('9999-99-99')
    });*/
	$('[id$=dateInitial]').datepicker({ 
		dateFormat: "yy-mm-dd",  
		changeYear: true,
		changeMonth: true,
	})
    $('[id$=dateFinal]').datepicker({
	dateFormat: "yy-mm-dd",
	changeYear: true,
	changeMonth: true
	})
	img_all()
}
function updateView(){
	$( "[id$=updateView]" ).click();
	preventDefault();
}
function modalHide(){
	$('[id$=popup]').modal('hide')
	$('.modal-backdrop').addClass('hide')
}
function disabledBtn(){
	$('[id$=btnPdf]').prop('disabled', true);
}

function filtro(){
	var filtro1 = document.getElementById("filtro1")
	var filtro2 = document.getElementById("filtro2")
	filtro1.addEventListener("change", function(e){
		e.preventDefault()
		filtro2.value = filtro1.value
		$('[id$=setFilter]').click();
	})
}
function validador(){
	var dtInitial = document.getElementById("dateInitial")
	var hourInitial = document.getElementById("hourInitial")
	var minuteInitial = document.getElementById("minuteInitial")
	var camera = document.getElementById("#camera")

	if(dtInitial.value == "" || hourInitial.value==""||
	minuteInitial.value == ""|| camera.value == ""){
		$('.ll').addClass('error')
		$('.ll').removeClass('ok')
		return false
	}else{
		dataPicker()
		loading()
		$('.ll').removeClass('error')
		$('.ll').addClass('ok')
		$('#modalPesquisa').modal('hide')
		$('[id$=setForm]').click();
		return true
	}
}
function setPdf(){
	$('[id$=gerarPdf]').click();
	$('#modalPdf').modal('hide')
	disabledBtn()
}
function updateDetails(){
	$('[id$=updateDetails]').click();
	preventDefault();
}
function img_all(){
	let img = $('[id$=img_all]')
	img.mouseleave(er =>{
		$('[id$=img_all_get]').val(img.val())
	})

}
$(document).ready(function () {
	disabledBtn()
	filtro()
	updateDetails()
	img_all()
});

