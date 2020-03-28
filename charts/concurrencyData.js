const fs = require('fs')

let dill = function (arr1, arr2, arr3, flag) {
    let a = [arr1, arr2, arr3];
    let max = [0, 0, 0];
    let ave = [0, 0, 0];
    for (let i = 0; i < arr1.length; i++) {
        for (let j = 0; j < a.length; j++) {
            max[j] = Math.max(max[j], a[j][i]);
            ave[j] += parseInt(a[j][i]);
        }
    }
    for (let j = 0; j < a.length; j++) {
        ave[j] /= arr1.length;
    }
    let m = max[0] + max[1] + max[2];
    m /= 3;
    let v = ave[0] + ave[1] + ave[2];
    v /= 3;
    m = parseInt(m * 100) / 100;
    v = parseInt(v * 100) / 100;
    if (flag) {
        maxMRe.push(m);
        aMRe.push(v);
    } else {
        maxPRe.push(m);
        aPRe.push(v);
    }
}


let content = fs.readFileSync("../surface_temperature/test05.txt").toString();
let contents = content.split("\r\n");
let values = [];
for (let i = 0; i < contents.length; i++) {
    if (contents[i].indexOf(',') !== -1) {
        values.push(contents[i].split(' ,'));
    }
}
let maxMRe = [];
let maxPRe = [];
let aMRe = [];
let aPRe = [];
for (let i = 0; i < 20; i++) {
    dill(values[i], values[i + 20], values[i + 40], i % 2 === 0);
}
console.log('['+maxMRe.toString()+']')
console.log('['+maxPRe.toString()+']')
console.log('['+aMRe.toString()+']')
console.log('['+aPRe.toString()+']')