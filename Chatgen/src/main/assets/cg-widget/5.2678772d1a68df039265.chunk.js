(window.webpackJsonp=window.webpackJsonp||[]).push([[5],{"./app/components/ChatHistoryList/index.js":function(e,t,a){"use strict";a.r(t);var s=a("./node_modules/react/index.js"),i=a.n(s),o=(a("./node_modules/prop-types/index.js"),a("./node_modules/react-redux/lib/index.js")),n=a("./node_modules/@material-ui/core/Badge/index.js"),r=a.n(n),c=(a("./app/components/ChatHistoryList/style.scss"),a("./app/Utility/getConversationHistory.js")),p=a("./node_modules/html-react-parser/index.js"),l=a.n(p),d=a("./app/Utility/restructureListInMessage.js"),m=a("./app/components/Utility/widgetDataUtil.js"),u=a("./app/containers/HomePage/reducers/participants/actions.js"),g=a("./app/components/ChatHistoryList/reducers/chatlist/actions.js"),h=a("./app/utils/imageUrl.js"),f=function(){function e(e,t){for(var a=0;a<t.length;a++){var s=t[a];s.enumerable=s.enumerable||!1,s.configurable=!0,"value"in s&&(s.writable=!0),Object.defineProperty(e,s.key,s)}}return function(t,a,s){return a&&e(t.prototype,a),s&&e(t,s),t}}();var y=function(e){function t(e){!function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}(this,t);var a=function(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));return a.handleOutsideClick=function(){a.setState({isActive:!1})},a.state={isActive:!0},a.timeFormat=a.timeFormat.bind(a),a}return function(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}(t,e),f(t,[{key:"componentDidMount",value:function(){var e,t,a,s=this;(e=this.props.visitorContext.widget_key,t=this.props.widgetData.getIn(["widgetInfo","chatgen_aid"]),a=this.props.cookies,m.e(e,t,a)).then((function(e){var t=e.data.message,a=[];t.map((function(e){var t={chat_id:e.chat_id,timestamp:e.timestamp,sender_id:"dialog"===e.sender.type?"bot":"visitor"===e.sender.type?"visitor":e.sender.sender_id},s="",i="conversation_rating"===e.message_data.type&&e.message_data.message_data&&e.message_data.message_data.ratingValue;s=e.message_data.attachments&&e.message_data.attachments>0?"Attachment":"carousel"===e.message_data.type?"carousel":"conversation_rating"===e.message_data.type&&i?e.message_data.message_data.botResponse:e.message_data.text||e.message_data.type,t.text=s,a.push(t)})),s.props.setHistory(a)})),document.addEventListener("click",this.handleOutsideClick,!1)}},{key:"componentWillUnmount",value:function(){document.removeEventListener("click",this.handleOutsideClick)}},{key:"getChatMessages",value:function(e){var t=this;"0"!==e&&"1"!==e?(this.setCookie("active_chat_id",e,30),Object(c.a)(e,this.props.cookies).then((function(a){t.props.getMessageHistory(a,e)}))):this.props.returnToConverstion(e)}},{key:"setCookie",value:function(e,t,a){var s=new Date;s.setTime(s.getTime()+60*a*1e3);var i=e+"="+t+";"+("expires="+s.toGMTString())+";path=/";try{document.cookie=i}catch(e){}window.parent.postMessage({type:"SET_COOKIE",payload:{status:"set_cookie",value:i}},"*")}},{key:"timeFormat",value:function(e){var t=new Date(e);""===e&&(t=new Date);var a=new Date,s=void 0,i=t.getMonth()+1;i=i<10?"0"+i:i;var o=t.getDate()+"-"+i+"-"+t.getFullYear();if(t.getFullYear()===a.getFullYear()&&t.getMonth()===a.getMonth())if(t.getDate()===a.getDate()){var n=t.getMinutes(),r=t.getHours()>=12?" PM":" AM";n=n<10?"0"+n:n,s=(t.getHours()%12==0?12:t.getHours()%12)+":"+n+r}else s=a.getDate()-t.getDate()==1?"Yesterday":o;else s=o;return s}},{key:"render",value:function(){var e=this;if(0===this.props.chats.length)return i.a.createElement("div",{className:"list-loading-wrapper"},i.a.createElement("div",{className:"sc-message sc-message-animation"}));var t,a=this.props.chats.keys(),s=(t=a,Array.isArray(t)?t:Array.from(t)).slice(0);return i.a.createElement("div",null,this.props.chats.size>0?i.a.createElement("div",{"data-testid":"conversation-list-container"},s.map((function(t,a){var s=e.props.chats.get(t),o=e.timeFormat(s.get("timestamp")),n=e.props.notify.getIn(["lastMessages",t]),c=Object(d.a)(n&&n.data&&n.data.text||s.get("text"),"lastMessage");return i.a.createElement("div",{className:"chat-item","data-testid":"conversation"+a,key:a,style:{backgroundColor:0===a&&!0===e.state.isActive?"#f8f8f8":"#ccc"},onClick:function(){e.getChatMessages(t)}},i.a.createElement(r.a,{anchorOrigin:{vertical:"bottom",horizontal:"right"},badgeContent:e.props.notify.getIn(["notifications",t]),color:"secondary",role:"conv-notification"+a},i.a.createElement("div",{"data-test":"conversation-profile-pic"},i.a.createElement("img",{className:"chat-history-image",src:e.props.widgetData.getIn(["agents",s.get("sender_id"),"avatar_url"])||h.a+"/visitor-profile-images/visitor_image.png"}))),i.a.createElement("div",{className:"text"},i.a.createElement("div",{className:"upper-text","data-test":"upper"},i.a.createElement("span",{"data-test":"visitor-name"},"visitor"===s.getIn(["sender_id"])?"You":e.props.widgetData.getIn(["agents",s.get("sender_id"),"agent_name"])),i.a.createElement("div",{className:"time","data-test":"time"},o)),i.a.createElement("div",{className:"last-message","data-test":"latest-message"+a},l()(c))))}))):i.a.createElement("center",null,i.a.createElement("div",null,"No data found")))}}]),t}(s.Component),v={assignParticipants:u.a,setHistory:g.b},x=Object(o.connect)((function(e){return{chats:e.getIn(["chatlist"]),notify:e.getIn(["notify"]),widgetData:e.getIn(["widgetData"])}}),v)(y);t.default=x},"./app/components/ChatHistoryList/style.scss":function(e,t,a){var s=a("./node_modules/css-loader/index.js!./node_modules/sass-loader/dist/cjs.js!./app/components/ChatHistoryList/style.scss");"string"==typeof s&&(s=[[e.i,s,""]]);var i={hmr:!0,transform:void 0,insertInto:void 0};a("./node_modules/style-loader/lib/addStyles.js")(s,i);s.locals&&(e.exports=s.locals)},"./node_modules/css-loader/index.js!./node_modules/sass-loader/dist/cjs.js!./app/components/ChatHistoryList/style.scss":function(e,t,a){(e.exports=a("./node_modules/css-loader/lib/css-base.js")(!1)).push([e.i,".chat-item{display:flex;flex-direction:row;border-bottom:1px solid #eee;padding:0 8px;background:none;height:70px;align-items:center;max-width:100%;cursor:pointer;justify-content:space-evenly}.chat-item div{position:relative}.chat-item .bot-icon{background:#0a5bff;border-radius:20px;padding:3px;height:35px;width:35px;margin-right:10px}.chat-item .bot-icon,.list-loading-wrapper{display:flex;justify-content:center;align-items:center}.list-loading-wrapper{height:77vh;width:100%}.chat-item:hover{background-color:#f8f8f8!important}.chat-history-image{width:40px;height:40px;border-radius:50%}.person{text-align:center}.text{display:flex;flex-direction:column}.upper-text{display:flex;flex-direction:row;justify-content:space-between;flex:1}.upper-text span{font-size:16px;width:164px;text-transform:capitalize}.last-message,.upper-text span{opacity:.7;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.last-message{width:250px;height:20px;font-size:14px;color:#848484}.last-message img{display:none}.last-message p{margin-bottom:unset!important}.time{padding-right:4px;font-size:14px;opacity:.7;color:#848484}.history-list{position:absolute;top:calc(50% - 1em);left:calc(50% - 1em);width:2em;height:2em;border:5px solid rgba(0,0,0,.2);border-left:5px solid #0a5bff;border-radius:50%;animation:load8 1.1s infinite linear}@keyframes load8{0%{transform:rotate(0deg)}to{transform:rotate(1turn)}}.conversation-unread-count{position:absolute;right:-10px;top:15px;z-index:99999999;color:#fff;background:#f44336;height:20px;width:20px;border:3px solid #fff;text-align:center;border-radius:15px;font-size:11px}.message-history{height:77vh;max-height:502px;overflow-y:auto}",""])}}]);