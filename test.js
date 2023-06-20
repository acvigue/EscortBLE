const input = "4829023d04462c791201";
//const escortSmartCordKey = [0x622f7b45, 0x312f3c69, 0x36535d67, 0x50677c5f];

const escortSmartCordKey = [0xB67423AB, 0x7B7F599E, 0x831E63EB, 0x535C1285];

function hexToBytes(hex) {
    let bytes = [];
    for (let c = 0; c < hex.length; c += 2)
        bytes.push(parseInt(hex.substr(c, 2), 16));
    return bytes;
}
const intin = hexToBytes(input);
console.log(intin)

console.log(toHexString(esc_unpack(xtea_encrypt(35, esc_pack(intin), escortSmartCordKey))));

function toHexString(byteArray) {
    return Array.from(byteArray, function (byte) {
        return ('0' + (byte & 0xFF).toString(16)).slice(-2);
    }).join(' ')
}

function xtea_encrypt(num_rounds, v, key) {
    let v0 = v[0];
    let v1 = v[1];

    let sum = 0;
    const delta = 0xf160a3d8;

    for (let i = 0; i < num_rounds; i++) {
        v0 += ((v1 << 4 ^ v1 >>> 5) + v1 ^ key[(sum & 0x3)] + sum);
        sum += delta;
        v1 += ((v0 << 4 ^ v0 >>> 5) + v0 ^ key[(sum >>> 11 & 0x3)] + sum);
    }

    return [v0, v1];
}

function esc_pack(esc_req) {
    const b0 = esc_req[0];
    const b1 = esc_req[1];
    const b2 = esc_req[2];
    const b3 = esc_req[3];
    const b4 = esc_req[4];
    const b5 = esc_req[5];
    const b6 = esc_req[6];
    const b7 = esc_req[7];
    const b8 = esc_req[8];
    const b9 = esc_req[9];

    const v0 = b0 & 0x7F | (b1 & 0x7F) << 7 | (b2 & 0x7F) << 14 | (b3 & 0x7F) << 21 | (b4 & 0xF) << 28;
    const v1 = (b4 & 0x70) >> 4 | (b5 & 0x7F) << 3 | (b6 & 0x7F) << 10 | (b7 & 0x7F) << 17 | (b8 & 0x7F) << 24 | (b9 & 0x1) << 31;
    return [v0, v1];
}

function esc_unpack(vv) {
    const v0 = vv[0];
    const v1 = vv[1];

    const b0 = (v0 & 0x7F);
    const b1 = (v0 >>> 7 & 0x7F);
    const b2 = (v0 >>> 14 & 0x7F);
    const b3 = (v0 >>> 21 & 0x7F);
    const b4 = ((v0 >>> 28) & 0x7f) | (v1 << 4) & 0x70;
    const b5 = (v1 >>> 3 & 0x7F);
    const b6 = (v1 >>> 10 & 0x7F);
    const b7 = (v1 >>> 17 & 0x7F);
    const b8 = (v1 >>> 24 & 0x7F);
    const b9 = (v1 >>> 31 & 0x1);

    return [b0, b1, b2, b3, b4, b5, b6, b7, b8, b9];
}