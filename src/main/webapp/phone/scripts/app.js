/* globals SIP,user,moment, Stopwatch */

var ctxSip;

async function initPhone() {


    if (typeof(user) === 'undefined') {
        user = JSON.parse(localStorage.getItem('SIPCreds'));
    }

    while (user === undefined || user === null)
        await sleep(500)

    ctxSip = {

        config  : {
            password        : user.Pass,
            displayName     : user.Display,
            uri             : 'sip:'+user.User+'@'+user.Realm,
            wsServers       : user.WSServer,
            registerExpires : 30,
            traceSip        : true,
            log : {
                level       : 0,
            }
        },
        ringtone            : document.getElementById('ringtone'),
        ringbacktone        : document.getElementById('ringbacktone'),
        dtmfTone            : document.getElementById('dtmfTone'),

        Sessions            : [],
        callTimers          : {},
        callActiveID        : null,
        callIncomingID      : null,
        callVolume          : 1,
        Stream              : null,

        /**
         * Parses a SIP uri and returns a formatted US phone number.
         *
         * @param  {string} phone number or uri to format
         * @return {string}       formatted number
         */
        formatPhone : function(phone) {

            var num;

            if (phone.indexOf('@')) {
                num =  phone.split('@')[0];
            } else {
                num = phone;
            }

            num = num.toString().replace(/[^0-9]/g, '');

            if (num.length === 10) {
                return '(' + num.substr(0, 3) + ') ' + num.substr(3, 3) + '-' + num.substr(6,4);
            } else if (num.length === 11) {
                return '(' + num.substr(1, 3) + ') ' + num.substr(4, 3) + '-' + num.substr(7,4);
            } else {
                return num;
            }
        },

        // Sound methods
        startRingTone : function() {
            try { ctxSip.ringtone.play(); } catch (e) { }
        },

        stopRingTone : function() {
            try { ctxSip.ringtone.pause(); } catch (e) { }
        },

        startRingbackTone : function() {
            try { ctxSip.ringbacktone.play(); } catch (e) { }
        },

        stopRingbackTone : function() {
            try { ctxSip.ringbacktone.pause(); } catch (e) { }
        },

        // Genereates a rendom string to ID a call
        getUniqueID : function() {
            return Math.random().toString(36).substr(2, 9);
        },

        newSession : function(newSess) {

            newSess.displayName = newSess.remoteIdentity.displayName || newSess.remoteIdentity.uri.user;
            newSess.ctxid       = newSess.ctxid || ctxSip.getUniqueID();

            var status;

            if (!ctxSip.callActiveID && !ctxSip.callIncomingID)
                if (newSess.direction === 'incoming') {
                    status = "Incoming: "+ newSess.displayName;
                    ctxSip.startRingTone();
                } else {
                    status = "Trying: "+ newSess.displayName;
                    ctxSip.startRingbackTone();
                }

            ctxSip.logCall(newSess, 'ringing');

            ctxSip.setCallSessionStatus(status);

            // EVENT CALLBACKS

            newSess.on('progress',function(e) {
                if (e.direction === 'outgoing') {
                    ctxSip.setCallSessionStatus('Calling...');
                }
            });

            newSess.on('connecting',function(e) {
                if (e.direction === 'outgoing') {
                    ctxSip.setCallSessionStatus('Connecting...');
                }
            });

            newSess.on('accepted',function(e) {
                // If there is another active call, hold it
                if (ctxSip.callActiveID && ctxSip.callActiveID !== newSess.ctxid) {
                    ctxSip.phoneHoldButtonPressed(ctxSip.callActiveID);
                }

                ctxSip.stopRingbackTone();
                ctxSip.stopRingTone();
                ctxSip.setCallSessionStatus('Answered');
                ctxSip.logCall(newSess, 'answered');
                ctxSip.callActiveID = newSess.ctxid;
            });

            newSess.on('hold', function(e) {
                ctxSip.callActiveID = null;
                ctxSip.logCall(newSess, 'holding');
            });

            newSess.on('unhold', function(e) {
                ctxSip.logCall(newSess, 'resumed');
                ctxSip.callActiveID = newSess.ctxid;
            });

            newSess.on('muted', function(e) {
                ctxSip.Sessions[newSess.ctxid].isMuted = true;
                ctxSip.setCallSessionStatus("Muted");
            });

            newSess.on('unmuted', function(e) {
                ctxSip.Sessions[newSess.ctxid].isMuted = false;
                ctxSip.setCallSessionStatus("Answered");
            });

            newSess.on('cancel', function(e) {
                ctxSip.stopRingTone();
                ctxSip.stopRingbackTone();
                ctxSip.setCallSessionStatus("Canceled");
                if (this.direction === 'outgoing') {
                    ctxSip.callActiveID = null;
                    newSess             = null;
                    ctxSip.logCall(this, 'ended');
                }
            });

            newSess.on('bye', function(e) {
                ctxSip.stopRingTone();
                ctxSip.stopRingbackTone();
                ctxSip.setCallSessionStatus("");
                ctxSip.logCall(newSess, 'ended');
                ctxSip.callActiveID = null;
                newSess             = null;
            });

            newSess.on('failed',function(e) {
                ctxSip.stopRingTone();
                ctxSip.stopRingbackTone();
                ctxSip.setCallSessionStatus('Terminated');
            });

            newSess.on('rejected',function(e) {
                ctxSip.stopRingTone();
                ctxSip.stopRingbackTone();
                ctxSip.setCallSessionStatus('Rejected');
                ctxSip.callActiveID = null;
                ctxSip.logCall(this, 'ended');
                newSess             = null;
            });

            ctxSip.Sessions[newSess.ctxid] = newSess;

        },

        // getUser media request refused or device was not present
        getUserMediaFailure : function(e) {
            window.console.error('getUserMedia failed:', e);
            // ctxSip.setError(true, 'Media Error.', 'You must allow access to your microphone.  Check the address bar.', true);
        },

        getUserMediaSuccess : function(stream) {
             ctxSip.Stream = stream;
        },

        /**
         * sets the ui call status field
         *
         * @param {string} status
         */
        setCallSessionStatus : function(status) {
            if (status)
                showStatesCallbox('open')
            else
                showStatesCallbox('close')
            $('#txtCallStatus').html(status);
        },

        /**
         * sets the ui connection status field
         *
         * @param {string} status
         */
        setStatus : function(status) {
            $("#txtRegStatus").html('<i class="fa fa-signal"></i> '+status);
        },

        /**
         * logs a call to localstorage
         *
         * @param  {object} session
         * @param  {string} status Enum 'ringing', 'answered', 'ended', 'holding', 'resumed'
         */
        logCall : function(session, status) {

            var log = {
                    clid : session.displayName || "Desconhecido",
                    uri  : (session.remoteIdentity.uri || "Desconhecido").toString(),
                    id   : session.ctxid,
                    time : new Date().getTime()
                },
                calllog = JSON.parse(localStorage.getItem('sipCalls'));

            if (!calllog) { calllog = {}; }

            if (!calllog.hasOwnProperty(session.ctxid)) {
                calllog[log.id] = {
                    id    : log.id,
                    clid  : log.clid,
                    uri   : log.uri,
                    start : log.time,
                    flow  : session.direction,
                    bdid  : session.EquipmentID
                };
            }

            calllog[log.id].owner = session.owner ? session.owner : calllog[log.id].owner

            if (status === 'ended') {
                calllog[log.id].stop = log.time;
            }

            if (status === 'ended' && calllog[log.id].status === 'ringing') {
                calllog[log.id].status = 'missed';
            } else {
                calllog[log.id].status = status;
            }

            localStorage.setItem('sipCalls', JSON.stringify(calllog));
            ctxSip.logShow();
        },

        /**
         * adds a ui item to the call log
         *
         * @param  {object} item log item
         */
        logItem : function(item) {

            var callActive = (item.status !== 'ended' && item.status !== 'missed'),
                callLength = (item.status !== 'ended')? '<span id="'+item.id+'"></span>': moment.duration(item.stop - item.start).humanize(),
                callClass  = '',
                callIcon,
                i;

            switch (item.status) {
                case 'ringing'  :
                    callClass = 'list-group-item-success';
                    callIcon  = 'fa-bell';
                    break;

                case 'missed'   :
                    callClass = 'list-group-item-danger';
                    if (item.flow === "incoming") { callIcon = 'fa-chevron-left'; }
                    if (item.flow === "outgoing") { callIcon = 'fa-chevron-right'; }
                    break;

                case 'holding'  :
                    callClass = 'list-group-item-warning';
                    callIcon  = 'fa-pause';
                    break;

                case 'answered' :
                case 'resumed'  :
                    callClass = 'list-group-item-info';
                    callIcon  = 'fa-phone-square';
                    break;

                case 'ended'  :
                    if (item.flow === "incoming") { callIcon = 'fa-chevron-left'; }
                    if (item.flow === "outgoing") { callIcon = 'fa-chevron-right'; }
                    break;
            }


            i  = '<div class="list-group-item sip-logitem clearfix '+callClass+'" data-uri="'+item.uri+'" data-sessionid="'+item.id+'" title="Double Click to Call">';
            i += '<div class="clearfix"><div class="float-left">';
            i += '<i class="fa fa-fw '+callIcon+' fa-fw"></i> <strong>'+ctxSip.formatPhone(item.uri)+'</strong><br><small>'+moment(item.start).format('MM/DD hh:mm:ss a')+'</small>';
            i += '</div>';
            i += '<div class="float-right text-right"><em>'+item.clid+'</em><br>' + callLength+'</div></div>';

            if (callActive) {
                i += '<div class="btn-group btn-group-xs float-right">';
                if (item.status === 'ringing' && item.flow === 'incoming') {
                    i += '<button class="btn btn-xs btn-success btnCall" title="Call"><i class="fa fa-phone"></i></button>';
                } else if (item.owner) {
                    i += '<button class="btn btn-xs btn-primary btnHoldResume" title="Hold"><i class="fa fa-pause"></i></button>';
                    // i += '<button class="btn btn-xs btn-info btnTransfer" title="Transfer"><i class="fa fa-random"></i></button>';
                    i += '<button class="btn btn-xs btn-warning btnMute" title="Mute"><i class="fa fa-fw fa-microphone"></i></button>';
                    i += '<button class="btn btn-xs btn-danger btnHangUp" title="Hangup"><i class="fa fa-stop"></i></button>';
                }
                i += '</div>';
            }
            i += '</div>';

            $('#sip-logitems').append(i);


            // Start call timer on answer
            if (item.status === 'answered') {
                var tEle = document.getElementById(item.id);
                ctxSip.callTimers[item.id] = new Stopwatch(tEle);
                ctxSip.callTimers[item.id].start();
            }

            if (callActive && item.status !== 'ringing') {
                ctxSip.callTimers[item.id].start({startTime : item.start});
            }

            $('#sip-logitems').scrollTop(0);
        },

        /**
         * updates the call log ui
         */
        logShow : function() {

            var calllog = JSON.parse(localStorage.getItem('sipCalls')),
                x       = [];

            if (calllog !== null) {

                $('#sip-splash').addClass('hide'); // ! Remover essa linha futuramente
                $('#sip-log').removeClass('hide'); // ! Remover essa linha futuramente

                // empty existing logs
                $('#sip-logitems').empty();

                for (const log of Object.entries(calllog))
                    if (log[1].owner && (log[1].status !== 'ended' && log[1].status !== 'missed') && log[1].session == "finish") {
                        ctxSip.sipHangUp(log[0], log[1].bdid)

                        return
                    }

                // JS doesn't guarantee property order so
                // create an array with the start time as
                // the key and sort by that.

                // Add start time to array
                $.each(calllog, function(k,v) {
                    x.push(v);
                });

                // sort descending
                x.sort(function(a, b) {
                    return b.start - a.start;
                });

                $.each(x, function(k, v) {
                    ctxSip.logItem(v);
                });

            } else {
                $('#sip-logitems').html('<p class="text-muted text-center mt-4">Sem chamadas recentes.</p>')

                $('#sip-splash').removeClass('hide'); // ! Remover essa linha futuramente
                $('#sip-log').addClass('hide'); // ! Remover essa linha futuramente
            }
        },

        /**
         * removes log items from localstorage and updates the UI
         */
        logClear : function() {
            if (!ctxSip.callActiveID && !ctxSip.callIncomingID) {
                localStorage.removeItem('sipCalls');
                ctxSip.logShow();
            }
        },

        sipCall : function(target) {

            try {
                var s = ctxSip.phone.invite(target, {
                    media : {
                        stream      : ctxSip.Stream,
                        constraints : { audio : true, video : false },
                        render      : {
                            remote : $('#audioRemote').get()[0]
                        },
                        RTCConstraints : { "optional": [{ 'DtlsSrtpKeyAgreement': 'true'} ]}
                    }
                });
                s.direction = 'outgoing';
                ctxSip.newSession(s);

            } catch(e) {
                throw(e);
            }
        },

        sipTransfer : function(sessionid) {

            var s      = ctxSip.Sessions[sessionid],
                target = window.prompt('Enter destination number', '');

            ctxSip.setCallSessionStatus('<i>Transfering the call...</i>');
            s.refer(target);
        },

        sipHangUp : function(sessionid, id) {

            var s = ctxSip.Sessions[sessionid];
            // s.terminate();
            if (!s && id) {
                ctxSip.logCall({ctxid: sessionid, remoteIdentity: {}}, 'ended')
                connectSOS(`GetAllActiveCalls`).then(response => {
                    for (const r of response)
                        if(r.UserID == loginAccount.ID && r.EquipmentID == id) {
                            connectSOS(`TerminateCall;${loginAccount.ID};${id}`)
                            break;
                        }
                });

                return;
            } else if (s.service) {
                s.call.bye();
            } else if (s.startTime) {
                // s.bye();
            } else if (s.reject) {
                // s.reject();
            } else if (s.cancel) {
                // s.cancel();
            }

        },

        sipSendDTMF : function(digit) {

            try { ctxSip.dtmfTone.play(); } catch(e) { }

            var a = ctxSip.callActiveID;
            if (a) {
                var s = ctxSip.Sessions[a];
                s.dtmf(digit);
            }
        },

        phoneCallButtonPressed : function(sessionid) {

            var s      = ctxSip.Sessions[sessionid];
                // target = $("#numDisplay").val();

            if (!ctxSip.callActiveID && !ctxSip.callIncomingID)
                if (!s) {

                    // $("#numDisplay").val("");
                    // ctxSip.sipCall(target);

                } else if (s.service) {
                    ctxSip.callIncomingID = sessionid;
                    connectSOS('GetAllActiveCalls').then(response => {
                        for (const r of response)
                            if (r.EquipmentID === s.EquipmentID && r.CallStateID === 4 && !r.AnsweredDate) {
                                connectSOS(`AnswerCall;${loginAccount.ID};${s.EquipmentID}`).then(response => {
                                    if (response.UserID != loginAccount.ID)
                                        ctxSip.callIncomingID = null;
                                })
                                
                                return
                            }

                        ctxSip.callIncomingID = null;
                    })
                } else if (s.accept && !s.startTime) {

                    // s.accept({
                    //     media : {
                    //         stream      : ctxSip.Stream,
                    //         constraints : { audio : true, video : false },
                    //         render      : {
                    //             remote : $('#audioRemote').get()[0]
                    //         },
                    //         RTCConstraints : { "optional": [{ 'DtlsSrtpKeyAgreement': 'true'} ]}
                    //     }
                    // });
                }
        },

        phoneMuteButtonPressed : function (sessionid) {

            var s = ctxSip.Sessions[sessionid];

            if (s.service)
                if (!s.isMuted) {
                    s.call.mute();
                    s.isMuted = true
                    ctxSip.setCallSessionStatus("Muted");
                } else {
                    s.call.unmute();
                    s.isMuted = false
                    ctxSip.setCallSessionStatus("Answered");
                }
        },

        phoneHoldButtonPressed : function(sessionid) {

            var s = ctxSip.Sessions[sessionid];
            let paused;

            if (s.service) {
                if (s.call.isOnHold().local === true && !ctxSip.callActiveID) {
                    s.call.unhold();
                    paused = false;
                } else {
                    s.call.hold();
                    paused = true;
                }

                connectSOS(`GetAllActiveCalls`).then(response => {
                    for (const r of response)
                        if(r.UserID == loginAccount.ID && r.EquipmentID == id) {
                            connectSOS(`HoldCall;${loginAccount.ID};${s.EquipmentID};${paused}`)
                            break;
                        }
                });
            }
        },


        setError : function(err, title, msg, closable) { // TODO: Corrigir a modal ou talvez remover completamente a mesma

            // Show modal if err = true
            if (err === true) {
                $("#mdlError p").html(msg);
                $("#mdlError").modal('show');

                if (closable) {
                    var b = '<button type="button" class="close" data-dismiss="modal">&times;</button>';
                    $("#mdlError .modal-header").find('button').remove();
                    $("#mdlError .modal-header").prepend(b);
                    $("#mdlError .modal-title").html(title);
                    $("#mdlError").modal({ keyboard : true });
                } else {
                    $("#mdlError .modal-header").find('button').remove();
                    $("#mdlError .modal-title").html(title);
                    $("#mdlError").modal({ keyboard : false });
                }
                $('#numDisplay').prop('disabled', 'disabled');
            } else {
                $('#numDisplay').removeProp('disabled');
                $("#mdlError").modal('hide');
            }
        },

        /**
         * Tests for a capable browser, return bool, and shows an
         * error modal on fail.
         */
        hasWebRTC : function() {

            if (navigator.webkitGetUserMedia) {
                return true;
            } else if (navigator.mozGetUserMedia) {
                return true;
            } else if (navigator.getUserMedia) {
                return true;
            } else {
                // ctxSip.setError(true, 'Unsupported Browser.', 'Your browser does not support the features required for this phone.');
                window.console.error("WebRTC support not found");
                return false;
            }
        }
    };




    // Throw an error if the browser can't hack it.
    if (!ctxSip.hasWebRTC()) {
        return true;
    }

    ctxSip.phone = new SIP.UA(ctxSip.config);

    ctxSip.phone.on('connected', function(e) {
        ctxSip.setStatus("Connected");
    });

    ctxSip.phone.on('disconnected', function(e) {
        ctxSip.setStatus("Disconnected");

        // disable phone
        // ctxSip.setError(true, 'Websocket Disconnected.', 'An Error occurred connecting to the websocket.');

        // remove existing sessions
        $("#sessions > .session").each(function(i, session) {
            ctxSip.removeSession(session, 500);
        });
    });

    ctxSip.phone.on('registered', function(e) {

        // var closeEditorWarning = function() {
        //     return 'If you close this window, you will not be able to make or receive calls from your browser.';
        // };

        var closePhone = function() {
            // stop the phone on unload
            localStorage.removeItem('ctxPhone');
            let log = JSON.parse(localStorage.getItem('sipCalls'))
            for (const l of Object.entries(log)) {
                log[l[0]].session = "finish"
            }
            localStorage.setItem('sipCalls', JSON.stringify(log))
            ctxSip.phone.stop();
        };

        // window.onbeforeunload = closeEditorWarning;
        window.onunload       = closePhone;

        // This key is set to prevent multiple windows.
        localStorage.setItem('ctxPhone', 'true');

        $("#mldError").modal('hide');
        ctxSip.setStatus("Ready");

        // Get the userMedia and cache the stream
        if (SIP.WebRTC.isSupported()) {
            SIP.WebRTC.getUserMedia({ audio : true, video : false }, ctxSip.getUserMediaSuccess, ctxSip.getUserMediaFailure);
        }
    });

    ctxSip.phone.on('registrationFailed', function(e) {
        // ctxSip.setError(true, 'Registration Error.', 'An Error occurred registering your phone. Check your settings.');
        ctxSip.setStatus("Error: Registration Failed");
    });

    ctxSip.phone.on('unregistered', function(e) {
        // ctxSip.setError(true, 'Registration Error.', 'An Error occurred registering your phone. Check your settings.');
        ctxSip.setStatus("Error: Registration Failed");
    });

    ctxSip.phone.on('invite', function (incomingSession) {
        if (ctxSip.callIncomingID) {
            ctxSip.callActiveID = ctxSip.callIncomingID;
            ctxSip.callIncomingID = null;
        } else return;

        var s = incomingSession;

        var closeEditorWarning = function() {
            return 'If you close this window, you will not be able to make or receive calls from your browser.';
        };

        // s.direction = 'incoming';
        // ctxSip.newSession(s);

        let session = ctxSip.Sessions[ctxSip.callActiveID]
        session.owner = true;

        window.onbeforeunload = closeEditorWarning;

        s.accept({
            media : {
                stream      : ctxSip.Stream,
                constraints : { audio : true, video : false },
                render      : {
                    remote : $('#audioRemote').get()[0]
                },
                RTCConstraints : { "optional": [{ 'DtlsSrtpKeyAgreement': 'true'} ]}
            }
        });

        s.on('bye', function() {
            ctxSip.logCall(session, 'ended')
            ctxSip.callActiveID = null;
            connectSOS(`GetAllActiveCalls`).then(response => {
                for (const r of response)
                    if(r.UserID == loginAccount.ID && r.EquipmentID == session.EquipmentID) {
                        connectSOS(`TerminateCall;${loginAccount.ID};${session.EquipmentID}`)
                        break;
                    }
            });

            window.onbeforeunload = null;
        });

        s.on('hold', function(e) {
            ctxSip.callActiveID = null;
            ctxSip.logCall(session, 'holding');
        });

        s.on('unhold', function(e) {
            ctxSip.logCall(session, 'resumed');
            ctxSip.callActiveID = session.ctxid;
        });

        ctxSip.logCall(session, "answered")
        ctxSip.Sessions[ctxSip.callActiveID].call = s
    });

    // // Auto-focus number input on backspace.
    // $('#sipClient').keydown(function(event) {
    //     if (event.which === 8) {
    //         $('#numDisplay').focus();
    //     }
    // });

    // $('#numDisplay').keypress(function(e) {
    //     // Enter pressed? so Dial.
    //     if (e.which === 13) {
    //         ctxSip.phoneCallButtonPressed();
    //     }
    // });

    // $('.digit').click(function(event) {
    //     event.preventDefault();
    //     var num = $('#numDisplay').val(),
    //         dig = $(this).data('digit');

    //     $('#numDisplay').val(num+dig);

    //     ctxSip.sipSendDTMF(dig);
    //     return false;
    // });

    $('#phoneUI').delegate('.btnCall', 'click', function(event) {
        // ctxSip.phoneCallButtonPressed();
        // // to close the dropdown
        // return true;
    });

    $('.sipLogClear').click(function(event) {
        event.preventDefault();
        ctxSip.logClear();
    });

    

    $('#sip-logitems').delegate('.sip-logitem .btnCall', 'click', function(event) {
        var sessionid = $(this).closest('.sip-logitem').data('sessionid');
        ctxSip.phoneCallButtonPressed(sessionid);
        return false;
    });

    $('#sip-logitems').delegate('.sip-logitem .btnHoldResume', 'click', function(event) {
        var sessionid = $(this).closest('.sip-logitem').data('sessionid');
        ctxSip.phoneHoldButtonPressed(sessionid);
        return false;
    });

    $('#sip-logitems').delegate('.sip-logitem .btnHangUp', 'click', function(event) {
        var sessionid = $(this).closest('.sip-logitem').data('sessionid');
        ctxSip.sipHangUp(sessionid);
        return false;
    });

    // $('#sip-logitems').delegate('.sip-logitem .btnTransfer', 'click', function(event) {
    //     var sessionid = $(this).closest('.sip-logitem').data('sessionid');
    //     ctxSip.sipTransfer(sessionid);
    //     return false;
    // });

    $('#sip-logitems').delegate('.sip-logitem .btnMute', 'click', function(event) {
        var sessionid = $(this).closest('.sip-logitem').data('sessionid');
        ctxSip.phoneMuteButtonPressed(sessionid);
        return false;
    });

    // $('#sip-logitems').delegate('.sip-logitem', 'dblclick', function(event) {
    //     event.preventDefault();

    //     var uri = $(this).data('uri');
    //     $('#numDisplay').val(uri);
    //     ctxSip.phoneCallButtonPressed();
    // });
    
    $('#sldVolume').on('input', function() {

        var v      = $(this).val() / 100,
            // player = $('audio').get()[0],
            btn    = $('#btnVol'),
            icon   = btn.find('svg'),
            active = ctxSip.callActiveID;

        // Set the object and media stream volumes
        if (ctxSip.Sessions[active]) {
            // ctxSip.Sessions[active].call.player.volume = v;
            ctxSip.callVolume                     = v;
        }

        // Set the others
        $('audio').each(function() {
            $(this).get()[0].volume = v;
        });

        if (v < 0.1) {
            btn.removeClass(function (index, css) {
                   return (css.match(/(^|\s)btn\S+/g) || []).join(' ');
                })
                .addClass('btn btn-sm btn-danger');
            icon.hide().eq(2).show()
        } else if (v < 0.8) {
            btn.removeClass(function (index, css) {
                   return (css.match(/(^|\s)btn\S+/g) || []).join(' ');
               }).addClass('btn btn-sm btn-info');
            icon.hide().eq(1).show()
        } else {
            btn.removeClass(function (index, css) {
                   return (css.match(/(^|\s)btn\S+/g) || []).join(' ');
               }).addClass('btn btn-sm btn-primary');
            icon.hide().eq(0).show()
        }
        return false;
    });

    // Hide the spalsh after 3 secs.
    setTimeout(function() {
        ctxSip.logShow();
    }, 3000);

    // receiver rabbitmq
    consume({
        callback_states: null,
        callback_alarms: null,
        callback_calls: message => {
            let response = JSON.parse(message.body);

            getEquipFromID(response.EquipmentID).then(equip => {
                let direction = 'incoming';
                let status;

                switch(response.CallStateID) {
                    case 1:
                        status = "answered";
                        ctxSip.stopRingbackTone();
                        ctxSip.stopRingTone();
                        ctxSip.setCallSessionStatus('Answered');
                        break

                    case 2:
                        status = "holding";
                        break

                    case 3:
                        status = "ended";
                        ctxSip.stopRingTone();
                        ctxSip.stopRingbackTone();
                        ctxSip.setCallSessionStatus('');
                        break

                    case 4:
                        status = "ringing";
                        break

                    case 5:
                        status = "ended";
                        ctxSip.stopRingTone();
                        ctxSip.stopRingbackTone();
                        ctxSip.setCallSessionStatus('Rejected');
                        break
                }

                response.displayName = equip.MasterName;
                response.direction = direction;
                response.service = true;
                response.ctxid = `${Date.parse(response.StartDate).toString().substr(0, 10)}id${equip.ID}`;
                response.remoteIdentity = {
                    uri: `${equip.MasterSIP}@${equip.IP}`,
                    displayName: equip.MasterName
                }

                response.on = () => {}
    
                if (status == "ringing") {
                    ctxSip.newSession(response)
                } else {
                    ctxSip.logCall(response, status)
                }
            });
        }
    })

    /**
     * Stopwatch object used for call timers
     *
     * @param {dom element} elem
     * @param {[object]} options
     */
    var Stopwatch = function(elem, options) {

        // private functions
        function createTimer() {
            return document.createElement("span");
        }

        var timer = createTimer(),
            offset,
            clock,
            interval;

        // default options
        options           = options || {};
        options.delay     = options.delay || 1000;
        options.startTime = options.startTime || Date.now();

        // append elements
        elem.appendChild(timer);

        function start() {
            if (!interval) {
                offset   = options.startTime;
                interval = setInterval(update, options.delay);
            }
        }

        function stop() {
            if (interval) {
                clearInterval(interval);
                interval = null;
            }
        }

        function reset() {
            clock = 0;
            render();
        }

        function update() {
            clock += delta();
            render();
        }

        function render() {
            timer.innerHTML = moment(clock).format('mm:ss');
        }

        function delta() {
            var now = Date.now(),
                d   = now - offset;

            offset = now;
            return d;
        }

        // initialize
        reset();

        // public API
        this.start = start; //function() { start; }
        this.stop  = stop; //function() { stop; }
    };

};

initPhone()
