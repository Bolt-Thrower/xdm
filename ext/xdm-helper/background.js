function sendURLToXDM(url) {
	var client = new XMLHttpRequest();
	client.open("POST", "http://127.0.0.1:9614/yt_dash_request", true);
	try {
		client.send(url);
	} catch (err) {
	}
}

function checkAndSend_YT_DASH_RequestToXDM(url2) {
	var url = (url2 + "").toLowerCase();
	if (url.indexOf("itag=") > 0 && url.indexOf("videoplayback") > 0) {
		//alert("sending: "+url);
		sendURLToXDM(url2 + "");
	}
}

var tablet = false;

var attached = false;
function attach() {
	var config = {
		mode : "pac_script",
		pacScript : {
			/*data: "function FindProxyForURL(url, host) {\n" +
			      "    return 'PROXY 127.0.0.1:9614;DIRECT;';\n" +
			      "}"*/
			url : "http://127.0.0.1:9614/proxy.pac"
		}
	};
	chrome.proxy.settings.set( {
		value : config,
		scope : 'regular'
	}, function() {
	});
	attached = true;
}

function detach() {
	var config = {
		mode : "system"
	};
	chrome.proxy.settings.set( {
		value : config,
		scope : 'regular'
	}, function() {
	});
	attached = false;
}

function isXDMUp() {
	try {
		var xhr = new XMLHttpRequest();
		xhr.open("GET", "http://127.0.0.1:9614/chrome", false);
		xhr.send();
		if (xhr.status === 200) {
			tablet = ("tablet" == xhr.responseText);
			return true;
		} else {
			table = false;
			return false;
		}
	} catch (e) {
		//alert(e+"");
	}
	//alert("failed");
	return false;
}

function observe() {

	if (isXDMUp()) {
		if (!attached) {
			attach();
			// tablet=true;
		}
	} else {
		if (attached) {
			detach();
			// tablet=false;
		}
	}
}

setInterval( function() {
	observe();
}, 1000);

chrome.webRequest.onBeforeSendHeaders
		.addListener( function(info) {
			checkAndSend_YT_DASH_RequestToXDM(info.url);
			// Replace the User-Agent header
				var headers = info.requestHeaders;
				headers
						.forEach( function(header, i) {
							if (tablet) {
								if (header.name.toLowerCase() == 'user-agent') {
									header.value = 'Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3';
								}
							}
						});
				return {
					requestHeaders : headers
				};
			},
			// Request filter
				{
					// Modify the headers for these pages
					urls : [ "<all_urls>" ]
				}, [ "blocking", "requestHeaders" ]);