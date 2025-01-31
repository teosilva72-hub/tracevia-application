const saveSpeedBtn = e => {
    let mode = range = limit = tolerance = flash = strobe = 0;
    
    if (e.stealthMode.checked)
        mode = 1 | (4 << 4);
    else if (e.Strobe.checked && !e.Strobe.disabled)
        mode = 1 | (2 << 4);
    else 
        mode = 1

    range = [Number(e.displayRange.value  > 0 ? e.displayRange.value : 20), Number(e.displayRange2.value > 0 ? e.displayRange2.value : 200)]
    limit = Number(e.displayLimit.value > 0 ? e.displayLimit.value : 100);
    // tolerance = Number(e.displayTolerated.value > limit ? e.displayTolerated.value : limit);
    tolerance = Number.parseInt(limit * 1.05); // ever 5% more than the limit

    if (e.displayFlashOption.checked && !e.stealthMode.checked)
        flash = Number(e.displayFlash.value > 0 ? e.displayFlash.value : limit);
    if (e.displayStrobeOption.checked)
        strobe = Number(e.displayStrobe.value > 0 ? e.displayStrobe.value : limit);

    connectSPEED(`SetConfig;${e.id.value};${mode};${range[0]};${range[1]};${limit};${tolerance};${flash};${strobe}`)

    $(e).closest(".col-speed").removeClass("invert")

    return false
}

const saveAllSpeed = (e, sameKm) => {
    $(`form.speed-form${ sameKm ? `[km='${e.attributes.km.value}']` : "" }`).each((idx, form) => {
        form.stealthMode.checked = e.stealthMode.checked
        form.Strobe.checked = e.Strobe.checked
        form.displayRange.value = e.displayRange.value
        form.displayRange2.value = e.displayRange2.value
        form.displayLimit.value = e.displayLimit.value
        form.displayTolerated.value = e.displayTolerated.value
        form.displayFlash.value = e.displayFlashOption.checked ? e.displayFlash.value : 0
        form.displayStrobe.value = e.displayStrobeOption.checked ? e.displayStrobe.value : 0

        $([form.stealthMode, form.displayFlash, form.displayStrobe]).trigger("change")
    }).submit()

    return false
}

const getConfigSpeed = async () => {
    let config = await connectSPEED("GetAllConfig");
    let content = $(`.contentPage`);

    const addRotate = config => {
        let body = '<svg width="16" height="16" fill="currentColor" class="bi bi-arrow-left-right" viewBox="0 0 16 16">\
                        <path fill-rule="evenodd" d="M1 11.5a.5.5 0 0 0 .5.5h11.793l-3.147 3.146a.5.5 0 0 0 .708.708l4-4a.5.5 0 0 0 0-.708l-4-4a.5.5 0 0 0-.708.708L13.293 11H1.5a.5.5 0 0 0-.5.5zm14-7a.5.5 0 0 1-.5.5H2.707l3.147 3.146a.5.5 0 1 1-.708.708l-4-4a.5.5 0 0 1 0-.708l4-4a.5.5 0 1 1 .708.708L2.707 4H14.5a.5.5 0 0 1 .5.5z"/>\
                    </svg>';

        config.click(function() {
            $(this).closest(".col-speed").toggleClass("invert")
        }).append(body)
    }
    
    for (let c of config) {
        let speed = content.find(`#col-speed${c.Id}`);
        let form = speed.find(`.speed-form`)[0];
        let card = speed.find(`.speed-config`);
        let withOption = $([form.displayFlash, form.displayStrobe]);
        let options = $([form.displayFlashOption, form.displayStrobeOption]);
        let stealth = $(form.stealthMode);

        mode = c.mode >> 4;

        stealth.change(function() {
            if (this.checked) {
                form.Strobe.disabled = true;
                form.displayRange.disabled = true;
                form.displayRange2.disabled = true;
                form.displayFlash.disabled = true;
                form.displayFlashOption.disabled = true;
            } else {
                form.Strobe.disabled = false;
                form.displayRange.disabled = false;
                form.displayRange2.disabled = false;
                form.displayFlash.disabled = false;
                form.displayFlashOption.disabled = false;
            }
        })

        options.change(function() {
            let option = this.parentNode.nextElementSibling.nextElementSibling.firstElementChild;

            if (this.checked)
                option.disabled = false;
            else
                option.disabled = true;

            if (option.name == "displayStrobe") {
                let extra = this.parentNode.parentNode.nextElementSibling.firstElementChild

                extra.disabled = !this.checked;
            }
        });

        withOption.change(function() {
            let option = this.parentNode.previousElementSibling.previousElementSibling.firstElementChild;

            if (this.value > 0)
                option.checked = true
            else
                option.checked = false

            $(option).trigger("change")
        })

        if (mode == 4)
            form.stealthMode.checked = true;
        else if (mode == 2)
            form.Strobe.checked = true;

        form.displayRange.value = c.Ranger[0];
        form.displayRange2.value = c.Ranger[1];
        form.displayLimit.value = c.Limit;
        form.displayTolerated.value = c.Tolerance;
        form.displayFlash.value = c.Flash;
        form.displayStrobe.value = c.Strobe;

        if (c.Online)
            addRotate(card);

        stealth.trigger("change");
        withOption.trigger("change");
    }
}

$(async () => {
    await getConfigSpeed();
})