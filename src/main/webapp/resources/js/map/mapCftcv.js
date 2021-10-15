const cftvEvent = data => {
	var status = data.status;
	switch (status) {
		case "begin":
			break;
		case "complete":
			break;
		case "success":
			cftvVideo(id);
			break;
	}
}
$(function(){
	$(".d-cftv-value").click(function() {
    var value = $(this).val();
	var cod = document.getElementById("cftvId")
	cod.value = value
	console.log(value)
	cftvVideo(value)
});
})
function getInfo(){
	$("#send-cftv-id").click();
}
function cftvVideo(id){
	var id = document.getElementById("cftvId").value
	var url = document.getElementById("url-img")
		$("#dinamic-div-cftv").append(`\
		<div class="img-cftv-div bg-dark ptz${id}">\				
				<i class="fas fa-times close-cftv ptz-close${id}"></i>\
				<img src="${url.value}" class="video-img video-img${id}"/>\
				<p class="text-cftv-barra">PTZ ${id}</p>\
		</div>`)
		$(".img-cftv-div").fadeIn(3000);
		document.querySelector(".video-img"+id).src = url.value
		close(`.ptz-close${id}`, `.ptz${id}`);
		dragdrop(`.img-cftv-div`);
		$( ".video-img" ).resizable()
}
function close(cam, close){
	setTimeout(() => {	
		$(cam).click(function() {
	  		$(close).remove();
		});
	}, 1000)
}
function dragdrop(cam){
	$(cam).draggable();
}
function cftvTop(){
	$("#cftvMoveUp" ).click();
}
function cftvBottom(){
	$("#cftvMoveDown" ).click();
}
function cftvLeft(){
	$("#cftvMoveLeft" ).click();
}
function cftvRight(){
	$("#cftvMoveRight" ).click();
}
function cftvZoomIn(){
	$("#cftvMoveIn" ).click();
}
function cftvZoomOut(){
	$("#cftvMoveOut" ).click();
}
function validatePresetCall(){
	let calls = document.getElementById("window-cftv")
	let calls1 = document.getElementById("window-cftv1")
	if(calls.value == "" && calls1.value == ""){
		calls.style.border ="Solid red 2px"
		calls1.style.border = "Solid red 2px"
		$('.msg-danger').addClass('show').fadeOut(2000)
	}else{
		$('.msg-success').addClass('show').fadeOut(2000)
		calls.style.border ="Solid green 2px"
		calls1.style.border = "Solid green 2px"
		comeBack()
	}
}
function presetCftv(){
	$(".btns-cftv-number").click(function(event){
	    var digito = $(this).html();
	    $("#window-cftv").val(function(){ return $(this).val()+digito; }).attr('maxlength','3');
		event.preventDefault()
	}).click(function(event){
	    var digito = $(this).html();
	    $("#window-cftv1").val(function(){ return $(this).val()+digito; }).attr('maxlength','3');
		event.preventDefault()
	});
	comeBack()
}
function removeNumber(){
	var texto = $("#window-cftv").val();
	var texto1 = $("#window-cftv1").val();
	$("#window-cftv").val(texto.substring(0, texto.length - 1));
	$("#window-cftv1").val(texto1.substring(0, texto1.length - 1));
}
function comeBack(){
	$('#window-cftv1').val('')
	//$('#presetSet').val('')
	$('#window-cftv').val('')
}
function patrol(){
	$('[id$=updateDetails]').click();
	preventDefault();
}
function msgError(){
	$('.msg-danger').addClass('show').fadeOut(2000)
	presetCftv()
	
}
function btnPreset(){
	var x = document.querySelector(".preset-call-row")
	var y = document.querySelector(".preset-patrol-row")
	x.style.display = "block"
	y.style.displayc = "none"	
}
function btnPatrol(){
	var x = document.querySelector(".preset-call-row")
	var y = document.querySelector(".preset-patrol-row")
	x.style.display = "none"
	y.style.displayc = "block"
}