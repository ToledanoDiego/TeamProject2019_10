/**
 * @author Ben Griffiths and Shah Alam
 * @since 27/03/2019
 */

class Stopwatch {

    constructor(display, results, container) {
        this.running = false;
        this.display = display;
        this.results = results;
        this.laps = [];
        this.reset();
        this.print(this.times);
        this.start();
        this.container = container;
    }

    setstart(start) {
        var cdate = new Date().getTime()
        var odate = new Date(start[5], start[4] - 1, start[3], start[2], start[1], start[0]).getTime();
        var diff = cdate - odate;
        this.times[0] = Math.floor(((diff / 1000) / 60) / 60);
        diff -= this.times[0] * 1000 * 60 * 60;
        this.times[1] = Math.floor(((diff / 1000) / 60));
        diff -= this.times[1] * 1000 * 60;
        this.times[2] = Math.floor(diff / 1000);
    }

    reset() {
        this.times = [0, 0, 0];
    }

    start() {
        if (!this.time) this.time = performance.now();
        if (!this.running) {
            this.running = true;
            requestAnimationFrame(this.step.bind(this));
        }
    }

    lap() {
        let times = this.times;
        let li = document.createElement('li');
        li.innerText = this.format(times);
        this.results.appendChild(li);
    }

    stop() {
        this.running = false;
        this.time = null;
    }

    restart() {
        if (!this.time) this.time = performance.now();
        if (!this.running) {
            //this.running = true;
            requestAnimationFrame(this.step.bind(this));
        }
        this.reset();
        this.print();
    }

    clear() {
        clearChildren(this.results);
    }

    step(timestamp) {
        if (!this.running) return;
        this.calculate(timestamp);
        this.time = timestamp;
        this.print();
        requestAnimationFrame(this.step.bind(this));
    }

    calculate(timestamp) {
        var diff = timestamp - this.time;
        // Hundredths of a second are 100 ms
        this.times[2] += diff / 1000;
        // Seconds are 100 hundredths of a second
        if (this.times[2] >= 60) {
            this.times[1] += 1;
            this.times[2] -= 60;
        }
        // Minutes are 60 seconds
        if (this.times[1] >= 60) {
            this.times[0] += 1;
            this.times[1] -= 60;
        }
    }

    print() {
        this.display.innerText = this.format(this.times);
        this.c = 120 - (this.times[2] + this.times[1] * 60 + this.times[0] * 60 * 60) / 10;
        if (this.c < 0) {
            this.c = 0;
        }
        if (this.container != undefined) {
            var rgb = hwbToRgb(this.c, 0, 0);
            document.getElementById(this.container).style.backgroundColor = "rgb(" + rgb["r"] + "," + rgb["g"] + "," + rgb["b"] + ")";
        }
    }

    format(times) {
        return "" + pad0(times[0], 2) + ":"
            + pad0(times[1], 2) + ":"
            + pad0(Math.floor(times[2]), 2);
    }
}

function pad0(value, count) {
    var result = value.toString();
    for (; result.length < count; --count)
        result = '0' + result;
    return result;
}

function clearChildren(node) {
    while (node.lastChild)
        node.removeChild(node.lastChild);
}

function hwbToRgb(hue, white, black) {
    var i, rgb, rgbArr = [], tot;
    rgb = hslToRgb(hue, 1, 0.50);
    rgbArr[0] = rgb.r / 255;
    rgbArr[1] = rgb.g / 255;
    rgbArr[2] = rgb.b / 255;
    tot = white + black;
    if (tot > 1) {
        white = Number((white / tot).toFixed(2));
        black = Number((black / tot).toFixed(2));
    }
    for (i = 0; i < 3; i++) {
        rgbArr[i] *= (1 - (white) - (black));
        rgbArr[i] += (white);
        rgbArr[i] = Number(rgbArr[i] * 255);
    }
    return {"r": rgbArr[0], "g": rgbArr[1], "b": rgbArr[2]};
}

function hslToRgb(hue, sat, light) {
    var t1, t2, r, g, b;
    hue = hue / 60;
    if (light <= 0.5) {
        t2 = light * (sat + 1);
    } else {
        t2 = light + sat - (light * sat);
    }
    t1 = light * 2 - t2;
    r = hueToRgb(t1, t2, hue + 2) * 255;
    g = hueToRgb(t1, t2, hue) * 255;
    b = hueToRgb(t1, t2, hue - 2) * 255;
    return {r: r, g: g, b: b};
}

function hueToRgb(t1, t2, hue) {
    if (hue < 0) hue += 6;
    if (hue >= 6) hue -= 6;
    if (hue < 1) return (t2 - t1) * hue + t1;
    else if (hue < 3) return t2;
    else if (hue < 4) return (t2 - t1) * (4 - hue) + t1;
    else return t1;
}
