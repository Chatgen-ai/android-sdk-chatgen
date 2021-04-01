/* eslint-disable */

const baseUrls = {
  dev: 'https://dev.chatgen.ai/cmp/chat-widget',
  test: 'https://test.chatgen.ai/cmp/chat-widget',
  app2: 'https://storage.googleapis.com/chatgen-static-files/widget-app2/v1.24',
  app: 'https://storage.googleapis.com/chatgen-static-files/widget-app/v1.24'
};

const baseUrl = baseUrls["test"];
console.log("comingtobotjs", baseUrl);
window.ChatGen = (function () {
  const isMobile = screen.width < 481;
  const getIframe = () => document.getElementById('selekt-chat-widget');

  const openWidget = (posType) => {
    const isMobile = screen.width < 481;
    const iframe = getIframe();
    if (isMobile) {
      iframe.style.height = '100%';
      iframe.style.width = '100%';
      iframe.style.bottom = '0';
      iframe.style.right = '0';
      iframe.style.left = '0';
      iframe.style.top = '0';
    }
    else {
      iframe.style.height = '90vh';
      iframe.style.width = '400px';
      if (posType.includes('middle')) {
        iframe.style.bottom = '24px';
      }
    }
  };

  const updateStylesInIframe = (styles) => {
    const iframe = getIframe();
    for(let key in styles) {
      iframe.style[key] = styles[key];
    }
    const isMobile = screen.width < 481;
    const dimensions = {
      height: (styles.height || 76) + 'px',
      width: (styles.width || 76) + 'px'
    };
    iframe.style.height = dimensions.height;
    iframe.style.width = dimensions.width;
  };

  const isDevelopment = false;
//  if (!isDevelopment) {
//    console.log = () => { };
//    console.warn = () => { };
//  }

  var widget_key;
  var browser = function () {
    var nAgt = navigator.userAgent;
    var browserName = navigator.appName;
    var nameOffset, verOffset, ix;

    // In Opera 15+, the true version is after "OPR/"
    if ((verOffset = nAgt.indexOf('OPR/')) !== -1) {
      browserName = 'Opera';
    }
    // In older Opera, the true version is after "Opera" or after "Version"
    else if ((verOffset = nAgt.indexOf('Opera')) !== -1) {
      browserName = 'Opera';
    }
    // In MSIE, the true version is after "MSIE" in userAgent
    else if ((verOffset = nAgt.indexOf('MSIE')) !== -1) {
      browserName = 'Microsoft Internet Explorer';
    }
    // In Chrome, the true version is after "Chrome"
    else if ((verOffset = nAgt.indexOf('Chrome')) !== -1) {
      browserName = 'Chrome';
    }
    // In Safari, the true version is after "Safari" or after "Version"
    else if ((verOffset = nAgt.indexOf('Safari')) !== -1) {
      browserName = 'Safari';
    }
    // In Firefox, the true version is after "Firefox"
    else if ((verOffset = nAgt.indexOf('Firefox')) !== -1) {
      browserName = 'Firefox';
    }
    // In Firefox, the true version is after "Firefox"
    else if ((verOffset = nAgt.indexOf('Mozilla')) !== -1) {
      browserName = 'Mozilla';
    }
    // In most other browsers, "name/version" is at the end of userAgent
    else if (
      (nameOffset = nAgt.lastIndexOf(' ') + 1) <
      (verOffset = nAgt.lastIndexOf('/'))
    ) {
      browserName = nAgt.substring(nameOffset, verOffset);
      if (browserName.toLowerCase() === browserName.toUpperCase()) {
        browserName = navigator.appName;
      }
    }
    return browserName;
  };
  var os = function () {
    var userAgent = window.navigator.userAgent;
    var platform = window.navigator.platform;
    var macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K'];
    var windowsPlatforms = ['Win32', 'Win64', 'Windows', 'WinCE'];
    var iosPlatforms = ['iPhone', 'iPad', 'iPod'];
    var os = null;

    if (macosPlatforms.indexOf(platform) !== -1) {
      os = 'Mac OS';
    }
    else if (iosPlatforms.indexOf(platform) !== -1) {
      os = 'iOS';
    }
    else if (windowsPlatforms.indexOf(platform) !== -1) {
      os = 'Windows';
    }
    else if (/Android/.test(userAgent)) {
      os = 'Android';
    }
    else if (!os && /Linux/.test(platform)) {
      os = 'Linux';
    }

    return os;
  };

  var _args = {}; // private
  var loaded = false;

  var PageTitleNotification = {
    Vars: {
      lockTitle: false,
      OriginalTitle: document.title,
      Interval: null
    },
    On: function (notification, intervalSpeed) {
      if (!this.Vars.lockTitle) {
        this.Vars.OriginalTitle = document.title;
        this.Vars.lockTitle = true;
      }
      clearInterval(this.Vars.Interval);
      this.Vars.Interval = setInterval(() => {
        document.title =
          this.Vars.OriginalTitle === document.title
            ? notification
            : this.Vars.OriginalTitle;
      }, intervalSpeed || 1000);
    },
    Off: function () {
      clearInterval(this.Vars.Interval);
      document.title = this.Vars.OriginalTitle;
    }
  };
  function getCookie (cname) {
    var name = cname + '=';
    var decodedCookie = decodeURIComponent(parent.document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0) === ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) === 0) {
        return c.substring(name.length, c.length);
      }
    }
    return '';
  };

  const sendDimentions = () => {
    const iframe = document.getElementById('selekt-chat-widget');
    return (
      iframe.contentWindow.postMessage({
        action: 'windowResized',
        values: {
          height: window.innerHeight,
          width: window.innerWidth
        }
      }, '*')
    )
  }

  let isBotLoaded = false;
  return {
    isModalOpen: false,
    startInteraction: function (interactionId) {
      var _args = { event_type: 'interaction' };
      _args.timestamp = new Date();
      // _args.ip_data = JSON.parse(res);
      _args.browser = browser();
      _args.platform = os();
      _args.interactionId = interactionId.interactionId;
      _args.url = window.location.href;
      _args.widget_key = widget_key;
      const iframeElement = document.getElementById('selekt-chat-widget');
      if(isBotLoaded) {
        iframeElement.contentWindow.postMessage(_args, '*');
      } else {
        setTimeout(() => {
          this.startInteraction(interactionId)
        }, 500);
      }
    },

    sendMessage: function (message) {
      var _args = { type: 'sendMessage', message };
      const iframeElement = document.getElementById('selekt-chat-widget');
      if(isBotLoaded){
        iframeElement.contentWindow.postMessage(_args, '*');
      } else {
        setTimeout(() => {
          this.sendMessage();
        }, 500);
      }
    },

    openWidget: function () {
      var _args = { type: 'openWidget', isChatgenLive: false };
      const url = window.location.href;
      const interactionLink = url.split('#');
      if(interactionLink.includes('ChatGenLive')){
        _args.isChatgenLive = true;
      }
      const iframeElement = document.getElementById('selekt-chat-widget');
      if(isBotLoaded) {
        iframeElement.contentWindow.postMessage(_args, '*');
      } else {
        setTimeout(() => {
          this.openWidget()
        }, 500);
      }
    },

    cleanWidget: function (eventData) {
      PageTitleNotification.Off();
    },

    identify: function (identifiers) {
      window.chatgenSettings = { ...window.chatgenSettings, ...identifiers };
      const chatgenIframe = document.getElementById('selekt-chat-widget');
      if(chatgenIframe) {
        chatgenIframe.contentWindow.postMessage({ type: 'userIdentification', value: window.chatgenSettings }, '*')
      }
    },

    init: function (Args, customPositions = {}) {
      _args = Args;
      const chatgenGAScript = `
      <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
      </script>
      `;
      const scriptEl = document.createRange().createContextualFragment(chatgenGAScript);
      document.body.append(scriptEl);
      console.log("WidgetKey", _args.widget_key);
      if (typeof _args.widget_key !== 'undefined') {
        widget_key = _args.widget_key;
        _args.identifier = window.chatgenSettings;
        function injectIframeFunction () {
          if (window.ChatGen && window.ChatGen.loaded) {
            console.log('return bhai ek bar hai');
            return;
          }
          console.log('inject iframe');
          loaded = true;
          window.ChatGen.loaded = true;
          const iframe = document.createElement('iframe');
          iframe.id = 'selekt-chat-widget';
          iframe.src = './index.html';
          console.log("iframe",iframe.src);
          iframe.style =
            'display: block; bottom: unset; left: unset; right: unset; top: unset; border: none; min-width: unset; min-height: unset; position: fixed;visibility:hidden;border: 0px;z-index:9999999999;margin: 0px;padding: 0px;background: none; width:0px;height:0px';
          _args.ip_data = '';
          _args.browser_type = browser();
          _args.os_type = os();
          _args.url = window.location.href;
          _args.active_chat_id = getCookie('active_chat_id')
            ? getCookie('active_chat_id')
            : '';
          _args.cookies = parent.document.cookie;
          _args.isOpener = window.opener !== null;
          document.body.appendChild(iframe);
          const imageModal = document.createElement('div');
          imageModal.id = 'image-modal';
          imageModal.style.position = 'absolute';
          imageModal.style.left = '0';
          imageModal.style.display = 'flex';
          imageModal.style.alignItems = 'center';
          imageModal.style.justifyContent = 'center';
          imageModal.style.top = '0';
          imageModal.style.transition = 'all 0.5s ease';
          imageModal.style.zIndex = '99999999999';
          document.body.appendChild(imageModal);

          imageModal.addEventListener('click', () => {
            console.log('isOpen: ', window.ChatGen.isModalOpen);
            if (!window.ChatGen.isModalOpen) {
              return;
            }
            window.ChatGen.isModalOpen = false;
            const imageEl = document.querySelector('#image-modal img');
            const wrapperEl = document.querySelector('#image-modal');
            wrapperEl.style.background = 'transparent';
            wrapperEl.style.opacity = '0';
            setTimeout(() => {
              const wrapper = document.getElementById('image-modal');
              wrapper.style.right = 'unset';
              wrapper.style.bottom = 'unset';
              wrapper.innerHTML = '';
            }, 200);
            console.log('image modal clicked: ', imageEl);
            imageEl.style.transform = 'translateY(40px)';
          })

          // For a new iframe, emit event to iframe
          iframe.onload = function () {
            console.log("iframeonload");
            iframe.contentWindow.postMessage(_args, '*');
            window.chatgenLoaded = true;
          };

          window.addEventListener('resize', function () {
            sendDimentions();
          });

          function getPositions (device) {
            var positions = { x: 0, y: 0 };
            if (Object.prototype.hasOwnProperty.call(customPositions, device)) {
              positions = customPositions[device];
            }

            return positions;
          }

          bindEvent(window, 'message', function (e) {
            if (e.data && e.data.type) {
              var dataType = e.data.type;
              var data = e.data.payload || e.data;
              if (dataType === 'INIT_GA') {
                ga('create', data.trackingId, 'chatgen');
              }
              if (dataType === 'SEND_GA') {
                ga('send', 'event', {
                  eventCategory: data.category,
                  eventAction: data.action,
                  eventLabel: data.label,
                  eventValue: data.value },
                'chatgen');
              }
              if (dataType === 'OPEN_IMAGE') {
                const el = document.getElementById('image-modal');
                window.ChatGen.isModalOpen = true;
                const imageEl = document.createElement('img');
                imageEl.src = e.data.value;
                imageEl.style.opacity = '1';
                imageEl.style.maxWidth = '90vw';
                imageEl.style.maxHeight = '90vh';
                imageEl.style.transition = 'all 0.5s ease';
                imageEl.style.transform = 'translateY(30px)';
                imageEl.style.cursor = 'zoom-out';
                el.appendChild(imageEl);
                setTimeout(() => {
                  imageEl.style.transform = 'translateY(0)';
                }, 0);
                el.style.right = 0;
                el.style.bottom = 0;
                el.style.background = 'rgba(0, 0, 0, 0.35)';
                el.style.opacity = '1';
                const closeIcon = document.createElement('div');
                closeIcon.classList.add('close-icon');
                closeIcon.style.position = 'absolute';
                closeIcon.style.top = closeIcon.style.right = '20px';
                closeIcon.innerHTML = `
                  <svg
                  x="0px"
                  y="0px"
                  viewBox="0 0 47.971 47.971"
                  fill="#eaeaea"
                  width="17px"
                  height="17px"
                  style="cursor: pointer"
                  >
                    <g>
                      <path
                        d="M28.228,23.986L47.092,5.122c1.172-1.171,1.172-3.071,0-4.242c-1.172-1.172-3.07-1.172-4.242,0L23.986,19.744L5.121,0.88
                        c-1.172-1.172-3.07-1.172-4.242,0c-1.172,1.171-1.172,3.071,0,4.242l18.865,18.864L0.879,42.85c-1.172,1.171-1.172,3.071,0,4.242
                        C1.465,47.677,2.233,47.97,3,47.97s1.535-0.293,2.121-0.879l18.865-18.864L42.85,47.091c0.586,0.586,1.354,0.879,2.121,0.879
                        s1.535-0.293,2.121-0.879c1.172-1.171,1.172-3.071,0-4.242L28.228,23.986z">
                      </path>
                    </g>
                  </svg>
                `;
                el.appendChild(closeIcon);
              }
              if (dataType === 'SET_COOKIE' || dataType === 'ERASE_COOKIE') {
                parent.document.cookie = data.value;
              }
              if (dataType === 'GET_COOKIE') {
                var cookies = parent.document.cookie;
                isBotLoaded = true;
                iframe.contentWindow.postMessage({ origin: 'cookies', cookies: cookies, args: e.data.args }, '*');
                sendDimentions();
                try{
                  ChatgenHandler.botLoaded();
                }catch(e){
                  console.log(e);
                }
              }
              else if (dataType === 'WIDGET_CLOSED'){
                try{
                  ChatgenHandler.closeBot();
                } catch(e){
                  console.log(e);
                }
              }
              else if (
                dataType === 'NOTIFICATION_ON' ||
                dataType === 'NOTIFICATION_OFF'
              ) {
                if (data.status === 'on') {
                  PageTitleNotification.On(data.message, data.time);
                }
                else {
                  PageTitleNotification.Off();
                }
              }
              else {
                try {
                  data = typeof data === 'string' ? JSON.parse(data) : data;
                  if (
                    data.isWidgetActive ||
                    (data.messages && data.messages.length > 0)
                  ) {
                    document.getElementById(
                      'selekt-chat-widget'
                    ).style.visibility = 'visible';
                  }
                  if (data.visible) {
                    const posType = isMobile ? data.mobilePos : data.desktopPos;
                    openWidget(posType);
                  }
                  else {
                    var mobilePosition = getPositions('mobile');
                    var desktopPosition = getPositions('desktop');
                    const offset = isMobile ? '8px' : '24px';
                    const position = isMobile ? mobilePosition : desktopPosition;
                    const topConstraint = {
                      top: `calc(${offset} + ` + position.y + 'px)',
                      bottom: 'unset'
                    };
                    const rightConstraint = {
                      left: 'unset',
                      right: `calc(${offset} - ` + position.x + 'px)'
                    };
                    const leftConstraint = {
                      left: `calc(${offset} + ` + position.x + 'px)',
                      right: 'unset'
                    };
                    const middleConstraint = {
                      bottom: 'calc(50% - ' + position.y + 'px)',
                      top: 'unset'
                    };
                    const bottomConstraint = {
                      bottom: `calc(${offset} - ` + position.y + 'px)',
                      top: 'unset'
                    };
                    if (data.notification !== 0) {
                      if (isMobile && data.showMobileBubble || data.showDesktopBubble) {
                        updateStylesInIframe({
                          height: 120,
                          width: 400
                        });
                      }
                    }
                    else {
                      const posType = isMobile ? data.mobilePos : data.desktopPos;
                      if (posType === 'bottomRight') {
                        updateStylesInIframe({
                          ...bottomConstraint,
                          ...rightConstraint
                        });
                      }
                      else if (posType === 'bottomLeft') {
                        updateStylesInIframe({
                          ...bottomConstraint,
                          ...leftConstraint
                        });
                      }
                      else if (posType === 'middleLeft') {
                        updateStylesInIframe({
                          ...middleConstraint,
                          ...leftConstraint
                        });
                      }
                      else if (posType === 'middleRight') {
                        updateStylesInIframe({
                          ...middleConstraint,
                          ...rightConstraint
                        });
                      }
                      else if (posType === 'topLeft') {
                        updateStylesInIframe({
                          ...topConstraint,
                          ...leftConstraint
                        });
                      }
                      else if (posType === 'topRight') {
                        updateStylesInIframe({
                          ...topConstraint,
                          ...rightConstraint
                        });
                      }
                    }
                  }
                }
                catch (e) {
                  console.log(e);
                }
              }
            }
          });

          function bindEvent (element, eventName, eventHandler) {
            if (element.addEventListener) {
              element.addEventListener(eventName, eventHandler, false);
            }
            else if (element.attachEvent) {
              element.attachEvent('on' + eventName, eventHandler);
            }
          }
        }
        function winLoad (callback) {
          if (document.readyState === 'complete') {
            callback();
          }
          else {
            window.addEventListener('load', callback);
          }
        }
        winLoad(injectIframeFunction);
      }
    },
    loaded: loaded
  };
})();
