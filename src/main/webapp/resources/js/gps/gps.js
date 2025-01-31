let draw = $(".drawLines")
let content = $('.overflow')
let idGps = {}
let roadPoint = []
let posZoom = {}
let carCustom = {}

const connectGPS = async (request, debug) => {
	return await sendMsgStomp(request, 'GPSRequest', debug, 'request')
}

const coordToPixel = (x, y, deg, name) => {
	let d, map;
	let pad = 0;
	if (!name) {
		d = draw;
		map = draw.prev()
		pad = (draw.height() - map.height()) / 2
	} else
		map = d = $(`[name=${name}]`);
	let start = d.attr('gps_start').replaceAll(',', '.').split(';')
	let end = d.attr('gps_end').replaceAll(',', '.').split(';')
	let pos = {
		s1: Number(start[0]),
		s2: Number(start[1]),
		e1: Number(end[0]),
		e2: Number(end[1]),
		px: (d.width() - map.width()) / 2,
		ps: Number(start[2] / 1000 * map.height()),
		pe: Number(end[2] / 1000 * map.height()),
		x: Number(x),
		y: Number(y),
		rad: deg * Math.PI / 180
	}
	let distance = {
		longitude: pos.e1 - pos.s1,
		latitude: pos.e2 - pos.s2,
		x: map.width(),
		y: pos.pe - pos.ps,
		hypotenuse: () => Math.sqrt(Math.pow(distance.longitude, 2) + Math.pow(distance.latitude, 2)),
		radius: () => Math.atan(distance.latitude / distance.longitude),
		radiusOpposite: () => Math.atan(distance.longitude / distance.latitude),
		pixel: () => Math.sqrt(Math.pow(map.width(), 2) + Math.pow(distance.y, 2)),
		radiusPixel: () => Math.atan(distance.y / distance.x),
	}
	let diff = {
		longitude: pos.x - pos.s1,
		latitude: pos.y - pos.s2,
		hypotenuse: () => Math.sqrt(Math.pow(diff.longitude, 2) + Math.pow(diff.latitude, 2)),
		radius: () => Math.atan(diff.latitude / diff.longitude),
		radiusOpposite: () => Math.atan(diff.longitude / diff.latitude),
		radiusDiff: () => (invertX ? -(distance.radiusOpposite() - diff.radiusOpposite() - (invertY ? Math.sign(diff.radius()) * 90 : 0)) : distance.radius() - diff.radius()) + distance.radiusPixel(),
		direction: () => {
			let d;
			if (Math.sign(distance.longitude) >= 0)
				if (Math.sign(distance.latitude) >= 0)
					d = distance.radiusOpposite() - pos.rad
				else
					d = distance.radius() - 0.5 * Math.PI + pos.rad
			else
				if (Math.sign(distance.latitude) <= 0)
					d = distance.radiusOpposite() + Math.PI - pos.rad
				else
					d = distance.radius() - 3 * Math.PI / 2 + pos.rad
			return d;
		}
	}
	let invertX = Math.sign(distance.longitude) != Math.sign(diff.longitude)
	let invertY = Math.sign(distance.latitude) != Math.sign(diff.latitude)
	let percent = {
		longitude: diff.longitude / distance.longitude,
		latitude: diff.latitude / distance.latitude,
		hypotenuse: diff.hypotenuse() / distance.hypotenuse()
	}
	return {
		x: pos.px + percent.hypotenuse * distance.pixel() * Math.cos(diff.radiusDiff()),
		y: pad + pos.ps + percent.hypotenuse * distance.pixel() * Math.sin(diff.radiusDiff()),
		rad: distance.radiusPixel() + diff.direction()
	}
}

const selectCars = () => {
	let areaSelect = $('<div>').css({
		position: 'absolute',
		left: 0,
		top: 0,
		width: '100%',
		height: '100%',
		'z-index': 9999
	})
	content.after(areaSelect);
	let selectMap = $('<div class="mapSelect">')
	let offset = areaSelect.offset()
	let start = {
		x: 0,
		y: 0
	};

	alertToast('haga clic en punto y arrastre el puntero para seleccionar')

	const mouseDown = function(e) {
		e.stopPropagation()
		start.x = (e.pageX - offset.left);
		start.y = (e.pageY - offset.top);

		selectMap.css({
			'transform-origin': '0 0',
			position: 'absolute',
			left: start.x,
			top: start.y
		})

		areaSelect.append(selectMap)
		areaSelect.on('mousemove', mouseMove)
		$(document).on('mouseup', mouseUp)
		areaSelect.unbind('mousedown', mouseDown)
	}

	const mouseMove = function(e) {
		let width = (e.pageX - offset.left) - start.x
		let height = (e.pageY - offset.top) - start.y

		selectMap.css({
			width: Math.abs(width),
			height: Math.abs(height),
			transform: `scale(${width / Math.abs(width)}, ${height / Math.abs(height)})`
		})
	}

	const mouseUp = function(e) {
		areaSelect.unbind('mousemove', mouseMove)
		$(document).unbind('mouseup', mouseUp)
		showCarsSelected({
			start: {
				x: Number(selectMap.css('left').replaceAll(/[a-zA-Z]/g, '')),
				y: Number(selectMap.css('top').replaceAll(/[a-zA-Z]/g, ''))
			},
			end: {
				x: e.pageX - offset.left,
				y: e.pageY - offset.top
			}
		})
		areaSelect.remove()
	}

	$('.carSelection').remove()
	areaSelect.on('mousedown', mouseDown)
}

const showCarsSelected = area => {
	let offset = draw.offset()
	let cars = draw.find('[id^=carGPS]').filter(function() {
		let self = $(this)
		let x = Number(self.css('left').replaceAll(/[a-zA-Z]/g, '')) * scale + offset.left
		let y = Number(self.css('top').replaceAll(/[a-zA-Z]/g, '')) * scale + offset.top
		return Math.abs(area.start.x - area.end.x) == Math.abs(area.end.x - x) + Math.abs(area.start.x - x)
			&& Math.abs(area.start.y - area.end.y) == Math.abs(area.end.y - y) + Math.abs(area.start.y - y)
	})
	
	let toast = $('<div class="position-fixed carSelection"> \
					<div class="toast" data-autohide="false"> \
						<div class="toast-header"> \
							<!-- <img src="..." class="rounded mr-2" alt="..."> --> \
							<strong class="mr-auto"></strong> \
							<small></small> \
							<button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close"> \
								<span aria-hidden="true">&times;</span> \
							</button> \
						</div> \
						<div class="toast-body"> \
							<div class="car-selected row"> \
							</div> \
						</div> \
					</div> \
				</div>').css({
					left: '10px',
					bottom: '10px',
					'z-index': 9999
				})
	toast.find('.toast').css('width', '500px').find('.toast-body').css({
		'overflow-x': 'hidden',
		'overflow-y': 'auto',
		'max-height': '500px'
	}).prev().find('strong').text($('#CarSelect').attr('title'))
	let selected = toast.find('.car-selected')
	let struct = $('<div class="col d-flex p-1"> \
						<div class="card mx-1"> \
							<div class="card-body selected"> \
							</div> \
							<div class="card-footer"> \
							</div> \
						</div> \
					</div>')
	for (let car of cars) {
		let c = struct.clone(false)
		let carCloned = $(car).clone(false)
		c.find('.selected').append(carCloned.removeAttr('style').css({
			height: '30px'
		})).next().text(carCloned.attr('data-original-title')).css('font-size', '10px')
		selected.append(c)
	}
	content.after(toast)
	toast.on("mousedown", move_gps)
	toast.children().toast('show').on('hidden.bs.toast', function () {
		toast.remove()
	  })
}

const move_gps = function(e) {
	let elmnt = $(this)
	e.preventDefault();
	e.stopPropagation()
	// Get the mouse cursor position at startup:
	pos3 = e.clientX;
	pos4 = e.clientY;

	$(document)
		.on("mouseup", closeDragElement)

		.on("mousemove", function (e) {
			e.preventDefault();

			// Calculate the new cursor position:
			pos1 = pos3 - e.clientX;
			pos2 = pos4 - e.clientY;
			pos3 = e.clientX;
			pos4 = e.clientY;

			let pos = {
				left: Math.round(Number(elmnt.css("left").replace("px", "")) - pos1),
				bottom: Math.round(Number(elmnt.css("bottom").replace("px", "")) + pos2)
			}

			// Set the element's new position:
			elmnt.css({
				left: pos.left,
				bottom: pos.bottom
			})
		})
	
	function closeDragElement() {
		// Stop moving when mouse button is released:
		$(document)
			.off("mouseup")
			.off("mousemove")
	}
}

const openModalGPS = () => {
	$('#modalCarImage').modal()
	document.forms.selectCarType.id_car.value = id
}

const fillEquips = name => {
	let zoom = $(`[name=${name}]`)
	let radius = zoom.width() / 2
	zoom.children().filter(':not(img, .background-zoomPoint)').remove()
	$('.equip-box, .equip-info, .equip-box-sat').each((idx, item) => {
		item = $(item).clone()

		let pos = {
			longitude: item.attr('longitude'),
			latitude: item.attr('latitude'),
			x: item.attr('posx'),
			y: item.attr('posy'),
		}

		let point = coordToPixel(pos.longitude, pos.latitude, undefined, name)
		point.x += +pos.x;
		point.y += +pos.y;
		let distance = Math.sqrt(Math.pow(point.x - radius, 2) + Math.pow(point.y - radius, 2))
		let outRange = distance > radius

		if (!outRange) {
			item.css({
				left: point.x,
				top: point.y,
				transform: `translate(-50%, -50%) scale(${item.attr('scale') * 2})`
			})
			zoom.append(item)
		}
	})
}

const insertZoomPoint = () => {
	let zooms = $(`[name$=-zoomPoint]`)
	zooms.each((i, zoom) => {
		zoom = $(zoom);
		let point = $(zoom).attr('for')
		let pos = posZoom[point]
	
		let width = Math.abs(pos.end.x - pos.start.x);
	
		zoom.css({
			position: "absolute",
			left: pos.start.x + (pos.end.x - pos.start.x) / 2,
			top: pos.start.y + (pos.end.y - pos.start.y) / 2,
			transform: "translate(-50%, -50%)",
			width: width,
			height: width,
			display: "block",
			"z-index": 2
		}).off('click').click(() => zoomRoadPoint(point))
	})
}

const zoomRoadPoint = name => {
	let zoom = $(`[name=${name}]`)
	if (zoom.css("display") == "block") {
		zoom.css("display", "none")
		zoom.find('.equip-box, .equip-info, .equip-box-sat, .plaque').remove()
		return
	}
	
	let pos = posZoom[name]

	let width = zoom.css({
		position: "absolute",
		left: pos.start.x + (pos.end.x - pos.start.x) / 2,
		top: pos.start.y + (pos.end.y - pos.start.y) / 2,
		transform: "translate(-32.5%, -75.5%)",
		display: "block",
		overflow: "hidden",
		"z-index": 1
	}).children().eq(1).css("height", "100%").css(`width`)
	zoom.children().first().css({
		height: width,
		width: width
	})

	fillEquips(name)
}

const drawPointZoom = (id, name, title, coord) => {
	let pos = coordToPixel(coord.x, coord.y, coord.deg, name)
	let draw = $(`[name=${name}]`)
	let divItem = draw.find(`#carGPS${id}`)
	let radius = draw.width() / 2
	let distance = Math.sqrt(Math.pow(pos.x - radius, 2) + Math.pow(pos.y - radius, 2))
	let outRange = distance > radius
	let defaultCss = {display: 'block', position: 'absolute', transition: '1s', left: `${pos.x}px`, top: `${pos.y}px`, transform: `translate(-50%, -50%) rotate(${pos.rad}rad)${Math.cos(pos.rad) < 0 ? ' scaleY(-1)' : ''}`, "transform-origin": "50% 50%", width: "30px", "z-index": 1}
	if (coord.speed < 15)
		defaultCss.transform = 'translate(-50%, -50%)'

	if (divItem.length) {
		if (outRange)
			divItem.remove()
		else {
			divItem.css(defaultCss)
		}
	} else if (!outRange) {
		let n = $(`<img id="carGPS${id}" src="/resources/images/equips/${carCustom[id] ? carCustom[id] : carCustom['unknown']}" target="carGPS" data-bs-toggle="tooltip" data-bs-placement="top" title="${title}">`).css(defaultCss)
		draw.append(n)
		n.tooltip()
	}
}

const verifRangeRoad = (point, maxRanger) => {
	maxRanger = maxRanger || 10
	return roadPoint.reduce((a, b, idx) => {
		if (a == true)
			return true
		let map = draw.prev()
		let pad = (draw.height() - map.height()) / 2
		let pointA = {
			x: a[1] / 1000 * draw.width(),
			y: a[2] / 1000 * map.height() + pad
		}
		let pointB = {
			x: b[1] / 1000 * draw.width(),
			y: b[2] / 1000 * map.height() + pad
		}
		let triangle = {
			base: Math.sqrt(Math.pow(pointA.x - pointB.x, 2) + Math.pow(pointA.y - pointB.y, 2)),
			A: Math.sqrt(Math.pow(pointA.x - point.x, 2) + Math.pow(pointA.y - point.y, 2)),
			B: Math.sqrt(Math.pow(pointB.x - point.x, 2) + Math.pow(pointB.y - point.y, 2)),
			p: function() {
				return this.base + this.A + this.B
			},
			area: function() {
				let p = this.p() / 2
				return Math.sqrt(p * (p - this.base) * (p - this.A) * (p - this.B))
			},
			h: function() {
				return this.area() * 2 / this.base
			}
		}
		let h = triangle.h()
		let limit = Math.sqrt(Math.pow(h, 2) + Math.pow(triangle.base, 2))
		return (h < maxRanger && triangle.A < limit && triangle.B < limit) || (roadPoint.length - 1 > idx && b)
	})
}

const drawPoint = item => {
	let id = item.i || item.id;
	let name = item.nm || idGps[id]
	let divItem = draw.find(`#carGPS${id}`)
	let pos = {
		x: Number(item.d ? item.d.pos.x : item.pos.x),
		y: Number(item.d ? item.d.pos.y : item.pos.y),
		deg: Number(item.d ? item.d.pos.c : item.pos.c),
		speed: Number(item.d ? item.d.pos.s : item.pos.s),
	}
	let point = coordToPixel(pos.x, pos.y, pos.deg)
	let outRange = !verifRangeRoad(point);
	let defaultCss = {display: 'block', position: 'absolute', transition: '1s', left: `${point.x}px`, top: `${point.y}px`, transform: `translate(-50%, -50%) rotate(${point.rad}rad)${Math.cos(point.rad) < 0 ? ' scaleY(-1)' : ''}`, "transform-origin": "50% 50%", width: "42px", "z-index": 1}
	if (pos.speed < 15)
		defaultCss.transform = 'translate(-50%, -50%)'

	$('[name$=-zoomPoint]').each((i, c) => {
		drawPointZoom(id, $(c).attr('for'), name, pos)
	})

	if (divItem.length) {
		if (outRange)
			divItem.remove()
		else {
			divItem.css(defaultCss)
		}
	} else if (!outRange) {
		let n = $(`<img id="carGPS${id}" src="/resources/images/equips/${carCustom[id] ? carCustom[id] : carCustom['unknown']}" target="carGPS" data-bs-toggle="tooltip" data-bs-placement="top" title="${name}">`).css(defaultCss)
		draw.append(n)
		n.tooltip()
		n.contextmenu(ev => {
			this.id = id
			this.type = 'carGPS'
			contextMenu(ev, 'carGPS', id, false)
		})
	}
}

const callback_gps_default = response => {
	if (response.body)
    	response = JSON.parse(response.body).events.forEach(r => drawPoint(r));
}

const consumeGPS = async ({ callback_gps = callback_gps_default, debug = false } = {}) => {
	var client = await getStomp();

	var on_connect = function() {
		client.subscribe(`/exchange/gps_localization/gps_localization`, callback_gps)
	};

	var on_error = async function() {
	    console.log('error');
		await sleep(1000);

		consumeGPS({callback_gps, debug})
	};

	if (!debug)
		client.debug = null
	client.reconnect_delay = 1000;
	client.connect(rabbitmq.user, rabbitmq.pass, on_connect, on_error, '/');
}

const replyPos = () => {
	$(`[name$=-zoomPoint]`).each((i, zoom) => {
		zoom = $(zoom);
		let point = $(zoom).attr('for')
		let z = $(`[name=${$(zoom).attr('for')}]`)
		let start = z.attr('gps_start').replaceAll(',', '.').split(';')
		let end = z.attr('gps_end').replaceAll(',', '.').split(';')
		posZoom[point] = {
			start: coordToPixel(start[0], start[1]),
			end: coordToPixel(end[0], end[1])
		}
		z.css({
			height: posZoom[point].start.y * 1.245,
			width: posZoom[point].start.y * 1.245 * 0.616608843537415,
			display: 'none'
		})
	})
}

const selectCar = () => {
	let carSelect = $('.car-select div.select')

	carSelect.find('img').each((idx, elmt) => {
		let car =  $(elmt)
		let saved = car.attr('saved')

		if (idx == 0)
			carCustom['unknown'] = car.attr('src').split('/').pop()

		if (saved)
			for (const id of saved.split(',').map(s => s.trim())) {
				let old = carCustom[id]
				carCustom[id] = car.attr('src').split('/').pop()

				if (old != carCustom[id])
					$(`[id=carGPS${id}]`).remove()
			}
	})

	carSelect.click(function() {
		let car = $(this)
		
		carSelect.removeClass('selected')
		car.addClass('selected')
		document.forms.selectCarType.type_car.value = car.find('img').attr('value')
	})
}

const ChangeCarEvent = data => {
	var status = data.status;

	switch (status) {
		case "begin":
			$('#modalCarImage').modal('hide')
			break;

		case "complete":
			break;

		case "success":
			selectCar()
			connectGPS('AllUnits').then(response => {
				for (const item of response.items)
					drawPoint(item)
			})

			break;
	}
}

const initConfigPointGPS = () => {
	roadPoint.map(road => {
		let map = draw.prev()
		let pad = (draw.height() - map.height()) / 2
		let dot = {
			x: road[1] / 1000 * draw.width(),
			y: (1000 - road[2]) / 1000 * map.height() + pad
		}
		let point = $('<div>').css({
			left: `${dot.x}px`,
			bottom: `${dot.y}px`,
			width: '5px',
			height: '5px',
			transform: 'translate(-50%, -50%)',
			position: 'absolute',
			'border-radius': '50%',
			'background-color': 'red',
		}).attr('title', `${road[1]}, ${road[2]}`).tooltip()
		draw.append(point)
		point.on("mousedown", move_gps)
		point.on("mousemove", () => {
			let pos = {
				x: Number(point.css('left').replace(/[a-zA-Z]/g, '')),
				y: Number(point.css('bottom').replace(/[a-zA-Z]/g, ''))
			}
			point.attr('data-original-title', `${pos.x / draw.width() * 1000}, ${1000 - (pos.y - pad) / map.height() * 1000}`)
		})
	})
}

const initGPS = async ({ callback_gps = callback_gps_default, debug = false } = {}) => {
    $(async function () {
		replyPos()
		insertZoomPoint()
		let units = await connectGPS('AllUnits')
		let carButton = $('#CarSelect')

		for (const item of units.items) {
			idGps[item.id] = item.nm
			drawPoint(item)
		}

		consumeGPS({ callback_gps, debug });
		
		if (carButton.hasClass('d-none'))
			carButton.removeClass('d-none').click(selectCars)
	});
}

$(async function() {
	$(window).resize(async () => {
		await sleep(1)
		replyPos()
		insertZoomPoint()
		$('[target=carGPS]').css('display', 'none')
		connectGPS('AllUnits').then(response => {
			for (const item of response.items)
				drawPoint(item)
		})
	})

	let road = document.forms.roadLine;
	while (!road) {
		await sleep(1000)
		
		road = document.forms.roadLine;
	}

	for (point of road)
		roadPoint.push(point.value.split(','));

	selectCar()
})

window.initGPS = initGPS