//resolução
var h = window.innerHeight |
 document.documentElement.clientHeight
 //document.body.clientHeight;
var z = Math.floor((h - 160) / 168);
var content = z * 2; /////quantidade de equipamento por página
function page(page){
	var x = $("#page").children().hide()
	for(var i = content * (page - 1); i < content * page; i++){
		x.eq(i).show()
	}
}
$(function(){
	let pag = Math.ceil($("#page").children().length / content)
	for(var i = 1; i <= pag; i++){
		$("#btnPage").append(`
		<label onclick="page(${i})" class="btn btn-dark">${i}
			<input class="btn-check" type="radio" id="btn-page${i}">
		</label>
		`)	
	}//paginação auto-incremento
	$("#btnPage").children().first().click(); //initial btn
	////////////////////////////////////////////////////////////////
	//window.requestAnimationFrame(g)
})
$(window).on('load', function () {
    $('#preloader .inner').fadeOut();
    $('#preloader').delay(250).fadeOut('slow'); 
    $('body').delay(250).css({'overflow': 'visible'});
})
$(function () {
  $('[data-toggle="popover"]').popover({
      html : true,
      trigger: 'hover',
      content: function() {
        var content = $(this).attr("data-popover-content");
        return $(content).children(".popover-body").html();
      },
      title: function() {
        var title = $(this).attr("data-popover-content");
        return $(title).children(".popover-header").html();
      }
  });

  });
//$("#sat2 [col=13] [row=5] [id=30]").addClass("online") //add info sat
/*function btnEnable(){
	$('[id$=btn-enable-user]').click();
	eventValidator();
}//atualizar tela*/