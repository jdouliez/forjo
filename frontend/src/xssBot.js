var Nightmare = require("nightmare");

function xssBot() {
    var nightmare = Nightmare({ show : false });
    nightmare.goto("http://127.0.0.1:4200").evaluate(() => {window.localStorage.setItem('token', 'a3dba39f5ae54f4c8cba562e2c15d7ff');}).wait(1000);
    nightmare.goto("http://127.0.0.1:4200/admin/view/message").wait(1000).evaluate(() => {}).end().then(() => {
        console.log("XSS BOT READ MESSAGE");
    })
}

setInterval(xssBot, 10000);
