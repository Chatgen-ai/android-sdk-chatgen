!function(e){var t={};function n(i){if(t[i])return t[i].exports;var o=t[i]={i:i,l:!1,exports:{}};return e[i].call(o.exports,o,o.exports,n),o.l=!0,o.exports}n.m=e,n.c=t,n.d=function(e,t,i){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:i})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var i=Object.create(null);if(n.r(i),Object.defineProperty(i,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)n.d(i,o,function(t){return e[t]}.bind(null,o));return i},n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="./",n(n.s=0)}({"./bot.js":function(e,t,n){var i=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var i in n)Object.prototype.hasOwnProperty.call(n,i)&&(e[i]=n[i])}return e},o="https://storage.googleapis.com/chatgen-static-files/widget-app/v1.36";window.ChatGen=function(){var e,t=screen.width<481,n=function(){return document.getElementById("selekt-chat-widget")},a=function(e){var t=screen.width<481,i=n();t?(i.style.height="100%",i.style.width="100%",i.style.bottom="0",i.style.right="0",i.style.left="0",i.style.top="0"):(i.style.height="90vh",i.style.width="400px",e.includes("middle")&&(i.style.bottom="24px"))},s=function(e){var t=n();for(var i in e)t.style[i]=e[i];screen.width;var o={height:(e.height||76)+"px",width:(e.width||76)+"px"};t.style.height=o.height,t.style.width=o.width};console.log=function(){},console.warn=function(){};var r=function(){var e,t,n=navigator.userAgent,i=navigator.appName;return-1!==(t=n.indexOf("OPR/"))||-1!==(t=n.indexOf("Opera"))?i="Opera":-1!==(t=n.indexOf("MSIE"))?i="Microsoft Internet Explorer":-1!==(t=n.indexOf("Chrome"))?i="Chrome":-1!==(t=n.indexOf("Safari"))?i="Safari":-1!==(t=n.indexOf("Firefox"))?i="Firefox":-1!==(t=n.indexOf("Mozilla"))?i="Mozilla":(e=n.lastIndexOf(" ")+1)<(t=n.lastIndexOf("/"))&&(i=n.substring(e,t)).toLowerCase()===i.toUpperCase()&&(i=navigator.appName),i},l=function(){var e=window.navigator.userAgent,t=window.navigator.platform,n=null;return-1!==["Macintosh","MacIntel","MacPPC","Mac68K"].indexOf(t)?n="Mac OS":-1!==["iPhone","iPad","iPod"].indexOf(t)?n="iOS":-1!==["Win32","Win64","Windows","WinCE"].indexOf(t)?n="Windows":/Android/.test(e)?n="Android":!n&&/Linux/.test(t)&&(n="Linux"),n},d={},c=!1,u={Vars:{lockTitle:!1,OriginalTitle:document.title,Interval:null},On:function(e,t){var n=this;this.Vars.lockTitle||(this.Vars.OriginalTitle=document.title,this.Vars.lockTitle=!0),clearInterval(this.Vars.Interval),this.Vars.Interval=setInterval((function(){document.title=n.Vars.OriginalTitle===document.title?e:n.Vars.OriginalTitle}),t||1e3)},Off:function(){clearInterval(this.Vars.Interval),document.title=this.Vars.OriginalTitle}};function g(e){for(var t=e+"=",n=decodeURIComponent(parent.document.cookie).split(";"),i=0;i<n.length;i++){for(var o=n[i];" "===o.charAt(0);)o=o.substring(1);if(0===o.indexOf(t))return o.substring(t.length,o.length)}return""}var p=function(){return document.getElementById("selekt-chat-widget").contentWindow.postMessage({action:"windowResized",values:{height:window.innerHeight,width:window.innerWidth}},"*")},h=!1;return{isModalOpen:!1,startInteraction:function(t,n){var i=this,o={event_type:"interaction"};o.timestamp=new Date,o.browser=r(),o.platform=l(),o.interactionId=t.interactionId,o.close=t.close,o.url=window.location.href,o.widget_key=e;var a=document.getElementById("selekt-chat-widget");h?a.contentWindow.postMessage(o,"*"):setTimeout((function(){i.startInteraction(t)}),500)},sendMessage:function(e){var t=this,n={type:"sendMessage",message:e},i=document.getElementById("selekt-chat-widget");h?i.contentWindow.postMessage(n,"*"):setTimeout((function(){t.sendMessage()}),500)},openChatWidget:function(){var e=this,t={type:"openWidget",isChatgenLive:!1};window.location.href.split("#").includes("ChatGenLive")&&(t.isChatgenLive=!0);var n=document.getElementById("selekt-chat-widget");h?n.contentWindow.postMessage(t,"*"):setTimeout((function(){e.openChatWidget()}),500)},cleanWidget:function(e){u.Off()},identify:function(e){window.chatgenSettings=i({},window.chatgenSettings,e);var t=document.getElementById("selekt-chat-widget");t&&t.contentWindow.postMessage({type:"userIdentification",value:window.chatgenSettings},"*")},init:function(n){var f=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{};d=n;var m=this,y=n.openChatWidget,w=n.botPreload,v="\n      <script>\n        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n        })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n      <\/script>\n      ",b=document.createRange().createContextualFragment(v);if(document.body.append(b),void 0!==d.widget_key){var O=function(){if(window.ChatGen&&window.ChatGen.loaded)console.log("return bhai ek bar hai");else{console.log("inject iframe"),c=!0,window.ChatGen.loaded=!0;var e=document.createElement("iframe");e.id="selekt-chat-widget",e.allowFullscreen=!0,e.src=o+"/index.html",e.style="display: block; bottom: unset; left: unset; right: unset; top: unset; border: none; min-width: unset; min-height: unset; position: fixed;visibility:hidden;border: 0px;z-index:9999999999;margin: 0px;padding: 0px;background: none; width:0px;height:0px",d.ip_data="",d.browser_type=r(),d.os_type=l(),d.url=window.location.href,d.active_chat_id=g("active_chat_id")?g("active_chat_id"):"",d.cookies=parent.document.cookie,d.isOpener=null!==window.opener,document.body.appendChild(e);var n,w,v,b=document.createElement("div");b.id="image-modal",b.style.position="absolute",b.style.left="0",b.style.display="flex",b.style.alignItems="center",b.style.justifyContent="center",b.style.top="0",b.style.transition="all 0.5s ease",b.style.zIndex="99999999999",document.body.appendChild(b),b.addEventListener("click",(function(){if(console.log("isOpen: ",window.ChatGen.isModalOpen),window.ChatGen.isModalOpen){window.ChatGen.isModalOpen=!1;var e=document.querySelector("#image-modal img"),t=document.querySelector("#image-modal");t.style.background="transparent",t.style.opacity="0",setTimeout((function(){var e=document.getElementById("image-modal");e.style.right="unset",e.style.bottom="unset",e.innerHTML=""}),200),console.log("image modal clicked: ",e),e.style.transform="translateY(40px)"}})),e.onload=function(){e.contentWindow.postMessage(d,"*"),window.chatgenLoaded=!0},window.addEventListener("resize",(function(){p()})),n=window,w="message",v=function(n){if(n.data&&n.data.type){var o=n.data.type,r=n.data.payload||n.data;if("INIT_GA"===o&&ga("create",r.trackingId,"chatgen"),"SEND_GA"===o&&ga("send","event",{eventCategory:r.category,eventAction:r.action,eventLabel:r.label,eventValue:r.value},"chatgen"),"OPEN_IMAGE"===o){var l=document.getElementById("image-modal");window.ChatGen.isModalOpen=!0;var d=document.createElement("img");d.src=n.data.value,d.style.opacity="1",d.style.maxWidth="90vw",d.style.maxHeight="90vh",d.style.transition="all 0.5s ease",d.style.transform="translateY(30px)",d.style.cursor="zoom-out",l.appendChild(d),setTimeout((function(){d.style.transform="translateY(0)"}),0),l.style.right=0,l.style.bottom=0,l.style.background="rgba(0, 0, 0, 0.35)",l.style.opacity="1";var c=document.createElement("div");c.classList.add("close-icon"),c.style.position="absolute",c.style.top=c.style.right="20px",c.innerHTML='\n                  <svg\n                  x="0px"\n                  y="0px"\n                  viewBox="0 0 47.971 47.971"\n                  fill="#eaeaea"\n                  width="17px"\n                  height="17px"\n                  style="cursor: pointer"\n                  >\n                    <g>\n                      <path\n                        d="M28.228,23.986L47.092,5.122c1.172-1.171,1.172-3.071,0-4.242c-1.172-1.172-3.07-1.172-4.242,0L23.986,19.744L5.121,0.88\n                        c-1.172-1.172-3.07-1.172-4.242,0c-1.172,1.171-1.172,3.071,0,4.242l18.865,18.864L0.879,42.85c-1.172,1.171-1.172,3.071,0,4.242\n                        C1.465,47.677,2.233,47.97,3,47.97s1.535-0.293,2.121-0.879l18.865-18.864L42.85,47.091c0.586,0.586,1.354,0.879,2.121,0.879\n                        s1.535-0.293,2.121-0.879c1.172-1.171,1.172-3.071,0-4.242L28.228,23.986z">\n                      </path>\n                    </g>\n                  </svg>\n                ',l.appendChild(c)}if("SET_COOKIE"!==o&&"ERASE_COOKIE"!==o||(parent.document.cookie=r.value),"GET_COOKIE"===o){var g=parent.document.cookie;h=!0,e.contentWindow.postMessage({origin:"cookies",cookies:g,args:n.data.args},"*"),p(),y&&setTimeout((function(){m.openChatWidget()}),1200)}else if("NOTIFICATION_ON"===o||"NOTIFICATION_OFF"===o)"on"===r.status?u.On(r.message,r.time):u.Off();else try{if(((r="string"==typeof r?JSON.parse(r):r).isWidgetActive||r.messages&&r.messages.length>0)&&(document.getElementById("selekt-chat-widget").style.visibility="visible"),r.visible){var f=t?r.mobilePos:r.desktopPos;a(f)}else{var w=O("mobile"),v=O("desktop"),b=t?"8px":"24px",x=t?w:v,I={top:"calc("+b+" + "+x.y+"px)",bottom:"unset"},C={left:"unset",right:"calc("+b+" - "+x.x+"px)"},E={left:"calc("+b+" + "+x.x+"px)",right:"unset"},k={bottom:"calc(50% - "+x.y+"px)",top:"unset"},M={bottom:"calc("+b+" - "+x.y+"px)",top:"unset"};if(0!==r.notification)(t&&r.showMobileBubble||r.showDesktopBubble)&&s({height:120,width:400});else{var _=t?r.mobilePos:r.desktopPos;"bottomRight"===_?s(i({},M,C)):"bottomLeft"===_?s(i({},M,E)):"middleLeft"===_?s(i({},k,E)):"middleRight"===_?s(i({},k,C)):"topLeft"===_?s(i({},I,E)):"topRight"===_&&s(i({},I,C))}}}catch(n){console.log(n)}}},n.addEventListener?n.addEventListener(w,v,!1):n.attachEvent&&n.attachEvent("on"+w,v)}function O(e){var t={x:0,y:0};return Object.prototype.hasOwnProperty.call(f,e)&&(t=f[e]),t}},x=function(e){"complete"===document.readyState||w?e():window.addEventListener("load",e)};e=d.widget_key,d.identifier=window.chatgenSettings,x(O)}},loaded:c}}()},0:function(e,t,n){e.exports=n("./bot.js")}});