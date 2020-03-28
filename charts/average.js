let v1 = [576,1050,1561,2105,2588]
let v2 = [576,1046,1560,2065,2611]
let v3 = [592,1056,1561,2065,2575]

let p1 = [522,1022,1471,2105,2496]
let p2 = [712,1024,1525,1985,2099]
let p3 = [512,1012,1472,1984,2542]

ave = function(values1,values2,values3) {
    let ave = [];
    for (let i = 0; i < values1.length; i++) {
        ave[i] = parseInt((values1[i] + values2[i] + values3[i]) / 3)
    }
    console.log(ave)
}

ave(v1,v2,v3)

ave(p1,p2,p3)