const noble = require('noble-mac');
const xtea = require('xtea');

const escortServiceUUID = ["B5E22DE9-31EE-42AB-BE6A-9BE0837AA344"]; // default: [] => all
const escortSmartCordKey = [0xB67423AB, 0x7B7F599E, 0x831E63EB, 0x535C1285];

noble.startScanning(escortServiceUUID, false, (err) => {
    console.log(err);
}); // particular UUID's

let connectedPeripheral = null;
let escortService = null;
let txCharacteristic = null;
let rxCharacteristic = null;

let state = 0;

noble.on('discover', peripheralDiscovered);

function peripheralDiscovered(peripheral) {
    console.log(`discovered ${peripheral.advertisement.localName}!`)
    connectedPeripheral = peripheral;
    peripheral.connect();
    peripheral.on('connect', peripheralConnected);
    peripheral.on('')
}

function peripheralConnected() {
    console.log(`connected to ${connectedPeripheral.advertisement.localName}!`)
    connectedPeripheral.once('servicesDiscover', peripheralServicesDiscovered)
    connectedPeripheral.discoverServices()
}

function peripheralServicesDiscovered(services) {
    console.log('services discovered')
    for (const service of services) {
        if (service.uuid == "b5e22de931ee42abbe6a9be0837aa344") {
            service.discoverCharacteristics()
            service.once('characteristicsDiscover', discoveredEscortCharacteristics);
            escortService = service
        }
    }
}

function discoveredEscortCharacteristics(characteristics) {
    console.log('characteristics discovered')
    for (const characteristic of characteristics) {
        if (characteristic.uuid == "b5e22dea31ee42abbe6a9be0837aa344") {
            txCharacteristic = characteristic
        }
        if (characteristic.uuid == "b5e22deb31ee42abbe6a9be0837aa344") {
            rxCharacteristic = characteristic
            characteristic.subscribe();
            characteristic.on('data', rxHandler)
        }
    }

    if (txCharacteristic != null && rxCharacteristic != null) {
        enterEventLoop().then(() => {
            console.log('all done!')
        })
    }
}

async function enterEventLoop() {
    await unlockDevice()
}

function unlockDevice() {
    return new Promise(async (resolve, reject) => {
        const unlockRequest = [0xF5, 0x01, 0x94];
        write(unlockRequest);
    })
}

function write(data) {
    const buf = Buffer.from(data);
    txCharacteristic.write(buf, false)
}

function displayText(text) {
    const textBytes = Buffer.from(text);
    const txPacket = [0xF5, textBytes.length + 2, 0x9A, 9, ...[...textBytes]];
    write(txPacket);
}

function handleCommand(command) {
    const cmdLen = command.length;
    const cmdType = command[0];
    const cmdData = command.slice(1);

    //Authentication request
    if (cmdType == 0xA1) {
        const authResponse = esc_unpack(xtea_encrypt(35, esc_pack(cmdData), escortSmartCordKey));
        const authResponsePacket = [0xF5, authResponse.length + 1, 0xA4, ...[...authResponse]]
        write(authResponsePacket)
    }

    else if (cmdType == 0xA3) {
        if (cmdData[0] == 1) {
            console.log('Radar Locked')
        } else if (cmdData[0] == 2) {
            console.log('Radar Unlock Attempts Exceeded')
        } else {
            console.log('Radar Unlocked!')
            setInterval(() => {
                write([0xF5, 0x01, 0x94])
            }, 5000)
            write([0xF5, 0x00, 0x97])
            write([0xF5, 14, 0x90, ...generateAlert(80)])
        }
    }

    //Requesting speed data
    else if (cmdType == 0xA6) {
        if (cmdData[0] == 1) {
            //send speed limit (45)
            write([0xF5, 0x02, 0xA9, 45])
        } else if (cmdData[0] == 2) {
            //send current speed (MPH) not working
            write([0xF5, 0x03, 0xAA, 0, 40])
        }
    }

    //Overspeed warn request
    else if (cmdType == 0xA7) {
        //Overspeed alert at 10 MPH over S/L
        write([0xF5, 0x02, 0xAB, 0x10]);
    }

    //Radar alert packet
    else if (cmdType == 0xA9) {
        if(cmdData.length > 0) {
            console.log(`alert packet: ${toHexString(cmdData)}`);
        }
    }

    //unhandled command
    else {
        console.log(`command handler: ${toHexString(command)}`);
    }
}

function rxHandler(data, isNotification) {
    const bytes = [...data];

    let commandStarted = false;
    let commandLength = -1;
    let command = [];
    for (const i in bytes) {
        const byte = bytes[i];
        if (byte == 0xF5 && !commandStarted) {
            commandStarted = true;
            command = [];
            commandLength = -1;
        } else if (commandStarted) {
            if (commandLength == -1) {
                commandLength = byte
            } else {
                commandLength--;
                command.push(byte);

                if (commandLength == 0) {
                    commandStarted = false;
                    handleCommand(command);
                }
            }
        }
    }
}

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

function generateAlert(type) {
    const bArr = [80, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64]
    return bArr;
}

function esc_pack(esc_req) {
    const v0 = esc_req[0] & 0x7F | (esc_req[1] & 0x7F) << 7 | (esc_req[2] & 0x7F) << 14 | (esc_req[3] & 0x7F) << 21 | (esc_req[4] & 0xF) << 28;
    const v1 = (esc_req[4] & 0x70) >> 4 | (esc_req[5] & 0x7F) << 3 | (esc_req[6] & 0x7F) << 10 | (esc_req[7] & 0x7F) << 17 | (esc_req[8] & 0x7F) << 24 | (esc_req[9] & 0x1) << 31;
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

console.log(generateAlert(80))